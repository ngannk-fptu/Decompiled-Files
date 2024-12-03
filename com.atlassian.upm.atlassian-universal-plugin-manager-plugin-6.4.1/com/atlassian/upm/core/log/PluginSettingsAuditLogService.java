/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionCallback
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Ordering
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.core.log;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.api.log.AuditLogEntry;
import com.atlassian.upm.api.log.EntryType;
import com.atlassian.upm.core.log.AuditLogEntryImpl;
import com.atlassian.upm.core.log.AuditLogService;
import com.atlassian.upm.core.log.AuditLoggingException;
import com.atlassian.upm.core.log.PluginSettingsAuditLogAccessor;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PluginSettingsAuditLogService
extends PluginSettingsAuditLogAccessor
implements AuditLogService {
    private static final Logger log = LoggerFactory.getLogger((String)PluginSettingsAuditLogService.class.getName());
    private final TransactionTemplate txTemplate;
    private volatile Date lastModified = new Date();

    public PluginSettingsAuditLogService(I18nResolver i18nResolver, ApplicationProperties applicationProperties, UserManager userManager, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate txTemplate, BaseUriBuilder uriBuilder, PluginSettingsAuditLogAccessor.Clock clock, String keyPrefix) {
        super(i18nResolver, applicationProperties, userManager, pluginSettingsFactory, uriBuilder, clock, keyPrefix);
        this.txTemplate = txTemplate;
    }

    public PluginSettingsAuditLogService(I18nResolver i18nResolver, ApplicationProperties applicationProperties, UserManager userManager, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate txTemplate, BaseUriBuilder uriBuilder, String keyPrefix) {
        this(i18nResolver, applicationProperties, userManager, pluginSettingsFactory, txTemplate, uriBuilder, new PluginSettingsAuditLogAccessor.SystemClock(), keyPrefix);
    }

    @Override
    public void logI18nMessage(String i18nKey, String ... params) {
        this.logI18nMessageWithUserKey(i18nKey, this.checkAnonymous(this.userManager.getRemoteUserKey()), params);
    }

    @Override
    public void logI18nMessageWithCurrentApplication(String i18nKey, String ... params) {
        this.log(i18nKey, this.applicationProperties.getDisplayName(), params);
    }

    @Override
    public void logI18nMessageWithUserKey(String i18nKey, UserKey userKey, String ... params) {
        this.log(i18nKey, userKey.getStringValue(), params);
    }

    private void log(String i18nKey, String actor, String ... params) {
        this.setLastModifiedToCurrentTime();
        AuditLogEntryImpl entry = new AuditLogEntryImpl(actor, this.lastModified, i18nKey, EntryType.valueOfI18n(i18nKey), params);
        try {
            this.saveEntryAndPurge(entry);
        }
        catch (IOException e) {
            throw new AuditLoggingException("Failed to log message: " + entry, e);
        }
        if (log.isInfoEnabled()) {
            log.info(entry.getMessage(this.i18nResolver));
        }
    }

    private UserKey checkAnonymous(UserKey userKey) {
        if (userKey == null) {
            return new UserKey("anonymous");
        }
        return userKey;
    }

    protected synchronized void setLogEntries(Iterable<AuditLogEntry> entries) {
        Collection<AuditLogEntry> purgedEntries = this.purgeEntriesAndTransform(entries);
        this.saveEntries(purgedEntries);
        this.setLastModifiedToCurrentTime();
    }

    @Override
    protected Date getLastModified() {
        return this.lastModified;
    }

    protected void setLastModifiedToCurrentTime() {
        this.lastModified = new Date(this.clock.currentTimeMillis());
    }

    @Override
    public void purgeLog() {
        this.saveEntries(Collections.emptyList());
        this.setLastModifiedToCurrentTime();
        this.setLastRetrievedToCurrentTime();
    }

    @Override
    public void setMaxEntries(int maxEntries) {
        if (maxEntries > 0) {
            this.getPluginSettings().put("upm_audit_log_max_entries", (Object)Integer.toString(maxEntries));
        }
    }

    @Override
    public void setPurgeAfter(int purgeAfter) {
        if (purgeAfter > 0) {
            this.getPluginSettings().put("upm_audit_log_purge_after", (Object)Integer.toString(purgeAfter));
        }
    }

    private void saveEntryAndPurge(AuditLogEntry entry) throws IOException {
        Iterable entries = Iterables.concat(this.getSavedEntriesAsStrings(), (Iterable)ImmutableList.of((Object)this.mapper.writeValueAsString((Object)entry)));
        Collection<AuditLogEntry> purgedEntries = this.purgeEntryStringsAndTransform(entries);
        this.saveEntries(purgedEntries);
    }

    private Iterable<String> getSavedEntriesAsStrings() {
        Object entries = this.getPluginSettings().get("upm_audit_log_v3");
        if (entries == null) {
            return Collections.emptyList();
        }
        if (!(entries instanceof List)) {
            log.error("Invalid audit log storage has been detected: " + entries);
            this.purgeLog();
            return Collections.emptyList();
        }
        return ImmutableList.copyOf((Collection)((List)entries));
    }

    private void saveEntries(Iterable<AuditLogEntry> stringEntries) {
        Iterable entries = Iterables.transform(stringEntries, (Function)new Function<AuditLogEntry, String>(){

            public String apply(AuditLogEntry from) {
                try {
                    return PluginSettingsAuditLogService.this.mapper.writeValueAsString((Object)from);
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to save AuditLogEntry to JSON: " + from, e);
                }
            }
        });
        final ArrayList<String> result = new ArrayList<String>();
        int count = 0;
        int maxEntries = this.getMaxEntries();
        for (String s : entries) {
            if (++count > maxEntries) break;
            result.add(s);
        }
        this.txTemplate.execute((TransactionCallback)new TransactionCallback<Void>(){

            public Void doInTransaction() {
                PluginSettingsAuditLogService.this.getPluginSettings().put("upm_audit_log_v3", (Object)result);
                return null;
            }
        });
    }

    private Collection<AuditLogEntry> purgeEntryStringsAndTransform(Iterable<String> stringEntries) {
        ImmutableList entries = ImmutableList.copyOf((Iterable)Iterables.transform(stringEntries, (Function)new Function<String, AuditLogEntry>(){

            public AuditLogEntry apply(String from) {
                try {
                    return (AuditLogEntry)PluginSettingsAuditLogService.this.mapper.readValue(from, AuditLogEntryImpl.class);
                }
                catch (IOException e) {
                    throw new RuntimeException("Failed to parse AuditLogEntry from JSON string: " + from, e);
                }
            }
        }));
        return this.purgeEntriesAndTransform((Iterable<AuditLogEntry>)entries);
    }

    private Collection<AuditLogEntry> purgeEntriesAndTransform(Iterable<AuditLogEntry> entries) {
        entries = Iterables.filter(entries, (Predicate)this.purgePolicy);
        return Ordering.natural().reverse().sortedCopy(entries);
    }
}

