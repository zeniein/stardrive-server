package cn.zeniein.stardrive.common;


import cn.zeniein.stardrive.exception.BizException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData<T> {

    private T data;
    private final int status;
    private String statusMsg;

    private ResponseData(int status){
        this.status = status;
    }

    private ResponseData(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ResponseData(int status, String statusMsg, T data) {
        this.status = status;
        this.statusMsg = statusMsg;
        this.data = data;
    }

    private ResponseData(int status, String statusMsg) {
        this.status = status;
        this.statusMsg = statusMsg;
    }


    /**
     * 使之不在json序列化结果当中
     */
    @JsonIgnore
    public boolean isSuccess() {
        return this.status == ResponseEnum.SUCCESS.getStatus();
    }

    public T getData() {
        return data;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public static <T> ResponseData<T> create(ResponseEnum e) {
        return new ResponseData<>(e.getStatus(), e.getMsg());
    }

    public static <T> ResponseData<T> create(BizException e) {
        return new ResponseData<>(e.getErrorCode(), e.getErrorMsg());
    }

    public static <T> ResponseData<T> success() {
        return new ResponseData<>(ResponseEnum.SUCCESS.getStatus());
    }

    public static <T> ResponseData<T> success(String msg) {
        return new ResponseData<>(ResponseEnum.SUCCESS.getStatus(), msg);
    }

    public static <T> ResponseData<T> success(T data) {
        return new ResponseData<>(ResponseEnum.SUCCESS.getStatus(), data);
    }

    public static <T> ResponseData<T> success(String msg, T data) {
        return new ResponseData<>(ResponseEnum.SUCCESS.getStatus(), msg, data);
    }


    public static <T> ResponseData<T> error() {
        return new ResponseData<>(ResponseEnum.ERROR.getStatus(), ResponseEnum.ERROR.getMsg());
    }


    public static <T> ResponseData<T> error(String errorMessage) {
        return new ResponseData<>(ResponseEnum.ERROR.getStatus(), errorMessage);
    }

    public static <T> ResponseData<T> error(int errorCode, String errorMessage) {
        return new ResponseData<>(errorCode, errorMessage);
    }
}
