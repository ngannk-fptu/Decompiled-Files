/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.model;

import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

@ReturnValuesAreNonnullByDefault
public class AuditAction {
    private final String action;
    private final Optional<String> actionI18nKey;

    public AuditAction(@Nonnull String action, String actionI18nKey) {
        this.action = Objects.requireNonNull(action, "action");
        this.actionI18nKey = Optional.ofNullable(actionI18nKey);
    }

    public String getAction() {
        return this.action;
    }

    public Optional<String> getActionI18nKey() {
        return this.actionI18nKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AuditAction that = (AuditAction)o;
        return this.action.equals(that.action) && this.actionI18nKey.equals(that.actionI18nKey);
    }

    public int hashCode() {
        return Objects.hash(this.action, this.actionI18nKey);
    }

    public String toString() {
        return "AuditAction{action='" + this.action + '\'' + ", actionI18nKey=" + this.actionI18nKey + '}';
    }
}

