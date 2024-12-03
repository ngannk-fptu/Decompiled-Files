/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Path
 *  javax.validation.Path$Node
 *  javax.validation.TraversableResolver
 */
package org.hibernate.cfg.beanvalidation;

import java.lang.annotation.ElementType;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.Path;
import javax.validation.TraversableResolver;
import org.hibernate.AssertionFailure;
import org.hibernate.Hibernate;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

public class HibernateTraversableResolver
implements TraversableResolver {
    private Set<String> associations;

    public HibernateTraversableResolver(EntityPersister persister, ConcurrentHashMap<EntityPersister, Set<String>> associationsPerEntityPersister, SessionFactoryImplementor factory) {
        this.associations = associationsPerEntityPersister.get(persister);
        if (this.associations == null) {
            this.associations = new HashSet<String>();
            this.addAssociationsToTheSetForAllProperties(persister.getPropertyNames(), persister.getPropertyTypes(), "", factory);
            associationsPerEntityPersister.put(persister, this.associations);
        }
    }

    private void addAssociationsToTheSetForAllProperties(String[] names, Type[] types, String prefix, SessionFactoryImplementor factory) {
        int length = names.length;
        for (int index = 0; index < length; ++index) {
            this.addAssociationsToTheSetForOneProperty(names[index], types[index], prefix, factory);
        }
    }

    private void addAssociationsToTheSetForOneProperty(String name, Type type, String prefix, SessionFactoryImplementor factory) {
        if (type.isCollectionType()) {
            CollectionType collType = (CollectionType)type;
            Type assocType = collType.getElementType(factory);
            this.addAssociationsToTheSetForOneProperty(name, assocType, prefix, factory);
        } else if (type.isEntityType() || type.isAnyType()) {
            this.associations.add(prefix + name);
        } else if (type.isComponentType()) {
            CompositeType componentType = (CompositeType)type;
            this.addAssociationsToTheSetForAllProperties(componentType.getPropertyNames(), componentType.getSubtypes(), (prefix.isEmpty() ? name : prefix + name) + '.', factory);
        }
    }

    private String getStringBasedPath(Path.Node traversableProperty, Path pathToTraversableObject) {
        StringBuilder path = new StringBuilder();
        for (Path.Node node : pathToTraversableObject) {
            if (node.getName() == null) continue;
            path.append(node.getName()).append('.');
        }
        if (traversableProperty.getName() == null) {
            throw new AssertionFailure("TraversableResolver being passed a traversableProperty with null name. pathToTraversableObject: " + path.toString());
        }
        path.append(traversableProperty.getName());
        return path.toString();
    }

    public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        return Hibernate.isInitialized(traversableObject) && Hibernate.isPropertyInitialized(traversableObject, traversableProperty.getName());
    }

    public boolean isCascadable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
        String path = this.getStringBasedPath(traversableProperty, pathToTraversableObject);
        return !this.associations.contains(path);
    }
}

