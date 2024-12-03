/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import net.java.ao.schema.UniqueNameConverter;

public final class DefaultUniqueNameConverter
implements UniqueNameConverter {
    @Override
    public String getName(String tableName, String fieldName) {
        return "U_" + tableName + '_' + fieldName;
    }
}

