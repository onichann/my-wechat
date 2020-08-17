package com.wt.springboot;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SpringbootWeChatApplication{

    public static void main(String[] args) {
        SpringApplication application=new SpringApplication(SpringbootWeChatApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }

}