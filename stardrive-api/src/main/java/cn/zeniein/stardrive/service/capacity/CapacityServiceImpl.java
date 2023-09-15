package cn.zeniein.stardrive.service.capacity;

import cn.zeniein.stardrive.common.ResponseEnum;
import cn.zeniein.stardrive.common.constant.CapacityConstant;
import cn.zeniein.stardrive.model.po.CapacityPO;
import cn.zeniein.stardrive.model.vo.CapacityVO;
import cn.zeniein.stardrive.exception.BizException;
import cn.zeniein.stardrive.mapper.CapacityMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;

@Service
public class CapacityServiceImpl implements CapacityService {

    private final CapacityMapper capacityMapper;

    public CapacityServiceImpl(CapacityMapper capacityMapper) {
        this.capacityMapper = capacityMapper;
    }


    /**
     * 获取用户容量信息
     *
     * @param userId 用户ID
     * @return the object
     */
    @Override
    public CapacityVO getUserCapacity(String userId) {
        CapacityPO userCapacity = capacityMapper.selectById(userId);

        return CapacityVO.builder()
                .useSpace(userCapacity.getUseSpace())
                .totalSpace(userCapacity.getTotalSpace())
                .build();
    }

    /**
     * 用户初始化容量信息
     *
     * @param userId 用户ID
     */
    @Override
    public void userInit(String userId) {
        CapacityPO userCapacity = CapacityPO.builder()
                .userId(userId)
                .totalSpace(CapacityConstant.USER_DEFAULT_TOTAL_SPACE_SIZE)
                .build();
        capacityMapper.insert(userCapacity);
    }

    /**
     * the useSpace
     *
     * @param userId the userId
     * @param size the size
     * @param mode the mode
     */
    @Override
    public void useSpace(String userId, Long size, int mode) {

        CapacityPO userSpaceCapacity = capacityMapper.selectById(userId);
        LambdaUpdateWrapper<CapacityPO> updateWrapper = new LambdaUpdateWrapper<>();

        if (mode == CapacityConstant.MODE_PLUS) {
            if (userSpaceCapacity.getUseSpace() + size > userSpaceCapacity.getTotalSpace()) {
                throw new BizException(ResponseEnum.INSUFFICIENT_STORAGE_SPACE);
            }
            updateWrapper.setSql("use_space=use_space+" + size);
        } else if (mode == CapacityConstant.MODE_SUB) {
            updateWrapper.setSql("use_space=use_space-" + size);
        } else {
            throw new RuntimeException("不支持的操作:" + mode);
        }
        updateWrapper.eq(CapacityPO::getUserId, userId);
        capacityMapper.update(null, updateWrapper);
    }
}
