/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.hibernate.HibernateException
 *  org.hibernate.MappingException
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.engine.spi.SharedSessionContractImplementor
 *  org.hibernate.id.Assigned
 *  org.hibernate.id.IdentifierGenerator
 *  org.hibernate.metadata.ClassMetadata
 *  org.hibernate.persister.entity.EntityPersister
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.confluence.core.persistence.hibernate.SessionHelper;
import com.atlassian.confluence.core.persistence.hibernate.TransientHibernateHandle;
import com.atlassian.confluence.importexport.ImportContext;
import com.atlassian.confluence.importexport.ImportProcessorSummary;
import com.atlassian.confluence.importexport.ImportedObjectPreProcessor;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.impl.StackPushOperation;
import com.atlassian.confluence.importexport.xmlimport.DeferredOperations;
import com.atlassian.confluence.importexport.xmlimport.Operation;
import com.atlassian.confluence.importexport.xmlimport.model.ImportedObject;
import com.atlassian.confluence.importexport.xmlimport.model.PrimitiveId;
import com.atlassian.core.util.ProgressMeter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Assigned;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;

@Deprecated
public class ImportProcessorContext
implements ImportProcessorSummary {
    private final SessionImplementor session;
    private final SessionFactoryImplementor sessionFactory;
    private final ProgressMeter meter;
    private final boolean preserveIds;
    private ExportDescriptor exportDescriptor;
    private Set<TransientHibernateHandle> persistedUnmappedHandles = Sets.newHashSetWithExpectedSize((int)0);
    private Set<TransientHibernateHandle> persistedMappedHandles = Sets.newHashSetWithExpectedSize((int)0);
    private Map<String, Object> contextVariables = Maps.newHashMapWithExpectedSize((int)0);
    private Map<TransientHibernateHandle, Serializable> idMappings = Maps.newHashMapWithExpectedSize((int)0);
    private DeferredOperations deferredOperations = new DeferredOperations();
    private Stack<ImportedObject> pendingDeferredImportedObjects = new Stack();
    private int writtenSinceLastFlush = 0;
    private Transaction tx;
    private ImportedObjectPreProcessor preProcessor;

    public ImportProcessorContext(SessionImplementor session, SessionFactoryImplementor sessionFactory, ProgressMeter meter, boolean preserveIds, ImportedObjectPreProcessor preProcessor) throws HibernateException {
        this.session = session;
        this.sessionFactory = sessionFactory;
        this.meter = meter;
        this.preserveIds = preserveIds;
        this.tx = session.getTransaction();
        this.preProcessor = preProcessor;
    }

    public ImportProcessorContext(SessionImplementor session, SessionFactoryImplementor sessionFactory, boolean preserveIds, ImportContext importContext) throws HibernateException {
        this(session, sessionFactory, importContext.getProgressMeter(), preserveIds, importContext.getPreProcessor());
        this.exportDescriptor = importContext.getExportDescriptor();
    }

    public void objectImported(TransientHibernateHandle handle) throws Exception {
        this.persistedUnmappedHandles.add(handle);
        this.persistedMappedHandles.add(TransientHibernateHandle.create(handle.getClazz(), this.getIdMappingFor(handle)));
        this.deferredOperations.doDeferredOperationsWaitingFor(handle);
        this.meter.setCurrentCount(this.meter.getCurrentCount() + 1);
        this.flushIfNeeded();
    }

    public EntityPersister getPersister(Class classToPersist) throws MappingException {
        return this.sessionFactory.getMetamodel().locateEntityPersister(classToPersist);
    }

    public EntityPersister getEntityPersister(Class classToPersist) throws MappingException {
        return this.sessionFactory.getMetamodel().entityPersister(classToPersist);
    }

    public boolean isObjectAlreadyImported(TransientHibernateHandle key) {
        return this.persistedUnmappedHandles.contains(key);
    }

    public void saveObject(Object object) throws HibernateException {
        this.session.saveOrUpdate(object);
        ++this.writtenSinceLastFlush;
    }

    public ClassMetadata getClassMetadata(Class classToPersist) throws HibernateException {
        return this.sessionFactory.getClassMetadata(classToPersist);
    }

    public Object lookupObjectByUnfixedHandle(TransientHibernateHandle key) throws HibernateException {
        if (this.idMappings.containsKey(key)) {
            return TransientHibernateHandle.create(key.getClazz(), this.idMappings.get(key)).get((Session)this.session);
        }
        if (this.preserveIds) {
            return key.get((Session)this.session);
        }
        return null;
    }

    public <T> T polyMorphicLookupByUnfixedId(long id, Class<? extends T> ... classes) throws HibernateException {
        for (Class<T> clazz : classes) {
            Object obj = this.lookupObjectByUnfixedHandle(TransientHibernateHandle.create(clazz, Long.valueOf(id)));
            if (obj == null) continue;
            return clazz.cast(obj);
        }
        return null;
    }

    public SessionImplementor getSession() {
        return this.session;
    }

    public void deferOperations(PrimitiveId idProperty, Map<TransientHibernateHandle, Set<Operation>> operations) {
        for (Map.Entry<TransientHibernateHandle, Set<Operation>> entry : operations.entrySet()) {
            for (Operation operation : entry.getValue()) {
                this.deferredOperations.addDeferredOperation(idProperty, Collections.singleton(entry.getKey()), operation);
            }
        }
    }

    public boolean hasPendingDeferredObject() {
        return !this.pendingDeferredImportedObjects.isEmpty();
    }

    public ImportedObject nextPendingDeferredObject() {
        return this.pendingDeferredImportedObjects.pop();
    }

    public void addUnsatisfiedObjectDependencies(Set<TransientHibernateHandle> unsatisfiedObjectDependencies, ImportedObject importedObject) {
        this.deferredOperations.addDeferredOperation(importedObject.getIdProperty(), unsatisfiedObjectDependencies, new StackPushOperation(this.pendingDeferredImportedObjects, unsatisfiedObjectDependencies, importedObject));
    }

    private void flushIfNeeded() throws HibernateException {
        if (this.writtenSinceLastFlush >= 100) {
            this.session.flush();
            this.session.clear();
            this.tx.commit();
            this.tx = this.session.beginTransaction();
            this.writtenSinceLastFlush = 0;
        }
    }

    public Object getContextVariable(String key) {
        return this.contextVariables.get(key);
    }

    public void setContextVariable(String key, Object value) {
        this.contextVariables.put(key, value);
    }

    @Override
    public Serializable getIdMappingFor(TransientHibernateHandle handle) {
        return this.preserveIds ? handle.getId() : this.idMappings.get(handle);
    }

    public void saveObject(Serializable id, Class classToPersist, Object objectToPersist) throws HibernateException, SQLException {
        SessionHelper.save((Session)this.session, objectToPersist, this.getIdMapping(objectToPersist, id, classToPersist));
        ++this.writtenSinceLastFlush;
    }

    public void addExplicitIdMapping(TransientHibernateHandle TransientHibernateHandle5, Serializable mappedId) {
        this.idMappings.put(TransientHibernateHandle5, mappedId);
    }

    @Override
    public Set<TransientHibernateHandle> getPersistedUnmappedHandles() {
        return this.persistedUnmappedHandles;
    }

    @Override
    public Set<TransientHibernateHandle> getPersistedMappedHandles() {
        return this.preserveIds ? this.persistedUnmappedHandles : this.persistedMappedHandles;
    }

    @Override
    public Object getUnfixedIdFor(Class clazz, Object newId) {
        if (this.preserveIds) {
            return newId;
        }
        for (Map.Entry<TransientHibernateHandle, Serializable> entry : this.idMappings.entrySet()) {
            if (!clazz.equals(entry.getKey().getClazz()) || !newId.equals(entry.getValue())) continue;
            return entry.getKey().getId();
        }
        return newId;
    }

    @Override
    public Collection<TransientHibernateHandle> getImportedObjectHandlesOfType(Class clazz) {
        ArrayList<TransientHibernateHandle> handles = new ArrayList<TransientHibernateHandle>();
        for (TransientHibernateHandle handle : this.getPersistedMappedHandles()) {
            if (!clazz.isAssignableFrom(handle.getClazz())) continue;
            handles.add(handle);
        }
        return handles;
    }

    @Override
    public <T> Collection<T> getImportedObjectsOfType(Class<T> clazz) throws HibernateException {
        ArrayList<Object> spaces = new ArrayList<Object>();
        for (TransientHibernateHandle handle : this.getImportedObjectHandlesOfType(clazz)) {
            spaces.add(handle.get((Session)this.session));
        }
        return spaces;
    }

    public void reportIncompleteDefferredOperations() throws Exception {
        this.deferredOperations.reportOutstandingOperations();
    }

    private Serializable getIdMapping(Object obj, Serializable idValue, Class clazz) throws HibernateException, SQLException {
        EntityPersister entityPersister = this.getPersister(clazz);
        Class mappedClass = entityPersister.getMappedClass();
        TransientHibernateHandle unmappedHandle = TransientHibernateHandle.create(mappedClass, idValue);
        if (this.preserveIds) {
            return idValue;
        }
        if (this.idMappings.containsKey(unmappedHandle)) {
            return this.idMappings.get(unmappedHandle);
        }
        IdentifierGenerator identifierGenerator = entityPersister.getIdentifierGenerator();
        Serializable fixedId = identifierGenerator instanceof Assigned ? idValue : identifierGenerator.generate((SharedSessionContractImplementor)this.session, obj);
        this.idMappings.put(unmappedHandle, fixedId);
        return fixedId;
    }

    public <T> Object generateNewIdFor(Class<T> clazz, T object) throws HibernateException, SQLException {
        return this.getPersister(clazz).getIdentifierGenerator().generate((SharedSessionContractImplementor)this.session, object);
    }

    public void setPreProcessor(ImportedObjectPreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }

    public ImportedObjectPreProcessor getPreProcessor() {
        return this.preProcessor;
    }

    public boolean isPreserveIds() {
        return this.preserveIds;
    }

    public ExportDescriptor getExportDescriptor() {
        return this.exportDescriptor;
    }
}

