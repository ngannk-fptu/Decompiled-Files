/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.web.api.provider;

import com.atlassian.plugin.web.api.WebSection;
import java.util.Map;

public interface WebSectionProvider {
    public Iterable<WebSection> getSections(Map<String, Object> var1);
}

