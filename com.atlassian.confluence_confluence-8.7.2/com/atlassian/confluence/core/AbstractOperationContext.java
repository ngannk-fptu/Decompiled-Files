/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.OperationContext;
import com.atlassian.confluence.core.OperationTrigger;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractOperationContext<TRIGGER extends OperationTrigger>
implements OperationContext<TRIGGER> {
    private final boolean eventSuppressed;
    protected boolean suppressNotifications;
    private final TRIGGER updateTrigger;

    protected AbstractOperationContext(BaseBuilder<?, TRIGGER> builder) {
        this.eventSuppressed = builder.eventSuppressed;
        this.suppressNotifications = builder.suppressNotifications;
        this.updateTrigger = builder.updateTrigger != null ? builder.updateTrigger : builder.unknownTrigger();
    }

    @Override
    public final boolean isEventSuppressed() {
        return this.eventSuppressed;
    }

    @Override
    public final boolean isSuppressNotifications() {
        return this.suppressNotifications;
    }

    @Override
    public final @NonNull TRIGGER getUpdateTrigger() {
        return this.updateTrigger;
    }

    public static abstract class BaseBuilder<T extends BaseBuilder, TRIGGER extends OperationTrigger> {
        private boolean eventSuppressed;
        private boolean suppressNotifications;
        private TRIGGER updateTrigger;

        protected abstract T builder();

        public T suppressNotifications(boolean suppressNotifications) {
            this.suppressNotifications = suppressNotifications;
            return this.builder();
        }

        public T suppressEvents(boolean eventSuppressed) {
            this.eventSuppressed = eventSuppressed;
            return this.builder();
        }

        public T updateTrigger(TRIGGER updateTrigger) {
            this.updateTrigger = updateTrigger;
            return this.builder();
        }

        protected abstract @NonNull TRIGGER unknownTrigger();
    }
}

