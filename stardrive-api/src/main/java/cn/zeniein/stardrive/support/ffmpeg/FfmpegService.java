package cn.zeniein.stardrive.support.ffmpeg;

import cn.zeniein.stardrive.common.constant.FileConstant;
import cn.zeniein.stardrive.handler.AsyncTaskHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FfmpegService {

    @Resource
    private AsyncTaskHandler asyncTaskHandler;


    /**
     * 从视频中截取出封面
     * <p>
     * command: ffmpeg -i xxx.mp4 -y -vframes 1 -vf scale=480:-1 output.jpg
     *
     * @param source     视频地址
     * @param destFolder 封面输出地址
     * @throws IOException the IOException
     */
    public void extractVideoCover(String source, String destFolder) throws IOException, InterruptedException {
        Path workDir = Paths.get(destFolder);
        if (!Files.exists(workDir)) {
            Files.createDirectories(workDir);
        }
        List<String> commands = new ArrayList<>();
        commands.add("ffmpeg");
        // 源文件
        commands.add("-i");
        commands.add(source);
        // 指定开始时间
        commands.add("-ss");
        commands.add("00:00:05");
        // 第一帧
        commands.add("-vframes");
        commands.add("1");
        // 宽度480, 高度自适应
        commands.add("-vf");
        commands.add("scale=480:-1");
        // 输出图片名
        commands.add("cover.png");

        Process process = new ProcessBuilder().command(commands).directory(workDir.toFile()).start();
        printProcessInfo(process);
        if (process.waitFor() != 0) {
            throw new RuntimeException("提取封面异常");
        }
    }

    /**
     * 将源视频文件转为m3u8格式的视频
     *
     * @param source     源视频位置
     * @param destFolder 目标文件夹
     * @param quality    视频画质
     * @throws IOException          the IOException
     */
    public void transcodeToM3u8(String source, String destFolder, String quality) throws IOException {
        if (!Files.exists(Paths.get(source))) {
            throw new IllegalArgumentException("源视频不存在" + source);
        }
        Path workDir = Paths.get(destFolder, quality);
        if (!Files.exists(workDir)) {
            Files.createDirectories(workDir);
        }

        List<String> commands = new ArrayList<>();
        commands.add("ffmpeg");
        // 源文件
        commands.add("-i");
        commands.add(source);
        // 视频编码为H264
        commands.add("-c:v");
        commands.add("libx264");
        // 标清视频
        if(FileConstant.VIDEO_QUALITY_STANDARD.equals(quality)) {
            // 视频画质处理
            commands.add("-crf");
            commands.add("28");
            // 视频大小
            commands.add("-vf");
            commands.add("scale=-2:360");
        }
        // 音频直接copy
        commands.add("-c:a");
        commands.add("copy");
        // ts切片大小
        commands.add("-hls_time");
        commands.add("30");
        // 点播模式
        commands.add("-hls_playlist_type");
        commands.add("vod");
        commands.add("-hls_segment_filename");
        // ts切片文件名称
        commands.add("%06d.ts");
        // 生成m3u8
        commands.add(FileConstant.M3U8_INDEX_NAME);

        // 异步处理视频转码
        asyncTaskHandler.execute(() -> {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            try {
                Process process = new ProcessBuilder().command(commands).directory(workDir.toFile()).start();
                printProcessInfo(process);

                if (process.waitFor() != 0) {
                    throw new RuntimeException("视频切片异常");
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            stopWatch.stop();
            log.info("视频:{}处理完成,耗时:{}", source, stopWatch.getTotalTimeSeconds() + "s");

        });

    }


    void printProcessInfo(Process process) {
        asyncTaskHandler.execute(() -> {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    log.info(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
