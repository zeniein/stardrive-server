package cn.zeniein.stardrive.utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FileUtils {


    public static void readFile(HttpServletResponse response, Path path) {

        if(!Files.exists(path)){
            log.info("文件{}不存在", path);
            response.setStatus(HttpStatus.NOT_FOUND.value());

            return;
        }
        try(InputStream inputStream = Files.newInputStream(path);
            ServletOutputStream outputStream = response.getOutputStream()) {
            byte[] buf = new byte[1024];
            int len;
            while((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void write(File file, InputStream src)  {

        try(FileOutputStream os = new FileOutputStream(file)) {
            byte[] buf = new byte[1024];
            int len;
            while((len = src.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
