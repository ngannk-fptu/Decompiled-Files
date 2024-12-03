/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.collection.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import org.hibernate.AssertionFailure;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LazyInitializationException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.ForeignKeys;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.SessionFactoryRegistry;
import org.hibernate.internal.util.MarkerObject;
import org.hibernate.internal.util.collections.IdentitySet;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.type.CompositeType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.hibernate.type.UUIDBinaryType;
import org.hibernate.type.UUIDCharType;

public abstract class AbstractPersistentCollection
implements Serializable,
PersistentCollection {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(AbstractPersistentCollection.class);
    private transient SharedSessionContractImplementor session;
    private boolean isTempSession = false;
    private boolean initialized;
    private transient List<DelayedOperation> operationQueue;
    private transient boolean directlyAccessible;
    private transient boolean initializing;
    private Object owner;
    private int cachedSize = -1;
    private String role;
    private Serializable key;
    private boolean dirty;
    protected boolean elementRemoved;
    private Serializable storedSnapshot;
    private String sessionFactoryUuid;
    private boolean allowLoadOutsideTransaction;
    protected static final Object UNKNOWN = new MarkerObject("UNKNOWN");

    public AbstractPersistentCollection() {
    }

    protected AbstractPersistentCollection(SharedSessionContractImplementor session) {
        this.session = session;
    }

    @Deprecated
    protected AbstractPersistentCollection(SessionImplementor session) {
        this((SharedSessionContractImplementor)session);
    }

    @Override
    public final String getRole() {
        return this.role;
    }

    @Override
    public final Serializable getKey() {
        return this.key;
    }

    @Override
    public final boolean isUnreferenced() {
        return this.role == null;
    }

    @Override
    public final boolean isDirty() {
        return this.dirty;
    }

    @Override
    public boolean isElementRemoved() {
        return this.elementRemoved;
    }

    @Override
    public final void clearDirty() {
        this.dirty = false;
        this.elementRemoved = false;
    }

    @Override
    public final void dirty() {
        this.dirty = true;
    }

    @Override
    public final Serializable getStoredSnapshot() {
        return this.storedSnapshot;
    }

    @Override
    public abstract boolean empty();

    protected final void read() {
        this.initialize(false);
    }

    protected boolean readSize() {
        if (!this.initialized) {
            if (this.cachedSize != -1 && !this.hasQueuedOperations()) {
                return true;
            }
            boolean isExtraLazy = this.withTemporarySessionIfNeeded(new LazyInitializationWork<Boolean>(){

                @Override
                public Boolean doWork() {
                    CollectionEntry entry = AbstractPersistentCollection.this.session.getPersistenceContextInternal().getCollectionEntry(AbstractPersistentCollection.this);
                    if (entry != null) {
                        CollectionPersister persister = entry.getLoadedPersister();
                        if (persister.isExtraLazy()) {
                            if (AbstractPersistentCollection.this.hasQueuedOperations()) {
                                AbstractPersistentCollection.this.session.flush();
                            }
                            AbstractPersistentCollection.this.cachedSize = persister.getSize(entry.getLoadedKey(), AbstractPersistentCollection.this.session);
                            return true;
                        }
                        AbstractPersistentCollection.this.read();
                    } else {
                        AbstractPersistentCollection.this.throwLazyInitializationExceptionIfNotConnected();
                    }
                    return false;
                }
            });
            if (isExtraLazy) {
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <T> T withTemporarySessionIfNeeded(LazyInitializationWork<T> lazyInitializationWork) {
        SharedSessionContractImplementor tempSession = null;
        if (this.session == null) {
            if (this.allowLoadOutsideTransaction) {
                tempSession = this.openTemporarySessionForLoading();
            } else {
                this.throwLazyInitializationException("could not initialize proxy - no Session");
            }
        } else if (!this.session.isOpenOrWaitingForAutoClose()) {
            if (this.allowLoadOutsideTransaction) {
                tempSession = this.openTemporarySessionForLoading();
            } else {
                this.throwLazyInitializationException("could not initialize proxy - the owning Session was closed");
            }
        } else if (!this.session.isConnected()) {
            if (this.allowLoadOutsideTransaction) {
                tempSession = this.openTemporarySessionForLoading();
            } else {
                this.throwLazyInitializationException("could not initialize proxy - the owning Session is disconnected");
            }
        }
        SharedSessionContractImplementor originalSession = null;
        boolean isJTA = false;
        if (tempSession != null) {
            this.isTempSession = true;
            originalSession = this.session;
            this.session = tempSession;
            isJTA = this.session.getTransactionCoordinator().getTransactionCoordinatorBuilder().isJta();
            if (!isJTA) {
                this.session.beginTransaction();
            }
            this.session.getPersistenceContextInternal().addUninitializedDetachedCollection(this.session.getFactory().getCollectionPersister(this.getRole()), this);
        }
        try {
            T t = lazyInitializationWork.doWork();
            return t;
        }
        finally {
            if (tempSession != null) {
                this.isTempSession = false;
                this.session = originalSession;
                try {
                    if (!isJTA) {
                        tempSession.getTransaction().commit();
                    }
                    tempSession.close();
                }
                catch (Exception e) {
                    LOG.warn("Unable to close temporary session used to load lazy collection associated to no session");
                }
            } else if (!this.session.isTransactionInProgress()) {
                this.session.getJdbcCoordinator().afterTransaction();
            }
        }
    }

    private SharedSessionContractImplementor openTemporarySessionForLoading() {
        if (this.sessionFactoryUuid == null) {
            this.throwLazyInitializationException("SessionFactory UUID not known to create temporary Session for loading");
        }
        SessionFactoryImplementor sf = (SessionFactoryImplementor)SessionFactoryRegistry.INSTANCE.getSessionFactory(this.sessionFactoryUuid);
        SharedSessionContractImplementor session = (SharedSessionContractImplementor)((Object)sf.openSession());
        session.getPersistenceContextInternal().setDefaultReadOnly(true);
        session.setFlushMode(FlushMode.MANUAL);
        return session;
    }

    protected Boolean readIndexExistence(final Object index) {
        Boolean extraLazyExistenceCheck;
        if (!this.initialized && (extraLazyExistenceCheck = this.withTemporarySessionIfNeeded(new LazyInitializationWork<Boolean>(){

            @Override
            public Boolean doWork() {
                CollectionEntry entry = AbstractPersistentCollection.this.session.getPersistenceContextInternal().getCollectionEntry(AbstractPersistentCollection.this);
                CollectionPersister persister = entry.getLoadedPersister();
                if (persister.isExtraLazy()) {
                    if (AbstractPersistentCollection.this.hasQueuedOperations()) {
                        AbstractPersistentCollection.this.session.flush();
                    }
                    return persister.indexExists(entry.getLoadedKey(), index, AbstractPersistentCollection.this.session);
                }
                AbstractPersistentCollection.this.read();
                return null;
            }
        })) != null) {
            return extraLazyExistenceCheck;
        }
        return null;
    }

    protected Boolean readElementExistence(final Object element) {
        Boolean extraLazyExistenceCheck;
        if (!this.initialized && (extraLazyExistenceCheck = this.withTemporarySessionIfNeeded(new LazyInitializationWork<Boolean>(){

            @Override
            public Boolean doWork() {
                CollectionEntry entry = AbstractPersistentCollection.this.session.getPersistenceContextInternal().getCollectionEntry(AbstractPersistentCollection.this);
                CollectionPersister persister = entry.getLoadedPersister();
                if (persister.isExtraLazy()) {
                    if (AbstractPersistentCollection.this.hasQueuedOperations()) {
                        AbstractPersistentCollection.this.session.flush();
                    }
                    return persister.elementExists(entry.getLoadedKey(), element, AbstractPersistentCollection.this.session);
                }
                AbstractPersistentCollection.this.read();
                return null;
            }
        })) != null) {
            return extraLazyExistenceCheck;
        }
        return null;
    }

    protected Object readElementByIndex(final Object index) {
        if (!this.initialized) {
            class ExtraLazyElementByIndexReader
            implements LazyInitializationWork {
                private boolean isExtraLazy;
                private Object element;

                ExtraLazyElementByIndexReader() {
                }

                public Object doWork() {
                    CollectionEntry entry = AbstractPersistentCollection.this.session.getPersistenceContextInternal().getCollectionEntry(AbstractPersistentCollection.this);
                    CollectionPersister persister = entry.getLoadedPersister();
                    this.isExtraLazy = persister.isExtraLazy();
                    if (this.isExtraLazy) {
                        if (AbstractPersistentCollection.this.hasQueuedOperations()) {
                            AbstractPersistentCollection.this.session.flush();
                        }
                        this.element = persister.getElementByIndex(entry.getLoadedKey(), index, AbstractPersistentCollection.this.session, AbstractPersistentCollection.this.owner);
                    } else {
                        AbstractPersistentCollection.this.read();
                    }
                    return null;
                }
            }
            ExtraLazyElementByIndexReader reader = new ExtraLazyElementByIndexReader();
            this.withTemporarySessionIfNeeded(reader);
            if (reader.isExtraLazy) {
                return reader.element;
            }
        }
        return UNKNOWN;
    }

    protected int getCachedSize() {
        return this.cachedSize;
    }

    protected boolean isConnectedToSession() {
        return this.session != null && this.session.isOpen() && this.session.getPersistenceContextInternal().containsCollection(this);
    }

    protected boolean isInitialized() {
        return this.initialized;
    }

    protected final void write() {
        this.initialize(true);
        this.dirty();
    }

    protected boolean isOperationQueueEnabled() {
        return !this.initialized && this.isConnectedToSession() && this.isInverseCollection();
    }

    protected boolean isPutQueueEnabled() {
        return !this.initialized && this.isConnectedToSession() && this.isInverseOneToManyOrNoOrphanDelete();
    }

    protected boolean isClearQueueEnabled() {
        return !this.initialized && this.isConnectedToSession() && this.isInverseCollectionNoOrphanDelete();
    }

    protected boolean isInverseCollection() {
        CollectionEntry ce = this.session.getPersistenceContextInternal().getCollectionEntry(this);
        return ce != null && ce.getLoadedPersister().isInverse();
    }

    protected boolean isInverseCollectionNoOrphanDelete() {
        CollectionEntry ce = this.session.getPersistenceContextInternal().getCollectionEntry(this);
        if (ce == null) {
            return false;
        }
        CollectionPersister loadedPersister = ce.getLoadedPersister();
        return loadedPersister.isInverse() && !loadedPersister.hasOrphanDelete();
    }

    protected boolean isInverseOneToManyOrNoOrphanDelete() {
        CollectionEntry ce = this.session.getPersistenceContextInternal().getCollectionEntry(this);
        if (ce == null) {
            return false;
        }
        CollectionPersister loadedPersister = ce.getLoadedPersister();
        return loadedPersister.isInverse() && (loadedPersister.isOneToMany() || !loadedPersister.hasOrphanDelete());
    }

    protected final void queueOperation(DelayedOperation operation) {
        if (this.operationQueue == null) {
            this.operationQueue = new ArrayList<DelayedOperation>(10);
        }
        this.operationQueue.add(operation);
        this.dirty = true;
    }

    public final void replaceQueuedOperationValues(CollectionPersister persister, Map copyCache) {
        for (DelayedOperation operation : this.operationQueue) {
            if (!ValueDelayedOperation.class.isInstance(operation)) continue;
            ((ValueDelayedOperation)operation).replace(persister, copyCache);
        }
    }

    protected final void performQueuedOperations() {
        for (DelayedOperation operation : this.operationQueue) {
            operation.operate();
        }
        this.clearOperationQueue();
    }

    @Override
    public void setSnapshot(Serializable key, String role, Serializable snapshot) {
        this.key = key;
        this.role = role;
        this.storedSnapshot = snapshot;
    }

    @Override
    public void postAction() {
        this.clearOperationQueue();
        this.cachedSize = -1;
        this.clearDirty();
    }

    public final void clearOperationQueue() {
        this.operationQueue = null;
    }

    @Override
    public Object getValue() {
        return this;
    }

    @Override
    public void beginRead() {
        this.initializing = true;
    }

    @Override
    public boolean endRead() {
        return this.afterInitialize();
    }

    @Override
    public boolean afterInitialize() {
        this.setInitialized();
        if (this.hasQueuedOperations()) {
            this.performQueuedOperations();
            this.cachedSize = -1;
            return false;
        }
        return true;
    }

    protected final void initialize(final boolean writing) {
        if (this.initialized) {
            return;
        }
        this.withTemporarySessionIfNeeded(new LazyInitializationWork<Object>(){

            @Override
            public Object doWork() {
                AbstractPersistentCollection.this.session.initializeCollection(AbstractPersistentCollection.this, writing);
                return null;
            }
        });
    }

    private void throwLazyInitializationExceptionIfNotConnected() {
        if (!this.isConnectedToSession()) {
            this.throwLazyInitializationException("no session or session was closed");
        }
        if (!this.session.isConnected()) {
            this.throwLazyInitializationException("session is disconnected");
        }
    }

    private void throwLazyInitializationException(String message) {
        throw new LazyInitializationException("failed to lazily initialize a collection" + (this.role == null ? "" : " of role: " + this.role) + ", " + message);
    }

    protected final void setInitialized() {
        this.initializing = false;
        this.initialized = true;
    }

    protected final void setDirectlyAccessible(boolean directlyAccessible) {
        this.directlyAccessible = directlyAccessible;
    }

    @Override
    public boolean isDirectlyAccessible() {
        return this.directlyAccessible;
    }

    @Override
    public final boolean unsetSession(SharedSessionContractImplementor currentSession) {
        this.prepareForPossibleLoadingOutsideTransaction();
        if (currentSession == this.session) {
            if (!this.isTempSession) {
                String collectionInfoString;
                if (this.hasQueuedOperations()) {
                    collectionInfoString = MessageHelper.collectionInfoString(this.getRole(), this.getKey());
                    try {
                        TransactionStatus transactionStatus = this.session.getTransactionCoordinator().getTransactionDriverControl().getStatus();
                        if (transactionStatus.isOneOf(TransactionStatus.ROLLED_BACK, TransactionStatus.MARKED_ROLLBACK, TransactionStatus.FAILED_COMMIT, TransactionStatus.FAILED_ROLLBACK, TransactionStatus.ROLLING_BACK)) {
                            LOG.queuedOperationWhenDetachFromSessionOnRollback(collectionInfoString);
                        } else {
                            LOG.queuedOperationWhenDetachFromSession(collectionInfoString);
                        }
                    }
                    catch (Exception e) {
                        LOG.queuedOperationWhenDetachFromSession(collectionInfoString);
                    }
                }
                if (this.allowLoadOutsideTransaction && !this.initialized && this.session.getLoadQueryInfluencers().hasEnabledFilters()) {
                    collectionInfoString = MessageHelper.collectionInfoString(this.getRole(), this.getKey());
                    LOG.enabledFiltersWhenDetachFromSession(collectionInfoString);
                }
                this.session = null;
            }
            return true;
        }
        if (this.session != null) {
            LOG.logCannotUnsetUnexpectedSessionInCollection(this.generateUnexpectedSessionStateMessage(currentSession));
        }
        return false;
    }

    protected void prepareForPossibleLoadingOutsideTransaction() {
        if (this.session != null) {
            this.allowLoadOutsideTransaction = this.session.getFactory().getSessionFactoryOptions().isInitializeLazyStateOutsideTransactionsEnabled();
            if (this.allowLoadOutsideTransaction && this.sessionFactoryUuid == null) {
                this.sessionFactoryUuid = this.session.getFactory().getUuid();
            }
        }
    }

    @Override
    public final boolean setCurrentSession(SharedSessionContractImplementor session) throws HibernateException {
        if (session == this.session) {
            return false;
        }
        if (this.session != null) {
            String msg = this.generateUnexpectedSessionStateMessage(session);
            if (this.isConnectedToSession()) {
                throw new HibernateException("Illegal attempt to associate a collection with two open sessions. " + msg);
            }
            LOG.logUnexpectedSessionInCollectionNotConnected(msg);
        }
        if (this.hasQueuedOperations()) {
            LOG.queuedOperationWhenAttachToSession(MessageHelper.collectionInfoString(this.getRole(), this.getKey()));
        }
        this.session = session;
        return true;
    }

    private String generateUnexpectedSessionStateMessage(SharedSessionContractImplementor session) {
        String roleCurrent = this.role;
        Serializable keyCurrent = this.key;
        StringBuilder sb = new StringBuilder("Collection : ");
        if (roleCurrent != null) {
            sb.append(MessageHelper.collectionInfoString(roleCurrent, keyCurrent));
        } else {
            CollectionEntry ce = session.getPersistenceContextInternal().getCollectionEntry(this);
            if (ce != null) {
                sb.append(MessageHelper.collectionInfoString(ce.getLoadedPersister(), this, ce.getLoadedKey(), session));
            } else {
                sb.append("<unknown>");
            }
        }
        if (LOG.isDebugEnabled()) {
            String collectionContents = this.wasInitialized() ? this.toString() : "<uninitialized>";
            sb.append("\nCollection contents: [").append(collectionContents).append("]");
        }
        return sb.toString();
    }

    @Override
    public boolean needsRecreate(CollectionPersister persister) {
        Type whereType = persister.hasIndex() ? persister.getIndexType() : persister.getElementType();
        if (whereType instanceof CompositeType) {
            CompositeType componentIndexType = (CompositeType)whereType;
            return !componentIndexType.hasNotNullProperty();
        }
        return false;
    }

    @Override
    public final void forceInitialization() throws HibernateException {
        if (!this.initialized) {
            if (this.initializing) {
                throw new AssertionFailure("force initialize loading collection");
            }
            this.initialize(false);
        }
    }

    protected final Serializable getSnapshot() {
        return this.session.getPersistenceContext().getSnapshot(this);
    }

    @Override
    public final boolean wasInitialized() {
        return this.initialized;
    }

    @Override
    public boolean isRowUpdatePossible() {
        return true;
    }

    @Override
    public final boolean hasQueuedOperations() {
        return this.operationQueue != null;
    }

    @Override
    public final Iterator queuedAdditionIterator() {
        if (this.hasQueuedOperations()) {
            return new Iterator(){
                private int index;

                public Object next() {
                    return ((DelayedOperation)AbstractPersistentCollection.this.operationQueue.get(this.index++)).getAddedInstance();
                }

                @Override
                public boolean hasNext() {
                    return this.index < AbstractPersistentCollection.this.operationQueue.size();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return Collections.emptyIterator();
    }

    @Override
    public final Collection getQueuedOrphans(String entityName) {
        if (this.hasQueuedOperations()) {
            ArrayList<Object> additions = new ArrayList<Object>(this.operationQueue.size());
            ArrayList<Object> removals = new ArrayList<Object>(this.operationQueue.size());
            for (DelayedOperation operation : this.operationQueue) {
                additions.add(operation.getAddedInstance());
                removals.add(operation.getOrphan());
            }
            return AbstractPersistentCollection.getOrphans(removals, additions, entityName, this.session);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public void preInsert(CollectionPersister persister) throws HibernateException {
    }

    @Override
    public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {
    }

    @Override
    public abstract Collection getOrphans(Serializable var1, String var2) throws HibernateException;

    public final SharedSessionContractImplementor getSession() {
        return this.session;
    }

    protected static Collection getOrphans(Collection oldElements, Collection currentElements, String entityName, SharedSessionContractImplementor session) throws HibernateException {
        if (currentElements.size() == 0) {
            return oldElements;
        }
        if (oldElements.size() == 0) {
            return oldElements;
        }
        EntityPersister entityPersister = session.getFactory().getEntityPersister(entityName);
        Type idType = entityPersister.getIdentifierType();
        boolean useIdDirect = AbstractPersistentCollection.mayUseIdDirect(idType);
        ArrayList res = new ArrayList();
        HashSet<Serializable> currentIds = new HashSet<Serializable>();
        IdentitySet currentSaving = new IdentitySet();
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        for (Object current : currentElements) {
            if (current == null || !ForeignKeys.isNotTransient(entityName, current, null, session)) continue;
            EntityEntry ee = persistenceContext.getEntry(current);
            if (ee != null && ee.getStatus() == Status.SAVING) {
                currentSaving.add(current);
                continue;
            }
            Serializable currentId = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, current, session);
            currentIds.add(useIdDirect ? currentId : new TypedValue(idType, currentId));
        }
        for (Object old : oldElements) {
            if (currentSaving.contains(old)) continue;
            Serializable oldId = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, old, session);
            if (currentIds.contains(useIdDirect ? oldId : new TypedValue(idType, oldId))) continue;
            res.add(old);
        }
        return res;
    }

    private static boolean mayUseIdDirect(Type idType) {
        return idType == StringType.INSTANCE || idType == IntegerType.INSTANCE || idType == LongType.INSTANCE || idType == UUIDBinaryType.INSTANCE || idType == UUIDCharType.INSTANCE || idType == PostgresUUIDType.INSTANCE;
    }

    public static void identityRemove(Collection list, Object entityInstance, String entityName, SharedSessionContractImplementor session) {
        if (entityInstance != null && ForeignKeys.isNotTransient(entityName, entityInstance, null, session)) {
            EntityPersister entityPersister = session.getFactory().getEntityPersister(entityName);
            Type idType = entityPersister.getIdentifierType();
            Serializable idOfCurrent = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, entityInstance, session);
            Iterator itr = list.iterator();
            while (itr.hasNext()) {
                Serializable idOfOld = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, itr.next(), session);
                if (!idType.isEqual(idOfCurrent, idOfOld, session.getFactory())) continue;
                itr.remove();
                break;
            }
        }
    }

    @Deprecated
    public static void identityRemove(Collection list, Object entityInstance, String entityName, SessionImplementor session) {
        AbstractPersistentCollection.identityRemove(list, entityInstance, entityName, (SharedSessionContractImplementor)session);
    }

    @Override
    public Object getIdentifier(Object entry, int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(Object owner) {
        this.owner = owner;
    }

    protected abstract class AbstractValueDelayedOperation
    implements ValueDelayedOperation {
        private Object addedValue;
        private Object orphan;

        protected AbstractValueDelayedOperation(Object addedValue, Object orphan) {
            this.addedValue = addedValue;
            this.orphan = orphan;
        }

        @Override
        public void replace(CollectionPersister persister, Map copyCache) {
            if (this.addedValue != null) {
                this.addedValue = this.getReplacement(persister.getElementType(), this.addedValue, copyCache);
            }
        }

        protected final Object getReplacement(Type type, Object current, Map copyCache) {
            return type.replace(current, null, AbstractPersistentCollection.this.session, AbstractPersistentCollection.this.owner, copyCache);
        }

        @Override
        public final Object getAddedInstance() {
            return this.addedValue;
        }

        @Override
        public final Object getOrphan() {
            return this.orphan;
        }
    }

    protected static interface ValueDelayedOperation
    extends DelayedOperation {
        public void replace(CollectionPersister var1, Map var2);
    }

    protected static interface DelayedOperation {
        public void operate();

        public Object getAddedInstance();

        public Object getOrphan();
    }

    protected final class ListProxy
    implements List {
        protected final List list;

        public ListProxy(List list) {
            this.list = list;
        }

        public void add(int index, Object value) {
            AbstractPersistentCollection.this.write();
            this.list.add(index, value);
        }

        @Override
        public boolean add(Object o) {
            AbstractPersistentCollection.this.write();
            return this.list.add(o);
        }

        @Override
        public boolean addAll(Collection c) {
            AbstractPersistentCollection.this.write();
            return this.list.addAll(c);
        }

        public boolean addAll(int i, Collection c) {
            AbstractPersistentCollection.this.write();
            return this.list.addAll(i, c);
        }

        @Override
        public void clear() {
            AbstractPersistentCollection.this.write();
            this.list.clear();
        }

        @Override
        public boolean contains(Object o) {
            return this.list.contains(o);
        }

        @Override
        public boolean containsAll(Collection c) {
            return this.list.containsAll(c);
        }

        public Object get(int i) {
            return this.list.get(i);
        }

        @Override
        public int indexOf(Object o) {
            return this.list.indexOf(o);
        }

        @Override
        public boolean isEmpty() {
            return this.list.isEmpty();
        }

        @Override
        public Iterator iterator() {
            return new IteratorProxy(this.list.iterator());
        }

        @Override
        public int lastIndexOf(Object o) {
            return this.list.lastIndexOf(o);
        }

        public ListIterator listIterator() {
            return new ListIteratorProxy(this.list.listIterator());
        }

        public ListIterator listIterator(int i) {
            return new ListIteratorProxy(this.list.listIterator(i));
        }

        public Object remove(int i) {
            AbstractPersistentCollection.this.write();
            return this.list.remove(i);
        }

        @Override
        public boolean remove(Object o) {
            AbstractPersistentCollection.this.write();
            return this.list.remove(o);
        }

        @Override
        public boolean removeAll(Collection c) {
            AbstractPersistentCollection.this.write();
            return this.list.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection c) {
            AbstractPersistentCollection.this.write();
            return this.list.retainAll(c);
        }

        public Object set(int i, Object o) {
            AbstractPersistentCollection.this.write();
            return this.list.set(i, o);
        }

        @Override
        public int size() {
            return this.list.size();
        }

        public List subList(int i, int j) {
            return this.list.subList(i, j);
        }

        @Override
        public Object[] toArray() {
            return this.list.toArray();
        }

        @Override
        public Object[] toArray(Object[] array) {
            return this.list.toArray(array);
        }
    }

    protected class SetProxy
    implements Set {
        protected final Collection set;

        public SetProxy(Collection set) {
            this.set = set;
        }

        @Override
        public boolean add(Object o) {
            AbstractPersistentCollection.this.write();
            return this.set.add(o);
        }

        @Override
        public boolean addAll(Collection c) {
            AbstractPersistentCollection.this.write();
            return this.set.addAll(c);
        }

        @Override
        public void clear() {
            AbstractPersistentCollection.this.write();
            this.set.clear();
        }

        @Override
        public boolean contains(Object o) {
            return this.set.contains(o);
        }

        @Override
        public boolean containsAll(Collection c) {
            return this.set.containsAll(c);
        }

        @Override
        public boolean isEmpty() {
            return this.set.isEmpty();
        }

        @Override
        public Iterator iterator() {
            return new IteratorProxy(this.set.iterator());
        }

        @Override
        public boolean remove(Object o) {
            AbstractPersistentCollection.this.write();
            return this.set.remove(o);
        }

        @Override
        public boolean removeAll(Collection c) {
            AbstractPersistentCollection.this.write();
            return this.set.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection c) {
            AbstractPersistentCollection.this.write();
            return this.set.retainAll(c);
        }

        @Override
        public int size() {
            return this.set.size();
        }

        @Override
        public Object[] toArray() {
            return this.set.toArray();
        }

        @Override
        public Object[] toArray(Object[] array) {
            return this.set.toArray(array);
        }
    }

    protected final class ListIteratorProxy
    implements ListIterator {
        protected final ListIterator itr;

        public ListIteratorProxy(ListIterator itr) {
            this.itr = itr;
        }

        public void add(Object o) {
            AbstractPersistentCollection.this.write();
            this.itr.add(o);
        }

        @Override
        public boolean hasNext() {
            return this.itr.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return this.itr.hasPrevious();
        }

        @Override
        public Object next() {
            return this.itr.next();
        }

        @Override
        public int nextIndex() {
            return this.itr.nextIndex();
        }

        public Object previous() {
            return this.itr.previous();
        }

        @Override
        public int previousIndex() {
            return this.itr.previousIndex();
        }

        @Override
        public void remove() {
            AbstractPersistentCollection.this.write();
            this.itr.remove();
        }

        public void set(Object o) {
            AbstractPersistentCollection.this.write();
            this.itr.set(o);
        }
    }

    protected final class IteratorProxy
    implements Iterator {
        protected final Iterator itr;

        public IteratorProxy(Iterator itr) {
            this.itr = itr;
        }

        @Override
        public boolean hasNext() {
            return this.itr.hasNext();
        }

        public Object next() {
            return this.itr.next();
        }

        @Override
        public void remove() {
            AbstractPersistentCollection.this.write();
            this.itr.remove();
        }
    }

    public static interface LazyInitializationWork<T> {
        public T doWork();
    }
}

