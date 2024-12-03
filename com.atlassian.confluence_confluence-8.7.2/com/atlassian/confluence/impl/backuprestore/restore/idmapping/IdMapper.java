/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping;

import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import java.util.Collection;
import java.util.List;

public interface IdMapper {
    public Object getDatabaseId(Class<?> var1, Object var2);

    public Collection<ImportedObjectV2> prepareObjectsToBePersisted(ExportableEntityInfo var1, Collection<ImportedObjectV2> var2);

    public boolean isPersistedXmlId(Class<?> var1, Object var2);

    public void markObjectsAsPersisted(ExportableEntityInfo var1, List<Object> var2);

    public Collection<HibernateField> getAllNotSatisfiedDependencies(ImportedObjectV2 var1);
}

