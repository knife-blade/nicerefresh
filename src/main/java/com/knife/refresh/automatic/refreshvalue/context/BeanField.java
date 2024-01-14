package com.knife.refresh.automatic.refreshvalue.context;

import lombok.Data;

@Data
public class BeanField {
    private Class<?> beanTargetClass;

    private String fieldName;
}
