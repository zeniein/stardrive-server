package cn.zeniein.stardrive.controller;

import cn.zeniein.stardrive.common.constant.FileConstant;
import cn.zeniein.stardrive.model.bo.EncryptUriBO;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.service.file.helper.FilePathHelper;
import cn.zeniein.stardrive.utils.FileUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin
@RestController
@RequestMapping("/image")
public class ImageController {


    @GetMapping("/thumbnail/{uri}")
    public void getThumbnailImage(HttpServletResponse response,
                                  @PathVariable(value = "uri") String uri) {
        Path path = getPath(uri, FileConstant.THUMBNAIL_PREFIX);
        response.setContentType("image/png");
        FileUtils.readFile(response, path);
    }

    @GetMapping("/preview/{uri}")
    public void getPreviewImage(HttpServletResponse response,
                                @PathVariable(value = "uri") String uri) {
        Path path = getPath(uri, FileConstant.PREVIEW_PREFIX);
        response.setContentType("image/png");
        FileUtils.readFile(response, path);
    }

    private Path getPath(String uri, String filePrefix) {
        String[] split = uri.split("\\.");
        String suffix = split.length == 2 ? "." + split[1] : "";
        uri = split[0];
        EncryptUriBO encryptUriBO = EncryptUriBO.decrypt(uri);
        if(encryptUriBO.isExpired()) {
            throw new BizException("expired");
        }
        String userId = encryptUriBO.getUserId();
        String fileId = encryptUriBO.getFileId();
        String local = FilePathHelper.getImagePath(userId) + "/" + filePrefix + fileId + suffix.toLowerCase();
        return Paths.get(local);
    }

    @GetMapping("/avatar/{uri}")
    public void getUserAvatar(@PathVariable("uri") String uri, HttpServletResponse response) {
        String avatarPath = FilePathHelper.getFolderBaseAvatar() + "/" + uri;
        Path path = Paths.get(avatarPath);
        FileUtils.readFile(response, path);
    }



}
