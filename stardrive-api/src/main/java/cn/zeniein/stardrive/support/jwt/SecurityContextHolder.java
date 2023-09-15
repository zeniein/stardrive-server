package cn.zeniein.stardrive.support.jwt;

public class SecurityContextHolder {

    private static final ThreadLocal<Authentication> CONTENT = new ThreadLocal<>();


    public static void setContext(Authentication authentication) {
        CONTENT.set(authentication);
    }

    public static Authentication getContext() {
        return CONTENT.get();
    }

    /**
     * 必须回收自定义的ThreadLocal变量，尤其在线程池场景下，线程经常会被复用，如果不清理自定义的 ThreadLocal变量，可能会影响后续业务逻辑和造成内存泄露等问题，尽量在代理中使用try-finally块进行回收。
     */
    public static void remove() {
        CONTENT.remove();
    }


}
