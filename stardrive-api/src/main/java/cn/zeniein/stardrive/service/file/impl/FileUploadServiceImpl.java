package cn.zeniein.stardrive.service.file.impl;

import cn.zeniein.stardrive.common.ResponseEnum;
import cn.zeniein.stardrive.common.constant.CapacityConstant;
import cn.zeniein.stardrive.common.constant.FileConstant;
import cn.zeniein.stardrive.model.bo.UploadTaskBO;
import cn.zeniein.stardrive.model.dto.UploadTaskDTO;
import cn.zeniein.stardrive.model.po.FilePO;
import cn.zeniein.stardrive.enums.FileNameModeEnum;
import cn.zeniein.stardrive.enums.FileTypeEnum;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.service.file.helper.FilePathHelper;
import cn.zeniein.stardrive.mapper.FileMapper;
import cn.zeniein.stardrive.service.capacity.CapacityService;
import cn.zeniein.stardrive.service.file.FileCheckService;
import cn.zeniein.stardrive.service.file.FileUploadService;
import cn.zeniein.stardrive.service.file.helper.FileUploadTaskHelper;
import cn.zeniein.stardrive.support.ffmpeg.FfmpegService;
import cn.zeniein.stardrive.utils.ImageUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Component
public class FileUploadServiceImpl implements FileUploadService {

    private final FileMapper fileMapper;

    private final CapacityService capacityService;

    private final FileCheckService fileCheckService;

    private final FfmpegService ffmpegService;

    public FileUploadServiceImpl(FileMapper fileMapper, CapacityService capacityService, FileCheckService fileCheckService, FfmpegService ffmpegService) {
        this.fileMapper = fileMapper;
        this.capacityService = capacityService;
        this.fileCheckService = fileCheckService;
        this.ffmpegService = ffmpegService;
    }


    public boolean checkMd5(String md5, InputStream inputStream) throws IOException {
        String fileMd5 = DigestUtils.md5DigestAsHex(inputStream);
        return md5.equals(fileMd5);
    }

    /**
     * 创建上传任务
     *
     * @param userId   用户ID
     * @param filename 文件名
     * @param fileMd5  文件MD5
     * @param fileSize 文件大小
     * @return 上传任务ID
     */
    @Override
    public UploadTaskDTO createTask(String userId, String filename, String fileMd5, long fileSize) {
        String fileId = IdWorker.getIdStr();
        String uploadId = IdWorker.getIdStr();
        int dotIndex = filename.lastIndexOf(".");
        String fileExtension = dotIndex == -1 ? "" : filename.substring(dotIndex + 1);
        String suffix = "".equals(fileExtension) ? "" : "." + fileExtension;
        String fileLocation = FilePathHelper.getFileLocation(userId, fileId, suffix);

        FileUploadTaskHelper.create(uploadId, new UploadTaskBO(fileId, filename, fileLocation, fileMd5, fileSize, uploadId));

        UploadTaskDTO uploadTaskDTO = new UploadTaskDTO();
        uploadTaskDTO.setUploadId(uploadId);
        return uploadTaskDTO;
    }

    /**
     * 上传文件-普通上传
     * <p>
     * 文件存在同名，则自动重命名文件
     *
     * @param parentFileId 父文件ID
     * @param file         文件信息
     * @param uploadId 父文件ID
     * @param userId       用户ID
     */
    @Override
    public void upload(String userId, MultipartFile file, String parentFileId, String uploadId) throws IOException {
        long fileSize = file.getSize();
        String filename = file.getOriginalFilename();
        if (filename == null) {
            filename = "文件";
        }
        UploadTaskBO uploadTaskBO = FileUploadTaskHelper.get(uploadId);
        if(uploadTaskBO == null) {
            throw new BizException(ResponseEnum.FILE_UPLOAD_TASK_NOT_EXISTS);
        }
        String fileId = uploadTaskBO.getFileId();
        int dotIndex = filename.lastIndexOf(".");
        String fileExtension = dotIndex == -1 ? "" : filename.substring(dotIndex + 1);
        // 名字处理
        filename = fileCheckService.checkName(userId, parentFileId, filename, FileNameModeEnum.IGNORE.name());


        String suffix = "".equals(fileExtension) ? "" : "." + fileExtension;
        String fileLocation = FilePathHelper.getFileLocation(userId, fileId, suffix);

        String url = FilePathHelper.getFileStoragePath(fileLocation);
        Path path = Paths.get(url);
        file.transferTo(path);
        FileTypeEnum fileTypeEnum = FileTypeEnum.byFileExtension(fileExtension);
        int type = fileTypeEnum.getType();

        if (fileTypeEnum == FileTypeEnum.IMAGE) {
            String thumbnail = FilePathHelper.getImagePath(userId) + "/" + FileConstant.THUMBNAIL_PREFIX + fileId + suffix;
            String preview = FilePathHelper.getImagePath(userId) + "/" + FileConstant.PREVIEW_PREFIX + fileId  + suffix;

            ImageUtils.scaleAndSaveImage(file, FileConstant.THUMBNAIL_STANDARD_WIDTH, thumbnail);
            ImageUtils.scaleAndSaveImage(file, FileConstant.PREVIEW_STANDARD_WIDTH, preview);
        } else if (fileTypeEnum == FileTypeEnum.VIDEO) {
            String videoPath = FilePathHelper.getVideoPath(userId) + "/" + fileId;
            try {
                ffmpegService.extractVideoCover(path.toString(), videoPath);
                ffmpegService.transcodeToM3u8(path.toString(), videoPath, FileConstant.VIDEO_QUALITY_ORIGIN);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        String fileMd5 = DigestUtils.md5DigestAsHex(file.getBytes());

        LocalDateTime now = LocalDateTime.now();
        FilePO build = FilePO.builder()
                .id(fileId)
                .name(filename)
                .fileLocation(fileLocation)
                .size(fileSize)
                .parentFileId(parentFileId)
                .createdAt(now)
                .modifiedAt(now)
                .userId(userId)
                .type(type)
                .status(FileConstant.STATUS_NORMAL)
                .fileExtension(fileExtension)
                .fileMd5(fileMd5)
                .build();

        fileMapper.insert(build);
        capacityService.useSpace(userId, fileSize, CapacityConstant.MODE_PLUS);
        FileUploadTaskHelper.remove(uploadId);
    }

    /**
     * 分片上传
     *
     * @param userId   用户ID
     * @param file     文件分片
     * @param uploadId 上传ID
     * @param chunk    当前分片
     * @param chunks   总分片数量
     * @param md5      分片md5
     */
    @Override
    public void splitUpload(String userId, MultipartFile file, String parentFileId, String uploadId, Integer chunk, Integer chunks, String md5) throws IOException {
        UploadTaskBO uploadTaskBO = FileUploadTaskHelper.get(uploadId);
        if (uploadTaskBO == null) {
            throw new BizException(ResponseEnum.FILE_UPLOAD_TASK_NOT_EXISTS);
        }
        String fileStoragePath = FilePathHelper.getFileStoragePath(uploadTaskBO.getFileLocation());
        FileUploadTaskHelper.uploaded(uploadId, chunk, chunks);
        FileUploadTaskHelper.uploadChunk(uploadId, fileStoragePath, chunk, chunks, uploadTaskBO.getFileSize(), file);
        boolean complete = FileUploadTaskHelper.isComplete(uploadId);
        if (complete) {

            boolean checkMd5 = this.checkMd5(uploadTaskBO.getMd5(), new FileInputStream(fileStoragePath));
            if (!checkMd5) {
                FileUploadTaskHelper.remove(uploadId);
                throw new BizException(ResponseEnum.FILE_VALIDATION_FAILED);
            }

            String filename = uploadTaskBO.getName();
            int dotIndex = filename.lastIndexOf(".");

            String fileExtension = dotIndex == -1 ? "" : filename.substring(dotIndex + 1);
            String suffix = "".equals(fileExtension) ? "" : "." + fileExtension;

            // 名字处理
            filename = fileCheckService.checkName(userId, parentFileId, filename, FileNameModeEnum.IGNORE.name());
            LocalDateTime now = LocalDateTime.now();
            FileTypeEnum fileTypeEnum = FileTypeEnum.byFileExtension(fileExtension);
            int type = fileTypeEnum.getType();
            if (fileTypeEnum == FileTypeEnum.IMAGE) {
                String thumbnail = FilePathHelper.getImagePath(userId) + "/" + FileConstant.THUMBNAIL_PREFIX + uploadTaskBO.getFileId() + suffix;
                String preview = FilePathHelper.getImagePath(userId) + "/" + FileConstant.PREVIEW_PREFIX + uploadTaskBO.getFileId() + suffix;
                File imageFile = new File(fileStoragePath);

                ImageUtils.scaleAndSaveImage(imageFile, FileConstant.THUMBNAIL_STANDARD_WIDTH, thumbnail);
                ImageUtils.scaleAndSaveImage(imageFile, FileConstant.PREVIEW_STANDARD_WIDTH, preview);
            } else if (fileTypeEnum == FileTypeEnum.VIDEO) {
                String videoPath = FilePathHelper.getVideoPath(userId) + "/" + uploadTaskBO.getFileId();

                try {
                    ffmpegService.extractVideoCover(fileStoragePath, videoPath);
                    ffmpegService.transcodeToM3u8(fileStoragePath, videoPath, FileConstant.VIDEO_QUALITY_STANDARD);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            long fileSize = uploadTaskBO.getFileSize();
            FilePO build = FilePO.builder()
                    .id(uploadTaskBO.getFileId())
                    .name(filename)
                    .fileLocation(uploadTaskBO.getFileLocation())
                    .size(fileSize)
                    .parentFileId(parentFileId)
                    .createdAt(now)
                    .modifiedAt(now)
                    .userId(userId)
                    .type(type)
                    .status(FileConstant.STATUS_NORMAL)
                    .fileExtension(fileExtension)
                    .fileMd5(uploadTaskBO.getMd5())
                    .build();
            fileMapper.insert(build);
            capacityService.useSpace(userId, fileSize, CapacityConstant.MODE_PLUS);
            FileUploadTaskHelper.remove(uploadId);
        }

    }
}
