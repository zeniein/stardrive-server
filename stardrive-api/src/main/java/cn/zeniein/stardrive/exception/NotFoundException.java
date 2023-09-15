package cn.zeniein.stardrive.exception;

import cn.zeniein.stardrive.common.ResponseEnum;

public class NotFoundException extends BizException{

    public NotFoundException(ResponseEnum err) {
        super(err);
    }

    public NotFoundException(Integer errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

}
