/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.Session;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;

public interface CustomEntityDirtinessStrategy {
    public boolean canDirtyCheck(Object var1, EntityPersister var2, Session var3);

    public boolean isDirty(Object var1, EntityPersister var2, Session var3);

    public void resetDirty(Object var1, EntityPersister var2, Session var3);

    public void findDirty(Object var1, EntityPersister var2, Session var3, DirtyCheckContext var4);

    public static interface AttributeInformation {
        public EntityPersister getContainingPersister();

        public int getAttributeIndex();

        public String getName();

        public Type getType();

        public Object getCurrentValue();

        public Object getLoadedValue();
    }

    public static interface AttributeChecker {
        public boolean isDirty(AttributeInformation var1);
    }

    public static interface DirtyCheckContext {
        public void doDirtyChecking(AttributeChecker var1);
    }
}

