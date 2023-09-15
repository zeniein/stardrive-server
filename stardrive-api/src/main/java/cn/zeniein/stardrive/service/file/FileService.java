package cn.zeniein.stardrive.service.file;

import cn.zeniein.stardrive.model.dto.FileActionDTO;
import cn.zeniein.stardrive.model.vo.FileVO;
import cn.zeniein.stardrive.model.vo.FolderVO;
import cn.zeniein.stardrive.model.vo.RecycleBinVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface FileService {


    /**
     * 获取文件信息
     *
     * @param userId       the userID
     * @param parentFileId 文件夹ID
     * @return the list
     */
    List<FileVO> listByFileDirAndUser(String userId, String parentFileId);

    /**
     * 获取下载链接
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 文件下载链接
     */
    String getDownloadUrl(String userId, String fileId);

    /**
     * 文件下载
     *
     * @param userId   the userId
     * @param fileId   the fileId
     * @param response the response
     */
    void download(String userId, String fileId, HttpServletResponse response);

    /**
     * 获取视频播放地址
     * @param userId the userId
     * @param fileId the fileId
     * @param quality video
     * @return 视频播放地址
     */
    String getPlayVideoAddress(String userId, String fileId, String quality);

    /**
     * 查询当前的目录结构
     *
     * @param userId       the userId
     * @param parentFileId the parentFileId
     * @return the list
     */
    List<FolderVO> getPath(String userId, String parentFileId);

    /**
     * 创建文件夹
     *
     * @param userId     the userId
     * @param fileAction the fileAction
     */
    void createFolder(String userId, FileActionDTO fileAction);

    /**
     * 移动文件
     *
     * @param userId     the userId
     * @param fileAction the fileAction
     */
    void moveFile(String userId, FileActionDTO fileAction);

    /**
     * 复制文件
     *
     * @param userId     the userId
     * @param fileAction the fileAction
     */
    void copyFile(String userId, FileActionDTO fileAction);

    /**
     * 重命名文件
     *
     * @param userId     the userId
     * @param fileAction the fileAction
     */
    void rename(String userId, FileActionDTO fileAction);

    /**
     * 将文件放入回收站
     *
     * @param userId     the userId
     * @param fileAction the fileAction
     */
    void trash(String userId, FileActionDTO fileAction);

    /**
     * 从回收站还原为文件
     *
     * @param userId     the userId
     * @param fileAction the fileAction
     */
    void restore(String userId, FileActionDTO fileAction);

    /**
     * 清空用户回收站
     *
     * @param userId the userId
     */
    void recycleBinClear(String userId);

    /**
     * 删除文件
     *
     * @param userId     the userId
     * @param fileAction the fileAction
     */
    void deleteFile(String userId, FileActionDTO fileAction);

    /**
     * 获取用户回收站数据
     *
     * @param userId the userId
     * @return the list
     */
    List<RecycleBinVO> getRecycleBinList(String userId);

}
