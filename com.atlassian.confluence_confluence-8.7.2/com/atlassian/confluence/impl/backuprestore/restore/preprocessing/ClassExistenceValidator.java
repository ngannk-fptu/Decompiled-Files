/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.preprocessing;

import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassExistenceValidator {
    private static final Logger log = LoggerFactory.getLogger(ClassExistenceValidator.class);
    private static final Object DUMMY = new Object();
    private final Map<String, Object> skippedClasses = new ConcurrentHashMap<String, Object>();

    public Optional<Class<?>> getEntityClass(ImportedObject importedObject) {
        String fullClassName = importedObject.getPackageName() + "." + importedObject.getClassName();
        try {
            return Optional.of(Class.forName(fullClassName));
        }
        catch (ClassNotFoundException e) {
            if (this.skippedClasses.putIfAbsent(fullClassName, DUMMY) == null) {
                log.info("Unable to instantiate '{}' class found in the input XML backup file. It usually happens when a class was deprecated and then removed from Confluence. All following objects of the same class will be skipped silently.", (Object)fullClassName);
            }
            return Optional.empty();
        }
    }
}

