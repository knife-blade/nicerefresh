package com.suchtool.nicerefresh.runner;

import com.suchtool.nicerefresh.context.BeanField;
import com.suchtool.nicerefresh.context.BeanFieldHolder;
import com.suchtool.nicerefresh.property.NiceRefreshProperty;
import com.suchtool.niceutil.util.spring.AopUtil;
import com.suchtool.niceutil.util.spring.ApplicationContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NiceRefreshApplicationRunner implements ApplicationRunner {
    @Autowired(required = false)
    private NiceRefreshProperty niceRefreshProperty;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<String, Object> runClassBeanMap = ApplicationContextHolder.getContext()
                .getBeansWithAnnotation(SpringBootApplication.class);

        // 支持自动更新的包名。多个用,隔开，如果没指定，则取启动类所在的包
        String packageName = null;
        if (niceRefreshProperty != null
            && StringUtils.hasText(niceRefreshProperty.getPackageName())) {
            packageName = niceRefreshProperty.getPackageName();
        } else {
            // 获得启动类
            Object runObject = runClassBeanMap.entrySet().iterator().next().getValue();
            packageName = AopUtil.getTargetClass(runObject).getPackage().getName();
        }

        List<String> packageNameList = Arrays.asList(packageName.split(","));
        String[] beanDefinitionNames = ApplicationContextHolder.getContext().getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = ApplicationContextHolder.getContext().getBean(beanDefinitionName);
            checkAndRecord(bean, packageNameList);
        }
    }

    private void checkAndRecord(Object bean, List<String> packageNameList) {
        Class<?> targetCls = AopUtil.getTargetClass(bean);
        for (String packageName : packageNameList) {
            if (targetCls.getName().startsWith(packageName)) {
                Field[] declaredFields = targetCls.getDeclaredFields();
                for (Field field : declaredFields) {
                    if (field.isAnnotationPresent(Value.class)) {
                        Value annotation = field.getAnnotation(Value.class);
                        record(annotation, targetCls, field);
                    }
                }
            }
        }
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
