package com.knife.refresh.automatic.refreshvalue;

import com.knife.refresh.automatic.refreshvalue.concrete.AutoRefreshValueBeanPostProcessor;
import com.knife.refresh.automatic.refreshvalue.concrete.AutoRefreshValueListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动刷新Bean里@Value的值
 */
@Configuration
@ConditionalOnProperty(name = "knife.refresh.enabled", havingValue = "true")
public class AutoRefreshValueConfiguration {

    @Bean
    public AutoRefreshValueBeanPostProcessor autoRefreshScopeBeanFactoryPostProcessor() {
        return new AutoRefreshValueBeanPostProcessor();
    }

    @Bean
    public AutoRefreshValueListener autoRefreshValueListener() {
        return new AutoRefreshValueListener();
    }
}