/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.velocity.tools.ToolInfo;

public class Toolbox
implements Serializable {
    public static final String KEY = Toolbox.class.getName();
    private static final long serialVersionUID = 888081253188664649L;
    private Map<String, ToolInfo> infoMap;
    private Map<String, Object> properties;
    private Map<String, Object> cache;

    public Toolbox(Map<String, ToolInfo> toolInfo) {
        this(toolInfo, null);
    }

    public Toolbox(Map<String, ToolInfo> toolInfo, Map<String, Object> properties) {
        this.infoMap = toolInfo == null ? Collections.emptyMap() : toolInfo;
        this.properties = properties;
    }

    protected void cacheData(Map<String, Object> data) {
        if (data != null && !data.isEmpty()) {
            this.cache = new HashMap<String, Object>(data);
        }
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public Object get(String key) {
        return this.get(key, null, null);
    }

    public Object get(String key, String path) {
        return this.get(key, path, null);
    }

    public Object get(String key, Map<String, Object> context) {
        return this.get(key, null, context);
    }

    public Object get(String key, String path, Map<String, Object> context) {
        Object tool = null;
        if (this.cache != null) {
            tool = this.getFromCache(key, path);
        }
        if (tool == null) {
            tool = this.getFromInfo(key, path, context);
        }
        return tool;
    }

    protected Object getFromCache(String key, String path) {
        if (this.cache == null) {
            return null;
        }
        Object tool = this.cache.get(key);
        if (tool == null) {
            return null;
        }
        if (path == null) {
            return tool;
        }
        if (this.hasPermission(this.infoMap.get(key), path)) {
            return tool;
        }
        return null;
    }

    protected Object getFromInfo(String key, String path, Map<String, Object> context) {
        ToolInfo info = this.infoMap.get(key);
        if (info != null && (path == null || this.hasPermission(info, path))) {
            Object tool = info.create(context);
            if (this.cache == null) {
                this.cache = new HashMap<String, Object>();
            }
            this.cache.put(key, tool);
            return tool;
        }
        return null;
    }

    protected boolean hasPermission(ToolInfo info, String path) {
        if (info == null || path == null) {
            return true;
        }
        return info.hasPermission(path);
    }

    public Set<String> getKeys() {
        HashSet<String> keys = new HashSet<String>(this.infoMap.keySet());
        if (this.cache != null) {
            keys.addAll(this.cache.keySet());
        }
        return keys;
    }

    public Map<String, Class> getToolClassMap() {
        HashMap<String, Class> classMap = new HashMap<String, Class>(this.infoMap.size());
        for (Map.Entry<String, ToolInfo> entry : this.infoMap.entrySet()) {
            classMap.put(entry.getKey(), entry.getValue().getToolClass());
        }
        return classMap;
    }

    public Map<String, Object> getAll(Map<String, Object> context) {
        for (ToolInfo info : this.infoMap.values()) {
            this.get(info.getKey(), context);
        }
        return new HashMap<String, Object>(this.cache);
    }

    public Toolbox combine(Toolbox ... toolboxes) {
        HashMap<String, ToolInfo> info = new HashMap<String, ToolInfo>(this.infoMap);
        HashMap<String, Object> props = new HashMap<String, Object>(this.properties);
        HashMap<String, Object> data = new HashMap<String, Object>(this.cache);
        for (Toolbox toolbox : toolboxes) {
            info.putAll(toolbox.infoMap);
            props.putAll(toolbox.properties);
            data.putAll(toolbox.cache);
        }
        Toolbox combination = new Toolbox(info, props);
        combination.cacheData(data);
        return combination;
    }
}

