/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.preprocessing;

import com.atlassian.confluence.core.NotExportable;
import com.atlassian.confluence.impl.backuprestore.restore.preprocessing.ImportedObjectFilter;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassShouldBeExportableFilter
implements ImportedObjectFilter {
    private static final Logger log = LoggerFactory.getLogger(ClassShouldBeExportableFilter.class);
    private static final Object DUMMY = new Object();
    private final Map<String, Object> skippedClasses = new ConcurrentHashMap<String, Object>();

    @Override
    public boolean test(ImportedObject importedObject, Class<?> entityClass) {
        if (!NotExportable.class.isAssignableFrom(entityClass)) {
            return true;
        }
        if (this.skippedClasses.putIfAbsent(entityClass.getName(), DUMMY) == null) {
            log.info("An object with class {} found in the input XML backup file marked as NotExportable and will be skipped. It usually happens when a class was NOT marked as NotExportable in previous Confluence versions. All the following objects of the same class will be skipped silently.", (Object)entityClass.getName());
        }
        return false;
    }
}

