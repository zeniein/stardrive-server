package cn.zeniein.stardrive.service.file;

import cn.zeniein.stardrive.model.dto.UploadTaskDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadService {

    /**
     * 创建上传任务
     *
     * @param userId   用户ID
     * @param filename 文件名
     * @param fileMd5  文件MD5
     * @param fileSize 文件大小
     * @return the uploadTaskDTO
     */
    UploadTaskDTO createTask(String userId, String filename, String fileMd5, long fileSize);

    /**
     * 文件上传（普通上传）
     *
     * @param userId       用户ID
     * @param file         文件
     * @param parentFileId 文件夹ID
     * @param uploadId     上传ID
     * @throws IOException the IOException
     */
    void upload(String userId, MultipartFile file, String parentFileId, String uploadId) throws IOException;

    /**
     * 文件上传（分片上传）
     *
     * @param userId       用户ID
     * @param file         分片文件
     * @param parentFileId 文件夹ID
     * @param uploadId     上传ID
     * @param chunk        当前分片
     * @param chunks       分片数量
     * @param md5          文件MD5
     * @throws IOException the IOException
     */
    void splitUpload(String userId, MultipartFile file, String parentFileId, String uploadId, Integer chunk, Integer chunks, String md5) throws IOException;
}
