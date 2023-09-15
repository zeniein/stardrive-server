package cn.zeniein.stardrive.service.capacity;

import cn.zeniein.stardrive.model.vo.CapacityVO;

public interface CapacityService {

    /**
     * 获取用户容量信息
     *
     * @param userId 用户Id
     * @return the capacityVO
     */
    CapacityVO getUserCapacity(String userId);

    /**
     * 用户初始化容量
     *
     * @param userId 用户Id
     */
    void userInit(String userId);

    /**
     * 使用容量
     *
     * @param userId 用户Id
     * @param size   大小(B)
     * @param mode   模式，加或减
     */
    void useSpace(String userId, Long size, int mode);

}
