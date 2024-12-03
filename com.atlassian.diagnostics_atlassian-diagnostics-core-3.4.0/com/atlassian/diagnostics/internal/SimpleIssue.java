/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Component
 *  com.atlassian.diagnostics.Issue
 *  com.atlassian.diagnostics.JsonMapper
 *  com.atlassian.diagnostics.Severity
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Objects
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal;

import com.atlassian.diagnostics.Component;
import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.JsonMapper;
import com.atlassian.diagnostics.Severity;
import com.atlassian.diagnostics.internal.IssueId;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.MoreObjects;
import java.util.Objects;
import javax.annotation.Nonnull;

class SimpleIssue
implements Issue {
    private final Component component;
    private final String descriptionI18nKey;
    private final IssueId id;
    private final JsonMapper jsonMapper;
    private final Severity severity;
    private final String summaryI18nKey;
    protected final I18nResolver i18nResolver;

    SimpleIssue(I18nResolver i18nResolver, Component component, IssueId id, String summaryI18nKey, String descriptionI18nKey, Severity severity, JsonMapper jsonMapper) {
        this.component = Objects.requireNonNull(component, "component");
        this.descriptionI18nKey = Objects.requireNonNull(descriptionI18nKey, "descriptionKey");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.jsonMapper = Objects.requireNonNull(jsonMapper, "jsonMapper");
        this.summaryI18nKey = Objects.requireNonNull(summaryI18nKey, "summaryKey");
        this.id = Objects.requireNonNull(id, "id");
        this.severity = Objects.requireNonNull(severity, "severity");
    }

    @Nonnull
    public Component getComponent() {
        return this.component;
    }

    @Nonnull
    public String getDescription() {
        return this.i18nResolver.getText(this.descriptionI18nKey);
    }

    @Nonnull
    public String getId() {
        return this.id.toString();
    }

    @Nonnull
    public <T> JsonMapper<T> getJsonMapper() {
        return this.jsonMapper;
    }

    @Nonnull
    public Severity getSeverity() {
        return this.severity;
    }

    @Nonnull
    public String getSummary() {
        return this.i18nResolver.getText(this.summaryI18nKey);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SimpleIssue)) {
            return false;
        }
        SimpleIssue that = (SimpleIssue)o;
        return com.google.common.base.Objects.equal((Object)this.id, (Object)that.id);
    }

    public int hashCode() {
        return com.google.common.base.Objects.hashCode((Object[])new Object[]{this.id});
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", (Object)this.id).add("severity", (Object)this.severity).toString();
    }
}

