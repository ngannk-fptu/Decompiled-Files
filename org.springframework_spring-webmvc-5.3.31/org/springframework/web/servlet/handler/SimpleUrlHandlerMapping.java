/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.web.servlet.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.BeansException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

public class SimpleUrlHandlerMapping
extends AbstractUrlHandlerMapping {
    private final Map<String, Object> urlMap = new LinkedHashMap<String, Object>();

    public SimpleUrlHandlerMapping() {
    }

    public SimpleUrlHandlerMapping(Map<String, ?> urlMap) {
        this.setUrlMap(urlMap);
    }

    public SimpleUrlHandlerMapping(Map<String, ?> urlMap, int order) {
        this.setUrlMap(urlMap);
        this.setOrder(order);
    }

    public void setMappings(Properties mappings) {
        CollectionUtils.mergePropertiesIntoMap((Properties)mappings, this.urlMap);
    }

    public void setUrlMap(Map<String, ?> urlMap) {
        this.urlMap.putAll(urlMap);
    }

    public Map<String, ?> getUrlMap() {
        return this.urlMap;
    }

    @Override
    public void initApplicationContext() throws BeansException {
        super.initApplicationContext();
        this.registerHandlers(this.urlMap);
    }

    protected void registerHandlers(Map<String, Object> urlMap) throws BeansException {
        if (urlMap.isEmpty()) {
            this.logger.trace((Object)("No patterns in " + this.formatMappingName()));
        } else {
            urlMap.forEach((url, handler) -> {
                if (!url.startsWith("/")) {
                    url = "/" + url;
                }
                if (handler instanceof String) {
                    handler = ((String)handler).trim();
                }
                this.registerHandler((String)url, handler);
            });
            this.logMappings();
        }
    }

    private void logMappings() {
        if (this.mappingsLogger.isDebugEnabled()) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>(this.getHandlerMap());
            if (this.getRootHandler() != null) {
                map.put("/", this.getRootHandler());
            }
            if (this.getDefaultHandler() != null) {
                map.put("/**", this.getDefaultHandler());
            }
            this.mappingsLogger.debug((Object)(this.formatMappingName() + " " + map));
        } else if (this.logger.isDebugEnabled()) {
            ArrayList<String> patterns = new ArrayList<String>();
            if (this.getRootHandler() != null) {
                patterns.add("/");
            }
            if (this.getDefaultHandler() != null) {
                patterns.add("/**");
            }
            patterns.addAll(this.getHandlerMap().keySet());
            this.logger.debug((Object)("Patterns " + patterns + " in " + this.formatMappingName()));
        }
    }
}

