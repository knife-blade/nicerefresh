package com.suchtool.nicerefresh.context;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class NiceRefreshBeanFieldHolder {
    /**
     * key：配置的key；value：用到此配置的对象字段
     */
    private static final Map<String, List<NiceRefreshBeanField>> keyObjectFieldMap = new HashMap<>();

    public static void add(String key, NiceRefreshBeanField niceRefreshBeanField) {
        List<NiceRefreshBeanField> niceRefreshBeanFieldList = keyObjectFieldMap.get(key);

        // 如果已经在map里了，不再添加
        if (!CollectionUtils.isEmpty(niceRefreshBeanFieldList)
                && niceRefreshBeanFieldList.contains(niceRefreshBeanField)) {
            return;
        }

        if (CollectionUtils.isEmpty(niceRefreshBeanFieldList)) {
            niceRefreshBeanFieldList = new ArrayList<>();
            keyObjectFieldMap.put(key, niceRefreshBeanFieldList);
        }

        niceRefreshBeanFieldList.add(niceRefreshBeanField);
    }

    public static List<NiceRefreshBeanField> read(String key) {
        return keyObjectFieldMap.get(key);
    }
}
