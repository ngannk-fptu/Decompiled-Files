/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.log;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.api.log.AuditLogEntry;
import com.atlassian.upm.api.log.EntryType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditLogEntryImpl
implements AuditLogEntry {
    @JsonProperty
    private final String userKey;
    @JsonProperty
    private final Date date;
    @JsonProperty
    private final String i18nKey;
    @JsonProperty
    private final String[] params;
    @JsonProperty
    private final EntryType entryType;

    @JsonCreator
    public AuditLogEntryImpl(@JsonProperty(value="userKey") String userKey, @JsonProperty(value="date") Date date, @JsonProperty(value="i18nKey") String i18nKey, @JsonProperty(value="entryType") EntryType entryType, String ... params) {
        this.userKey = Objects.requireNonNull(userKey, "userKey");
        this.date = Objects.requireNonNull(date, "date");
        this.i18nKey = Objects.requireNonNull(i18nKey, "i18nKey");
        this.params = Objects.requireNonNull(params, "params");
        this.entryType = entryType == null ? EntryType.valueOfI18n(i18nKey) : entryType;
    }

    @Override
    public String getTitle(I18nResolver i18nResolver) {
        return i18nResolver.getText(this.i18nKey, (Serializable[])this.params);
    }

    @Override
    public String getMessage(I18nResolver i18nResolver) {
        return this.date + " " + this.userKey + ": " + i18nResolver.getText(this.i18nKey, (Serializable[])this.params);
    }

    @Override
    public Date getDate() {
        return this.date;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.getUserKey();
    }

    @Override
    public String getUserKey() {
        return this.userKey;
    }

    @Override
    public String getI18nKey() {
        return this.i18nKey;
    }

    @Override
    public EntryType getEntryType() {
        return this.entryType;
    }

    @Override
    public int compareTo(AuditLogEntry auditLogEntry) {
        return this.getDate().compareTo(auditLogEntry.getDate());
    }
}

