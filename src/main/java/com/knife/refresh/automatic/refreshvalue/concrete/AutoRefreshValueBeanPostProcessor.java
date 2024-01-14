package com.knife.refresh.automatic.refreshvalue.concrete;

import com.knife.refresh.automatic.refreshvalue.context.BeanFieldHolder;
import com.knife.refresh.automatic.refreshvalue.context.BeanField;
import com.knife.util.util.AopUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

@Slf4j
public class AutoRefreshValueBeanPostProcessor implements BeanPostProcessor {
    @Value("${knife.refresh.package}")
    private String businessPackage;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetCls = AopUtil.getTargetClass(bean);
        if (targetCls.getName().startsWith(businessPackage)) {
            Field[] declaredFields = targetCls.getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.isAnnotationPresent(Value.class)) {
                    Value annotation = field.getAnnotation(Value.class);
                    record(annotation, targetCls, field);
                }
            }
        }

        return bean;
    }

    private void record(Value value, Class<?> beanTargetClass, Field field) {
        String key = parseKey(value.value());
        if (!StringUtils.hasText(key)) {
            return;
        }

        BeanField beanField = new BeanField();
        beanField.setBeanTargetClass(beanTargetClass);
        beanField.setFieldName(field.getName());

        BeanFieldHolder.add(key, beanField);
    }

    /**
     * 解析出配置的key
     */
    private String parseKey(String valueString) {
        int configIndex = valueString.indexOf("${");
        // 如果不是${}这种配置，则不处理
        if (configIndex == -1) {
            return null;
        }
        // 取出${}之内的值
        String key = valueString.substring(configIndex + 2, valueString.length() - 1);
        // 只取第一个:之前的
        key = key.split(":")[0];

        return key;
    }
}