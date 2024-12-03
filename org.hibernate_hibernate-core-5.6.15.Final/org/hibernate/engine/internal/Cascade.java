/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.CascadePoint;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EntityType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;

public final class Cascade {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(Cascade.class);

    private Cascade() {
    }

    public static void cascade(CascadingAction action, CascadePoint cascadePoint, EventSource eventSource, EntityPersister persister, Object parent) throws HibernateException {
        Cascade.cascade(action, cascadePoint, eventSource, persister, parent, null);
    }

    public static void cascade(CascadingAction action, CascadePoint cascadePoint, EventSource eventSource, EntityPersister persister, Object parent, Object anything) throws HibernateException {
        if (persister.hasCascades() || action.requiresNoCascadeChecking()) {
            boolean traceEnabled = LOG.isTraceEnabled();
            if (traceEnabled) {
                LOG.tracev("Processing cascade {0} for: {1}", action, persister.getEntityName());
            }
            PersistenceContext persistenceContext = eventSource.getPersistenceContextInternal();
            Type[] types = persister.getPropertyTypes();
            String[] propertyNames = persister.getPropertyNames();
            CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();
            boolean hasUninitializedLazyProperties = persister.hasUninitializedLazyProperties(parent);
            for (int i = 0; i < types.length; ++i) {
                boolean isUninitializedProperty;
                CascadeStyle style = cascadeStyles[i];
                String propertyName = propertyNames[i];
                boolean bl = isUninitializedProperty = hasUninitializedLazyProperties && !persister.getBytecodeEnhancementMetadata().isAttributeLoaded(parent, propertyName);
                if (style.doCascade(action)) {
                    Object child;
                    if (isUninitializedProperty) {
                        if (persistenceContext.getEntry(parent) == null) continue;
                        if (types[i].isCollectionType()) {
                            CollectionType collectionType = (CollectionType)types[i];
                            child = collectionType.getCollection(collectionType.getKeyOfOwner(parent, eventSource), eventSource, parent, null);
                        } else {
                            if (types[i].isComponentType()) {
                                throw new UnsupportedOperationException("Lazy components are not supported.");
                            }
                            if (!action.performOnLazyProperty() || !types[i].isEntityType()) continue;
                            LazyAttributeLoadingInterceptor interceptor = persister.getBytecodeEnhancementMetadata().extractInterceptor(parent);
                            child = interceptor.fetchAttribute(parent, propertyName);
                        }
                    } else {
                        child = persister.getPropertyValue(parent, i);
                    }
                    Cascade.cascadeProperty(action, cascadePoint, eventSource, null, parent, child, types[i], style, propertyName, anything, false);
                    continue;
                }
                if (action.requiresNoCascadeChecking()) {
                    action.noCascade(eventSource, parent, persister, types[i], i);
                }
                if (!action.deleteOrphans() || isUninitializedProperty) continue;
                Cascade.cascadeLogicalOneToOneOrphanRemoval(action, eventSource, null, parent, persister.getPropertyValue(parent, i), types[i], style, propertyName, false);
            }
            if (traceEnabled) {
                LOG.tracev("Done processing cascade {0} for: {1}", action, persister.getEntityName());
            }
        }
    }

    private static void cascadeProperty(CascadingAction action, CascadePoint cascadePoint, EventSource eventSource, List<String> componentPath, Object parent, Object child, Type type, CascadeStyle style, String propertyName, Object anything, boolean isCascadeDeleteEnabled) throws HibernateException {
        if (child != null) {
            if (type.isAssociationType()) {
                AssociationType associationType = (AssociationType)type;
                if (Cascade.cascadeAssociationNow(cascadePoint, associationType)) {
                    Cascade.cascadeAssociation(action, cascadePoint, eventSource, componentPath, parent, child, type, style, anything, isCascadeDeleteEnabled);
                }
            } else if (type.isComponentType()) {
                if (componentPath == null && propertyName != null) {
                    componentPath = new ArrayList<String>();
                }
                if (componentPath != null) {
                    componentPath.add(propertyName);
                }
                Cascade.cascadeComponent(action, cascadePoint, eventSource, componentPath, parent, child, (CompositeType)type, anything);
                if (componentPath != null) {
                    componentPath.remove(componentPath.size() - 1);
                }
            }
        }
        Cascade.cascadeLogicalOneToOneOrphanRemoval(action, eventSource, componentPath, parent, child, type, style, propertyName, isCascadeDeleteEnabled);
    }

    private static void cascadeLogicalOneToOneOrphanRemoval(CascadingAction action, EventSource eventSource, List<String> componentPath, Object parent, Object child, Type type, CascadeStyle style, String propertyName, boolean isCascadeDeleteEnabled) throws HibernateException {
        PersistenceContext persistenceContext;
        EntityEntry entry;
        if (Cascade.isLogicalOneToOne(type) && style.hasOrphanDelete() && action.deleteOrphans() && (entry = (persistenceContext = eventSource.getPersistenceContextInternal()).getEntry(parent)) != null && entry.getStatus() != Status.SAVING) {
            Object loadedValue;
            if (componentPath == null) {
                loadedValue = entry.getLoadedValue(propertyName);
            } else {
                Type propertyType = entry.getPersister().getPropertyType(componentPath.get(0));
                if (propertyType instanceof ComponentType) {
                    loadedValue = entry.getLoadedValue(componentPath.get(0));
                    ComponentType componentType = (ComponentType)propertyType;
                    if (componentPath.size() != 1) {
                        for (int i = 1; i < componentPath.size(); ++i) {
                            int subPropertyIndex = componentType.getPropertyIndex(componentPath.get(i));
                            loadedValue = componentType.getPropertyValue(loadedValue, subPropertyIndex);
                            componentType = (ComponentType)componentType.getSubtypes()[subPropertyIndex];
                        }
                    }
                    loadedValue = componentType.getPropertyValue(loadedValue, componentType.getPropertyIndex(propertyName));
                } else {
                    loadedValue = null;
                }
            }
            if (child == null || loadedValue != null && child != loadedValue) {
                EntityEntry valueEntry = persistenceContext.getEntry(loadedValue);
                if (valueEntry == null && loadedValue instanceof HibernateProxy) {
                    loadedValue = persistenceContext.unproxyAndReassociate(loadedValue);
                    valueEntry = persistenceContext.getEntry(loadedValue);
                    if (child == loadedValue) {
                        return;
                    }
                }
                if (valueEntry != null) {
                    String entityName = valueEntry.getPersister().getEntityName();
                    if (LOG.isTraceEnabled()) {
                        Serializable id = valueEntry.getPersister().getIdentifier(loadedValue, eventSource);
                        String description = MessageHelper.infoString(entityName, id);
                        LOG.tracev("Deleting orphaned entity instance: {0}", description);
                    }
                    if (type.isAssociationType() && ((AssociationType)type).getForeignKeyDirection().equals((Object)ForeignKeyDirection.TO_PARENT)) {
                        eventSource.removeOrphanBeforeUpdates(entityName, loadedValue);
                    } else {
                        eventSource.delete(entityName, loadedValue, isCascadeDeleteEnabled, new HashSet());
                    }
                }
            }
        }
    }

    private static boolean isLogicalOneToOne(Type type) {
        return type.isEntityType() && ((EntityType)type).isLogicalOneToOne();
    }

    private static boolean cascadeAssociationNow(CascadePoint cascadePoint, AssociationType associationType) {
        return associationType.getForeignKeyDirection().cascadeNow(cascadePoint);
    }

    private static void cascadeComponent(CascadingAction action, CascadePoint cascadePoint, EventSource eventSource, List<String> componentPath, Object parent, Object child, CompositeType componentType, Object anything) {
        Object[] children = null;
        Type[] types = componentType.getSubtypes();
        String[] propertyNames = componentType.getPropertyNames();
        for (int i = 0; i < types.length; ++i) {
            CascadeStyle componentPropertyStyle = componentType.getCascadeStyle(i);
            String subPropertyName = propertyNames[i];
            if (!componentPropertyStyle.doCascade(action) && (!componentPropertyStyle.hasOrphanDelete() || !action.deleteOrphans())) continue;
            if (children == null) {
                children = componentType.getPropertyValues(child, eventSource);
            }
            Cascade.cascadeProperty(action, cascadePoint, eventSource, componentPath, parent, children[i], types[i], componentPropertyStyle, subPropertyName, anything, false);
        }
    }

    private static void cascadeAssociation(CascadingAction action, CascadePoint cascadePoint, EventSource eventSource, List<String> componentPath, Object parent, Object child, Type type, CascadeStyle style, Object anything, boolean isCascadeDeleteEnabled) {
        if (type.isEntityType() || type.isAnyType()) {
            Cascade.cascadeToOne(action, eventSource, parent, child, type, style, anything, isCascadeDeleteEnabled);
        } else if (type.isCollectionType()) {
            Cascade.cascadeCollection(action, cascadePoint, eventSource, componentPath, parent, child, style, anything, (CollectionType)type);
        }
    }

    private static void cascadeCollection(CascadingAction action, CascadePoint cascadePoint, EventSource eventSource, List<String> componentPath, Object parent, Object child, CascadeStyle style, Object anything, CollectionType type) {
        CollectionPersister persister = eventSource.getFactory().getCollectionPersister(type.getRole());
        Type elemType = persister.getElementType();
        CascadePoint elementsCascadePoint = cascadePoint;
        if (cascadePoint == CascadePoint.AFTER_INSERT_BEFORE_DELETE) {
            elementsCascadePoint = CascadePoint.AFTER_INSERT_BEFORE_DELETE_VIA_COLLECTION;
        }
        if (elemType.isEntityType() || elemType.isAnyType() || elemType.isComponentType()) {
            Cascade.cascadeCollectionElements(action, elementsCascadePoint, eventSource, componentPath, parent, child, type, style, elemType, anything, persister.isCascadeDeleteEnabled());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void cascadeToOne(CascadingAction action, EventSource eventSource, Object parent, Object child, Type type, CascadeStyle style, Object anything, boolean isCascadeDeleteEnabled) {
        String entityName;
        String string = entityName = type.isEntityType() ? ((EntityType)type).getAssociatedEntityName() : null;
        if (style.reallyDoCascade(action)) {
            PersistenceContext persistenceContext = eventSource.getPersistenceContextInternal();
            persistenceContext.addChildParent(child, parent);
            try {
                action.cascade(eventSource, child, entityName, anything, isCascadeDeleteEnabled);
            }
            finally {
                persistenceContext.removeChildParent(child);
            }
        }
    }

    private static void cascadeCollectionElements(CascadingAction action, CascadePoint cascadePoint, EventSource eventSource, List<String> componentPath, Object parent, Object child, CollectionType collectionType, CascadeStyle style, Type elemType, Object anything, boolean isCascadeDeleteEnabled) throws HibernateException {
        boolean deleteOrphans;
        boolean reallyDoCascade;
        boolean bl = reallyDoCascade = style.reallyDoCascade(action) && child != CollectionType.UNFETCHED_COLLECTION;
        if (reallyDoCascade) {
            boolean traceEnabled = LOG.isTraceEnabled();
            if (traceEnabled) {
                LOG.tracev("Cascade {0} for collection: {1}", action, collectionType.getRole());
            }
            Iterator itr = action.getCascadableChildrenIterator(eventSource, collectionType, child);
            while (itr.hasNext()) {
                Cascade.cascadeProperty(action, cascadePoint, eventSource, componentPath, parent, itr.next(), elemType, style, collectionType.getRole().substring(collectionType.getRole().lastIndexOf(46) + 1), anything, isCascadeDeleteEnabled);
            }
            if (traceEnabled) {
                LOG.tracev("Done cascade {0} for collection: {1}", action, collectionType.getRole());
            }
        }
        boolean bl2 = deleteOrphans = style.hasOrphanDelete() && action.deleteOrphans() && elemType.isEntityType() && child instanceof PersistentCollection && !((PersistentCollection)child).isNewlyInstantiated();
        if (deleteOrphans) {
            boolean traceEnabled = LOG.isTraceEnabled();
            if (traceEnabled) {
                LOG.tracev("Deleting orphans for collection: {0}", collectionType.getRole());
            }
            String entityName = collectionType.getAssociatedEntityName(eventSource.getFactory());
            Cascade.deleteOrphans(eventSource, entityName, (PersistentCollection)child);
            if (traceEnabled) {
                LOG.tracev("Done deleting orphans for collection: {0}", collectionType.getRole());
            }
        }
    }

    private static void deleteOrphans(EventSource eventSource, String entityName, PersistentCollection pc) throws HibernateException {
        CollectionEntry ce;
        Collection orphans = pc.wasInitialized() ? ((ce = eventSource.getPersistenceContextInternal().getCollectionEntry(pc)) == null ? Collections.EMPTY_LIST : ce.getOrphans(entityName, pc)) : pc.getQueuedOrphans(entityName);
        for (Object orphan : orphans) {
            if (orphan == null) continue;
            LOG.tracev("Deleting orphaned entity instance: {0}", entityName);
            eventSource.delete(entityName, orphan, false, new HashSet());
        }
    }
}

