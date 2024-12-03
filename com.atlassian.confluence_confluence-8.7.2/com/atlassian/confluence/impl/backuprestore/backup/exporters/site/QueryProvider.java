/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.site;

import java.util.List;

public interface QueryProvider {
    public String getInitialQuery();

    public String getRepetitiveQuery();

    public List<String> getIdColumnNames();

    public String getLatestIdParamName(int var1);

    public String getTableName();
}

