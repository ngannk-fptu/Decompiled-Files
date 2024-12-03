/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.CollectionKey;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.MarkerObject;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.collection.QueryableCollection;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.type.AbstractType;
import org.hibernate.type.AssociationType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.jboss.logging.Logger;

public abstract class CollectionType
extends AbstractType
implements AssociationType {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)CollectionType.class.getName());
    private static final Object NOT_NULL_COLLECTION = new MarkerObject("NOT NULL COLLECTION");
    public static final Object UNFETCHED_COLLECTION = new MarkerObject("UNFETCHED COLLECTION");
    private final String role;
    private final String foreignKeyPropertyName;
    private volatile CollectionPersister persister;

    @Deprecated
    public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName) {
        this(role, foreignKeyPropertyName);
    }

    public CollectionType(String role, String foreignKeyPropertyName) {
        this.role = role;
        this.foreignKeyPropertyName = foreignKeyPropertyName;
    }

    public String getRole() {
        return this.role;
    }

    public Object indexOf(Object collection, Object element) {
        throw new UnsupportedOperationException("generic collections don't have indexes");
    }

    public boolean contains(Object collection, Object childObject, SharedSessionContractImplementor session) {
        Iterator elems = this.getElementsIterator(collection, session);
        while (elems.hasNext()) {
            LazyInitializer li;
            Object element = elems.next();
            if (element instanceof HibernateProxy && !(li = ((HibernateProxy)element).getHibernateLazyInitializer()).isUninitialized()) {
                element = li.getImplementation();
            }
            if (element != childObject) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isCollectionType() {
        return true;
    }

    @Override
    public final boolean isEqual(Object x, Object y) {
        return x == y || x instanceof PersistentCollection && this.isEqual((PersistentCollection)x, y) || y instanceof PersistentCollection && this.isEqual((PersistentCollection)y, x);
    }

    private boolean isEqual(PersistentCollection x, Object y) {
        return x.wasInitialized() && (x.isWrapper(y) || x.isDirectlyProvidedCollection(y));
    }

    @Override
    public int compare(Object x, Object y) {
        return 0;
    }

    @Override
    public int getHashCode(Object x) {
        throw new UnsupportedOperationException("cannot doAfterTransactionCompletion lookups on collections");
    }

    public abstract PersistentCollection instantiate(SharedSessionContractImplementor var1, CollectionPersister var2, Serializable var3);

    @Override
    public Object nullSafeGet(ResultSet rs, String name, SharedSessionContractImplementor session, Object owner) throws SQLException {
        return this.nullSafeGet(rs, new String[]{name}, session, owner);
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] name, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        return this.resolve(null, session, owner);
    }

    @Override
    public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SharedSessionContractImplementor session) throws HibernateException, SQLException {
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
    }

    @Override
    public int[] sqlTypes(Mapping session) throws MappingException {
        return ArrayHelper.EMPTY_INT_ARRAY;
    }

    @Override
    public Size[] dictatedSizes(Mapping mapping) throws MappingException {
        return new Size[]{LEGACY_DICTATED_SIZE};
    }

    @Override
    public Size[] defaultSizes(Mapping mapping) throws MappingException {
        return new Size[]{LEGACY_DEFAULT_SIZE};
    }

    @Override
    public int getColumnSpan(Mapping session) throws MappingException {
        return 0;
    }

    @Override
    public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
        if (value == null) {
            return "null";
        }
        if (!this.getReturnedClass().isInstance(value) && !PersistentCollection.class.isInstance(value)) {
            CollectionPersister persister = this.getPersister(factory);
            if (persister.getKeyType().getReturnedClass().isInstance(value)) {
                return this.getRole() + "#" + this.getPersister(factory).getKeyType().toLoggableString(value, factory);
            }
            if (persister.getIdentifierType() != null && persister.getIdentifierType().getReturnedClass().isInstance(value)) {
                return this.getRole() + "#" + this.getPersister(factory).getIdentifierType().toLoggableString(value, factory);
            }
        }
        return this.renderLoggableString(value, factory);
    }

    protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
        if (!Hibernate.isInitialized(value)) {
            return "<uninitialized>";
        }
        ArrayList<String> list = new ArrayList<String>();
        Type elemType = this.getElementType(factory);
        Iterator itr = this.getElementsIterator(value);
        while (itr.hasNext()) {
            Object element = itr.next();
            if (element == LazyPropertyInitializer.UNFETCHED_PROPERTY || !Hibernate.isInitialized(element)) {
                list.add("<uninitialized>");
                continue;
            }
            list.add(elemType.toLoggableString(element, factory));
        }
        return ((Object)list).toString();
    }

    @Override
    public Object deepCopy(Object value, SessionFactoryImplementor factory) throws HibernateException {
        return value;
    }

    @Override
    public String getName() {
        return this.getReturnedClass().getName() + '(' + this.getRole() + ')';
    }

    public Iterator getElementsIterator(Object collection, SharedSessionContractImplementor session) {
        return this.getElementsIterator(collection);
    }

    protected Iterator getElementsIterator(Object collection) {
        return ((Collection)collection).iterator();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        Serializable key = this.getKeyOfOwner(owner, session);
        if (key == null) {
            return null;
        }
        return this.getPersister(session).getKeyType().disassemble(key, session, owner);
    }

    @Override
    public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        if (cached == null) {
            return null;
        }
        Serializable key = (Serializable)this.getPersister(session).getKeyType().assemble(cached, session, owner);
        return this.resolveKey(key, session, owner, null);
    }

    private boolean isOwnerVersioned(SharedSessionContractImplementor session) throws MappingException {
        return this.getPersister(session).getOwnerEntityPersister().isVersioned();
    }

    private CollectionPersister getPersister(SharedSessionContractImplementor session) {
        CollectionPersister p = this.persister;
        if (p != null) {
            return p;
        }
        return this.getPersister(session.getFactory());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private CollectionPersister getPersister(SessionFactoryImplementor factory) {
        CollectionPersister p = this.persister;
        if (p != null) {
            return p;
        }
        CollectionType collectionType = this;
        synchronized (collectionType) {
            p = this.persister;
            if (p != null) {
                return p;
            }
            this.persister = p = factory.getMetamodel().collectionPersister(this.role);
            return p;
        }
    }

    @Override
    public boolean isDirty(Object old, Object current, SharedSessionContractImplementor session) throws HibernateException {
        return super.isDirty(old, current, session);
    }

    @Override
    public boolean isDirty(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        return this.isDirty(old, current, session);
    }

    public abstract PersistentCollection wrap(SharedSessionContractImplementor var1, Object var2);

    @Override
    public boolean isAssociationType() {
        return true;
    }

    @Override
    public ForeignKeyDirection getForeignKeyDirection() {
        return ForeignKeyDirection.TO_PARENT;
    }

    public Serializable getKeyOfOwner(Object owner, SharedSessionContractImplementor session) {
        PersistenceContext pc = session.getPersistenceContextInternal();
        EntityEntry entityEntry = pc.getEntry(owner);
        if (entityEntry == null) {
            return null;
        }
        if (this.foreignKeyPropertyName == null) {
            return entityEntry.getId();
        }
        Object id = entityEntry.getLoadedState() != null ? entityEntry.getLoadedValue(this.foreignKeyPropertyName) : entityEntry.getPersister().getPropertyValue(owner, this.foreignKeyPropertyName);
        Type keyType = this.getPersister(session).getKeyType();
        Class returnedClass = keyType.getReturnedClass();
        if (!returnedClass.isInstance(id)) {
            id = keyType.semiResolve(entityEntry.getLoadedValue(this.foreignKeyPropertyName), session, owner);
        }
        return (Serializable)id;
    }

    public Serializable getIdOfOwnerOrNull(Serializable key, SharedSessionContractImplementor session) {
        Serializable ownerId = null;
        if (this.foreignKeyPropertyName == null) {
            ownerId = key;
        } else {
            CollectionPersister persister = this.getPersister(session);
            Type keyType = persister.getKeyType();
            EntityPersister ownerPersister = persister.getOwnerEntityPersister();
            Class ownerMappedClass = ownerPersister.getMappedClass();
            if (ownerMappedClass.isAssignableFrom(keyType.getReturnedClass()) && keyType.getReturnedClass().isInstance(key)) {
                ownerId = ownerPersister.getIdentifier(key, session);
            }
        }
        return ownerId;
    }

    @Override
    public Object hydrate(ResultSet rs, String[] name, SharedSessionContractImplementor session, Object owner) {
        return NOT_NULL_COLLECTION;
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return this.resolve(value, session, owner, null);
    }

    @Override
    public Object resolve(Object value, SharedSessionContractImplementor session, Object owner, Boolean overridingEager) throws HibernateException {
        return this.resolveKey(this.getKeyOfOwner(owner, session), session, owner, overridingEager);
    }

    private Object resolveKey(Serializable key, SharedSessionContractImplementor session, Object owner, Boolean overridingEager) {
        return key == null ? null : this.getCollection(key, session, owner, overridingEager);
    }

    @Override
    public Object semiResolve(Object value, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        throw new UnsupportedOperationException("collection mappings may not form part of a property-ref");
    }

    public boolean isArrayType() {
        return false;
    }

    @Override
    public boolean useLHSPrimaryKey() {
        return this.foreignKeyPropertyName == null;
    }

    @Override
    public String getRHSUniqueKeyPropertyName() {
        return null;
    }

    @Override
    public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException {
        return (Joinable)((Object)factory.getCollectionPersister(this.role));
    }

    @Override
    public boolean isModified(Object old, Object current, boolean[] checkable, SharedSessionContractImplementor session) throws HibernateException {
        return false;
    }

    @Override
    public String getAssociatedEntityName(SessionFactoryImplementor factory) throws MappingException {
        try {
            QueryableCollection collectionPersister = (QueryableCollection)factory.getCollectionPersister(this.role);
            if (!collectionPersister.getElementType().isEntityType()) {
                throw new MappingException("collection was not an association: " + collectionPersister.getRole());
            }
            return collectionPersister.getElementPersister().getEntityName();
        }
        catch (ClassCastException cce) {
            throw new MappingException("collection role is not queryable " + this.role);
        }
    }

    public Object replaceElements(Object original, Object target, Object owner, Map copyCache, SharedSessionContractImplementor session) {
        Collection result = (Collection)target;
        result.clear();
        Type elemType = this.getElementType(session.getFactory());
        Iterator iter = ((Collection)original).iterator();
        while (iter.hasNext()) {
            result.add(elemType.replace(iter.next(), null, session, owner, copyCache));
        }
        if (original instanceof PersistentCollection && result instanceof PersistentCollection) {
            PersistentCollection originalPersistentCollection = (PersistentCollection)original;
            PersistentCollection resultPersistentCollection = (PersistentCollection)((Object)result);
            this.preserveSnapshot(originalPersistentCollection, resultPersistentCollection, elemType, owner, copyCache, session);
            if (!originalPersistentCollection.isDirty()) {
                resultPersistentCollection.clearDirty();
            }
        }
        return result;
    }

    /*
     * WARNING - void declaration
     */
    private void preserveSnapshot(PersistentCollection original, PersistentCollection result, Type elemType, Object owner, Map copyCache, SharedSessionContractImplementor session) {
        Serializable targetSnapshot;
        Serializable originalSnapshot = original.getStoredSnapshot();
        Serializable resultSnapshot = result.getStoredSnapshot();
        if (originalSnapshot instanceof List) {
            targetSnapshot = new ArrayList(((List)((Object)originalSnapshot)).size());
            for (Object e : (List)((Object)originalSnapshot)) {
                ((List)((Object)targetSnapshot)).add(elemType.replace(e, null, session, owner, copyCache));
            }
        } else if (originalSnapshot instanceof Map) {
            targetSnapshot = originalSnapshot instanceof SortedMap ? new TreeMap(((SortedMap)((Object)originalSnapshot)).comparator()) : new HashMap(CollectionHelper.determineProperSizing(((Map)((Object)originalSnapshot)).size()), 0.75f);
            for (Map.Entry entry : ((Map)((Object)originalSnapshot)).entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                Object resultSnapshotValue = resultSnapshot == null ? null : ((Map)((Object)resultSnapshot)).get(key);
                Object newValue = elemType.replace(value, resultSnapshotValue, session, owner, copyCache);
                if (key == value) {
                    ((Map)((Object)targetSnapshot)).put(newValue, newValue);
                    continue;
                }
                ((Map)((Object)targetSnapshot)).put(key, newValue);
            }
        } else if (originalSnapshot instanceof Object[]) {
            void var11_14;
            Object[] arr = (Object[])originalSnapshot;
            boolean bl = false;
            while (var11_14 < arr.length) {
                arr[var11_14] = elemType.replace(arr[var11_14], null, session, owner, copyCache);
                ++var11_14;
            }
            targetSnapshot = originalSnapshot;
        } else {
            targetSnapshot = resultSnapshot;
        }
        CollectionEntry ce = session.getPersistenceContextInternal().getCollectionEntry(result);
        if (ce != null) {
            ce.resetStoredSnapshot(result, targetSnapshot);
        }
    }

    protected Object instantiateResult(Object original) {
        return this.instantiate(-1);
    }

    public abstract Object instantiate(int var1);

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner, Map copyCache) throws HibernateException {
        if (original == null) {
            return null;
        }
        if (!Hibernate.isInitialized(original)) {
            if (((PersistentCollection)original).hasQueuedOperations()) {
                if (original == target) {
                    AbstractPersistentCollection pc = (AbstractPersistentCollection)original;
                    pc.replaceQueuedOperationValues(this.getPersister(session), copyCache);
                } else {
                    LOG.ignoreQueuedOperationsOnMerge(MessageHelper.collectionInfoString(this.getRole(), ((PersistentCollection)original).getKey()));
                }
            }
            return target;
        }
        Object result = target == null || target == original || target == LazyPropertyInitializer.UNFETCHED_PROPERTY || target instanceof PersistentCollection && ((PersistentCollection)target).isWrapper(original) ? this.instantiateResult(original) : target;
        result = this.replaceElements(original, result, owner, copyCache, session);
        if (original == target) {
            boolean wasClean = PersistentCollection.class.isInstance(target) && !((PersistentCollection)target).isDirty();
            this.replaceElements(result, target, owner, copyCache, session);
            if (wasClean) {
                ((PersistentCollection)target).clearDirty();
            }
            result = target;
        }
        return result;
    }

    public final Type getElementType(SessionFactoryImplementor factory) throws MappingException {
        return factory.getCollectionPersister(this.getRole()).getElementType();
    }

    public String toString() {
        return this.getClass().getName() + '(' + this.getRole() + ')';
    }

    @Override
    public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) throws MappingException {
        return this.getAssociatedJoinable(factory).filterFragment(alias, enabledFilters);
    }

    @Override
    public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters, Set<String> treatAsDeclarations) {
        return this.getAssociatedJoinable(factory).filterFragment(alias, enabledFilters, treatAsDeclarations);
    }

    public Object getCollection(Serializable key, SharedSessionContractImplementor session, Object owner, Boolean overridingEager) {
        CollectionPersister persister = this.getPersister(session);
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        CollectionKey collectionKey = new CollectionKey(persister, key);
        PersistentCollection collection = persistenceContext.getLoadContexts().locateLoadingCollection(persister, collectionKey);
        if (collection == null && (collection = persistenceContext.useUnownedCollection(collectionKey)) == null && (collection = persistenceContext.getCollection(collectionKey)) == null) {
            boolean eager;
            collection = this.instantiate(session, persister, key);
            collection.setOwner(owner);
            persistenceContext.addUninitializedCollection(persister, collection, key);
            boolean bl = overridingEager != null ? overridingEager : (eager = !persister.isLazy());
            if (this.initializeImmediately()) {
                session.initializeCollection(collection, false);
            } else if (eager) {
                persistenceContext.addNonLazyCollection(collection);
            }
            if (this.hasHolder()) {
                persistenceContext.addCollectionHolder(collection);
            }
            if (LOG.isTraceEnabled()) {
                LOG.tracef("Created collection wrapper: %s", MessageHelper.collectionInfoString(persister, collection, key, session));
            }
            return collection.getValue();
        }
        collection.setOwner(owner);
        return collection.getValue();
    }

    public boolean hasHolder() {
        return false;
    }

    protected boolean initializeImmediately() {
        return false;
    }

    @Override
    public String getLHSPropertyName() {
        return this.foreignKeyPropertyName;
    }

    @Override
    public boolean isAlwaysDirtyChecked() {
        return true;
    }

    @Override
    public boolean[] toColumnNullness(Object value, Mapping mapping) {
        return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
    }
}

