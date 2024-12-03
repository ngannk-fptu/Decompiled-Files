/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.core;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.core.AbstractOperationContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.pages.PageUpdateTrigger;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

@ParametersAreNonnullByDefault
public class DefaultSaveContext
extends AbstractOperationContext<PageUpdateTrigger>
implements SaveContext {
    public static final SaveContext MINOR_EDIT = ((Builder)DefaultSaveContext.builder().suppressNotifications(true)).build();
    public static final SaveContext SUPPRESS_NOTIFICATIONS = ((Builder)DefaultSaveContext.builder().suppressNotifications(true)).updateLastModifier(true).build();
    public static final SaveContext REFACTORING = ((Builder)((Builder)DefaultSaveContext.builder().suppressNotifications(true)).updateTrigger(PageUpdateTrigger.LINK_REFACTORING)).build();
    public static final SaveContext BULK_OPERATION = ((Builder)((Builder)DefaultSaveContext.builder().suppressNotifications(true)).suppressAutowatch(true).updateLastModifier(true).updateTrigger(PageUpdateTrigger.LINK_REFACTORING)).build();
    public static final SaveContext DEFAULT = DefaultSaveContext.builder().updateLastModifier(true).build();
    public static final SaveContext DRAFT = ((Builder)((Builder)DefaultSaveContext.builder().suppressNotifications(true)).suppressEvents(true)).suppressAutowatch(true).updateLastModifier(true).build();
    public static final SaveContext RAW_DRAFT = ((Builder)((Builder)DefaultSaveContext.builder().suppressNotifications(true)).suppressEvents(true)).suppressAutowatch(true).updateLastModifier(false).build();
    public static final SaveContext DRAFT_REFACTORING = ((Builder)((Builder)DefaultSaveContext.builder().suppressNotifications(true)).updateLastModifier(false).updateTrigger(PageUpdateTrigger.LINK_REFACTORING)).build();
    public static final SaveContext REVERT = ((Builder)DefaultSaveContext.builder().updateLastModifier(true).updateTrigger(PageUpdateTrigger.REVERT)).build();
    private final boolean suppressAutowatch;
    private boolean updateLastModifier;

    @Deprecated
    public DefaultSaveContext(boolean suppressNotifications, boolean updateLastModifier, boolean suppressEvents) {
        this((Builder)((Builder)DefaultSaveContext.builder().suppressNotifications(suppressNotifications)).updateLastModifier(updateLastModifier).suppressEvents(suppressEvents));
    }

    @Deprecated
    public DefaultSaveContext(boolean suppressNotifications, boolean updateLastModifier, boolean suppressEvents, PageUpdateTrigger updateTrigger) {
        this((Builder)((Builder)((Builder)DefaultSaveContext.builder().suppressNotifications(suppressNotifications)).updateLastModifier(updateLastModifier).suppressEvents(suppressEvents)).updateTrigger(updateTrigger));
    }

    private DefaultSaveContext(Builder builder) {
        super(builder);
        this.suppressAutowatch = builder.suppressAutowatch;
        this.updateLastModifier = builder.updateLastModifier;
    }

    @Override
    public boolean isSuppressAutowatch() {
        return this.suppressAutowatch;
    }

    @Override
    @Deprecated
    public void setSuppressNotifications(boolean suppressNotifications) {
        this.suppressNotifications = suppressNotifications;
    }

    @Override
    public boolean doUpdateLastModifier() {
        return this.updateLastModifier;
    }

    @Override
    @Deprecated
    public void setUpdateLastModifier(boolean updateLastModifier) {
        this.updateLastModifier = updateLastModifier;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DefaultSaveContext that = (DefaultSaveContext)o;
        return this.isSuppressNotifications() == that.isSuppressNotifications() && this.suppressAutowatch == that.suppressAutowatch && this.updateLastModifier == that.updateLastModifier && this.isEventSuppressed() == that.isEventSuppressed() && Objects.equals(this.getUpdateTrigger(), that.getUpdateTrigger());
    }

    public int hashCode() {
        return Objects.hash(this.isSuppressAutowatch(), this.suppressAutowatch, this.updateLastModifier, this.isEventSuppressed(), this.getUpdateTrigger());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    extends AbstractOperationContext.BaseBuilder<Builder, PageUpdateTrigger> {
        private boolean suppressAutowatch;
        private boolean updateLastModifier;

        public Builder suppressAutowatch(boolean suppressAutowatch) {
            this.suppressAutowatch = suppressAutowatch;
            return this;
        }

        public Builder updateLastModifier(boolean updateLastModifier) {
            this.updateLastModifier = updateLastModifier;
            return this;
        }

        @Override
        protected Builder builder() {
            return this;
        }

        public DefaultSaveContext build() {
            return new DefaultSaveContext(this);
        }

        @Override
        protected @NonNull PageUpdateTrigger unknownTrigger() {
            return PageUpdateTrigger.UNKNOWN;
        }
    }
}

