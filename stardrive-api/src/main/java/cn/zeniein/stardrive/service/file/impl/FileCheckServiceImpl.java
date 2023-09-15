package cn.zeniein.stardrive.service.file.impl;

import cn.zeniein.stardrive.common.ResponseEnum;
import cn.zeniein.stardrive.common.constant.FileConstant;
import cn.zeniein.stardrive.model.po.FilePO;
import cn.zeniein.stardrive.enums.FileNameModeEnum;
import cn.zeniein.stardrive.enums.FileTypeEnum;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.mapper.FileMapper;
import cn.zeniein.stardrive.service.file.FileCheckService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileCheckServiceImpl implements FileCheckService {

    private final FileMapper fileMapper;

    public FileCheckServiceImpl(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    @Override
    public boolean fileExist(String userId, String fileId, String status) {

        return fileMapper.exists(new LambdaQueryWrapper<FilePO>()
                .eq(FilePO::getStatus, status)
                .eq(FilePO::getId, fileId)
                .eq(FilePO::getUserId, userId));
    }

    @Override
    public boolean folderExist(String userId, String fileId) {
        if(FileConstant.USER_ROOT_FOLDER.equals(fileId)) {
            return true;
        }
        return fileMapper.exists(new LambdaQueryWrapper<FilePO>()
                .eq(FilePO::getType, FileTypeEnum.FOLDER.getType())
                .eq(FilePO::getId, fileId)
                .eq(FilePO::getUserId, userId));
    }

    @Override
    public boolean fileNameExist(String userId, String parentFileId, String name) {
        LambdaQueryWrapper<FilePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FilePO::getParentFileId, parentFileId)
                .eq(FilePO::getName, name)
                .eq(FilePO::getStatus, FileConstant.STATUS_NORMAL)
                .eq(FilePO::getUserId, userId);
        return fileMapper.exists(queryWrapper);
    }

    @Override
    public boolean isFolder(String userId, String fileId) {
        return false;
    }

    @Override
    public boolean fileMd5Exist(String md5) {
        return fileMapper.exists(new LambdaQueryWrapper<FilePO>()
                .eq(FilePO::getFileMd5, md5));
    }

    @Override
    public FilePO getFileByUser(String userId, String fileId) {

        return fileMapper.selectOne(new LambdaQueryWrapper<FilePO>()
                .eq(FilePO::getId, fileId)
                .eq(FilePO::getUserId, userId));
    }

    /**
     * 处理名字
     *
     * @param filename 文件名
     * @param mode 处理模式
     * @return 处理后的文件名
     */
    @Override
    public String checkName(String userId, String parentFileId, String filename, String mode) {
        FileNameModeEnum fileNameModeEnum = FileNameModeEnum.match(mode);
        switch (fileNameModeEnum) {
            case AUTO_RENAME -> {
                int dotIndex = filename.lastIndexOf(".");
                String suffix = dotIndex == -1 ? "" : filename.substring(dotIndex);
                StringBuilder name = new StringBuilder(dotIndex == -1 ? filename : filename.substring(0, dotIndex));
                while(fileNameExist(userId, parentFileId, filename)) {
                    name.append("(2)");
                    filename = name + suffix;
                }
                return filename;
            }
            case REFUSE -> {
                if(fileNameExist(userId, parentFileId, filename)) {
                    throw new BizException(ResponseEnum.DUPLICATE_FILENAME);
                }
                return filename;
            }
            case IGNORE -> {
                return filename;
            }
            default -> throw new BizException(ResponseEnum.ERROR.getStatus(), "Unsupported mode");
        }
    }

}
