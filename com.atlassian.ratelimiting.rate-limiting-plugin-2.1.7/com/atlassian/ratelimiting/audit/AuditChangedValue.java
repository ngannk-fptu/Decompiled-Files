/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.ratelimiting.audit;

import com.atlassian.sal.api.message.I18nResolver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AuditChangedValue {
    private final String name;
    private final Optional<String> from;
    private final Optional<String> to;

    public AuditChangedValue(String name, Optional<String> from, Optional<String> to) {
        this.name = name;
        this.from = from;
        this.to = to;
    }

    public String getName() {
        return this.name;
    }

    public Optional<String> getFrom() {
        return this.from;
    }

    public Optional<String> getTo() {
        return this.to;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AuditChangedValue)) {
            return false;
        }
        AuditChangedValue other = (AuditChangedValue)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        Optional<String> this$from = this.getFrom();
        Optional<String> other$from = other.getFrom();
        if (this$from == null ? other$from != null : !((Object)this$from).equals(other$from)) {
            return false;
        }
        Optional<String> this$to = this.getTo();
        Optional<String> other$to = other.getTo();
        return !(this$to == null ? other$to != null : !((Object)this$to).equals(other$to));
    }

    protected boolean canEqual(Object other) {
        return other instanceof AuditChangedValue;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Optional<String> $from = this.getFrom();
        result = result * 59 + ($from == null ? 43 : ((Object)$from).hashCode());
        Optional<String> $to = this.getTo();
        result = result * 59 + ($to == null ? 43 : ((Object)$to).hashCode());
        return result;
    }

    public String toString() {
        return "AuditChangedValue(name=" + this.getName() + ", from=" + this.getFrom() + ", to=" + this.getTo() + ")";
    }

    public static class AuditChangedValuesBuilder {
        private final I18nResolver i18nResolver;
        private final List<AuditChangedValue> changes = new ArrayList<AuditChangedValue>();

        public AuditChangedValuesBuilder(I18nResolver i18nResolver) {
            this.i18nResolver = i18nResolver;
        }

        public <V> AuditChangedValuesBuilder addIfChanged(String name, Optional<V> from, Optional<V> to) {
            Objects.requireNonNull(name);
            if (!Objects.equals(from, to)) {
                this.changes.add(new AuditChangedValue(this.getText(name), this.getText(from), this.getText(to)));
            }
            return this;
        }

        public List<AuditChangedValue> build() {
            return Collections.unmodifiableList(this.changes);
        }

        private Optional<String> getText(Optional<?> value) {
            return value.map(Object::toString).map(this::getText);
        }

        private String getText(String key) {
            return this.i18nResolver.getText(key);
        }
    }
}

