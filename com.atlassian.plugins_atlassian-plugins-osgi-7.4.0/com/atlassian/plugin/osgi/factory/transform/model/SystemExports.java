/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.atlassian.plugin.osgi.factory.transform.model;

import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SystemExports {
    private static final String VERSION = "version";
    private final Map<String, Map<String, String>> exports;
    public static final SystemExports NONE = new SystemExports("");

    public SystemExports(String exportsLine) {
        if (exportsLine == null) {
            exportsLine = "";
        }
        this.exports = SystemExports.internAttributeKeys(OsgiHeaderUtil.parseHeader(exportsLine));
    }

    private static Map<String, Map<String, String>> internAttributeKeys(Map<String, Map<String, String>> map) {
        return Maps.transformValues(map, SystemExports::internKeys);
    }

    private static Map<String, String> internKeys(Map<String, String> innerMap) {
        return Collections.unmodifiableMap(innerMap.entrySet().stream().collect(Collectors.toMap(keyMapper -> ((String)keyMapper.getKey()).intern(), Map.Entry::getValue)));
    }

    public String getFullExport(String pkg) {
        if (this.exports.containsKey(pkg)) {
            LinkedHashMap<String, String> attrs = new LinkedHashMap<String, String>(this.exports.get(pkg));
            if (attrs.containsKey(VERSION)) {
                String version = (String)attrs.get(VERSION);
                attrs.put(VERSION, "[" + version + "," + version + "]");
            }
            return OsgiHeaderUtil.buildHeader(pkg, attrs);
        }
        return pkg;
    }

    public boolean isExported(String pkg) {
        return this.exports.containsKey(pkg);
    }
}

