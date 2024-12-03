/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph;

import com.atlassian.seraph.config.SecurityConfig;
import java.util.Map;

public interface Initable {
    public void init(Map<String, String> var1, SecurityConfig var2);
}

