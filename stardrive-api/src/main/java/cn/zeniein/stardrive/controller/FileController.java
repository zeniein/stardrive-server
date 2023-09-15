package cn.zeniein.stardrive.controller;

import cn.zeniein.stardrive.common.ResponseData;
import cn.zeniein.stardrive.common.ResponseEnum;
import cn.zeniein.stardrive.model.dto.FileActionDTO;
import cn.zeniein.stardrive.model.dto.UploadTaskDTO;
import cn.zeniein.stardrive.model.vo.FileVO;
import cn.zeniein.stardrive.enums.FileNameModeEnum;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.service.file.FileService;
import cn.zeniein.stardrive.service.file.FileUploadService;
import cn.zeniein.stardrive.support.jwt.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    private final FileUploadService uploadService;

    public FileController(FileService fileService, FileUploadService uploadService) {
        this.fileService = fileService;
        this.uploadService = uploadService;
    }

    /**
     * 创建上传任务
     *
     * @param uploadTaskDTO the uploadTaskDTO
     * @return the uploadId
     */
    @PostMapping("/task")
    public ResponseData<?> createUploadTask(@RequestBody UploadTaskDTO uploadTaskDTO) {
        String md5 = uploadTaskDTO.getMd5();
        Long size = uploadTaskDTO.getSize();
        String name = uploadTaskDTO.getName();
        if (md5 == null || size == null || name == null) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "参数错误");
        }
        String userId = SecurityContextHolder.getContext().getUserId();
        UploadTaskDTO task = uploadService.createTask(userId, uploadTaskDTO.getName(), uploadTaskDTO.getMd5(), uploadTaskDTO.getSize());

        return ResponseData.success(task);
    }

    /**
     * @param file         文件
     * @param parentFileId 文件夹ID
     * @param chunk        分块
     * @param chunks       分块数量
     * @param hash         hash值
     * @param size         大小
     * @param uploadId     上传ID
     * @return the result
     * @throws IOException the IOException
     */
    @PostMapping("/upload")
    public ResponseData<?> uploadFile(@RequestParam(value = "file") MultipartFile file,
                                      @RequestParam(value = "parent_file_id") String parentFileId,
                                      @RequestParam(value = "chunk", required = false) Integer chunk,
                                      @RequestParam(value = "chunks", required = false) Integer chunks,
                                      @RequestParam(value = "hash") String hash,
                                      @RequestParam(value = "size", required = false) Long size,
                                      @RequestParam(value = "upload_id") String uploadId) throws IOException {
        String userId = SecurityContextHolder.getContext().getUserId();
        if (chunk != null && chunks != null) {
            uploadService.splitUpload(userId, file, parentFileId, uploadId, chunk, chunks, hash);
        } else {
            // 普通上传
            uploadService.upload(userId, file, parentFileId, uploadId);
        }

        return ResponseData.success();
    }

    @GetMapping("/list")
    public ResponseData<?> loadFileList(@RequestParam(value = "parent_file_id") String parentFileId) {
        String userId = SecurityContextHolder.getContext().getUserId();
        List<FileVO> file = fileService.listByFileDirAndUser(userId, parentFileId);
        return ResponseData.success(file);
    }

    /**
     * 获取文件下载URL
     *
     * @param fileId 文件ID
     * @return 文件下载URL
     */
    @GetMapping("/download/{id}")
    public ResponseData<?> getDownloadUrl(@PathVariable("id") String fileId) {
        String userId = SecurityContextHolder.getContext().getUserId();
        String downloadUrl = fileService.getDownloadUrl(userId, fileId);
        Map<String, Object> res = new HashMap<>(2);
        res.put("result", downloadUrl);
        return ResponseData.success(res);
    }

    /**
     * 创建文件夹
     *
     * @param fileAction name, mode, parentFileId
     * @return the result
     */
    @PostMapping("/folder")
    public ResponseData<?> createFolder(@RequestBody FileActionDTO fileAction) {
        String userId = SecurityContextHolder.getContext().getUserId();
        String mode = fileAction.getMode();
        String name = fileAction.getName();
        String parentFileId = fileAction.getParentFileId();
        if (name == null || parentFileId == null) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "参数错误");
        }
        if (mode == null) {
            fileAction.setMode(FileNameModeEnum.IGNORE.name());
        }
        fileService.createFolder(userId, fileAction);
        return ResponseData.success();
    }

    /**
     * 获取文件层级路径
     *
     * @param id 文件ID
     * @return 文件路径
     */
    @GetMapping("/path/{id}")
    public ResponseData<?> getPath(@PathVariable("id") String id) {
        String userId = SecurityContextHolder.getContext().getUserId();
        Object path = fileService.getPath(userId, id);
        return ResponseData.success(path);
    }

    @PostMapping("/rename")
    public ResponseData<?> rename(@RequestBody FileActionDTO fileAction) {
        String userId = SecurityContextHolder.getContext().getUserId();
        String name = fileAction.getName();
        String mode = fileAction.getMode();

        if (name == null) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "参数错误");
        }
        if (mode == null) {
            fileAction.setMode(FileNameModeEnum.IGNORE.name());
        }
        fileService.rename(userId, fileAction);
        SecurityContextHolder.remove();
        return ResponseData.success();
    }

    @PostMapping("/move")
    public ResponseData<?> fileMove(@RequestBody FileActionDTO fileAction) {
        String userId = SecurityContextHolder.getContext().getUserId();
        fileService.moveFile(userId, fileAction);
        SecurityContextHolder.remove();
        return ResponseData.success();
    }

    @PostMapping("/move/batch")
    public ResponseData<?> batchFileMove(@RequestBody FileActionDTO fileAction) {
        String userId = SecurityContextHolder.getContext().getUserId();
        fileService.moveFile(userId, fileAction);
        SecurityContextHolder.remove();
        return ResponseData.success();
    }

    @PostMapping("/copy")
    public ResponseData<?> fileCopy(@RequestBody FileActionDTO fileAction) {
        String userId = SecurityContextHolder.getContext().getUserId();
        fileService.copyFile(userId, fileAction);
        SecurityContextHolder.remove();
        return ResponseData.success();
    }

    @PostMapping("/delete")
    public ResponseData<?> fileDelete(@RequestBody FileActionDTO fileAction) {
        String userId = SecurityContextHolder.getContext().getUserId();
        if (fileAction.getFileId() == null) {
            throw new BizException("参数错误");
        }
        fileService.deleteFile(userId, fileAction);
        SecurityContextHolder.remove();
        return ResponseData.success();
    }

    @PostMapping("/delete/batch")
    public ResponseData<?> batchFileDelete(@RequestBody FileActionDTO fileAction) {
        String userId = SecurityContextHolder.getContext().getUserId();
        if (fileAction.getFileIds() == null) {
            throw new BizException("参数错误");
        }
        fileService.deleteFile(userId, fileAction);
        SecurityContextHolder.remove();
        return ResponseData.success();
    }

    /**
     * 获取视频播放地址
     * @param fileId 文件ID
     * @param quality 视频质量
     * @return the response
     */
    @GetMapping("/video/address/{fileId}/{quality}")
    public ResponseData<?> getPlayVideoAddress(@PathVariable("fileId") String fileId,
                                               @PathVariable("quality") String quality) {
        String userId = SecurityContextHolder.getContext().getUserId();
        if(fileId == null || quality == null) {
            throw new BizException(ResponseEnum.ERROR.getStatus(), "参数错误");
        }
        String videoAddress = fileService.getPlayVideoAddress(userId, fileId, quality);
        Map<String, String> res = new HashMap<>(2);
        res.put("result", videoAddress);
        SecurityContextHolder.remove();
        return ResponseData.success(res);
    }

}
