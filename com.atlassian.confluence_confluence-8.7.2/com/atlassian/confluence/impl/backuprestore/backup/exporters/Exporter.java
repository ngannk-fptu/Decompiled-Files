/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters;

import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;

public interface Exporter {
    public ExportableEntityInfo getEntityInfo();

    public ExportableEntityInfo getEntityInfo(Class<?> var1);
}

