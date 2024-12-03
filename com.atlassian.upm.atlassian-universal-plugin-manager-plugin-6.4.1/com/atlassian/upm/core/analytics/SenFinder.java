/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.analytics;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;

public interface SenFinder {
    public Option<String> findSen(Plugin var1);

    public Option<String> findSen(String var1);
}

