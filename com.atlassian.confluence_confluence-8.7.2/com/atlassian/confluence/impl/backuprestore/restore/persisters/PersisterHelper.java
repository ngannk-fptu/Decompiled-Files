/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters;

import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateField;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.statistics.OnObjectsProcessingHandler;
import com.atlassian.confluence.impl.backuprestore.statistics.SkippedObjectsReason;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersisterHelper {
    private final int BATCH_SIZE = Integer.getInteger("confluence.restore.persisting-batch-size", 1000);
    private static final Logger log = LoggerFactory.getLogger(PersisterHelper.class);
    private final OnObjectsProcessingHandler onObjectsProcessingHandler;

    public PersisterHelper(OnObjectsProcessingHandler onObjectsProcessingHandler) {
        this.onObjectsProcessingHandler = onObjectsProcessingHandler;
    }

    public int getBatchSize() {
        return this.BATCH_SIZE;
    }

    protected void logInformationAboutNotPersistedObject(ImportedObjectV2 importedObject, SkippedObjectsReason skippedObjectsReason, Collection<HibernateField> notSatisfiedDependencies) {
        this.onObjectsProcessingHandler.onObjectsSkipping(Collections.singleton(importedObject), skippedObjectsReason);
    }

    ImportedObjectV2 clearNotSatisfiedUserDependencies(ImportedObjectV2 importedObject, Collection<HibernateField> notSatisfiedDependencies) {
        List<String> notSatisfiedUserFields = notSatisfiedDependencies.stream().filter(hibernateField -> hibernateField.getReferencedClass().equals(ConfluenceUserImpl.class)).map(HibernateField::getPropertyName).collect(Collectors.toList());
        return importedObject.clearValues(notSatisfiedUserFields);
    }

    public Collection<HibernateField> getNotEmptyDependencies(ImportedObjectV2 importedObject, Class<?> className) {
        Collection<HibernateField> allReferences = importedObject.getEntityInfo().getAllExternalReferences();
        return allReferences.stream().filter(reference -> className.isAssignableFrom(reference.getReferencedClass())).filter(reference -> !this.isReferenceEmpty((HibernateField)reference, importedObject)).collect(Collectors.toList());
    }

    private boolean isReferenceEmpty(HibernateField reference, ImportedObjectV2 importedObject) {
        String propertyName = reference.getPropertyName();
        Object value = importedObject.getFieldValue(propertyName);
        return value == null;
    }
}

