/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.spi;

public interface RestoreProgressMonitor {
    public void beginRestore();

    public void endRestore();

    public void beginDatabaseInformationRestore();

    public void beginTableDefinitionsRestore();

    public void beginTablesRestore();

    public void beginTableDataRestore(String var1);

    public void beginTableCreationRestore(String var1);

    public void beginTableRowRestore();

    public void endDatabaseInformationRestore();

    public void endTableDefinitionsRestore();

    public void endTablesRestore();

    public void endTableDataRestore(String var1);

    public void endTableCreationRestore(String var1);

    public void endTableRowRestore();

    public void updateTotalNumberOfTablesToRestore(int var1);
}

