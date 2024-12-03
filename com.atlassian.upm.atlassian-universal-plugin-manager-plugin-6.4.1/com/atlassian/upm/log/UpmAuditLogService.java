/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.rometools.rome.feed.atom.Feed
 */
package com.atlassian.upm.log;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.api.log.AuditLogEntry;
import com.atlassian.upm.api.log.EntryType;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.log.PluginInstallerPluginLogAccessor;
import com.atlassian.upm.core.log.PluginSettingsAuditLogAccessor;
import com.atlassian.upm.core.log.PluginSettingsAuditLogService;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.rometools.rome.feed.atom.Feed;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class UpmAuditLogService
extends PluginSettingsAuditLogService {
    public static final String KEY_PREFIX = "com.atlassian.upm.log.PluginSettingsAuditLogService:log:";
    private final PluginInstallerPluginLogAccessor pipLogAccessor;

    public UpmAuditLogService(I18nResolver i18nResolver, ApplicationProperties applicationProperties, UserManager userManager, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate txTemplate, UpmUriBuilder uriBuilder, PluginInstallerPluginLogAccessor pipLogAccessor, PluginSettingsAuditLogAccessor.Clock clock) {
        super(i18nResolver, applicationProperties, userManager, pluginSettingsFactory, txTemplate, uriBuilder, clock, KEY_PREFIX);
        this.pipLogAccessor = Objects.requireNonNull(pipLogAccessor, "pipLogAccessor");
    }

    public UpmAuditLogService(I18nResolver i18nResolver, ApplicationProperties applicationProperties, UserManager userManager, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate txTemplate, UpmUriBuilder uriBuilder, PluginInstallerPluginLogAccessor pipLogAccessor) {
        super(i18nResolver, applicationProperties, userManager, pluginSettingsFactory, txTemplate, uriBuilder, KEY_PREFIX);
        this.pipLogAccessor = Objects.requireNonNull(pipLogAccessor, "pipLogAccessor");
    }

    @Override
    public synchronized Iterable<AuditLogEntry> getLogEntries() {
        this.updateFromPipLogAccessor();
        return super.getLogEntries();
    }

    @Override
    public synchronized Collection<AuditLogEntry> getLogEntries(Integer maxResults, Integer startIndex) {
        this.updateFromPipLogAccessor();
        return super.getLogEntries(maxResults, startIndex);
    }

    @Override
    public synchronized Iterable<AuditLogEntry> getLogEntries(Integer maxResults, Integer startIndex, Set<EntryType> entryTypes) {
        this.updateFromPipLogAccessor();
        return super.getLogEntries(maxResults, startIndex, entryTypes);
    }

    @Override
    public synchronized Feed getFeed() {
        this.updateFromPipLogAccessor();
        return super.getFeed();
    }

    @Override
    public synchronized Feed getFeed(Integer maxResults, Integer startIndex) {
        this.updateFromPipLogAccessor();
        return super.getFeed(maxResults, startIndex);
    }

    private synchronized void updateFromPipLogAccessor() {
        List newPipEntries = StreamSupport.stream(this.pipLogAccessor.getLogEntries(Option.some(this.getLastRetrieved())).spliterator(), false).collect(Collectors.toList());
        if (!newPipEntries.isEmpty()) {
            List<AuditLogEntry> mergeNewAndOldEntries = Stream.concat(newPipEntries.stream(), super.getLogEntries(null, null).stream()).sorted(Comparator.naturalOrder().reversed()).collect(Collectors.toList());
            this.setLogEntries(mergeNewAndOldEntries);
        }
    }
}

