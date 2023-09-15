package cn.zeniein.stardrive.service.file;

public interface FileTaskService {

    /**
     * 从磁盘中删除文件，删除已经被标记为已经删除的文件
     */
    void deleteFileFromDisk();

    /**
     * 检测文件是否达到过期时间
     *
     * 将达到过期时间的文件状态修改为已删除
     */
    void pollFileExpired();

}
