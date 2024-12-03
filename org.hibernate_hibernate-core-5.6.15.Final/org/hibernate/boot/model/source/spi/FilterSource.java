/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.Map;

public interface FilterSource {
    public String getName();

    public String getCondition();

    public boolean shouldAutoInjectAliases();

    public Map<String, String> getAliasToTableMap();

    public Map<String, String> getAliasToEntityMap();
}

