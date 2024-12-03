/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.preprocessing;

import com.atlassian.confluence.impl.backuprestore.hibernate.HibernateMetadataHelper;
import com.atlassian.confluence.impl.backuprestore.restore.preprocessing.ImportedObjectFilter;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateEntityExistenceFilter
implements ImportedObjectFilter {
    private static final Logger log = LoggerFactory.getLogger(HibernateEntityExistenceFilter.class);
    private final HibernateMetadataHelper hibernateMetadataHelper;
    private static final Object DUMMY = new Object();
    private final Map<String, Object> skippedClasses = new ConcurrentHashMap<String, Object>();

    public HibernateEntityExistenceFilter(HibernateMetadataHelper hibernateMetadataHelper) {
        this.hibernateMetadataHelper = hibernateMetadataHelper;
    }

    @Override
    public boolean test(ImportedObject importedObject, Class<?> entityClass) {
        if (this.hibernateMetadataHelper.getEntityInfoByClass(entityClass) != null) {
            return true;
        }
        if (this.skippedClasses.putIfAbsent(entityClass.getName(), DUMMY) == null) {
            log.info("An object with class {} found in the input XML backup file is not known by Hibernate and will be skipped. It usually happens when the class was deprecated in previous Confluence versions. All the following objects of the same class will be skipped silently. ", (Object)entityClass.getName());
        }
        return false;
    }
}

