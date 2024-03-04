package com.suchtool.nicerefresh.listener;

import com.suchtool.nicerefresh.context.NiceRefreshBeanField;
import com.suchtool.nicerefresh.context.NiceRefreshBeanFieldHolder;
import com.suchtool.nicetool.util.spring.AopUtil;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;

public class NiceRefreshEnvironmentChangeListener implements
        ApplicationContextAware, ApplicationListener<EnvironmentChangeEvent> {
    private ApplicationContext applicationContext;

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
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
