/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations;

import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.restore.dao.RestoreDao;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations.DeferredAction;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations.DeferredUpdateRecordAction;

public class DeferredActionsFactory {
    private final HibernateMetadataHelper hibernateMetadataHelper;
    private final RestoreDao restoreDao;
    private final IdMapper idMapper;

    public DeferredActionsFactory(HibernateMetadataHelper hibernateMetadataHelper, RestoreDao restoreDao, IdMapper idMapper) {
        this.hibernateMetadataHelper = hibernateMetadataHelper;
        this.restoreDao = restoreDao;
        this.idMapper = idMapper;
    }

    public DeferredAction createUpdateOperation(ExportableEntityInfo entityInfoToUpdate, HibernateField hibernateFieldToUpdate, Object originalRecordId, Object referencedObjectId) {
        ExportableEntityInfo referencedEntityInfo = this.hibernateMetadataHelper.getEntityInfoByClass(hibernateFieldToUpdate.getReferencedClass());
        return new DeferredUpdateRecordAction(this.idMapper, this.restoreDao, entityInfoToUpdate, hibernateFieldToUpdate, referencedEntityInfo, originalRecordId, referencedObjectId);
    }
}

