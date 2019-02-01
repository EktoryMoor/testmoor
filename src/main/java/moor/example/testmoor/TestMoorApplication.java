/*
 * Copyright (c) 2019.
 * @autor Kate Moor
 */

package moor.example.testmoor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

import java.net.Authenticator;
import java.net.PasswordAuthentication;


@SpringBootApplication
@Configuration
public class TestMoorApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(new Class<?>[]{Bot.class, TestMoorApplication.class}, args);
    }

    /**
     * данные для настройки прокси
     */
    @Value("${bot.proxy.user}")
    private String proxyUser;

    @Value("${bot.proxy.pswd}")
    private String proxyPswd;

    @Value("${bot.proxy.host}")
    private String proxyHost;

    @Value("${bot.proxy.port}")
    private short proxyPort;

    @Bean(name = "botOptions")
    public DefaultBotOptions getBotOptions() {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(proxyUser, proxyPswd.toCharArray());
            }
        });
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
        botOptions.setProxyHost(proxyHost);
        botOptions.setProxyPort(proxyPort);
        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

        return botOptions;
    }


}

