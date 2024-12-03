/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import org.hibernate.LockMode;
import org.hibernate.action.internal.EntityAction;
import org.hibernate.engine.internal.ForeignKeys;
import org.hibernate.engine.internal.NonNullableTransientDependencies;
import org.hibernate.engine.internal.Nullability;
import org.hibernate.engine.internal.Versioning;
import org.hibernate.engine.spi.CachedNaturalIdValueSource;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.persister.entity.EntityPersister;

public abstract class AbstractEntityInsertAction
extends EntityAction {
    private transient Object[] state;
    private final boolean isVersionIncrementDisabled;
    private boolean isExecuted;
    private boolean areTransientReferencesNullified;

    protected AbstractEntityInsertAction(Serializable id, Object[] state, Object instance, boolean isVersionIncrementDisabled, EntityPersister persister, SharedSessionContractImplementor session) {
        super(session, id, instance, persister);
        this.state = state;
        this.isVersionIncrementDisabled = isVersionIncrementDisabled;
        this.isExecuted = false;
        this.areTransientReferencesNullified = false;
        if (id != null) {
            this.handleNaturalIdPreSaveNotifications();
        }
    }

    public Object[] getState() {
        return this.state;
    }

    public abstract boolean isEarlyInsert();

    public NonNullableTransientDependencies findNonNullableTransientEntities() {
        return ForeignKeys.findNonNullableTransientEntities(this.getPersister().getEntityName(), this.getInstance(), this.getState(), this.isEarlyInsert(), this.getSession());
    }

    protected final void nullifyTransientReferencesIfNotAlready() {
        if (!this.areTransientReferencesNullified) {
            new ForeignKeys.Nullifier(this.getInstance(), false, this.isEarlyInsert(), this.getSession(), this.getPersister()).nullifyTransientReferences(this.getState());
            new Nullability(this.getSession()).checkNullability(this.getState(), this.getPersister(), false);
            this.areTransientReferencesNullified = true;
        }
    }

    public final void makeEntityManaged() {
        this.nullifyTransientReferencesIfNotAlready();
        Object version = Versioning.getVersion(this.getState(), this.getPersister());
        this.getSession().getPersistenceContextInternal().addEntity(this.getInstance(), this.getPersister().isMutable() ? Status.MANAGED : Status.READ_ONLY, this.getState(), this.getEntityKey(), version, LockMode.WRITE, this.isExecuted, this.getPersister(), this.isVersionIncrementDisabled);
    }

    protected void markExecuted() {
        this.isExecuted = true;
    }

    protected abstract EntityKey getEntityKey();

    @Override
    public void afterDeserialize(SharedSessionContractImplementor session) {
        super.afterDeserialize(session);
        if (session != null) {
            EntityEntry entityEntry = session.getPersistenceContextInternal().getEntry(this.getInstance());
            this.state = entityEntry.getLoadedState();
        }
    }

    protected void handleNaturalIdPreSaveNotifications() {
        this.getSession().getPersistenceContextInternal().getNaturalIdHelper().manageLocalNaturalIdCrossReference(this.getPersister(), this.getId(), this.state, null, CachedNaturalIdValueSource.INSERT);
    }

    public void handleNaturalIdPostSaveNotifications(Serializable generatedId) {
        PersistenceContext.NaturalIdHelper naturalIdHelper = this.getSession().getPersistenceContextInternal().getNaturalIdHelper();
        if (this.isEarlyInsert()) {
            naturalIdHelper.manageLocalNaturalIdCrossReference(this.getPersister(), generatedId, this.state, null, CachedNaturalIdValueSource.INSERT);
        }
        naturalIdHelper.manageSharedNaturalIdCrossReference(this.getPersister(), generatedId, this.state, null, CachedNaturalIdValueSource.INSERT);
    }
}

