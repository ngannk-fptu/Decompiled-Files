/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 */
package org.hibernate.cfg;

import java.util.Map;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.ClassPropertyHolder;
import org.hibernate.cfg.CollectionPropertyHolder;
import org.hibernate.cfg.ComponentPropertyHolder;
import org.hibernate.cfg.InheritanceState;
import org.hibernate.cfg.PropertyData;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.cfg.annotations.EntityBinder;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;

public final class PropertyHolderBuilder {
    private PropertyHolderBuilder() {
    }

    public static PropertyHolder buildPropertyHolder(XClass clazzToProcess, PersistentClass persistentClass, EntityBinder entityBinder, MetadataBuildingContext context, Map<XClass, InheritanceState> inheritanceStatePerClass) {
        return new ClassPropertyHolder(persistentClass, clazzToProcess, entityBinder, context, inheritanceStatePerClass);
    }

    public static PropertyHolder buildPropertyHolder(Component component, String path, PropertyData inferredData, PropertyHolder parent, MetadataBuildingContext context) {
        return new ComponentPropertyHolder(component, path, inferredData, parent, context);
    }

    public static CollectionPropertyHolder buildPropertyHolder(Collection collection, String path, XClass clazzToProcess, XProperty property, PropertyHolder parentPropertyHolder, MetadataBuildingContext context) {
        return new CollectionPropertyHolder(collection, path, clazzToProcess, property, parentPropertyHolder, context);
    }

    public static PropertyHolder buildPropertyHolder(PersistentClass persistentClass, Map<String, Join> joins, MetadataBuildingContext context, Map<XClass, InheritanceState> inheritanceStatePerClass) {
        return new ClassPropertyHolder(persistentClass, null, joins, context, inheritanceStatePerClass);
    }
}

