package cn.zeniein.stardrive.cache;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;

/**
 * 验证码缓存
 *
 */
public class VerifiableCodeCache {

    /**
     * 默认验证码，仅临时使用
     */
    public static final String DEFAULT_CODE = "123456";

    /**
     * 验证码默认过期时间，十五分钟
     */
    private static final long DEFAULT_EXPIRATION_TIME = DateUnit.MINUTE.getMillis() * 15;

    /**
     * 缓存对象
     */
    private static final TimedCache<String, String> CODE_CACHE = CacheUtil.newTimedCache(DEFAULT_EXPIRATION_TIME);

    /**
     * 将对象加入到缓存，使用默认失效时长
     *
     * @param key 键
     * @param value 值
     */
    public static void put(String key, String value) {
        CODE_CACHE.put(key, value);
    }

    /**
     * 从缓存中获得对象，当对象不在缓存中或已经过期返回null
     *
     * @param key 键
     * @return 键对应的对象
     */
    public static String get(String key) {
        return CODE_CACHE.get(key, false);
    }

    /**
     * 移除缓存中key对应的对象
     *
     * @param key 键
     */
    public static void remove(String key) {
        CODE_CACHE.remove(key);
    }

}
