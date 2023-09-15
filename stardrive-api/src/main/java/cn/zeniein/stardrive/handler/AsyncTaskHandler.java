package cn.zeniein.stardrive.handler;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class AsyncTaskHandler{

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public AsyncTaskHandler(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }


    public void execute(Runnable runnable) {
        threadPoolTaskExecutor.execute(runnable);
    }

}
