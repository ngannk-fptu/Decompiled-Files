/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.hibernate.engine.spi.EnhancedEntity;
import org.hibernate.engine.spi.Managed;
import org.hibernate.engine.spi.ManagedEntity;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.SelfDirtinessTracker;

public final class ManagedTypeHelper {
    public static boolean isManagedType(Class type) {
        return EnhancedEntity.class.isAssignableFrom(type) || Managed.class.isAssignableFrom(type);
    }

    public static boolean isManaged(Object entity) {
        return entity instanceof EnhancedEntity || entity instanceof Managed;
    }

    public static boolean isManagedEntity(Object entity) {
        return entity instanceof EnhancedEntity || entity instanceof ManagedEntity;
    }

    public static boolean isPersistentAttributeInterceptableType(Class type) {
        return EnhancedEntity.class.isAssignableFrom(type) || PersistentAttributeInterceptable.class.isAssignableFrom(type);
    }

    public static boolean isPersistentAttributeInterceptable(Object entity) {
        return entity instanceof EnhancedEntity || entity instanceof PersistentAttributeInterceptable;
    }

    public static boolean isSelfDirtinessTracker(Object entity) {
        return entity instanceof EnhancedEntity || entity instanceof SelfDirtinessTracker;
    }

    public static <T> void processIfPersistentAttributeInterceptable(Object entity, BiConsumer<PersistentAttributeInterceptable, T> action, T optionalParam) {
        if (entity instanceof EnhancedEntity) {
            EnhancedEntity e = (EnhancedEntity)entity;
            action.accept(e, (PersistentAttributeInterceptable)optionalParam);
        } else if (entity instanceof PersistentAttributeInterceptable) {
            PersistentAttributeInterceptable e = (PersistentAttributeInterceptable)entity;
            action.accept(e, (PersistentAttributeInterceptable)optionalParam);
        }
    }

    public static void processIfSelfDirtinessTracker(Object entity, Consumer<SelfDirtinessTracker> action) {
        if (entity instanceof EnhancedEntity) {
            EnhancedEntity e = (EnhancedEntity)entity;
            action.accept(e);
        } else if (entity instanceof SelfDirtinessTracker) {
            SelfDirtinessTracker e = (SelfDirtinessTracker)entity;
            action.accept(e);
        }
    }

    public static PersistentAttributeInterceptable asPersistentAttributeInterceptable(Object entity) {
        if (entity instanceof EnhancedEntity) {
            return (EnhancedEntity)entity;
        }
        return (PersistentAttributeInterceptable)entity;
    }

    public static ManagedEntity asManagedEntity(Object entity) {
        if (entity instanceof EnhancedEntity) {
            return (EnhancedEntity)entity;
        }
        return (ManagedEntity)entity;
    }

    public static SelfDirtinessTracker asSelfDirtinessTracker(Object entity) {
        if (entity instanceof EnhancedEntity) {
            return (EnhancedEntity)entity;
        }
        return (SelfDirtinessTracker)entity;
    }
}

