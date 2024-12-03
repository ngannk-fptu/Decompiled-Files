/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import java.util.Map;

public interface PaginatedMapping {
    public boolean next();

    public Map<String, String> getMapping();
}

