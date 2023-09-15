package cn.zeniein.stardrive.service.file;

import cn.zeniein.stardrive.model.po.FilePO;

public interface FileCheckService {

    /**
     * 文件是否存在
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     * @param status 文件状态
     * @return 是否存在
     */
    boolean fileExist(String userId, String fileId, String status);

    /**
     * 文件夹是否存在
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return 是否存在
     */
    boolean folderExist(String userId, String fileId);

    /**
     * 文件名是否存在
     *
     * @param userId       用户ID
     * @param parentFileId 文件夹ID
     * @param name         文件名
     * @return 是否存在
     */
    boolean fileNameExist(String userId, String parentFileId, String name);

    /**
     * 是否是文件夹
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return the boolean
     */
    boolean isFolder(String userId, String fileId);

    /**
     * 文件MD5是否存在
     *
     * @param md5 文件md5
     * @return the boolean
     */
    boolean fileMd5Exist(String md5);

    /**
     * 获取用户文件
     *
     * @param userId 用户ID
     * @param fileId 文件ID
     * @return the file
     */
    FilePO getFileByUser(String userId, String fileId);

    /**
     * 检查名字，按照不同模式进行处理，最后返回处理后的文件名
     *
     * @param userId       用户ID
     * @param parentFileId 文件夹ID
     * @param name         文件名
     * @param mode         处理模式
     * @return 文件名
     */
    String checkName(String userId, String parentFileId, String name, String mode);

}
