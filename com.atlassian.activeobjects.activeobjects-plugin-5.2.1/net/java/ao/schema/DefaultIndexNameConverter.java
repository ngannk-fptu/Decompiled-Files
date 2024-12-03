/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import net.java.ao.schema.Case;
import net.java.ao.schema.IndexNameConverter;

public final class DefaultIndexNameConverter
implements IndexNameConverter {
    @Override
    public String getName(String tableName, String fieldName) {
        return this.getPrefix(tableName) + "_" + Case.LOWER.apply(fieldName);
    }

    @Override
    public String getPrefix(String tableName) {
        return "index_" + Case.LOWER.apply(tableName);
    }
}

