/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema;

import net.java.ao.schema.SequenceNameConverter;

public final class DefaultSequenceNameConverter
implements SequenceNameConverter {
    @Override
    public String getName(String tableName, String fieldName) {
        return tableName + '_' + fieldName + "_SEQ";
    }
}

