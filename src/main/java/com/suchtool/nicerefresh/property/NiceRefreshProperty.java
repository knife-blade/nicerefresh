package com.suchtool.nicerefresh.property;

import lombok.Data;

import java.util.List;

@Data
public class NiceRefreshProperty {
    private Boolean enabled;

    /**
     * 要刷新的包名
     */
    private List<String> packageName;
}
