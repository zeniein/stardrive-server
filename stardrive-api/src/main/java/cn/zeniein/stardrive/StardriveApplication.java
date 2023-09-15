package cn.zeniein.stardrive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class StardriveApplication {

    public static void main(String[] args) {
        SpringApplication.run(StardriveApplication.class, args);
    }

}
