/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations;

import com.atlassian.confluence.impl.backuprestore.helpers.TableAndFieldNameValidator;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.EntityInfoSqlHelper;
import com.atlassian.confluence.impl.backuprestore.restore.dao.RestoreDao;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.IdMapper;
import com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations.DeferredAction;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeferredUpdateRecordAction
implements DeferredAction {
    private static final Logger log = LoggerFactory.getLogger(DeferredUpdateRecordAction.class);
    private final IdMapper idMapper;
    private final RestoreDao restoreDao;
    private final ExportableEntityInfo entityInfoToUpdate;
    private final HibernateField hibernateFieldToUpdate;
    private final ExportableEntityInfo referencedEntityInfo;
    private final Object originalRecordId;
    private final Object referencedObjectId;

    public DeferredUpdateRecordAction(IdMapper idMapper, RestoreDao restoreDao, ExportableEntityInfo entityInfoToUpdate, HibernateField hibernateFieldToUpdate, ExportableEntityInfo referencedEntityInfo, Object originalRecordId, Object referencedObjectId) {
        this.idMapper = idMapper;
        this.restoreDao = restoreDao;
        this.entityInfoToUpdate = entityInfoToUpdate;
        this.hibernateFieldToUpdate = hibernateFieldToUpdate;
        this.referencedEntityInfo = referencedEntityInfo;
        this.originalRecordId = originalRecordId;
        this.referencedObjectId = referencedObjectId;
    }

    public Object getOriginalRecordId() {
        return this.originalRecordId;
    }

    public HibernateField getHibernateFieldToUpdate() {
        return this.hibernateFieldToUpdate;
    }

    public Object getReferencedObjectId() {
        return this.referencedObjectId;
    }

    public ExportableEntityInfo getEntityInfoToUpdate() {
        return this.entityInfoToUpdate;
    }

    @Override
    public boolean perform() {
        Map<String, Object> params;
        Class<?> referencedClass = this.hibernateFieldToUpdate.getReferencedClass();
        Object databaseId = this.idMapper.getDatabaseId(this.entityInfoToUpdate.getEntityClass(), this.getOriginalRecordId());
        if (!this.isDatabaseIdExistAndPersisted(this.entityInfoToUpdate.getEntityClass(), this.getOriginalRecordId(), databaseId)) {
            return false;
        }
        Object newReferencedId = this.idMapper.getDatabaseId(referencedClass, this.referencedObjectId);
        if (!this.isDatabaseIdExistAndPersisted(this.referencedEntityInfo.getEntityClass(), this.referencedObjectId, newReferencedId)) {
            return false;
        }
        String updateQuery = "UPDATE " + TableAndFieldNameValidator.checkNameDoesNotHaveSqlInjections(this.entityInfoToUpdate.getTableName()) + " SET " + TableAndFieldNameValidator.checkNameDoesNotHaveSqlInjections(this.hibernateFieldToUpdate.getSingleColumnName()) + " = :value  WHERE " + TableAndFieldNameValidator.checkNameDoesNotHaveSqlInjections(this.entityInfoToUpdate.getId().getSingleColumnName()) + " = :id";
        return this.restoreDao.runNativeUpdateQuery(updateQuery, params = Map.of("value", EntityInfoSqlHelper.getDbReadyValueFromProperty(newReferencedId), "id", EntityInfoSqlHelper.getDbReadyValueFromProperty(databaseId))) > 0;
    }

    private boolean isDatabaseIdExistAndPersisted(Class<?> entityClass, Object xmlId, Object databaseId) {
        if (databaseId == null) {
            log.warn("Unable to find new id for entity class {} with original id {}. It was not found or persisted for some reasons?", entityClass, xmlId);
            return false;
        }
        if (!this.idMapper.isPersistedXmlId(entityClass, xmlId)) {
            log.warn("Unable to perform update operation for entity class {} with original id {}. The new id {} is found but the entity was not persisted by this moment.", new Object[]{entityClass, xmlId, databaseId});
            return false;
        }
        return true;
    }
}

