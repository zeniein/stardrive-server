package cn.zeniein.stardrive.model.bo;


import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class UploadTaskBO {

    private final String fileId;
    /**
     * 文件名
     */
    private final String name;


    private final String fileLocation;

    private final String md5;

    private final Long fileSize;

    /**
     * 任务所属用户ID
     */
    private final String userId;

    private Boolean[] uploadedChunk;

    private Boolean[] handledChunk;

    public UploadTaskBO(String fileId, String name, String fileLocation, String md5, long fileSize, String userId) {
        this.userId = userId;
        this.name = name;
        this.fileId = fileId;
        this.fileLocation = fileLocation;
        this.md5 = md5;
        this.fileSize = fileSize;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getFileId() {
        return fileId;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public String getMd5() {
        return md5;
    }

    public long getFileSize() {
        return fileSize;
    }

    public Boolean[] getUploadedChunk() {
        return uploadedChunk;
    }

    public Boolean[] getHandledChunk() {
        return handledChunk;
    }



    /**
     * 更新已上传的分片状态
     * @param chunk 当前分片
     * @param chunks 分片数量
     */
    public void updateUploadedChunk(int chunk, int chunks) {
        synchronized (UploadTaskBO.class) {
            if(this.uploadedChunk == null) {
                log.info("uploadChunk is null, init");
                this.uploadedChunk = new Boolean[chunks];
                for(int i = 0; i < chunks; i++) {
                    this.uploadedChunk[i] = Boolean.FALSE;
                }
            }
            this.uploadedChunk[chunk] = Boolean.TRUE;
            log.info("uploadedChunk is {}", Arrays.toString(this.uploadedChunk));
        }

    }


    public void updateHandledChunk(int chunk, int chunks) {
        synchronized (UploadTaskBO.class) {
            if (this.handledChunk == null) {
                log.info("handledChunk is null, init");
                this.handledChunk = new Boolean[chunks];
                for(int i = 0; i < chunks; i++) {
                    this.handledChunk[i] = Boolean.FALSE;
                }
            }
            log.info("handledChunk is {}", Arrays.toString(this.handledChunk));
            this.handledChunk[chunk] = Boolean.TRUE;
        }
    }

    public boolean isFinish(){
        synchronized (UploadTaskBO.class) {
            if (this.handledChunk == null) {
                return false;
            }
            for (Boolean handle : this.handledChunk) {
                if (handle == null || !handle) {
                    return false;
                }

            }
            return true;
        }
    }

    @Override
    public String toString() {
        return "UploadTaskBO{" +
                "fileId='" + fileId + '\'' +
                ", name='" + name + '\'' +
                ", fileLocation='" + fileLocation + '\'' +
                ", md5='" + md5 + '\'' +
                ", fileSize=" + fileSize +
                ", userId='" + userId + '\'' +
                ", uploadedChunk=" + Arrays.toString(uploadedChunk) +
                ", handledChunk=" + Arrays.toString(handledChunk) +
                '}';
    }
}
