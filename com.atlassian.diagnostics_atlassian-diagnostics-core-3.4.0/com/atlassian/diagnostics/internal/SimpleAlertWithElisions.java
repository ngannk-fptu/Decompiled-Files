/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertWithElisions
 *  com.atlassian.diagnostics.Elisions
 *  com.atlassian.diagnostics.Issue
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.AlertWithElisions;
import com.atlassian.diagnostics.Elisions;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.internal.SimpleAlert;
import java.util.Optional;
import javax.annotation.Nonnull;

public class SimpleAlertWithElisions
extends SimpleAlert
implements AlertWithElisions {
    private final Elisions elisions;

    private SimpleAlertWithElisions(Builder builder) {
        super(builder);
        this.elisions = builder.elisions;
    }

    @Nonnull
    public Optional<Elisions> getElisions() {
        return Optional.ofNullable(this.elisions);
    }

    public static class Builder
    extends SimpleAlert.AbstractBuilder<Builder> {
        private Elisions elisions;

        public Builder(@Nonnull Issue issue, @Nonnull String nodeName) {
            super(issue, nodeName);
        }

        @Nonnull
        public SimpleAlertWithElisions build() {
            return new SimpleAlertWithElisions(this);
        }

        @Nonnull
        public Builder elisions(Elisions value) {
            this.elisions = value;
            return this.self();
        }

        @Override
        @Nonnull
        protected Builder self() {
            return this;
        }
    }
}

