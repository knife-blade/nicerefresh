package com.suchtool.nicerefresh.context;

import lombok.Data;

@Data
public class BeanField {
    private Class<?> beanTargetClass;

    private String fieldName;
}
