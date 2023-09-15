package cn.zeniein.stardrive.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageUtils {

    /**
     *
     * @param file 图片文件
     * @return BufferedImage
     * @throws IOException 1
     */
    public static BufferedImage loadImage(MultipartFile file) throws IOException {
        return ImageIO.read(file.getInputStream());
    }

    public static BufferedImage loadImage(File file) throws IOException {
        return ImageIO.read(file);
    }

    public static String getImageFormatName(MultipartFile file) {
        String contentType = file.getContentType();
        if(contentType == null) {
            throw new RuntimeException("ContentType is null");
        }
        return contentType.substring(contentType.indexOf("/") + 1);
    }

    /**
     *
     * @param file 图片资源
     * @param standard 标准宽
     * @param outPath 输出路径
     * @throws IOException 1
     */
    public static void scaleAndSaveImage(MultipartFile file, int standard, String outPath) throws IOException {

        BufferedImage bufferedImage = loadImage(file);

        String formatName = getImageFormatName(file);

        scaleAndSaveImage(bufferedImage, formatName, standard, outPath);

    }

    public static void scaleAndSaveImage(File file, int standard, String outPath) throws IOException {

        BufferedImage bufferedImage = loadImage(file);
        Path filePath = file.toPath();
        String contentType = Files.probeContentType(filePath);
        String formatName = contentType.substring(contentType.indexOf("/") + 1);
        scaleAndSaveImage(bufferedImage, formatName, standard, outPath);
    }


    public static void scaleAndSaveImage(BufferedImage bufferedImage, String formatName, int standard, String outPath) throws IOException {

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        if(width > standard){
            float rate = 1.0f * width / standard;
            width = standard;
            height = (int) (height / rate);
        }


        Image scaledInstance = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(width, height, bufferedImage.getType());

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(scaledInstance, 0, 0, null);
        graphics2D.dispose();


        ImageIO.write(outputImage, formatName, new File(outPath));

    }

}
