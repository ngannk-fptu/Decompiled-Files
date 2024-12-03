/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import net.java.ao.SchemaConfiguration;

public class DefaultSchemaConfiguration
implements SchemaConfiguration {
    @Override
    public boolean shouldManageTable(String tableName, boolean caseSensitive) {
        return true;
    }
}

