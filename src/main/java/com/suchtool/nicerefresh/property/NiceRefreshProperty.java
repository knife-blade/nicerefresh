package com.suchtool.nicerefresh.property;

import lombok.Data;

@Data
public class NiceRefreshProperty {
    private Boolean enabled;

    /**
     * 要刷新的包名，多个用逗号隔开
     */
    private String packageName;
}
