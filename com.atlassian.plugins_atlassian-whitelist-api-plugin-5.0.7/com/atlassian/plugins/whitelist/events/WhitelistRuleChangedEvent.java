/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.plugins.whitelist.events;

import com.atlassian.plugins.whitelist.WhitelistRule;
import com.atlassian.plugins.whitelist.events.WhitelistRuleEvent;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.qual.NonNull;

public class WhitelistRuleChangedEvent
extends WhitelistRuleEvent {
    @Nullable
    private final WhitelistRule oldRule;

    public WhitelistRuleChangedEvent(@Nonnull WhitelistRule whitelistRule) {
        this(null, whitelistRule);
    }

    public WhitelistRuleChangedEvent(@Nullable WhitelistRule oldRule, @Nonnull WhitelistRule newRule) {
        super(Objects.requireNonNull(newRule));
        this.oldRule = oldRule;
    }

    @Nullable
    public WhitelistRule getOldRule() {
        return this.oldRule;
    }

    public @NonNull WhitelistRule getNewRule() {
        return this.whitelistRule;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WhitelistRuleChangedEvent that = (WhitelistRuleChangedEvent)o;
        return Objects.equals(this.getOldRule(), that.getOldRule()) && Objects.equals(this.getNewRule(), that.getNewRule());
    }

    public int hashCode() {
        return Objects.hash(this.getOldRule(), this.getNewRule());
    }

    public String toString() {
        return "WhitelistRuleChangedEvent{oldRule=" + this.getOldRule() + ", newRule=" + this.getNewRule() + '}';
    }
}

