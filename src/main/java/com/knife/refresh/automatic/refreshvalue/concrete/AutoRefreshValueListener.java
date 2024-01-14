package com.knife.refresh.automatic.refreshvalue.concrete;

import com.knife.refresh.automatic.refreshvalue.context.BeanField;
import com.knife.refresh.automatic.refreshvalue.context.BeanFieldHolder;
import com.knife.refresh.common.util.AopUtil;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.List;

public class AutoRefreshValueListener implements
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
            List<BeanField> beanFieldList = BeanFieldHolder.read(key);
            if (CollectionUtils.isEmpty(beanFieldList)) {
                continue;
            }
            for (BeanField beanField : beanFieldList) {
                Object bean = applicationContext.getBean(beanField.getBeanTargetClass());
                Object targetBean = AopUtil.getTargetBean(bean);
                try {
                    Field field = targetBean.getClass().getDeclaredField(beanField.getFieldName());
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
