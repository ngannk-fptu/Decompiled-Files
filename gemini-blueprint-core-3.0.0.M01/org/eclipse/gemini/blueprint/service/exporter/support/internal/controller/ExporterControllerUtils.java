/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.exporter.support.internal.controller;

import java.lang.reflect.Field;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.controller.ExporterInternalActions;

public abstract class ExporterControllerUtils {
    private static final String FIELD_NAME = "controller";
    private static final Field field;

    public static ExporterInternalActions getControllerFor(Object exporter) {
        try {
            return (ExporterInternalActions)field.get(exporter);
        }
        catch (IllegalAccessException iae) {
            throw (RuntimeException)new IllegalArgumentException("Cannot access field [controller] on object [" + exporter + "]").initCause(iae);
        }
    }

    static {
        String className = "org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean";
        try {
            Class<?> cls = ExporterControllerUtils.class.getClassLoader().loadClass(className);
            field = cls.getDeclaredField(FIELD_NAME);
            field.setAccessible(true);
        }
        catch (Exception ex) {
            throw (RuntimeException)new IllegalStateException("Cannot read field [controller] on class [" + className + "]").initCause(ex);
        }
    }
}

