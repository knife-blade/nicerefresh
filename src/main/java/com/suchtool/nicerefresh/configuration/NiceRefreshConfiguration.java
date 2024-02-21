package com.suchtool.nicerefresh.configuration;

import com.suchtool.nicerefresh.listener.NiceRefreshEnvironmentChangeListener;
import com.suchtool.nicerefresh.property.NiceRefreshProperty;
import com.suchtool.nicerefresh.runner.NiceRefreshApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动刷新Bean里@Value的值
 */
@Configuration
@ConditionalOnProperty(name = "suchtool.nicerefresh.enabled", havingValue = "true")
public class NiceRefreshConfiguration {
    @Bean(name = "com.suchtool.nicerefresh.niceRefreshApplicationRunner")
    public NiceRefreshApplicationRunner niceRefreshApplicationRunner() {
        return new NiceRefreshApplicationRunner();
    }

    @Bean(name = "com.suchtool.nicerefresh.environmentChangeListener")
    public NiceRefreshEnvironmentChangeListener environmentChangeListener() {
        return new NiceRefreshEnvironmentChangeListener();
    }

    @Bean(name = "com.suchtool.nicerefresh.niceRefreshProperty")
    @ConfigurationProperties(prefix = "suchtool.nicerefresh")
    public NiceRefreshProperty niceRefreshProperty() {
        return new NiceRefreshProperty();
    }
}