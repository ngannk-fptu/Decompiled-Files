/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDiscriminatorSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmEntityBaseDefinition;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmHibernateMapping;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmJoinedSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmRootEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSubclassEntityBaseDefinition;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmUnionSubclassEntityType;
import org.hibernate.boot.model.source.internal.hbm.AbstractEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.EntityHierarchySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.JoinedSubclassEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RootEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.SubclassEntitySourceImpl;
import org.hibernate.boot.model.source.spi.EntitySource;
import org.hibernate.internal.util.StringHelper;
import org.jboss.logging.Logger;

public class EntityHierarchyBuilder {
    private static final Logger log = Logger.getLogger(EntityHierarchyBuilder.class);
    private final List<EntityHierarchySourceImpl> entityHierarchyList = new ArrayList<EntityHierarchySourceImpl>();
    private final Map<String, AbstractEntitySourceImpl> entitySourceByNameMap = new HashMap<String, AbstractEntitySourceImpl>();
    private Map<String, List<ExtendsQueueEntry>> toBeLinkedQueue;

    public List<EntityHierarchySourceImpl> buildHierarchies() throws HibernateException {
        if (this.toBeLinkedQueue != null && !this.toBeLinkedQueue.isEmpty()) {
            if (log.isDebugEnabled()) {
                for (Map.Entry<String, List<ExtendsQueueEntry>> waitingListEntry : this.toBeLinkedQueue.entrySet()) {
                    for (ExtendsQueueEntry waitingEntry : waitingListEntry.getValue()) {
                        log.debugf("Entity super-type named as extends [%s] for subclass [%s:%s] not found", (Object)waitingListEntry.getKey(), (Object)waitingEntry.sourceMappingDocument.getOrigin(), (Object)waitingEntry.sourceMappingDocument.determineEntityName(waitingEntry.jaxbSubEntityMapping));
                    }
                }
            }
            throw new HibernateException("Not all named super-types (extends) were found : " + this.toBeLinkedQueue.keySet());
        }
        return this.entityHierarchyList;
    }

    public void indexMappingDocument(MappingDocument mappingDocument) {
        log.tracef("Indexing mapping document [%s] for purpose of building entity hierarchy ordering", (Object)mappingDocument.getOrigin());
        JaxbHbmHibernateMapping mappingBinding = mappingDocument.getDocumentRoot();
        for (JaxbHbmRootEntityType jaxbRootEntity : mappingBinding.getClazz()) {
            RootEntitySourceImpl rootEntitySource = new RootEntitySourceImpl(mappingDocument, jaxbRootEntity);
            this.entitySourceByNameMap.put(rootEntitySource.getEntityNamingSource().getEntityName(), rootEntitySource);
            EntityHierarchySourceImpl hierarchy = new EntityHierarchySourceImpl(rootEntitySource);
            this.entityHierarchyList.add(hierarchy);
            this.linkAnyWaiting(mappingDocument, rootEntitySource);
            this.processRootEntitySubEntityElements(mappingDocument, jaxbRootEntity, rootEntitySource);
        }
        for (JaxbHbmDiscriminatorSubclassEntityType discriminatorSubclassEntityBinding : mappingBinding.getSubclass()) {
            this.processTopLevelSubClassBinding(mappingDocument, discriminatorSubclassEntityBinding);
        }
        for (JaxbHbmJoinedSubclassEntityType joinedSubclassEntityBinding : mappingBinding.getJoinedSubclass()) {
            this.processTopLevelSubClassBinding(mappingDocument, joinedSubclassEntityBinding);
        }
        for (JaxbHbmUnionSubclassEntityType unionSubclassEntityBinding : mappingBinding.getUnionSubclass()) {
            this.processTopLevelSubClassBinding(mappingDocument, unionSubclassEntityBinding);
        }
    }

    private void processRootEntitySubEntityElements(MappingDocument mappingDocument, JaxbHbmRootEntityType jaxbRootEntity, RootEntitySourceImpl rootEntitySource) {
        this.processElements(mappingDocument, jaxbRootEntity.getSubclass(), rootEntitySource);
        this.processElements(mappingDocument, jaxbRootEntity.getJoinedSubclass(), rootEntitySource);
        this.processElements(mappingDocument, jaxbRootEntity.getUnionSubclass(), rootEntitySource);
    }

    private void processSubEntityElements(MappingDocument mappingDocument, JaxbHbmEntityBaseDefinition entityBinding, AbstractEntitySourceImpl container) {
        if (JaxbHbmDiscriminatorSubclassEntityType.class.isInstance(entityBinding)) {
            JaxbHbmDiscriminatorSubclassEntityType jaxbSubclass = (JaxbHbmDiscriminatorSubclassEntityType)entityBinding;
            this.processElements(mappingDocument, jaxbSubclass.getSubclass(), container);
        } else if (JaxbHbmJoinedSubclassEntityType.class.isInstance(entityBinding)) {
            JaxbHbmJoinedSubclassEntityType jaxbJoinedSubclass = (JaxbHbmJoinedSubclassEntityType)entityBinding;
            this.processElements(mappingDocument, jaxbJoinedSubclass.getJoinedSubclass(), container);
        } else if (JaxbHbmUnionSubclassEntityType.class.isInstance(entityBinding)) {
            JaxbHbmUnionSubclassEntityType jaxbUnionSubclass = (JaxbHbmUnionSubclassEntityType)entityBinding;
            this.processElements(mappingDocument, jaxbUnionSubclass.getUnionSubclass(), container);
        }
    }

    private void processElements(MappingDocument mappingDocument, List<? extends JaxbHbmSubclassEntityBaseDefinition> nestedSubEntityList, AbstractEntitySourceImpl container) {
        for (JaxbHbmSubclassEntityBaseDefinition jaxbHbmSubclassEntityBaseDefinition : nestedSubEntityList) {
            SubclassEntitySourceImpl subClassEntitySource = this.createSubClassEntitySource(mappingDocument, jaxbHbmSubclassEntityBaseDefinition, container);
            this.entitySourceByNameMap.put(subClassEntitySource.getEntityNamingSource().getEntityName(), subClassEntitySource);
            container.add(subClassEntitySource);
            this.linkAnyWaiting(mappingDocument, subClassEntitySource);
            this.processSubEntityElements(mappingDocument, jaxbHbmSubclassEntityBaseDefinition, subClassEntitySource);
        }
    }

    private SubclassEntitySourceImpl createSubClassEntitySource(MappingDocument mappingDocument, JaxbHbmSubclassEntityBaseDefinition jaxbSubEntity, EntitySource superEntity) {
        if (JaxbHbmJoinedSubclassEntityType.class.isInstance(jaxbSubEntity)) {
            return new JoinedSubclassEntitySourceImpl(mappingDocument, (JaxbHbmJoinedSubclassEntityType)JaxbHbmJoinedSubclassEntityType.class.cast(jaxbSubEntity), superEntity);
        }
        return new SubclassEntitySourceImpl(mappingDocument, jaxbSubEntity, superEntity);
    }

    private void processTopLevelSubClassBinding(MappingDocument mappingDocument, JaxbHbmSubclassEntityBaseDefinition jaxbSubEntityMapping) {
        AbstractEntitySourceImpl entityItExtends = this.locateExtendedEntitySource(mappingDocument, jaxbSubEntityMapping);
        if (entityItExtends == null) {
            this.addToToBeLinkedQueue(mappingDocument, jaxbSubEntityMapping);
        } else {
            SubclassEntitySourceImpl subEntitySource = this.createSubClassEntitySource(mappingDocument, jaxbSubEntityMapping, entityItExtends);
            this.entitySourceByNameMap.put(subEntitySource.getEntityNamingSource().getEntityName(), subEntitySource);
            entityItExtends.add(subEntitySource);
            this.linkAnyWaiting(mappingDocument, subEntitySource);
            this.processSubEntityElements(mappingDocument, jaxbSubEntityMapping, subEntitySource);
        }
    }

    private AbstractEntitySourceImpl locateExtendedEntitySource(MappingDocument mappingDocument, JaxbHbmSubclassEntityBaseDefinition jaxbSubEntityMapping) {
        AbstractEntitySourceImpl entityItExtends = this.entitySourceByNameMap.get(jaxbSubEntityMapping.getExtends());
        if (entityItExtends == null) {
            entityItExtends = this.entitySourceByNameMap.get(mappingDocument.qualifyClassName(jaxbSubEntityMapping.getExtends()));
        }
        return entityItExtends;
    }

    private void addToToBeLinkedQueue(MappingDocument mappingDocument, JaxbHbmSubclassEntityBaseDefinition jaxbSubEntityMapping) {
        List<ExtendsQueueEntry> waitingList = null;
        if (this.toBeLinkedQueue == null) {
            this.toBeLinkedQueue = new HashMap<String, List<ExtendsQueueEntry>>();
        } else {
            waitingList = this.toBeLinkedQueue.get(jaxbSubEntityMapping.getExtends());
        }
        if (waitingList == null) {
            waitingList = new ArrayList<ExtendsQueueEntry>();
            this.toBeLinkedQueue.put(jaxbSubEntityMapping.getExtends(), waitingList);
        }
        waitingList.add(new ExtendsQueueEntry(mappingDocument, jaxbSubEntityMapping));
    }

    private void linkAnyWaiting(MappingDocument mappingDocument, AbstractEntitySourceImpl entitySource) {
        if (this.toBeLinkedQueue == null) {
            return;
        }
        List<ExtendsQueueEntry> waitingList = this.toBeLinkedQueue.remove(entitySource.jaxbEntityMapping().getEntityName());
        if (waitingList != null) {
            this.processWaitingSubEntityMappings(entitySource, waitingList);
            waitingList.clear();
        }
        if (StringHelper.isNotEmpty(entitySource.jaxbEntityMapping().getName())) {
            String qualifiedEntityClassName;
            String entityClassName = entitySource.jaxbEntityMapping().getName();
            waitingList = this.toBeLinkedQueue.remove(entityClassName);
            if (waitingList != null) {
                this.processWaitingSubEntityMappings(entitySource, waitingList);
                waitingList.clear();
            }
            if (!entityClassName.equals(qualifiedEntityClassName = mappingDocument.qualifyClassName(entityClassName)) && (waitingList = this.toBeLinkedQueue.remove(qualifiedEntityClassName)) != null) {
                this.processWaitingSubEntityMappings(entitySource, waitingList);
                waitingList.clear();
            }
        }
    }

    private void processWaitingSubEntityMappings(AbstractEntitySourceImpl entitySource, List<ExtendsQueueEntry> waitingList) {
        for (ExtendsQueueEntry entry : waitingList) {
            SubclassEntitySourceImpl subEntitySource = this.createSubClassEntitySource(entry.sourceMappingDocument, entry.jaxbSubEntityMapping, entitySource);
            this.entitySourceByNameMap.put(subEntitySource.getEntityNamingSource().getEntityName(), subEntitySource);
            entitySource.add(subEntitySource);
            this.linkAnyWaiting(entry.sourceMappingDocument, subEntitySource);
            this.processSubEntityElements(entry.sourceMappingDocument, entry.jaxbSubEntityMapping, subEntitySource);
        }
    }

    private static class ExtendsQueueEntry {
        private final MappingDocument sourceMappingDocument;
        private final JaxbHbmSubclassEntityBaseDefinition jaxbSubEntityMapping;

        public ExtendsQueueEntry(MappingDocument sourceMappingDocument, JaxbHbmSubclassEntityBaseDefinition jaxbSubEntityMapping) {
            this.sourceMappingDocument = sourceMappingDocument;
            this.jaxbSubEntityMapping = jaxbSubEntityMapping;
        }
    }
}

