/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dbexporter.EntityNameProcessor
 */
package com.atlassian.confluence.upgrade.recovery;

import com.atlassian.dbexporter.EntityNameProcessor;

public class EchoEntityNameProcessor
implements EntityNameProcessor {
    public String tableName(String table) {
        return table;
    }

    public String columnName(String column) {
        return column;
    }
}

