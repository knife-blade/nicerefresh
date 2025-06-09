package com.suchtool.nicerefresh.listener;

import com.suchtool.nicerefresh.context.NiceRefreshBeanField;
import com.suchtool.nicerefresh.context.NiceRefreshBeanFieldHolder;
import com.suchtool.nicerefresh.property.NiceRefreshProperty;
import com.suchtool.nicetool.util.spring.AopUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
public class NiceRefreshEnvironmentChangeListener implements
        ApplicationContextAware, ApplicationListener<EnvironmentChangeEvent> {
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private NiceRefreshProperty niceRefreshProperty;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(EnvironmentChangeEvent event) {
        if (this.applicationContext.equals(event.getSource())
                // Backwards compatible
                || event.getKeys().equals(event.getSource())) {
            proceed(event);
        }
    }

    private void proceed(EnvironmentChangeEvent event) {
        for (String key : event.getKeys()) {
            List<NiceRefreshBeanField> niceRefreshBeanFieldList = NiceRefreshBeanFieldHolder.read(key);
            if (CollectionUtils.isEmpty(niceRefreshBeanFieldList)) {
                continue;
            }
            for (NiceRefreshBeanField niceRefreshBeanField : niceRefreshBeanFieldList) {
                Object bean = applicationContext.getBean(niceRefreshBeanField.getBeanTargetClass());
                Object targetBean = AopUtil.getTargetBean(bean);
                try {
                    Field field = targetBean.getClass().getDeclaredField(niceRefreshBeanField.getFieldName());
                    field.setAccessible(true);
                    Object value = applicationContext.getEnvironment()
                            .getProperty(key, field.getType());
                    field.set(targetBean, value);
                    field.setAccessible(false);

                    if (niceRefreshProperty.getEnableDebug() != null
                        && niceRefreshProperty.getEnableDebug()) {
                        log.info("Nicerefresh update property successfully。new value: {}, key: {}, beanName：{}, field: {}",
                                value == null ? "null" : value.toString(),
                                key,
                                bean.getClass().getName(),
                                field.getName()
                        );
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
