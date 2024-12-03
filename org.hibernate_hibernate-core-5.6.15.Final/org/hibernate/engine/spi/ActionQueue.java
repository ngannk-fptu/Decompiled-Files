/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.PropertyValueException;
import org.hibernate.action.internal.AbstractEntityInsertAction;
import org.hibernate.action.internal.BulkOperationCleanupAction;
import org.hibernate.action.internal.CollectionRecreateAction;
import org.hibernate.action.internal.CollectionRemoveAction;
import org.hibernate.action.internal.CollectionUpdateAction;
import org.hibernate.action.internal.EntityActionVetoException;
import org.hibernate.action.internal.EntityDeleteAction;
import org.hibernate.action.internal.EntityIdentityInsertAction;
import org.hibernate.action.internal.EntityInsertAction;
import org.hibernate.action.internal.EntityUpdateAction;
import org.hibernate.action.internal.OrphanRemovalAction;
import org.hibernate.action.internal.QueuedOperationCollectionAction;
import org.hibernate.action.internal.UnresolvedEntityInsertActions;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.action.spi.Executable;
import org.hibernate.cache.CacheException;
import org.hibernate.engine.internal.NonNullableTransientDependencies;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.ExecutableList;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.EntityType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.OneToOneType;
import org.hibernate.type.Type;

public class ActionQueue {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ActionQueue.class);
    private SessionImplementor session;
    private UnresolvedEntityInsertActions unresolvedInsertions;
    private ExecutableList<AbstractEntityInsertAction> insertions;
    private ExecutableList<EntityDeleteAction> deletions;
    private ExecutableList<EntityUpdateAction> updates;
    private ExecutableList<CollectionRecreateAction> collectionCreations;
    private ExecutableList<CollectionUpdateAction> collectionUpdates;
    private ExecutableList<QueuedOperationCollectionAction> collectionQueuedOps;
    private ExecutableList<CollectionRemoveAction> collectionRemovals;
    private ExecutableList<OrphanRemovalAction> orphanRemovals;
    private transient boolean isTransactionCoordinatorShared;
    private AfterTransactionCompletionProcessQueue afterTransactionProcesses;
    private BeforeTransactionCompletionProcessQueue beforeTransactionProcesses;
    private static final LinkedHashMap<Class<? extends Executable>, ListProvider> EXECUTABLE_LISTS_MAP = new LinkedHashMap(8);

    public ActionQueue(SessionImplementor session) {
        this.session = session;
        this.isTransactionCoordinatorShared = false;
    }

    public void clear() {
        EXECUTABLE_LISTS_MAP.forEach((k, listProvider) -> {
            ExecutableList l = listProvider.get(this);
            if (l != null) {
                l.clear();
            }
        });
        if (this.unresolvedInsertions != null) {
            this.unresolvedInsertions.clear();
        }
    }

    public void addAction(EntityInsertAction action) {
        LOG.tracev("Adding an EntityInsertAction for [{0}] object", action.getEntityName());
        this.addInsertAction(action);
    }

    private void addInsertAction(AbstractEntityInsertAction insert) {
        NonNullableTransientDependencies nonNullableTransientDependencies;
        if (insert.isEarlyInsert()) {
            LOG.tracev("Executing inserts before finding non-nullable transient entities for early insert: [{0}]", insert);
            this.executeInserts();
        }
        if ((nonNullableTransientDependencies = insert.findNonNullableTransientEntities()) == null) {
            LOG.tracev("Adding insert with no non-nullable, transient entities: [{0}]", insert);
            this.addResolvedEntityInsertAction(insert);
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.tracev("Adding insert with non-nullable, transient entities; insert=[{0}], dependencies=[{1}]", insert, nonNullableTransientDependencies.toLoggableString(insert.getSession()));
            }
            if (this.unresolvedInsertions == null) {
                this.unresolvedInsertions = new UnresolvedEntityInsertActions();
            }
            this.unresolvedInsertions.addUnresolvedEntityInsertAction(insert, nonNullableTransientDependencies);
        }
    }

    private void addResolvedEntityInsertAction(AbstractEntityInsertAction insert) {
        if (insert.isEarlyInsert()) {
            LOG.trace("Executing insertions before resolved early-insert");
            this.executeInserts();
            LOG.debug("Executing identity-insert immediately");
            this.execute(insert);
        } else {
            LOG.trace("Adding resolved non-early insert action.");
            this.addAction(AbstractEntityInsertAction.class, insert);
        }
        if (!insert.isVeto()) {
            insert.makeEntityManaged();
            if (this.unresolvedInsertions != null) {
                for (AbstractEntityInsertAction resolvedAction : this.unresolvedInsertions.resolveDependentActions(insert.getInstance(), this.session)) {
                    this.addResolvedEntityInsertAction(resolvedAction);
                }
            }
        } else {
            throw new EntityActionVetoException("The EntityInsertAction was vetoed.", insert);
        }
    }

    private <T extends Executable & Comparable> void addAction(Class<T> executableClass, T action) {
        EXECUTABLE_LISTS_MAP.get(executableClass).getOrInit(this).add(action);
    }

    public void addAction(EntityIdentityInsertAction action) {
        LOG.tracev("Adding an EntityIdentityInsertAction for [{0}] object", action.getEntityName());
        this.addInsertAction(action);
    }

    public void addAction(EntityDeleteAction action) {
        this.addAction(EntityDeleteAction.class, action);
    }

    public void addAction(OrphanRemovalAction action) {
        this.addAction(OrphanRemovalAction.class, action);
    }

    public void addAction(EntityUpdateAction action) {
        this.addAction(EntityUpdateAction.class, action);
    }

    public void addAction(CollectionRecreateAction action) {
        this.addAction(CollectionRecreateAction.class, action);
    }

    public void addAction(CollectionRemoveAction action) {
        this.addAction(CollectionRemoveAction.class, action);
    }

    public void addAction(CollectionUpdateAction action) {
        this.addAction(CollectionUpdateAction.class, action);
    }

    public void addAction(QueuedOperationCollectionAction action) {
        this.addAction(QueuedOperationCollectionAction.class, action);
    }

    public void addAction(BulkOperationCleanupAction action) {
        this.registerCleanupActions(action);
    }

    private void registerCleanupActions(Executable executable) {
        if (executable.getBeforeTransactionCompletionProcess() != null) {
            if (this.beforeTransactionProcesses == null) {
                this.beforeTransactionProcesses = new BeforeTransactionCompletionProcessQueue(this.session);
            }
            this.beforeTransactionProcesses.register(executable.getBeforeTransactionCompletionProcess());
        }
        if (this.session.getFactory().getSessionFactoryOptions().isQueryCacheEnabled()) {
            this.invalidateSpaces(ActionQueue.convertTimestampSpaces(executable.getPropertySpaces()));
        }
        if (executable.getAfterTransactionCompletionProcess() != null) {
            if (this.afterTransactionProcesses == null) {
                this.afterTransactionProcesses = new AfterTransactionCompletionProcessQueue(this.session);
            }
            this.afterTransactionProcesses.register(executable.getAfterTransactionCompletionProcess());
        }
    }

    private static String[] convertTimestampSpaces(Serializable[] spaces) {
        return (String[])spaces;
    }

    public boolean hasUnresolvedEntityInsertActions() {
        return this.unresolvedInsertions != null && !this.unresolvedInsertions.isEmpty();
    }

    public void checkNoUnresolvedActionsAfterOperation() throws PropertyValueException {
        if (this.unresolvedInsertions != null) {
            this.unresolvedInsertions.checkNoUnresolvedActionsAfterOperation();
        }
    }

    public void registerProcess(AfterTransactionCompletionProcess process) {
        if (this.afterTransactionProcesses == null) {
            this.afterTransactionProcesses = new AfterTransactionCompletionProcessQueue(this.session);
        }
        this.afterTransactionProcesses.register(process);
    }

    public void registerProcess(BeforeTransactionCompletionProcess process) {
        if (this.beforeTransactionProcesses == null) {
            this.beforeTransactionProcesses = new BeforeTransactionCompletionProcessQueue(this.session);
        }
        this.beforeTransactionProcesses.register(process);
    }

    public void executeInserts() throws HibernateException {
        if (this.insertions != null && !this.insertions.isEmpty()) {
            this.executeActions(this.insertions);
        }
    }

    public void executeActions() throws HibernateException {
        if (this.hasUnresolvedEntityInsertActions()) {
            throw new IllegalStateException("About to execute actions, but there are unresolved entity insert actions.");
        }
        EXECUTABLE_LISTS_MAP.forEach((k, listProvider) -> {
            ExecutableList l = listProvider.get(this);
            if (l != null && !l.isEmpty()) {
                this.executeActions(l);
            }
        });
    }

    public void prepareActions() throws HibernateException {
        this.prepareActions(this.collectionRemovals);
        this.prepareActions(this.collectionUpdates);
        this.prepareActions(this.collectionCreations);
        this.prepareActions(this.collectionQueuedOps);
    }

    private void prepareActions(ExecutableList<?> queue) throws HibernateException {
        if (queue == null) {
            return;
        }
        for (Executable executable : queue) {
            executable.beforeExecutions();
        }
    }

    public void afterTransactionCompletion(boolean success) {
        if (!this.isTransactionCoordinatorShared && this.afterTransactionProcesses != null) {
            this.afterTransactionProcesses.afterTransactionCompletion(success);
        }
    }

    public void beforeTransactionCompletion() {
        if (!this.isTransactionCoordinatorShared && this.beforeTransactionProcesses != null) {
            this.beforeTransactionProcesses.beforeTransactionCompletion();
        }
    }

    public boolean areInsertionsOrDeletionsQueued() {
        return this.insertions != null && !this.insertions.isEmpty() || this.hasUnresolvedEntityInsertActions() || this.deletions != null && !this.deletions.isEmpty() || this.orphanRemovals != null && !this.orphanRemovals.isEmpty();
    }

    public boolean areTablesToBeUpdated(Set tables) {
        if (tables.isEmpty()) {
            return false;
        }
        for (ListProvider listProvider : EXECUTABLE_LISTS_MAP.values()) {
            ExecutableList l = listProvider.get(this);
            if (!ActionQueue.areTablesToBeUpdated(l, tables)) continue;
            return true;
        }
        if (this.unresolvedInsertions == null) {
            return false;
        }
        return ActionQueue.areTablesToBeUpdated(this.unresolvedInsertions, tables);
    }

    private static boolean areTablesToBeUpdated(ExecutableList<?> actions, Set tableSpaces) {
        if (actions == null || actions.isEmpty()) {
            return false;
        }
        for (Serializable actionSpace : actions.getQuerySpaces()) {
            if (!tableSpaces.contains(actionSpace)) continue;
            LOG.debugf("Changes must be flushed to space: %s", actionSpace);
            return true;
        }
        return false;
    }

    private static boolean areTablesToBeUpdated(UnresolvedEntityInsertActions actions, Set tableSpaces) {
        for (Executable executable : actions.getDependentEntityInsertActions()) {
            Serializable[] spaces;
            for (Serializable space : spaces = executable.getPropertySpaces()) {
                if (!tableSpaces.contains(space)) continue;
                LOG.debugf("Changes must be flushed to space: %s", space);
                return true;
            }
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private <E extends Executable & Comparable<?>> void executeActions(ExecutableList<E> list) throws HibernateException {
        try {
            for (Executable e : list) {
                try {
                    e.execute();
                }
                finally {
                    if (e.getBeforeTransactionCompletionProcess() != null) {
                        if (this.beforeTransactionProcesses == null) {
                            this.beforeTransactionProcesses = new BeforeTransactionCompletionProcessQueue(this.session);
                        }
                        this.beforeTransactionProcesses.register(e.getBeforeTransactionCompletionProcess());
                    }
                    if (e.getAfterTransactionCompletionProcess() == null) continue;
                    if (this.afterTransactionProcesses == null) {
                        this.afterTransactionProcesses = new AfterTransactionCompletionProcessQueue(this.session);
                    }
                    this.afterTransactionProcesses.register(e.getAfterTransactionCompletionProcess());
                }
            }
        }
        finally {
            if (this.session.getFactory().getSessionFactoryOptions().isQueryCacheEnabled()) {
                Set<Serializable> propertySpaces = list.getQuerySpaces();
                this.invalidateSpaces(ActionQueue.convertTimestampSpaces(propertySpaces));
            }
        }
        list.clear();
        this.session.getJdbcCoordinator().executeBatch();
    }

    private static String[] convertTimestampSpaces(Set spaces) {
        return spaces.toArray(new String[spaces.size()]);
    }

    public <E extends Executable & Comparable<?>> void execute(E executable) {
        try {
            executable.execute();
        }
        finally {
            this.registerCleanupActions(executable);
        }
    }

    private void invalidateSpaces(String ... spaces) {
        if (spaces != null && spaces.length > 0) {
            for (String s : spaces) {
                if (this.afterTransactionProcesses == null) {
                    this.afterTransactionProcesses = new AfterTransactionCompletionProcessQueue(this.session);
                }
                this.afterTransactionProcesses.addSpaceToInvalidate(s);
            }
            this.session.getFactory().getCache().getTimestampsCache().preInvalidate(spaces, (SharedSessionContractImplementor)this.session);
        }
    }

    public String toString() {
        return "ActionQueue[insertions=" + ActionQueue.toString(this.insertions) + " updates=" + ActionQueue.toString(this.updates) + " deletions=" + ActionQueue.toString(this.deletions) + " orphanRemovals=" + ActionQueue.toString(this.orphanRemovals) + " collectionCreations=" + ActionQueue.toString(this.collectionCreations) + " collectionRemovals=" + ActionQueue.toString(this.collectionRemovals) + " collectionUpdates=" + ActionQueue.toString(this.collectionUpdates) + " collectionQueuedOps=" + ActionQueue.toString(this.collectionQueuedOps) + " unresolvedInsertDependencies=" + this.unresolvedInsertions + "]";
    }

    private static String toString(ExecutableList q) {
        return q == null ? "ExecutableList{size=0}" : q.toString();
    }

    public int numberOfCollectionRemovals() {
        if (this.collectionRemovals == null) {
            return 0;
        }
        return this.collectionRemovals.size();
    }

    public int numberOfCollectionUpdates() {
        if (this.collectionUpdates == null) {
            return 0;
        }
        return this.collectionUpdates.size();
    }

    public int numberOfCollectionCreations() {
        if (this.collectionCreations == null) {
            return 0;
        }
        return this.collectionCreations.size();
    }

    public int numberOfDeletions() {
        int del = this.deletions == null ? 0 : this.deletions.size();
        int orph = this.orphanRemovals == null ? 0 : this.orphanRemovals.size();
        return del + orph;
    }

    public int numberOfUpdates() {
        if (this.updates == null) {
            return 0;
        }
        return this.updates.size();
    }

    public int numberOfInsertions() {
        if (this.insertions == null) {
            return 0;
        }
        return this.insertions.size();
    }

    public TransactionCompletionProcesses getTransactionCompletionProcesses() {
        if (this.beforeTransactionProcesses == null) {
            this.beforeTransactionProcesses = new BeforeTransactionCompletionProcessQueue(this.session);
        }
        if (this.afterTransactionProcesses == null) {
            this.afterTransactionProcesses = new AfterTransactionCompletionProcessQueue(this.session);
        }
        return new TransactionCompletionProcesses(this.beforeTransactionProcesses, this.afterTransactionProcesses);
    }

    public void setTransactionCompletionProcesses(TransactionCompletionProcesses processes, boolean isTransactionCoordinatorShared) {
        this.isTransactionCoordinatorShared = isTransactionCoordinatorShared;
        this.beforeTransactionProcesses = processes.beforeTransactionCompletionProcesses;
        this.afterTransactionProcesses = processes.afterTransactionCompletionProcesses;
    }

    public void sortCollectionActions() {
        if (this.isOrderUpdatesEnabled()) {
            if (this.collectionCreations != null) {
                this.collectionCreations.sort();
            }
            if (this.collectionUpdates != null) {
                this.collectionUpdates.sort();
            }
            if (this.collectionQueuedOps != null) {
                this.collectionQueuedOps.sort();
            }
            if (this.collectionRemovals != null) {
                this.collectionRemovals.sort();
            }
        }
    }

    public void sortActions() {
        if (this.isOrderUpdatesEnabled() && this.updates != null) {
            this.updates.sort();
        }
        if (this.isOrderInsertsEnabled() && this.insertions != null) {
            this.insertions.sort();
        }
    }

    private boolean isOrderUpdatesEnabled() {
        return this.session.getFactory().getSessionFactoryOptions().isOrderUpdatesEnabled();
    }

    private boolean isOrderInsertsEnabled() {
        return this.session.getFactory().getSessionFactoryOptions().isOrderInsertsEnabled();
    }

    public void clearFromFlushNeededCheck(int previousCollectionRemovalSize) {
        if (this.collectionCreations != null) {
            this.collectionCreations.clear();
        }
        if (this.collectionUpdates != null) {
            this.collectionUpdates.clear();
        }
        if (this.collectionQueuedOps != null) {
            this.collectionQueuedOps.clear();
        }
        if (this.updates != null) {
            this.updates.clear();
        }
        if (this.collectionRemovals != null && this.collectionRemovals.size() > previousCollectionRemovalSize) {
            this.collectionRemovals.removeLastN(this.collectionRemovals.size() - previousCollectionRemovalSize);
        }
    }

    public boolean hasAfterTransactionActions() {
        return this.isTransactionCoordinatorShared ? false : this.afterTransactionProcesses != null && this.afterTransactionProcesses.hasActions();
    }

    public boolean hasBeforeTransactionActions() {
        return this.isTransactionCoordinatorShared ? false : this.beforeTransactionProcesses != null && this.beforeTransactionProcesses.hasActions();
    }

    public boolean hasAnyQueuedActions() {
        return this.updates != null && !this.updates.isEmpty() || this.insertions != null && !this.insertions.isEmpty() || this.hasUnresolvedEntityInsertActions() || this.deletions != null && !this.deletions.isEmpty() || this.collectionUpdates != null && !this.collectionUpdates.isEmpty() || this.collectionQueuedOps != null && !this.collectionQueuedOps.isEmpty() || this.collectionRemovals != null && !this.collectionRemovals.isEmpty() || this.collectionCreations != null && !this.collectionCreations.isEmpty();
    }

    public void unScheduleDeletion(EntityEntry entry, Object rescuedEntity) {
        EntityDeleteAction action;
        LazyInitializer initializer;
        if (rescuedEntity instanceof HibernateProxy && !(initializer = ((HibernateProxy)rescuedEntity).getHibernateLazyInitializer()).isUninitialized()) {
            rescuedEntity = initializer.getImplementation(this.session);
        }
        if (this.deletions != null) {
            for (int i = 0; i < this.deletions.size(); ++i) {
                action = this.deletions.get(i);
                if (action.getInstance() != rescuedEntity) continue;
                this.deletions.remove(i);
                return;
            }
        }
        if (this.orphanRemovals != null) {
            for (int i = 0; i < this.orphanRemovals.size(); ++i) {
                action = this.orphanRemovals.get(i);
                if (action.getInstance() != rescuedEntity) continue;
                this.orphanRemovals.remove(i);
                return;
            }
        }
        throw new AssertionFailure("Unable to perform un-delete for instance " + entry.getEntityName());
    }

    public void serialize(ObjectOutputStream oos) throws IOException {
        LOG.trace("Serializing action-queue");
        if (this.unresolvedInsertions == null) {
            this.unresolvedInsertions = new UnresolvedEntityInsertActions();
        }
        this.unresolvedInsertions.serialize(oos);
        for (ListProvider p : EXECUTABLE_LISTS_MAP.values()) {
            ExecutableList l = p.get(this);
            if (l == null) {
                oos.writeBoolean(false);
                continue;
            }
            oos.writeBoolean(true);
            l.writeExternal(oos);
        }
    }

    public static ActionQueue deserialize(ObjectInputStream ois, SessionImplementor session) throws IOException, ClassNotFoundException {
        boolean traceEnabled = LOG.isTraceEnabled();
        if (traceEnabled) {
            LOG.trace("Deserializing action-queue");
        }
        ActionQueue rtn = new ActionQueue(session);
        rtn.unresolvedInsertions = UnresolvedEntityInsertActions.deserialize(ois, session);
        for (ListProvider provider : EXECUTABLE_LISTS_MAP.values()) {
            ExecutableList l = provider.get(rtn);
            boolean notNull = ois.readBoolean();
            if (!notNull) continue;
            if (l == null) {
                l = provider.init(rtn);
            }
            l.readExternal(ois);
            if (traceEnabled) {
                LOG.tracev("Deserialized [{0}] entries", l.size());
            }
            l.afterDeserialize(session);
        }
        return rtn;
    }

    static {
        EXECUTABLE_LISTS_MAP.put(OrphanRemovalAction.class, new ListProvider<OrphanRemovalAction>(){

            @Override
            ExecutableList<OrphanRemovalAction> get(ActionQueue instance) {
                return instance.orphanRemovals;
            }

            @Override
            ExecutableList<OrphanRemovalAction> init(ActionQueue instance) {
                return instance.orphanRemovals = new ExecutableList(false);
            }
        });
        EXECUTABLE_LISTS_MAP.put(AbstractEntityInsertAction.class, new ListProvider<AbstractEntityInsertAction>(){

            @Override
            ExecutableList<AbstractEntityInsertAction> get(ActionQueue instance) {
                return instance.insertions;
            }

            @Override
            ExecutableList<AbstractEntityInsertAction> init(ActionQueue instance) {
                if (instance.isOrderInsertsEnabled()) {
                    return instance.insertions = new ExecutableList<AbstractEntityInsertAction>(new InsertActionSorter());
                }
                return instance.insertions = new ExecutableList(false);
            }
        });
        EXECUTABLE_LISTS_MAP.put(EntityUpdateAction.class, new ListProvider<EntityUpdateAction>(){

            @Override
            ExecutableList<EntityUpdateAction> get(ActionQueue instance) {
                return instance.updates;
            }

            @Override
            ExecutableList<EntityUpdateAction> init(ActionQueue instance) {
                return instance.updates = new ExecutableList(instance.isOrderUpdatesEnabled());
            }
        });
        EXECUTABLE_LISTS_MAP.put(QueuedOperationCollectionAction.class, new ListProvider<QueuedOperationCollectionAction>(){

            @Override
            ExecutableList<QueuedOperationCollectionAction> get(ActionQueue instance) {
                return instance.collectionQueuedOps;
            }

            @Override
            ExecutableList<QueuedOperationCollectionAction> init(ActionQueue instance) {
                return instance.collectionQueuedOps = new ExecutableList(instance.isOrderUpdatesEnabled());
            }
        });
        EXECUTABLE_LISTS_MAP.put(CollectionRemoveAction.class, new ListProvider<CollectionRemoveAction>(){

            @Override
            ExecutableList<CollectionRemoveAction> get(ActionQueue instance) {
                return instance.collectionRemovals;
            }

            @Override
            ExecutableList<CollectionRemoveAction> init(ActionQueue instance) {
                return instance.collectionRemovals = new ExecutableList(instance.isOrderUpdatesEnabled());
            }
        });
        EXECUTABLE_LISTS_MAP.put(CollectionUpdateAction.class, new ListProvider<CollectionUpdateAction>(){

            @Override
            ExecutableList<CollectionUpdateAction> get(ActionQueue instance) {
                return instance.collectionUpdates;
            }

            @Override
            ExecutableList<CollectionUpdateAction> init(ActionQueue instance) {
                return instance.collectionUpdates = new ExecutableList(instance.isOrderUpdatesEnabled());
            }
        });
        EXECUTABLE_LISTS_MAP.put(CollectionRecreateAction.class, new ListProvider<CollectionRecreateAction>(){

            @Override
            ExecutableList<CollectionRecreateAction> get(ActionQueue instance) {
                return instance.collectionCreations;
            }

            @Override
            ExecutableList<CollectionRecreateAction> init(ActionQueue instance) {
                return instance.collectionCreations = new ExecutableList(instance.isOrderUpdatesEnabled());
            }
        });
        EXECUTABLE_LISTS_MAP.put(EntityDeleteAction.class, new ListProvider<EntityDeleteAction>(){

            @Override
            ExecutableList<EntityDeleteAction> get(ActionQueue instance) {
                return instance.deletions;
            }

            @Override
            ExecutableList<EntityDeleteAction> init(ActionQueue instance) {
                return instance.deletions = new ExecutableList(false);
            }
        });
    }

    private static abstract class ListProvider<T extends Executable & Comparable> {
        private ListProvider() {
        }

        abstract ExecutableList<T> get(ActionQueue var1);

        abstract ExecutableList<T> init(ActionQueue var1);

        ExecutableList<T> getOrInit(ActionQueue instance) {
            ExecutableList<T> list = this.get(instance);
            if (list == null) {
                list = this.init(instance);
            }
            return list;
        }
    }

    private static class InsertActionSorter
    implements ExecutableList.Sorter<AbstractEntityInsertAction> {
        public static final InsertActionSorter INSTANCE = new InsertActionSorter();
        private Map<BatchIdentifier, List<AbstractEntityInsertAction>> actionBatches;

        @Override
        public void sort(List<AbstractEntityInsertAction> insertions) {
            this.actionBatches = new HashMap<BatchIdentifier, List<AbstractEntityInsertAction>>();
            ArrayList<BatchIdentifier> latestBatches = new ArrayList<BatchIdentifier>();
            for (AbstractEntityInsertAction action : insertions) {
                BatchIdentifier batchIdentifier = new BatchIdentifier(action.getEntityName(), action.getSession().getFactory().getMetamodel().entityPersister(action.getEntityName()).getRootEntityName());
                int index = latestBatches.indexOf(batchIdentifier);
                if (index != -1) {
                    batchIdentifier = (BatchIdentifier)latestBatches.get(index);
                } else {
                    latestBatches.add(batchIdentifier);
                }
                this.addParentChildEntityNames(action, batchIdentifier);
                this.addToBatch(batchIdentifier, action);
            }
            for (int i = 0; i < latestBatches.size(); ++i) {
                int j;
                BatchIdentifier batchIdentifier = (BatchIdentifier)latestBatches.get(i);
                for (j = i - 1; j >= 0; --j) {
                    BatchIdentifier prevBatchIdentifier = (BatchIdentifier)latestBatches.get(j);
                    if (prevBatchIdentifier.hasAnyParentEntityNames(batchIdentifier)) {
                        prevBatchIdentifier.parent = batchIdentifier;
                        continue;
                    }
                    if (!batchIdentifier.hasAnyChildEntityNames(prevBatchIdentifier)) continue;
                    prevBatchIdentifier.parent = batchIdentifier;
                }
                for (j = i + 1; j < latestBatches.size(); ++j) {
                    BatchIdentifier nextBatchIdentifier = (BatchIdentifier)latestBatches.get(j);
                    if (nextBatchIdentifier.hasAnyParentEntityNames(batchIdentifier)) {
                        nextBatchIdentifier.parent = batchIdentifier;
                        nextBatchIdentifier.getParentEntityNames().add(batchIdentifier.getEntityName());
                        continue;
                    }
                    if (!batchIdentifier.hasAnyChildEntityNames(nextBatchIdentifier)) continue;
                    nextBatchIdentifier.parent = batchIdentifier;
                    nextBatchIdentifier.getParentEntityNames().add(batchIdentifier.getEntityName());
                }
            }
            boolean sorted = false;
            long maxIterations = latestBatches.size() * latestBatches.size();
            long iterations = 0L;
            block4: do {
                ++iterations;
                for (int i = 0; i < latestBatches.size(); ++i) {
                    BatchIdentifier batchIdentifier = (BatchIdentifier)latestBatches.get(i);
                    for (int j = i + 1; j < latestBatches.size(); ++j) {
                        BatchIdentifier nextBatchIdentifier = (BatchIdentifier)latestBatches.get(j);
                        if (!batchIdentifier.hasParent(nextBatchIdentifier)) continue;
                        if (nextBatchIdentifier.hasParent(batchIdentifier)) break block4;
                        latestBatches.remove(batchIdentifier);
                        latestBatches.add(j, batchIdentifier);
                        continue block4;
                    }
                }
                sorted = true;
            } while (!sorted && iterations <= maxIterations);
            if (iterations > maxIterations) {
                LOG.warn("The batch containing " + latestBatches.size() + " statements could not be sorted after " + maxIterations + " iterations. This might indicate a circular entity relationship.");
            }
            if (sorted) {
                insertions.clear();
                for (BatchIdentifier rootIdentifier : latestBatches) {
                    List<AbstractEntityInsertAction> batch = this.actionBatches.get(rootIdentifier);
                    insertions.addAll(batch);
                }
            }
        }

        private void addParentChildEntityNames(AbstractEntityInsertAction action, BatchIdentifier batchIdentifier) {
            Object[] propertyValues = action.getState();
            ClassMetadata classMetadata = action.getPersister().getClassMetadata();
            if (classMetadata != null) {
                Type[] propertyTypes = classMetadata.getPropertyTypes();
                Type identifierType = classMetadata.getIdentifierType();
                for (int i = 0; i < propertyValues.length; ++i) {
                    Object value = propertyValues[i];
                    if (value == null) continue;
                    Type type = propertyTypes[i];
                    this.addParentChildEntityNameByPropertyAndValue(action, batchIdentifier, type, value);
                }
                if (identifierType.isComponentType()) {
                    Type[] compositeIdentifierTypes;
                    CompositeType compositeType = (CompositeType)identifierType;
                    for (Type type : compositeIdentifierTypes = compositeType.getSubtypes()) {
                        this.addParentChildEntityNameByPropertyAndValue(action, batchIdentifier, type, null);
                    }
                }
            }
        }

        private void addParentChildEntityNameByPropertyAndValue(AbstractEntityInsertAction action, BatchIdentifier batchIdentifier, Type type, Object value) {
            block8: {
                block9: {
                    block6: {
                        String valueClass;
                        String rootEntityName;
                        String entityName;
                        block7: {
                            if (!type.isEntityType()) break block6;
                            EntityType entityType = (EntityType)type;
                            entityName = entityType.getName();
                            rootEntityName = action.getSession().getFactory().getMetamodel().entityPersister(entityName).getRootEntityName();
                            if (!entityType.isOneToOne() || ((OneToOneType)OneToOneType.class.cast(entityType)).getForeignKeyDirection() != ForeignKeyDirection.TO_PARENT) break block7;
                            if (!entityType.isReferenceToPrimaryKey()) {
                                batchIdentifier.getChildEntityNames().add(entityName);
                            }
                            if (!rootEntityName.equals(entityName)) {
                                batchIdentifier.getChildEntityNames().add(rootEntityName);
                            }
                            break block8;
                        }
                        if (!batchIdentifier.getEntityName().equals(entityName)) {
                            batchIdentifier.getParentEntityNames().add(entityName);
                        }
                        if (value != null && !(valueClass = value.getClass().getName()).equals(entityName)) {
                            batchIdentifier.getParentEntityNames().add(valueClass);
                        }
                        if (rootEntityName.equals(entityName)) break block8;
                        batchIdentifier.getParentEntityNames().add(rootEntityName);
                        break block8;
                    }
                    if (!type.isCollectionType()) break block9;
                    CollectionType collectionType = (CollectionType)type;
                    SessionFactoryImplementor sessionFactory = ((SessionImplementor)action.getSession()).getSessionFactory();
                    if (!collectionType.getElementType(sessionFactory).isEntityType() || sessionFactory.getMetamodel().collectionPersister(collectionType.getRole()).isManyToMany()) break block8;
                    String entityName = collectionType.getAssociatedEntityName(sessionFactory);
                    String rootEntityName = action.getSession().getFactory().getMetamodel().entityPersister(entityName).getRootEntityName();
                    batchIdentifier.getChildEntityNames().add(entityName);
                    if (rootEntityName.equals(entityName)) break block8;
                    batchIdentifier.getChildEntityNames().add(rootEntityName);
                    break block8;
                }
                if (type.isComponentType() && value != null) {
                    CompositeType compositeType = (CompositeType)type;
                    SharedSessionContractImplementor session = action.getSession();
                    Object[] componentValues = compositeType.getPropertyValues(value, session);
                    for (int j = 0; j < componentValues.length; ++j) {
                        Type componentValueType = compositeType.getSubtypes()[j];
                        Object componentValue = componentValues[j];
                        this.addParentChildEntityNameByPropertyAndValue(action, batchIdentifier, componentValueType, componentValue);
                    }
                }
            }
        }

        private void addToBatch(BatchIdentifier batchIdentifier, AbstractEntityInsertAction action) {
            List<AbstractEntityInsertAction> actions = this.actionBatches.get(batchIdentifier);
            if (actions == null) {
                actions = new LinkedList<AbstractEntityInsertAction>();
                this.actionBatches.put(batchIdentifier, actions);
            }
            actions.add(action);
        }

        private static class BatchIdentifier {
            private final String entityName;
            private final String rootEntityName;
            private Set<String> parentEntityNames = new HashSet<String>();
            private Set<String> childEntityNames = new HashSet<String>();
            private BatchIdentifier parent;

            BatchIdentifier(String entityName, String rootEntityName) {
                this.entityName = entityName;
                this.rootEntityName = rootEntityName;
            }

            public BatchIdentifier getParent() {
                return this.parent;
            }

            public void setParent(BatchIdentifier parent) {
                this.parent = parent;
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof BatchIdentifier)) {
                    return false;
                }
                BatchIdentifier that = (BatchIdentifier)o;
                return Objects.equals(this.entityName, that.entityName);
            }

            public int hashCode() {
                return Objects.hash(this.entityName);
            }

            String getEntityName() {
                return this.entityName;
            }

            String getRootEntityName() {
                return this.rootEntityName;
            }

            Set<String> getParentEntityNames() {
                return this.parentEntityNames;
            }

            Set<String> getChildEntityNames() {
                return this.childEntityNames;
            }

            boolean hasAnyParentEntityNames(BatchIdentifier batchIdentifier) {
                return this.parentEntityNames.contains(batchIdentifier.getEntityName()) || this.parentEntityNames.contains(batchIdentifier.getRootEntityName());
            }

            boolean hasAnyChildEntityNames(BatchIdentifier batchIdentifier) {
                return this.childEntityNames.contains(batchIdentifier.getEntityName());
            }

            boolean hasParent(BatchIdentifier batchIdentifier) {
                return this.parent == batchIdentifier || this.parentEntityNames.contains(batchIdentifier.getEntityName()) || this.parentEntityNames.contains(batchIdentifier.getRootEntityName()) && !this.getEntityName().equals(batchIdentifier.getRootEntityName()) || this.parent != null && this.parent.hasParent(batchIdentifier, new ArrayList<BatchIdentifier>());
            }

            private boolean hasParent(BatchIdentifier batchIdentifier, List<BatchIdentifier> stack) {
                if (!stack.contains(this) && this.parent != null) {
                    stack.add(this);
                    return this.parent.hasParent(batchIdentifier, stack);
                }
                return this.parent == batchIdentifier || this.parentEntityNames.contains(batchIdentifier.getEntityName());
            }
        }
    }

    public static class TransactionCompletionProcesses {
        private final BeforeTransactionCompletionProcessQueue beforeTransactionCompletionProcesses;
        private final AfterTransactionCompletionProcessQueue afterTransactionCompletionProcesses;

        private TransactionCompletionProcesses(BeforeTransactionCompletionProcessQueue beforeTransactionCompletionProcessQueue, AfterTransactionCompletionProcessQueue afterTransactionCompletionProcessQueue) {
            this.beforeTransactionCompletionProcesses = beforeTransactionCompletionProcessQueue;
            this.afterTransactionCompletionProcesses = afterTransactionCompletionProcessQueue;
        }
    }

    private static class AfterTransactionCompletionProcessQueue
    extends AbstractTransactionCompletionProcessQueue<AfterTransactionCompletionProcess> {
        private Set<String> querySpacesToInvalidate = new HashSet<String>();

        private AfterTransactionCompletionProcessQueue(SessionImplementor session) {
            super(session);
        }

        public void addSpaceToInvalidate(String space) {
            this.querySpacesToInvalidate.add(space);
        }

        public void afterTransactionCompletion(boolean success) {
            while (!this.processes.isEmpty()) {
                try {
                    ((AfterTransactionCompletionProcess)this.processes.poll()).doAfterTransactionCompletion(success, this.session);
                }
                catch (CacheException ce) {
                    LOG.unableToReleaseCacheLock(ce);
                }
                catch (Exception e) {
                    throw new HibernateException("Unable to perform afterTransactionCompletion callback: " + e.getMessage(), e);
                }
            }
            if (this.session.getFactory().getSessionFactoryOptions().isQueryCacheEnabled()) {
                this.session.getFactory().getCache().getTimestampsCache().invalidate(this.querySpacesToInvalidate.toArray(new String[this.querySpacesToInvalidate.size()]), (SharedSessionContractImplementor)this.session);
            }
            this.querySpacesToInvalidate.clear();
        }
    }

    private static class BeforeTransactionCompletionProcessQueue
    extends AbstractTransactionCompletionProcessQueue<BeforeTransactionCompletionProcess> {
        private BeforeTransactionCompletionProcessQueue(SessionImplementor session) {
            super(session);
        }

        public void beforeTransactionCompletion() {
            while (!this.processes.isEmpty()) {
                try {
                    ((BeforeTransactionCompletionProcess)this.processes.poll()).doBeforeTransactionCompletion(this.session);
                }
                catch (HibernateException he) {
                    throw he;
                }
                catch (Exception e) {
                    throw new HibernateException("Unable to perform beforeTransactionCompletion callback: " + e.getMessage(), e);
                }
            }
        }
    }

    private static abstract class AbstractTransactionCompletionProcessQueue<T> {
        protected SessionImplementor session;
        protected Queue<T> processes = new ConcurrentLinkedQueue<T>();

        private AbstractTransactionCompletionProcessQueue(SessionImplementor session) {
            this.session = session;
        }

        public void register(T process) {
            if (process == null) {
                return;
            }
            this.processes.add(process);
        }

        public boolean hasActions() {
            return !this.processes.isEmpty();
        }
    }
}

