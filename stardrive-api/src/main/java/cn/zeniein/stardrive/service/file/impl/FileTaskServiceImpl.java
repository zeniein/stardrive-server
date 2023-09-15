package cn.zeniein.stardrive.service.file.impl;

import cn.zeniein.stardrive.common.constant.FileConstant;
import cn.zeniein.stardrive.model.po.FilePO;
import cn.zeniein.stardrive.mapper.FileMapper;
import cn.zeniein.stardrive.service.file.FileTaskService;
import cn.zeniein.stardrive.service.file.helper.FilePathHelper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FileTaskServiceImpl implements FileTaskService {

    private final FileMapper fileMapper;

    public FileTaskServiceImpl(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    @Override
    public void deleteFileFromDisk() {
        LambdaQueryWrapper<FilePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FilePO::getStatus, FileConstant.STATUS_DELETED);

        List<FilePO> deletedFileList = fileMapper.selectList(queryWrapper);
        List<String> handleFileIds = new ArrayList<>();
        for (FilePO fileInfo : deletedFileList) {
            String fileStoragePath = FilePathHelper.getFileStoragePath(fileInfo.getFileLocation());
            try {
                if (Files.exists(Paths.get(fileStoragePath))) {
                    boolean isSuccess = Files.deleteIfExists(Paths.get(fileStoragePath));
                    if(isSuccess){
                        handleFileIds.add(fileInfo.getId());
                    }
                }
            } catch (IOException e) {
                log.info("从磁盘中删除文件失败, 文件路径:{}", fileStoragePath);
            }
        }
        if(handleFileIds.size() != 0) {
            LambdaUpdateWrapper<FilePO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(FilePO::getStatus, FileConstant.STATUS_HARD_DELETED);
            updateWrapper.in(FilePO::getId, handleFileIds);
            fileMapper.update(null, updateWrapper);
        }

    }

    /**
     * 将达到过期时间的文件状态修改为已删除
     */
    @Override
    public void pollFileExpired() {
        LambdaQueryWrapper<FilePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FilePO::getStatus, FileConstant.STATUS_WAIT_DELETED);
        List<FilePO> waitDeletedFileList = fileMapper.selectList(queryWrapper);
        List<String> handleIds = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (FilePO fileInfo : waitDeletedFileList) {
            LocalDateTime expiredAt = fileInfo.getExpiredAt();
            if(expiredAt.isBefore(now)){
                handleIds.add(fileInfo.getId());
            }
        }
        if(handleIds.size() > 0) {
            LambdaUpdateWrapper<FilePO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(FilePO::getStatus, FileConstant.STATUS_DELETED);
            updateWrapper.in(FilePO::getId, handleIds);
            fileMapper.update(null, updateWrapper);
        }
    }
}
