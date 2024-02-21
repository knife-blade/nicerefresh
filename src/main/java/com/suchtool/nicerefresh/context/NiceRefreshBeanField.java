package com.suchtool.nicerefresh.context;

import lombok.Data;

@Data
public class NiceRefreshBeanField {
    private Class<?> beanTargetClass;

    private String fieldName;
}
