/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.impl.backuprestore.hibernate.ExportableEntityInfo;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FakeObjectProvider {
    private final Map<Class<?>, Object> fakeObjectsMap = new HashMap();

    public FakeObjectProvider(Collection<ExportableEntityInfo> importableEntitiesInfo) throws BackupRestoreException {
        for (ExportableEntityInfo importableEntity : importableEntitiesInfo) {
            this.registerFakeObjectForIdGeneration(importableEntity.getEntityClass());
        }
    }

    public Object getFakeObjectForIdGeneration(Class<?> entityClass) {
        Class<?> clazz = this.fixContentEntityObjectClass(entityClass);
        Object fakeObjectForIdGeneration = this.fakeObjectsMap.get(clazz);
        if (fakeObjectForIdGeneration == null) {
            throw new IllegalStateException(String.format("Class %s is not registered for id generation", clazz));
        }
        return fakeObjectForIdGeneration;
    }

    private void registerFakeObjectForIdGeneration(Class<?> entityClass) throws BackupRestoreException {
        Class<?> clazz = this.fixContentEntityObjectClass(entityClass);
        if (ContentEntityObject.class.equals(clazz)) {
            if (this.fakeObjectsMap.containsKey(clazz)) {
                return;
            }
            this.fakeObjectsMap.put(clazz, this.createFakeContentEntityObject());
        } else {
            this.fakeObjectsMap.put(clazz, this.createFakeGenericObject(clazz));
        }
    }

    private Object createFakeGenericObject(Class<?> supportedClass) throws BackupRestoreException {
        try {
            return supportedClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new BackupRestoreException("No args public constructor for class " + supportedClass + " found");
        }
    }

    private ContentEntityObject createFakeContentEntityObject() {
        return new ContentEntityObject(){

            @Override
            public String getType() {
                return null;
            }

            @Override
            public String getUrlPath() {
                return null;
            }

            @Override
            public String getNameForComparison() {
                return null;
            }
        };
    }

    private Class<?> fixContentEntityObjectClass(Class<?> clazz) {
        return ContentEntityObject.class.isAssignableFrom(clazz) ? ContentEntityObject.class : clazz;
    }
}

