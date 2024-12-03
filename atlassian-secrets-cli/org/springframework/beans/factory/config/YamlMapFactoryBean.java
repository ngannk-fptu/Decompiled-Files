/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.lang.Nullable;

public class YamlMapFactoryBean
extends YamlProcessor
implements FactoryBean<Map<String, Object>>,
InitializingBean {
    private boolean singleton = true;
    @Nullable
    private Map<String, Object> map;

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override
    public boolean isSingleton() {
        return this.singleton;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.isSingleton()) {
            this.map = this.createMap();
        }
    }

    @Override
    @Nullable
    public Map<String, Object> getObject() {
        return this.map != null ? this.map : this.createMap();
    }

    @Override
    public Class<?> getObjectType() {
        return Map.class;
    }

    protected Map<String, Object> createMap() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        this.process((properties, map) -> this.merge(result, map));
        return result;
    }

    private void merge(Map<String, Object> output, Map<String, Object> map) {
        map.forEach((key, value) -> {
            Object existing = output.get(key);
            if (value instanceof Map && existing instanceof Map) {
                LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>((Map)existing);
                this.merge(result, (Map)value);
                output.put((String)key, result);
            } else {
                output.put((String)key, value);
            }
        });
    }
}

