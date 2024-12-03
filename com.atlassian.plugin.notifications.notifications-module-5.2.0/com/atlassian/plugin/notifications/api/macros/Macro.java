/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.api.macros;

import java.util.Map;

public interface Macro {
    public String getName();

    public String resolve(Map<String, Object> var1);
}

