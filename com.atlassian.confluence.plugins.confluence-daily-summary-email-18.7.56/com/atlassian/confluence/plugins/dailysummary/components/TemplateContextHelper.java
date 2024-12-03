/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.CaseFormat
 */
package com.atlassian.confluence.plugins.dailysummary.components;

import com.google.common.base.CaseFormat;
import java.util.HashMap;
import java.util.Map;

public enum TemplateContextHelper {
    VELOCITY2SOY;


    public Map<String, Object> convert(Map<String, Object> map) {
        HashMap<String, Object> aliasMap = new HashMap<String, Object>(map);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String alias;
            String key = entry.getKey();
            if (key.indexOf(45) == -1 || map.containsKey(alias = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, key))) continue;
            aliasMap.put(alias, entry.getValue());
        }
        return aliasMap;
    }
}

