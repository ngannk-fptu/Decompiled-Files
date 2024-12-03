/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.action.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.hibernate.PropertyValueException;
import org.hibernate.TransientPropertyValueException;
import org.hibernate.action.internal.AbstractEntityInsertAction;
import org.hibernate.engine.internal.NonNullableTransientDependencies;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.collections.IdentitySet;
import org.hibernate.pretty.MessageHelper;
import org.jboss.logging.Logger;

public class UnresolvedEntityInsertActions {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)UnresolvedEntityInsertActions.class.getName());
    private static final int INIT_SIZE = 5;
    private final Map<AbstractEntityInsertAction, NonNullableTransientDependencies> dependenciesByAction = new IdentityHashMap<AbstractEntityInsertAction, NonNullableTransientDependencies>(5);
    private final Map<Object, Set<AbstractEntityInsertAction>> dependentActionsByTransientEntity = new IdentityHashMap<Object, Set<AbstractEntityInsertAction>>(5);

    public void addUnresolvedEntityInsertAction(AbstractEntityInsertAction insert, NonNullableTransientDependencies dependencies) {
        if (dependencies == null || dependencies.isEmpty()) {
            throw new IllegalArgumentException("Attempt to add an unresolved insert action that has no non-nullable transient entities.");
        }
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Adding insert with non-nullable, transient entities; insert=[{0}], dependencies=[{1}]", insert, dependencies.toLoggableString(insert.getSession()));
        }
        this.dependenciesByAction.put(insert, dependencies);
        this.addDependenciesByTransientEntity(insert, dependencies);
    }

    public Iterable<AbstractEntityInsertAction> getDependentEntityInsertActions() {
        return this.dependenciesByAction.keySet();
    }

    public void checkNoUnresolvedActionsAfterOperation() throws PropertyValueException {
        if (!this.isEmpty()) {
            AbstractEntityInsertAction firstDependentAction = this.dependenciesByAction.keySet().iterator().next();
            this.logCannotResolveNonNullableTransientDependencies(firstDependentAction.getSession());
            NonNullableTransientDependencies nonNullableTransientDependencies = this.dependenciesByAction.get(firstDependentAction);
            Object firstTransientDependency = nonNullableTransientDependencies.getNonNullableTransientEntities().iterator().next();
            String firstPropertyPath = nonNullableTransientDependencies.getNonNullableTransientPropertyPaths(firstTransientDependency).iterator().next();
            throw new TransientPropertyValueException("Not-null property references a transient value - transient instance must be saved before current operation", firstDependentAction.getSession().guessEntityName(firstTransientDependency), firstDependentAction.getEntityName(), firstPropertyPath);
        }
        LOG.trace("No entity insert actions have non-nullable, transient entity dependencies.");
    }

    private void logCannotResolveNonNullableTransientDependencies(SharedSessionContractImplementor session) {
        for (Map.Entry<Object, Set<AbstractEntityInsertAction>> entry : this.dependentActionsByTransientEntity.entrySet()) {
            Object transientEntity = entry.getKey();
            String transientEntityName = session.guessEntityName(transientEntity);
            Serializable transientEntityId = session.getFactory().getMetamodel().entityPersister(transientEntityName).getIdentifier(transientEntity, session);
            String transientEntityString = MessageHelper.infoString(transientEntityName, transientEntityId);
            TreeSet<String> dependentEntityStrings = new TreeSet<String>();
            TreeSet<String> nonNullableTransientPropertyPaths = new TreeSet<String>();
            for (AbstractEntityInsertAction dependentAction : entry.getValue()) {
                dependentEntityStrings.add(MessageHelper.infoString(dependentAction.getEntityName(), dependentAction.getId()));
                for (String path : this.dependenciesByAction.get(dependentAction).getNonNullableTransientPropertyPaths(transientEntity)) {
                    String fullPath = dependentAction.getEntityName() + '.' + path;
                    nonNullableTransientPropertyPaths.add(fullPath);
                }
            }
            LOG.cannotResolveNonNullableTransientDependencies(transientEntityString, dependentEntityStrings, nonNullableTransientPropertyPaths);
        }
    }

    public boolean isEmpty() {
        return this.dependenciesByAction.isEmpty();
    }

    private void addDependenciesByTransientEntity(AbstractEntityInsertAction insert, NonNullableTransientDependencies dependencies) {
        for (Object transientEntity : dependencies.getNonNullableTransientEntities()) {
            IdentitySet dependentActions = this.dependentActionsByTransientEntity.get(transientEntity);
            if (dependentActions == null) {
                dependentActions = new IdentitySet();
                this.dependentActionsByTransientEntity.put(transientEntity, dependentActions);
            }
            dependentActions.add((AbstractEntityInsertAction)insert);
        }
    }

    public Set<AbstractEntityInsertAction> resolveDependentActions(Object managedEntity, SessionImplementor session) {
        EntityEntry entityEntry = session.getPersistenceContextInternal().getEntry(managedEntity);
        if (entityEntry.getStatus() != Status.MANAGED && entityEntry.getStatus() != Status.READ_ONLY) {
            throw new IllegalArgumentException("EntityEntry did not have status MANAGED or READ_ONLY: " + entityEntry);
        }
        boolean traceEnabled = LOG.isTraceEnabled();
        Set<AbstractEntityInsertAction> dependentActions = this.dependentActionsByTransientEntity.remove(managedEntity);
        if (dependentActions == null) {
            if (traceEnabled) {
                LOG.tracev("No unresolved entity inserts that depended on [{0}]", MessageHelper.infoString(entityEntry.getEntityName(), entityEntry.getId()));
            }
            return Collections.emptySet();
        }
        IdentitySet resolvedActions = new IdentitySet();
        if (traceEnabled) {
            LOG.tracev("Unresolved inserts before resolving [{0}]: [{1}]", MessageHelper.infoString(entityEntry.getEntityName(), entityEntry.getId()), this.toString());
        }
        for (AbstractEntityInsertAction dependentAction : dependentActions) {
            if (traceEnabled) {
                LOG.tracev("Resolving insert [{0}] dependency on [{1}]", MessageHelper.infoString(dependentAction.getEntityName(), dependentAction.getId()), MessageHelper.infoString(entityEntry.getEntityName(), entityEntry.getId()));
            }
            NonNullableTransientDependencies dependencies = this.dependenciesByAction.get(dependentAction);
            dependencies.resolveNonNullableTransientEntity(managedEntity);
            if (!dependencies.isEmpty()) continue;
            if (traceEnabled) {
                LOG.tracev("Resolving insert [{0}] (only depended on [{1}])", dependentAction, MessageHelper.infoString(entityEntry.getEntityName(), entityEntry.getId()));
            }
            this.dependenciesByAction.remove(dependentAction);
            resolvedActions.add(dependentAction);
        }
        if (traceEnabled) {
            LOG.tracev("Unresolved inserts after resolving [{0}]: [{1}]", MessageHelper.infoString(entityEntry.getEntityName(), entityEntry.getId()), this.toString());
        }
        return resolvedActions;
    }

    public void clear() {
        this.dependenciesByAction.clear();
        this.dependentActionsByTransientEntity.clear();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append('[');
        for (Map.Entry<AbstractEntityInsertAction, NonNullableTransientDependencies> entry : this.dependenciesByAction.entrySet()) {
            AbstractEntityInsertAction insert = entry.getKey();
            NonNullableTransientDependencies dependencies = entry.getValue();
            sb.append("[insert=").append(insert).append(" dependencies=[").append(dependencies.toLoggableString(insert.getSession())).append("]");
        }
        sb.append(']');
        return sb.toString();
    }

    public void serialize(ObjectOutputStream oos) throws IOException {
        int queueSize = this.dependenciesByAction.size();
        LOG.tracev("Starting serialization of [{0}] unresolved insert entries", queueSize);
        oos.writeInt(queueSize);
        for (AbstractEntityInsertAction unresolvedAction : this.dependenciesByAction.keySet()) {
            oos.writeObject(unresolvedAction);
        }
    }

    public static UnresolvedEntityInsertActions deserialize(ObjectInputStream ois, SessionImplementor session) throws IOException, ClassNotFoundException {
        UnresolvedEntityInsertActions rtn = new UnresolvedEntityInsertActions();
        int queueSize = ois.readInt();
        LOG.tracev("Starting deserialization of [{0}] unresolved insert entries", queueSize);
        for (int i = 0; i < queueSize; ++i) {
            AbstractEntityInsertAction unresolvedAction = (AbstractEntityInsertAction)ois.readObject();
            unresolvedAction.afterDeserialize(session);
            rtn.addUnresolvedEntityInsertAction(unresolvedAction, unresolvedAction.findNonNullableTransientEntities());
        }
        return rtn;
    }
}

