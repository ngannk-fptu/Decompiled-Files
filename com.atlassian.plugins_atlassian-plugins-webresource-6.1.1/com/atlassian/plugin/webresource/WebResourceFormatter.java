/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.WebResourceFilter;
import java.util.Map;

public interface WebResourceFormatter
extends WebResourceFilter {
    public String formatResource(String var1, Map<String, String> var2);
}

