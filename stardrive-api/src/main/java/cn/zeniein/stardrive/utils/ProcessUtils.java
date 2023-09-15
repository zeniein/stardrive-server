package cn.zeniein.stardrive.utils;

import java.io.IOException;

public class ProcessUtils {

    public void exec(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
    }

}
