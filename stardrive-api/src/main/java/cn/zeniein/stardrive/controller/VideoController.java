package cn.zeniein.stardrive.controller;

import cn.zeniein.stardrive.common.ResponseEnum;
import cn.zeniein.stardrive.common.constant.FileConstant;
import cn.zeniein.stardrive.model.bo.EncryptUriBO;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.service.file.helper.FilePathHelper;
import cn.zeniein.stardrive.utils.FileUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;

@CrossOrigin
@RestController
@RequestMapping("/video")
public class VideoController {

    @GetMapping("/cover/{uri}")
    public void videoCover(@PathVariable("uri") String uri,
                           HttpServletResponse response) {
        EncryptUriBO encryptUriBO = EncryptUriBO.decrypt(uri);
        if(encryptUriBO.isExpired()) {
            throw new BizException(ResponseEnum.EXPIRED_LINK);
        }
        String userId = encryptUriBO.getUserId();
        String fileId = encryptUriBO.getFileId();
        String videoCoverPath = FilePathHelper.getVideoCoverPath(userId, fileId);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        FileUtils.readFile(response, Paths.get(videoCoverPath));
    }
    @GetMapping("/{uri}/{index}")
    public void videoM3u8(@PathVariable("uri") String uri,
                          @PathVariable("index") String index,
                          HttpServletResponse response) {
        EncryptUriBO encryptUriBO = EncryptUriBO.decrypt(uri);
        if(encryptUriBO.isExpired()) {
            throw new BizException(ResponseEnum.EXPIRED_LINK);
        }
        String userId = encryptUriBO.getUserId();
        String fileId = encryptUriBO.getFileId();
        // 兼容前期上传的视频
        String slicePath = FilePathHelper.getVideoSlicePath(userId, fileId, FileConstant.VIDEO_QUALITY_STANDARD, index);
        if(Files.notExists(Paths.get(slicePath))) {
            slicePath = FilePathHelper.getVideoSlicePath(userId, fileId, FileConstant.VIDEO_QUALITY_ORIGIN, index);
        }
        FileUtils.readFile(response, Paths.get(slicePath));
    }

}
