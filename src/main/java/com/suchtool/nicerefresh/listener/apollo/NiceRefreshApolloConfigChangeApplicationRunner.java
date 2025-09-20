package com.suchtool.nicerefresh.listener.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.suchtool.nicerefresh.property.NiceRefreshProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
public class NiceRefreshApolloConfigChangeApplicationRunner
        implements ApplicationContextAware, ApplicationRunner {

    private ApplicationContext applicationContext;

    private final NiceRefreshProperty niceRefreshProperty;

    private final RefreshScope refreshScope;

    @Value("${apollo.bootstrap.namespaces}")
    private String namespaces;

    public NiceRefreshApolloConfigChangeApplicationRunner(NiceRefreshProperty niceRefreshProperty,
                                                          RefreshScope refreshScope
    ) {
        this.niceRefreshProperty = niceRefreshProperty;
        this.refreshScope = refreshScope;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (niceRefreshProperty.getDebug()) {
                log.info("nicerefresh apollo config change application runner start");
            }

            Config config = ConfigService.getConfig(namespaces);
            config.addChangeListener(new NiceRefreshApolloConfigChangeListener(
                    niceRefreshProperty, refreshScope, applicationContext));

            if (niceRefreshProperty.getDebug()) {
                log.info("nicerefresh apollo config change application runner end");
            }
        } catch (Exception e) {
            log.error("nicerefresh apollo config change application runner process error", e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
