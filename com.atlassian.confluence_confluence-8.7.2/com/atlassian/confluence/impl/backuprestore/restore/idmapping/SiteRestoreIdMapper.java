/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping;

import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.AbstractIdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.PersistedObjectsRegister;
import java.util.Collection;

public class SiteRestoreIdMapper
extends AbstractIdMapper {
    public SiteRestoreIdMapper(PersistedObjectsRegister persistedObjectsRegister) {
        super(persistedObjectsRegister);
    }

    @Override
    public Object getDatabaseId(Class<?> clazz, Object xmlId) {
        return xmlId;
    }

    @Override
    public Collection<ImportedObjectV2> prepareObjectsToBePersisted(ExportableEntityInfo entityInfo, Collection<ImportedObjectV2> importedObjects) {
        return importedObjects;
    }
}

