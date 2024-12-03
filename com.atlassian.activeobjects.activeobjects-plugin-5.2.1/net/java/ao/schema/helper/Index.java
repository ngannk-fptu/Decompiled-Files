/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.helper;

import java.util.Collection;

public interface Index {
    public String getTableName();

    public Collection<String> getFieldNames();

    public String getIndexName();
}

