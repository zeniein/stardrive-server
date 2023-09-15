package cn.zeniein.stardrive.controller;

import cn.zeniein.stardrive.model.bo.EncryptUriBO;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.service.file.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/download")
public class DownloadController {

    private final FileService fileService;

    public DownloadController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/{uri}")
    public void download(@PathVariable("uri") String uri, HttpServletResponse response) {

        EncryptUriBO encryptUriBO = EncryptUriBO.decrypt(uri);
        if (encryptUriBO.isExpired()) {
            throw new BizException("下载链接过期");
        }
        String userId = encryptUriBO.getUserId();
        String fileId = encryptUriBO.getFileId();
        fileService.download(userId, fileId, response);
    }

}
