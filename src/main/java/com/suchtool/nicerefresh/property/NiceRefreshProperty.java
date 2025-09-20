package com.suchtool.nicerefresh.property;

import lombok.Data;

import java.util.List;

@Data
public class NiceRefreshProperty {
    private Boolean enabled;

    /**
     * 发布事件
     */
    private Boolean publishEvent = false;

    /**
     * 更新@Value的值
     */
    private Boolean updateAnnotation = true;

    /**
     * 要刷新的包名（updateValue为true时生效）
     */
    private List<String> packageName;

    /**
     * 是否开启调试
     */
    private Boolean debug = false;
}
