package cn.zeniein.stardrive.controller;

import cn.zeniein.stardrive.common.ResponseData;
import cn.zeniein.stardrive.model.dto.FileActionDTO;
import cn.zeniein.stardrive.model.vo.RecycleBinVO;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.service.file.FileService;
import cn.zeniein.stardrive.support.jwt.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/recyclebin")
public class RecycleBinController {

    private final FileService fileService;

    public RecycleBinController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping
    public ResponseData<?> list() {
        String userId = SecurityContextHolder.getContext().getUserId();
        List<RecycleBinVO> recycleBin = fileService.getRecycleBinList(userId);
        SecurityContextHolder.remove();
        return ResponseData.success(recycleBin);
    }

    @PostMapping("/trash")
    public ResponseData<?> trash(@RequestBody FileActionDTO fileAction) {
        String userId = SecurityContextHolder.getContext().getUserId();
        if(fileAction.getFileId() == null) {
            throw new BizException("参数错误");
        }
        fileService.trash(userId, fileAction);
        SecurityContextHolder.remove();
        return ResponseData.success();
    }

    @PostMapping("/trash/batch")
    public ResponseData<?> batchTrash(@RequestBody FileActionDTO fileAction) {
        String userId = SecurityContextHolder.getContext().getUserId();
        if(fileAction.getFileIds() == null) {
            throw new BizException("参数错误");
        }
        fileService.trash(userId, fileAction);
        SecurityContextHolder.remove();
        return ResponseData.success();
    }

    @PostMapping("/restore")
    public ResponseData<?> restore(@RequestBody FileActionDTO fileAction) {
        String userId = SecurityContextHolder.getContext().getUserId();
        if(fileAction.getFileId() == null) {
            throw new BizException("参数错误");
        }
        fileService.restore(userId, fileAction);
        SecurityContextHolder.remove();
        return ResponseData.success();
    }

    @PostMapping("/restore/batch")
    public ResponseData<?> batchRestore(@RequestBody FileActionDTO fileAction) {
        String userId = SecurityContextHolder.getContext().getUserId();
        if(fileAction.getFileIds() == null) {
            throw new BizException("参数错误");
        }
        fileService.restore(userId, fileAction);
        SecurityContextHolder.remove();
        return ResponseData.success();
    }

    @PostMapping("/clear")
    public ResponseData<?> clear() {
        String userId = SecurityContextHolder.getContext().getUserId();
        fileService.recycleBinClear(userId);
        SecurityContextHolder.remove();
        return ResponseData.success();
    }

}
