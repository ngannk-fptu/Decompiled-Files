/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.AbstractOperationContext;
import com.atlassian.confluence.core.DeleteContext;
import com.atlassian.confluence.pages.PageDeleteTrigger;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DefaultDeleteContext
extends AbstractOperationContext<PageDeleteTrigger>
implements DeleteContext {
    public static final DeleteContext SUPPRESS_NOTIFICATIONS = ((Builder)DefaultDeleteContext.builder().suppressNotifications(true)).build();
    public static final DeleteContext BULK_OPERATION = ((Builder)((Builder)DefaultDeleteContext.builder().suppressNotifications(true)).updateTrigger(PageDeleteTrigger.BULK_OPERATION)).build();
    public static final DeleteContext DEFAULT = DefaultDeleteContext.builder().build();

    private DefaultDeleteContext(Builder builder) {
        super(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    extends AbstractOperationContext.BaseBuilder<Builder, PageDeleteTrigger> {
        @Override
        protected Builder builder() {
            return this;
        }

        public DefaultDeleteContext build() {
            return new DefaultDeleteContext(this);
        }

        @Override
        protected @NonNull PageDeleteTrigger unknownTrigger() {
            return PageDeleteTrigger.UNKNOWN;
        }
    }
}

