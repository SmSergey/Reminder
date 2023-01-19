package com.smirnov.app.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.server.Ssl;
import org.springframework.stereotype.Repository;

@Getter
@Setter
@Repository @Slf4j
@ConfigurationProperties(prefix = "server")
public class ApplicationConfiguration {

    public String address;
    public String port;
    public Ssl ssl;


    public String getServerUrl() {
        return (ssl.isEnabled() ? "https://" : "http://") + address + ":" + port;
    }
}
