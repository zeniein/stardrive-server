package cn.zeniein.stardrive.controller;

import cn.zeniein.stardrive.common.ResponseData;
import cn.zeniein.stardrive.model.vo.CapacityVO;
import cn.zeniein.stardrive.service.capacity.CapacityService;
import cn.zeniein.stardrive.support.jwt.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/capacity")
public class CapacityController {

    private final CapacityService capacityService;

    public CapacityController(CapacityService capacityService) {
        this.capacityService = capacityService;
    }


    @GetMapping
    public ResponseData<?> getCapacity() {
        String userId = SecurityContextHolder.getContext().getUserId();
        CapacityVO userCapacity = capacityService.getUserCapacity(userId);
        SecurityContextHolder.remove();
        return ResponseData.success(userCapacity);
    }

}
