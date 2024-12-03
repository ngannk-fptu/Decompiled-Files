/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.backup.exporters.converters;

import com.atlassian.confluence.impl.backuprestore.backup.models.DbRawObjectData;
import com.atlassian.confluence.impl.backuprestore.backup.models.EntityObjectReadyForExport;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import java.util.List;

public interface Converter {
    public List<EntityObjectReadyForExport> convertToObjectsReadyForSerialisation(List<DbRawObjectData> var1);

    public ExportableEntityInfo getEntityInfo();

    public ExportableEntityInfo getEntityInfo(Class<?> var1);
}

