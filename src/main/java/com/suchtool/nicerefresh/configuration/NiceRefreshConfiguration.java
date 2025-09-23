package com.suchtool.nicerefresh.configuration;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.suchtool.nicerefresh.listener.NiceRefreshEnvironmentChangeListener;
import com.suchtool.nicerefresh.listener.apollo.NiceRefreshApolloConfigChangeApplicationRunner;
import com.suchtool.nicerefresh.property.NiceRefreshProperty;
import com.suchtool.nicerefresh.runner.NiceRefreshApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动刷新
 */
@Configuration(value = "com.suchtool.nicerefresh.niceRefreshConfiguration", proxyBeanMethods = false)
@ConditionalOnProperty(name = "suchtool.nicerefresh.enabled", havingValue = "true")
public class NiceRefreshConfiguration {
    @Bean(name = "com.suchtool.nicerefresh.niceRefreshProperty")
    @ConfigurationProperties(prefix = "suchtool.nicerefresh")
    public NiceRefreshProperty niceRefreshProperty() {
        return new NiceRefreshProperty();
    }

    @Bean(name = "com.suchtool.nicerefresh.niceRefreshApplicationRunner")
    public NiceRefreshApplicationRunner niceRefreshApplicationRunner(NiceRefreshProperty niceRefreshProperty) {
        return new NiceRefreshApplicationRunner(niceRefreshProperty);
    }

    @Bean(name = "com.suchtool.nicerefresh.niceRefreshEnvironmentChangeListener")
    @ConditionalOnProperty(name = "suchtool.nicerefresh.update-annotation", havingValue = "true")
    public NiceRefreshEnvironmentChangeListener niceRefreshEnvironmentChangeListener(NiceRefreshProperty niceRefreshProperty) {
        return new NiceRefreshEnvironmentChangeListener(niceRefreshProperty);
    }

    @Bean(name = "com.suchtool.nicerefresh.niceRefreshApolloConfigChangeListener")
    @ConditionalOnProperty(name = "suchtool.nicerefresh.publish-event", havingValue = "true")
    @ConditionalOnClass(ConfigChangeEvent.class)
    public NiceRefreshApolloConfigChangeApplicationRunner niceRefreshApolloConfigChangeListener(
            NiceRefreshProperty niceRefreshProperty) {
        return new NiceRefreshApolloConfigChangeApplicationRunner(niceRefreshProperty);
    }
}