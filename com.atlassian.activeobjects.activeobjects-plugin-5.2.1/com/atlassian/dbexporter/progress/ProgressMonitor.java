/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.progress;

public interface ProgressMonitor {
    public void begin(Object ... var1);

    public void end(Object ... var1);

    public void begin(Task var1, Object ... var2);

    public void end(Task var1, Object ... var2);

    public void totalNumberOfTables(int var1);

    public static enum Task {
        DATABASE_INFORMATION,
        TABLE_DEFINITION,
        TABLE_CREATION,
        TABLES_DATA,
        TABLE_DATA,
        TABLE_ROW;

    }
}

