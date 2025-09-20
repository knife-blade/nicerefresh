package com.suchtool.nicerefresh.listener.apollo;

import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import com.suchtool.nicerefresh.property.NiceRefreshProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
public class NiceRefreshApolloConfigChangeListener implements ConfigChangeListener {
    private ApplicationContext applicationContext;

    private final NiceRefreshProperty niceRefreshProperty;

    private final RefreshScope refreshScope;

    public NiceRefreshApolloConfigChangeListener(NiceRefreshProperty niceRefreshProperty,
                                                 RefreshScope refreshScope,
                                                 ApplicationContext applicationContext) {
        this.niceRefreshProperty = niceRefreshProperty;
        this.refreshScope = refreshScope;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        try {
            if (!niceRefreshProperty.getEnabled()) {
                return;
            }

            if (niceRefreshProperty.getDebug()) {
                log.info("nicerefresh apollo listener process start");
            }

            process(changeEvent);

            if (niceRefreshProperty.getDebug()) {
                log.info("nicerefresh apollo listener process end");
            }
        } catch (Exception e) {
            log.error("nicerefresh apollo listener process error", e);
        }
    }

    private void process(ConfigChangeEvent changeEvent) {
        for (String changedKey : changeEvent.changedKeys()) {
            ConfigChange configChange = changeEvent.getChange(changedKey);
            String oldValue = configChange.getOldValue();
            String newValue = configChange.getNewValue();
            if (niceRefreshProperty.getDebug()) {
                log.info("nicerefresh apollo listener process: [changedKey:{}, oldValue:{}, newValue:{}]",
                        changedKey, oldValue, newValue);
            }
        }

        refreshProperties(changeEvent);
    }

    private void refreshProperties(ConfigChangeEvent changeEvent) {
        // 更新相应的bean的属性值，主要是存在@ConfigurationProperties注解的bean
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
        refreshScope.refreshAll();
    }
}
