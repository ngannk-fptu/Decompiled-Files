/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.preprocessing;

import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.restore.EntityInfoSqlHelper;
import com.atlassian.confluence.impl.backuprestore.restore.container.XMLBackupContainerReader;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.preprocessing.ClassExistenceValidator;
import com.atlassian.confluence.impl.backuprestore.restore.preprocessing.ImportedObjectFilter;
import com.atlassian.confluence.impl.backuprestore.restore.preprocessing.ImportedObjectPreprocessor;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportedObjectV1ToV2Converter
implements Function<ImportedObject, Optional<ImportedObjectV2>> {
    private static final Logger log = LoggerFactory.getLogger(XMLBackupContainerReader.class);
    private final HibernateMetadataHelper hibernateMetadataHelper;
    private final EntityInfoSqlHelper entityInfoSqlHelper;
    private final Collection<ImportedObjectFilter> filters;
    private final Collection<ImportedObjectPreprocessor> preprocessors;
    private final ClassExistenceValidator classExistenceValidator = new ClassExistenceValidator();

    public ImportedObjectV1ToV2Converter(Collection<ImportedObjectFilter> filters, Collection<ImportedObjectPreprocessor> preprocessors, HibernateMetadataHelper hibernateMetadataHelper, EntityInfoSqlHelper entityInfoSqlHelper) {
        this.filters = filters;
        this.preprocessors = preprocessors;
        this.entityInfoSqlHelper = entityInfoSqlHelper;
        this.hibernateMetadataHelper = hibernateMetadataHelper;
    }

    @Override
    public Optional<ImportedObjectV2> apply(ImportedObject importedObject) {
        Optional<Class<?>> optionalEntityClass = this.classExistenceValidator.getEntityClass(importedObject);
        if (optionalEntityClass.isEmpty()) {
            return Optional.empty();
        }
        Class<?> entityClass = optionalEntityClass.get();
        if (!this.filters.stream().allMatch(filter -> filter.test(importedObject, entityClass))) {
            return Optional.empty();
        }
        Optional<ImportedObjectV2> importedObjectV2 = this.convert(importedObject, entityClass);
        if (importedObjectV2.isEmpty()) {
            return Optional.empty();
        }
        return this.applyAllPreprocessors(importedObjectV2.get());
    }

    private Optional<ImportedObjectV2> applyAllPreprocessors(ImportedObjectV2 importedObjectV2) {
        for (ImportedObjectPreprocessor preprocessor : this.preprocessors) {
            Optional<ImportedObjectV2> processedObject = preprocessor.apply(importedObjectV2);
            if (processedObject.isEmpty()) {
                return Optional.empty();
            }
            importedObjectV2 = processedObject.get();
        }
        return Optional.of(importedObjectV2);
    }

    private Optional<ImportedObjectV2> convert(ImportedObject importedObject, Class<?> entityClass) {
        ExportableEntityInfo exportableEntityInfo = this.hibernateMetadataHelper.getEntityInfoByClass(entityClass);
        try {
            return Optional.of(ImportedObjectV2.fromLegacyImportedObject(importedObject, exportableEntityInfo, this.entityInfoSqlHelper, this.hibernateMetadataHelper));
        }
        catch (ClassNotFoundException e) {
            log.error("fromLegacyImportedObject failed, the object will be skipped: " + importedObject, (Throwable)e);
            return Optional.empty();
        }
    }
}

