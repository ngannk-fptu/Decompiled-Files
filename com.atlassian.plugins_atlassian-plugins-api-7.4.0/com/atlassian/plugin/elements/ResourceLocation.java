/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.elements;

import java.util.Collections;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ResourceLocation {
    private final String location;
    private final String name;
    private final String type;
    private final String contentType;
    private final String content;
    private final Map<String, String> params;
    private static final Predicate<Map.Entry<?, ?>> KEY_AND_VALUE_NOT_NULL = e -> e.getKey() != null && e.getValue() != null;

    public ResourceLocation(String location, String name, String type, String contentType, String content, Map<String, String> params) {
        this.location = location;
        this.name = name;
        this.type = type;
        this.contentType = contentType;
        this.content = content;
        this.params = Collections.unmodifiableMap(params.entrySet().stream().filter(KEY_AND_VALUE_NOT_NULL).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public String getLocation() {
        return this.location;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public String getContentType() {
        return this.contentType;
    }

    public String getContent() {
        return this.content;
    }

    public String getParameter(String key) {
        return this.params.get(key);
    }

    public Map<String, String> getParams() {
        return this.params;
    }
}

