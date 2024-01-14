package com.knife.refresh.automatic.refreshvalue.context;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class BeanFieldHolder {
    /**
     * key：配置的key；value：用到此配置的对象字段
     */
    private static final Map<String, List<BeanField>> keyObjectFieldMap = new HashMap<>();

    public static void add(String key, BeanField beanField) {
        List<BeanField> beanFieldList = keyObjectFieldMap.get(key);

        // 如果已经在map里了，不再添加
        if (!CollectionUtils.isEmpty(beanFieldList)
                && beanFieldList.contains(beanField)) {
            return;
        }

        if (CollectionUtils.isEmpty(beanFieldList)) {
            beanFieldList = new ArrayList<>();
            keyObjectFieldMap.put(key, beanFieldList);
        }

        beanFieldList.add(beanField);
    }

    public static List<BeanField> read(String key) {
        return keyObjectFieldMap.get(key);
    }
}
