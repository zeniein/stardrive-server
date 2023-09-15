package cn.zeniein.stardrive.service.file.task;

import cn.zeniein.stardrive.service.file.FileTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileTask {

    private final FileTaskService fileTaskService;

    public FileTask(FileTaskService fileTaskService) {
        this.fileTaskService = fileTaskService;
    }

    @Scheduled(cron = "0 20 5 * * 5")
    private void clearDeletedFiles() {
        fileTaskService.deleteFileFromDisk();
    }

    @Scheduled(cron = "0 0 0/1 1/1 * * ")
    private void pollRecycleBin(){
        fileTaskService.pollFileExpired();
    }
}
