/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.controller;

import java.lang.reflect.Field;
import org.eclipse.gemini.blueprint.service.importer.support.internal.controller.ImporterInternalActions;

public abstract class ImporterControllerUtils {
    private static final String FIELD_NAME = "controller";
    private static final Field singleProxyField;
    private static final Field collectionProxyField;
    private static final Class<?> singleImporter;

    public static ImporterInternalActions getControllerFor(Object importer) {
        Field field = singleImporter == importer.getClass() ? singleProxyField : collectionProxyField;
        try {
            return (ImporterInternalActions)field.get(importer);
        }
        catch (IllegalAccessException iae) {
            throw (RuntimeException)new IllegalArgumentException("Cannot access field [controller] on object [" + importer + "]").initCause(iae);
        }
    }

    static {
        Class<?> clazz = null;
        String singleImporterName = "org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceProxyFactoryBean";
        String multiImporterName = "org.eclipse.gemini.blueprint.service.importer.support.OsgiServiceCollectionProxyFactoryBean";
        try {
            ClassLoader cl = ImporterControllerUtils.class.getClassLoader();
            clazz = cl.loadClass(singleImporterName);
            singleImporter = clazz;
            singleProxyField = clazz.getDeclaredField(FIELD_NAME);
            singleProxyField.setAccessible(true);
            clazz = cl.loadClass(multiImporterName);
            collectionProxyField = clazz.getDeclaredField(FIELD_NAME);
            collectionProxyField.setAccessible(true);
        }
        catch (Exception ex) {
            throw (RuntimeException)new IllegalStateException("Cannot read field [controller] on class [" + clazz + "]").initCause(ex);
        }
    }
}

