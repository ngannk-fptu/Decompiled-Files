/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import org.hibernate.engine.spi.EntityEntryExtraState;

public class EntityEntryExtraStateHolder
implements EntityEntryExtraState {
    private EntityEntryExtraState next;
    private Object[] deletedState;

    public Object[] getDeletedState() {
        return this.deletedState;
    }

    public void setDeletedState(Object[] deletedState) {
        this.deletedState = deletedState;
    }

    @Override
    public void addExtraState(EntityEntryExtraState extraState) {
        if (this.next == null) {
            this.next = extraState;
        } else {
            this.next.addExtraState(extraState);
        }
    }

    @Override
    public <T extends EntityEntryExtraState> T getExtraState(Class<T> extraStateType) {
        if (this.next == null) {
            return null;
        }
        if (extraStateType.isAssignableFrom(this.next.getClass())) {
            return (T)this.next;
        }
        return this.next.getExtraState(extraStateType);
    }
}

