/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import net.java.ao.schema.TriggerNameConverter;

public final class DefaultTriggerNameConverter
implements TriggerNameConverter {
    @Override
    public String autoIncrementName(String tableName, String fieldName) {
        return tableName + '_' + fieldName + "_autoinc";
    }
}

