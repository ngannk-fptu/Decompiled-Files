/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.vcache.metrics;

import java.util.List;

public interface Statistics {
    public String getName();

    public String getLoggingKey();

    public List<String> getTags();

    public Object getStats();
}

