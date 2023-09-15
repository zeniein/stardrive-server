package cn.zeniein.stardrive.service.file.helper;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import cn.zeniein.stardrive.model.bo.UploadTaskBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

@Slf4j
public class FileUploadTaskHelper {


//    public static final ConcurrentMap<String, UploadTaskBO> TASK_MAP = new ConcurrentHashMap<>(16);
    /**
     * key:上传ID value:上传任务信息
     */
    private static final TimedCache<String, UploadTaskBO> TASK_MAP = CacheUtil.newTimedCache(DateUnit.HOUR.getMillis());

    public static void create(String uploadId, UploadTaskBO uploadTask) {
        if(uploadTask == null) {
            log.error("create fail!upload task is null");
            return;
        }
        TASK_MAP.put(uploadId, uploadTask);
    }

    public static UploadTaskBO get(String uploadId){
        return TASK_MAP.get(uploadId);
    }

    public static void update(String uploadId, UploadTaskBO uploadTaskBO) {
        TASK_MAP.put(uploadId, uploadTaskBO);
    }

    public static void remove(String uploadId) {
        TASK_MAP.remove(uploadId);
    }

    public static void uploaded(String uploadId, int chunk, int chunks) {
        UploadTaskBO uploadTaskBO = TASK_MAP.get(uploadId);
        if(uploadTaskBO == null) {
            log.error("update fail! task #{} is null", uploadId);
            return;
        }
        uploadTaskBO.updateUploadedChunk(chunk, chunks);
        TASK_MAP.put(uploadId, uploadTaskBO);
    }

    /**
     * 全部分片处理完成后，需要进行对文件进行MD5检验
     */
    public static void handled(String uploadId, int chunk, int chunks) {
        UploadTaskBO uploadTaskBO = TASK_MAP.get(uploadId);
        if(uploadTaskBO == null) {
            log.error("update fail! task #{} is null", uploadId);
            return;
        }
        uploadTaskBO.updateHandledChunk(chunk, chunks);
        TASK_MAP.put(uploadId, uploadTaskBO);
        boolean finish = uploadTaskBO.isFinish();
        if(finish) {
            log.info("分片处理完成");
        }
    }

    public static boolean isComplete(String uploadId) {
        UploadTaskBO uploadTaskBO = TASK_MAP.get(uploadId);
        return uploadTaskBO.isFinish();
    }



    /**
     * 处理分片
     *
     * 设置文件的大小为
     *
     * @param uploadId 上传任务ID
     * @param target 分片写入位置
     * @param chunk 当前分片
     * @param chunks 分片数
     * @param size 完整文件大小
     * @param splitFile 分片文件
     */
    public static void uploadChunk(String uploadId, String target, int chunk, int chunks, long size, MultipartFile splitFile) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RandomAccessFile randomAccessFile = new RandomAccessFile(target, "rw");
        InputStream inputStream = splitFile.getInputStream();
        randomAccessFile.setLength(size);
        if(chunk == chunks - 1) {
            randomAccessFile.seek(size - splitFile.getSize());
        } else {
            randomAccessFile.seek(chunk * splitFile.getSize());
        }
        byte[] buf = new byte[1024];
        int len;
        while (-1 != (len = inputStream.read(buf))) {
            randomAccessFile.write(buf,0,len);
        }
        randomAccessFile.close();
        stopWatch.stop();
        log.info("{}写入分片[{}]完成，耗时:{}", target, chunk, stopWatch.getTotalTimeSeconds() + "s");
        handled(uploadId, chunk, chunks);
    }




}
