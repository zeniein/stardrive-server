package cn.zeniein.stardrive.service.file.impl;

import cn.zeniein.stardrive.common.ResponseEnum;
import cn.zeniein.stardrive.common.constant.CapacityConstant;
import cn.zeniein.stardrive.common.constant.FileConstant;
import cn.zeniein.stardrive.config.UploadConfig;
import cn.zeniein.stardrive.model.bo.EncryptUriBO;
import cn.zeniein.stardrive.model.dto.FileActionDTO;
import cn.zeniein.stardrive.model.po.FilePO;
import cn.zeniein.stardrive.model.vo.FileVO;
import cn.zeniein.stardrive.model.vo.FolderVO;
import cn.zeniein.stardrive.model.vo.RecycleBinVO;
import cn.zeniein.stardrive.enums.FileNameModeEnum;
import cn.zeniein.stardrive.enums.FileTypeEnum;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.exception.NotFoundException;
import cn.zeniein.stardrive.mapper.FileMapper;
import cn.zeniein.stardrive.service.capacity.CapacityService;
import cn.zeniein.stardrive.service.file.FileCheckService;
import cn.zeniein.stardrive.service.file.FileService;
import cn.zeniein.stardrive.service.file.FileUploadService;
import cn.zeniein.stardrive.service.file.helper.FilePathHelper;
import cn.zeniein.stardrive.utils.FileUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private final FileMapper fileMapper;

    private final CapacityService capacityService;

    public final FileUploadService uploadService;

    public final FileCheckService fileCheckService;

    public FileServiceImpl(FileMapper fileMapper, CapacityService capacityService, FileUploadService uploadService, FileCheckService fileCheckService) {
        this.fileMapper = fileMapper;
        this.capacityService = capacityService;
        this.uploadService = uploadService;
        this.fileCheckService = fileCheckService;
    }


    /**
     * 获取用户文件列表
     *
     * @param userId       用户ID
     * @param parentFileId 文件夹ID
     * @return the list
     */
    @Override
    public List<FileVO> listByFileDirAndUser(String userId, String parentFileId) {
        boolean folderExist = fileCheckService.folderExist(userId, parentFileId);
        if (!folderExist) {
            throw new NotFoundException(ResponseEnum.NOT_FOUND_FOLDER_BY_ID.getStatus(), String.format("The resource file_id cannot be found. file_id: %s", parentFileId));
        }

        LambdaQueryWrapper<FilePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FilePO::getParentFileId, parentFileId);
        queryWrapper.eq(FilePO::getUserId, userId);
        queryWrapper.eq(FilePO::getStatus, FileConstant.STATUS_NORMAL);
        queryWrapper.orderByAsc(FilePO::getType);
        queryWrapper.orderByDesc(FilePO::getModifiedAt);
        List<FilePO> list = fileMapper.selectList(queryWrapper);

        return list.stream().map((item) -> {
            FileVO vo = FileVO.builder()
                    .id(item.getId())
                    .type(item.getType())
                    .name(item.getName())
                    .createdAt(item.getCreatedAt())
                    .modifiedAt(item.getModifiedAt())
                    .size(item.getSize())
                    .parentFileId(item.getParentFileId())
                    .fileMd5(item.getFileMd5())
                    .fileExtension(item.getFileExtension())
                    .build();


            if (vo.getType() == FileTypeEnum.IMAGE.getType()) {
                String uri = EncryptUriBO.encrypt(userId, vo.getId());
                vo.setThumbnailUrl(FilePathHelper.getImageThumbnailUrl(uri, vo.getFileExtension()));
                uri = EncryptUriBO.encrypt(userId, vo.getId());
                vo.setPreviewUrl(FilePathHelper.getImagePreviewUrl(uri, vo.getFileExtension()));
            } else if (vo.getType() == FileTypeEnum.VIDEO.getType()) {
                String uri = EncryptUriBO.encrypt(userId, vo.getId());
                vo.setThumbnailUrl(FilePathHelper.getVideoCoverUrl(uri));
                // m3u8
                String videoStandardM3u8IndexPath = FilePathHelper.getVideoSlicePath(userId, vo.getId(), FileConstant.VIDEO_QUALITY_STANDARD, FileConstant.M3U8_INDEX_NAME);
                String videoOriginM3u8IndexPath = FilePathHelper.getVideoSlicePath(userId, vo.getId(), FileConstant.VIDEO_QUALITY_ORIGIN, FileConstant.M3U8_INDEX_NAME);
                uri = EncryptUriBO.encrypt(userId, vo.getId());
                String previewUrl;
                if (Files.exists(Paths.get(videoStandardM3u8IndexPath))) {
                    previewUrl = FilePathHelper.getVideoSourceUrl(uri);
                } else if (Files.exists(Paths.get(videoOriginM3u8IndexPath))) {
                    previewUrl = FilePathHelper.getVideoSourceUrl(uri);
                } else {
                    previewUrl = "";
                }
                vo.setPreviewUrl(previewUrl);

            }
            return vo;
        }).toList();

    }

    /**
     * 获取文件下载路径
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 文件下载url
     */
    @Override
    public String getDownloadUrl(String userId, String fileId) {

        String encryptStr = EncryptUriBO.encrypt(userId, fileId, System.currentTimeMillis(), FileConstant.FILE_DOWNLOAD_ADDRESS_EXPIRES);
        return UploadConfig.apiHost + "/download/" + encryptStr;
    }

    /**
     * 下载文件
     *
     * @param userId   用户ID
     * @param fileId   文件ID
     * @param response 返回数据
     */
    @Override
    public void download(String userId, String fileId, HttpServletResponse response) {
        FilePO file = getFile(userId, fileId);
        if (file.getStatus() != FileConstant.STATUS_NORMAL) {
            throw new NotFoundException(ResponseEnum.NOT_FOUND_FILE_BY_ID.getStatus(), String.format("The resource file_id cannot be found. file_id: %s", fileId));
        }
        if (Objects.equals(file.getType(), FileTypeEnum.FOLDER.getType())) {
            throw new BizException(ResponseEnum.FOLDER_NONSUPPORT_DOWNLOAD);
        }
        response.setContentLengthLong(file.getSize());
        response.setHeader("Content-Disposition", "attachment;fileName=" + file.getName());
        String local = FilePathHelper.getFileStoragePath(file.getFileLocation());
        Path path = Paths.get(local);
        FileUtils.readFile(response, path);
    }

    /**
     * 获取视频播放地址
     *
     *
     * @param userId the userId
     * @param fileId the fileId
     * @param quality video
     * @return 视频播放地址
     */
    @Override
    public String getPlayVideoAddress(String userId, String fileId, String quality) {

        String videoM3u8IndexPath = FilePathHelper.getVideoSlicePath(userId, fileId, quality, FileConstant.M3U8_INDEX_NAME);
        if (Files.notExists(Paths.get(videoM3u8IndexPath))) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "获取视频播放地址失败");
        }
        String uri = EncryptUriBO.encrypt(userId, fileId);

        return FilePathHelper.getVideoSourceUrl(uri);
    }


    /**
     * 查询当前的目录结构
     *
     * @param userId       the userId
     * @param parentFileId the parentFileId
     * @return the list folder
     */
    @Override
    public List<FolderVO> getPath(String userId, String parentFileId) {
        boolean folderExist = fileCheckService.folderExist(userId, parentFileId);
        if (!folderExist) {
            throw new NotFoundException(ResponseEnum.NOT_FOUND_FOLDER_BY_ID.getStatus(), String.format("The resource file_id cannot be found. file_id: %s", parentFileId));
        }
        List<FolderVO> folders = new ArrayList<>();
        List<FilePO> fileInfos = fileMapper.selectList(
                new LambdaQueryWrapper<FilePO>()
                        .eq(FilePO::getStatus, FileConstant.STATUS_NORMAL)
                        .eq(FilePO::getType, FileTypeEnum.FOLDER.getType())
                        .eq(FilePO::getUserId, userId)
        );
        if (FileConstant.USER_ROOT_FOLDER.equals(parentFileId)) {
            return folders;
        }

        String fileId = parentFileId;
        FilePO fileNode = null;
        while (!FileConstant.USER_ROOT_FOLDER.equals(fileId)) {

            for (FilePO item : fileInfos) {
                if (fileId.equals(item.getId())) {
                    fileNode = item;
                    break;
                }
            }
            if (fileNode == null) {
                break;
            }
            folders.add(new FolderVO(fileNode.getName(), fileNode.getId()));
            fileId = fileNode.getParentFileId();
        }

        Collections.reverse(folders);
        return folders;
    }

    @Override
    public void createFolder(String userId, FileActionDTO fileAction) {
        String name = fileAction.getName();
        String parentFileId = fileAction.getParentFileId();
        String mode = fileAction.getMode();
        if (!FileConstant.USER_ROOT_FOLDER.equals(parentFileId)) {
            boolean pathExists = fileMapper.exists(new LambdaQueryWrapper<FilePO>().eq(FilePO::getUserId, userId).eq(FilePO::getId, parentFileId));
            if (!pathExists) {
                throw new NotFoundException(ResponseEnum.NOT_FOUND_FOLDER_BY_ID.getStatus(), String.format("The resource file_id cannot be found. file_id: %s", parentFileId));
            }
        }
        mode = FileNameModeEnum.match(mode).name();
        name = fileCheckService.checkName(userId, parentFileId, name, mode);
        FilePO build = FilePO.builder()
                .id(IdWorker.get32UUID())
                .createdAt(LocalDateTime.now())
                .status(FileConstant.STATUS_NORMAL)
                .name(name)
                .parentFileId(parentFileId)
                .type(FileTypeEnum.FOLDER.getType())
                .userId(userId)
                .build();
        fileMapper.insert(build);
    }

    /**
     * 移动文件
     * <p>
     * 如果fileIds存在，进行批量修改
     *
     * @param userId     用户ID
     * @param fileAction 操作信息
     */
    @Override
    public void moveFile(String userId, FileActionDTO fileAction) {
        String fileId = fileAction.getFileId();
        String toParentFileId = fileAction.getToParentFileId();
        List<String> fileIds = fileAction.getFileIds();
        boolean folderExist = fileCheckService.folderExist(toParentFileId, userId);
        if (!folderExist) {
            throw new NotFoundException(ResponseEnum.NOT_FOUND_FOLDER_BY_ID.getStatus(), String.format("Move failure. The resource file_id cannot be found. file_id: %s", toParentFileId));
        }
        LambdaUpdateWrapper<FilePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FilePO::getParentFileId, toParentFileId);
        if (fileIds != null) {
            if (fileIds.contains(toParentFileId)) {
                throw new BizException(ResponseEnum.ERROR.getStatus(), "移动失败");
            }
            updateWrapper.in(FilePO::getId, fileIds);
        } else {
            if (fileId.equals(toParentFileId)) {
                throw new BizException(ResponseEnum.ERROR.getStatus(), "移动失败");
            }
            updateWrapper.eq(FilePO::getId, fileId);
        }


        updateWrapper.eq(FilePO::getUserId, userId);
        fileMapper.update(null, updateWrapper);
    }


    @Override
    public void copyFile(String userId, FileActionDTO fileAction) {
        String toParentFileId = fileAction.getToParentFileId();
        String fileId = fileAction.getFileId();
        boolean folderExist = fileCheckService.folderExist(userId, toParentFileId);
        if (!folderExist) {
            throw new NotFoundException(ResponseEnum.NOT_FOUND_FOLDER_BY_ID.getStatus(), String.format("The resource file_id cannot be found. file_id: %s", toParentFileId));
        }
        FilePO fileInfo = getFile(userId, fileId);
        FilePO file = new FilePO();
        BeanUtils.copyProperties(fileInfo, file);
        file.setId(IdWorker.get32UUID());
        file.setCreatedAt(LocalDateTime.now());
        fileMapper.insert(file);
    }


    @Override
    public void rename(String userId, FileActionDTO fileAction) {
        String fileId = fileAction.getFileId();
        String name = fileAction.getName();
        FilePO file = getFile(userId, fileId);
        String parentFileId = file.getParentFileId();
        name = fileCheckService.checkName(userId, parentFileId, name, FileNameModeEnum.match(fileAction.getMode()).name());

        LambdaUpdateWrapper<FilePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FilePO::getName, name);
        updateWrapper.set(FilePO::getModifiedAt, LocalDateTime.now());
        updateWrapper.eq(FilePO::getId, fileId);
        updateWrapper.eq(FilePO::getUserId, userId);

        fileMapper.update(null, updateWrapper);

    }

    /**
     * 将文件放入回收站，更新用户空间容量
     * <p>
     * 文件状态需要为normal, 放入回收站后更改为wait_deleted
     * <p>
     * 设置过期时间为10天后
     *
     * @param userId     用户ID
     * @param fileAction 文件信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void trash(String userId, FileActionDTO fileAction) {
        String fileId = fileAction.getFileId();
        List<String> fileIds = fileAction.getFileIds();

        LocalDateTime now = LocalDateTime.now();

        int recycleBinFileCount = fileMapper.selectCount(new LambdaQueryWrapper<FilePO>()
                .eq(FilePO::getStatus, FileConstant.STATUS_WAIT_DELETED)
                .eq(FilePO::getUserId, userId)
        ).intValue();


        LambdaUpdateWrapper<FilePO> updateWrapper = new LambdaUpdateWrapper<>();

        if (fileIds != null) {
            List<FilePO> fileList = fileMapper.selectList(
                    new LambdaQueryWrapper<FilePO>()
                            .in(FilePO::getId, fileIds)
                            .eq(FilePO::getStatus, FileConstant.STATUS_NORMAL)
                            .eq(FilePO::getUserId, userId)
            );
            int fileCount = fileIds.size();
            if(fileCount + recycleBinFileCount > FileConstant.RECYCLE_BIN_MAX_SIZE) {
                throw new BizException(ResponseEnum.RECYCLE_BIN_INSUFFICIENT_STORAGE_SPACE);
            }
            long totalFileSize = fileList.stream().map(FilePO::getSize).reduce(0L, Long::sum);
            capacityService.useSpace(userId, totalFileSize, CapacityConstant.MODE_SUB);
            updateWrapper.in(FilePO::getId, fileIds);
        } else {
            if(recycleBinFileCount >= FileConstant.RECYCLE_BIN_MAX_SIZE) {
                throw new BizException(ResponseEnum.RECYCLE_BIN_INSUFFICIENT_STORAGE_SPACE);
            }
            FilePO file = getFile(userId, fileId);
            capacityService.useSpace(userId, file.getSize(), CapacityConstant.MODE_SUB);
            updateWrapper.eq(FilePO::getId, fileId);
        }

        updateWrapper.set(FilePO::getStatus, FileConstant.STATUS_WAIT_DELETED);
        updateWrapper.set(FilePO::getModifiedAt, now);
        updateWrapper.set(FilePO::getTrashedAt, now);
        updateWrapper.set(FilePO::getExpiredAt, now.plusDays(FileConstant.RECYCLE_BIN_DEFAULT_EXPIRED_DAYS));
        updateWrapper.eq(FilePO::getStatus, FileConstant.STATUS_NORMAL);
        updateWrapper.eq(FilePO::getUserId, userId);
        fileMapper.update(null, updateWrapper);

    }

    /**
     * 文件还原
     * <p>
     * 将状态修改为normal，清除放入回收站时间和过期时间
     *
     * @param userId     用户ID
     * @param fileAction 文件信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restore(String userId, FileActionDTO fileAction) {
        String fileId = fileAction.getFileId();
        List<String> fileIds = fileAction.getFileIds();
        LambdaUpdateWrapper<FilePO> updateWrapper = new LambdaUpdateWrapper<>();
        if (fileIds != null) {
            List<FilePO> fileList = fileMapper.selectList(
                    new LambdaQueryWrapper<FilePO>()
                            .in(FilePO::getId, fileIds)
                            .eq(FilePO::getStatus, FileConstant.STATUS_WAIT_DELETED)
                            .eq(FilePO::getUserId, userId)
            );

            long totalFileSize = fileList.stream().map(FilePO::getSize).reduce(0L, Long::sum);
            capacityService.useSpace(userId, totalFileSize, CapacityConstant.MODE_PLUS);
            updateWrapper.in(FilePO::getId, fileIds);
        } else {
            FilePO file = getFile(userId, fileId);
            updateWrapper.eq(FilePO::getId, fileId);
            capacityService.useSpace(userId, file.getSize(), CapacityConstant.MODE_PLUS);
        }
        LocalDateTime now = LocalDateTime.now();
        updateWrapper.set(FilePO::getStatus, FileConstant.STATUS_NORMAL);
        updateWrapper.set(FilePO::getModifiedAt, now);
        updateWrapper.set(FilePO::getTrashedAt, null);
        updateWrapper.set(FilePO::getExpiredAt, null);
        updateWrapper.set(FilePO::getStatus, FileConstant.STATUS_WAIT_DELETED);
        updateWrapper.eq(FilePO::getUserId, userId);
        fileMapper.update(null, updateWrapper);
    }

    /**
     * 清空回收站
     *
     * @param userId 用户ID
     */
    @Override
    public void recycleBinClear(String userId) {
        LambdaUpdateWrapper<FilePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FilePO::getStatus, FileConstant.STATUS_DELETED);
        updateWrapper.eq(FilePO::getStatus, FileConstant.STATUS_WAIT_DELETED);
        updateWrapper.eq(FilePO::getUserId, userId);
        fileMapper.update(null, updateWrapper);
    }

    /**
     * 删除文件，并将过期时间设置为删除的时间
     *
     * @param userId     用户ID
     * @param fileAction 文件信息
     */
    @Override
    public void deleteFile(String userId, FileActionDTO fileAction) {
        String fileId = fileAction.getFileId();
        List<String> fileIds = fileAction.getFileIds();
        LambdaUpdateWrapper<FilePO> updateWrapper = new LambdaUpdateWrapper<>();
        if (fileIds != null) {
            updateWrapper.in(FilePO::getId, fileIds);
        } else {
            updateWrapper.eq(FilePO::getId, fileId);
        }

        updateWrapper.set(FilePO::getStatus, FileConstant.STATUS_DELETED);
        updateWrapper.set(FilePO::getExpiredAt, LocalDateTime.now());
        updateWrapper.eq(FilePO::getStatus, FileConstant.STATUS_WAIT_DELETED);
        updateWrapper.eq(FilePO::getUserId, userId);
        fileMapper.update(null, updateWrapper);
    }

    /**
     * 回收站列表
     *
     * @param userId the userId
     * @return the list
     */
    @Override
    public List<RecycleBinVO> getRecycleBinList(String userId) {
        LambdaQueryWrapper<FilePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FilePO::getStatus, FileConstant.STATUS_WAIT_DELETED);
        queryWrapper.ge(FilePO::getExpiredAt, LocalDateTime.now());
        queryWrapper.eq(FilePO::getUserId, userId);
        queryWrapper.orderByAsc(FilePO::getType);
        queryWrapper.orderByDesc(FilePO::getTrashedAt);
        List<FilePO> list = fileMapper.selectList(queryWrapper);

        return list.stream().map((item) -> {
            RecycleBinVO vo = RecycleBinVO.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .type(item.getType())
                    .size(item.getSize())
                    .fileExtension(item.getFileExtension())
                    .trashedAt(item.getTrashedAt())
                    .expiredAt(item.getExpiredAt())
                    .build();
            String uri = EncryptUriBO.encrypt(userId, vo.getId());
            if (vo.getType() == FileTypeEnum.IMAGE.getType()) {
                vo.setThumbnailUrl(FilePathHelper.getImageThumbnailUrl(uri, vo.getFileExtension()));
            } else if (vo.getType() == FileTypeEnum.VIDEO.getType()) {
                vo.setThumbnailUrl(FilePathHelper.getVideoCoverUrl(uri));
            }
            return vo;
        }).toList();
    }

    /**
     * 根据文件ID获取用户文件
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 文件信息
     */
    private FilePO getFile(String userId, String fileId) {
        LambdaQueryWrapper<FilePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FilePO::getUserId, userId);
        queryWrapper.eq(FilePO::getId, fileId);
        FilePO file = fileMapper.selectOne(queryWrapper);
        if (file == null) {
            throw new BizException("文件不存在");
        }
        return file;
    }

}
