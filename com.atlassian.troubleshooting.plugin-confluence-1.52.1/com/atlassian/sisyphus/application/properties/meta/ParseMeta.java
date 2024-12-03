/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.sisyphus.application.properties.meta;

import java.util.List;
import java.util.Map;

public interface ParseMeta {
    public Map<String, List<String>> getPathMap();

    public String getTitle();

    public List<String> getGroupNode();
}

