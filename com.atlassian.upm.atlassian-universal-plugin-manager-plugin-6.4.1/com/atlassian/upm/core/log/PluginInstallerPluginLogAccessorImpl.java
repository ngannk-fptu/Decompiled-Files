/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Iterables
 *  org.joda.time.DateTime
 *  org.joda.time.ReadableInstant
 */
package com.atlassian.upm.core.log;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.api.log.AuditLogEntry;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.log.PluginInstallerPluginLogAccessor;
import com.atlassian.upm.core.log.PluginSettingsAuditLogAccessor;
import com.atlassian.upm.core.rest.BaseUriBuilder;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.Iterator;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

public class PluginInstallerPluginLogAccessorImpl
extends PluginSettingsAuditLogAccessor
implements PluginInstallerPluginLogAccessor {
    public static final String KEY_PREFIX = PluginInstallerPluginLogAccessorImpl.class.getName() + ":log:";

    public PluginInstallerPluginLogAccessorImpl(I18nResolver i18nResolver, ApplicationProperties applicationProperties, UserManager userManager, PluginSettingsFactory pluginSettingsFactory, BaseUriBuilder uriBuilder, PluginSettingsAuditLogAccessor.Clock clock) {
        super(i18nResolver, applicationProperties, userManager, pluginSettingsFactory, uriBuilder, clock, KEY_PREFIX);
    }

    public PluginInstallerPluginLogAccessorImpl(I18nResolver i18nResolver, ApplicationProperties applicationProperties, UserManager userManager, PluginSettingsFactory pluginSettingsFactory, BaseUriBuilder uriBuilder) {
        super(i18nResolver, applicationProperties, userManager, pluginSettingsFactory, uriBuilder, KEY_PREFIX);
    }

    @Override
    public Iterable<AuditLogEntry> getLogEntries(Option<DateTime> since) {
        Iterator<DateTime> iterator = since.iterator();
        if (iterator.hasNext()) {
            DateTime timestamp = iterator.next();
            return Iterables.filter(super.getLogEntries(), this.since(timestamp));
        }
        return super.getLogEntries();
    }

    private Predicate<AuditLogEntry> since(final DateTime since) {
        return new Predicate<AuditLogEntry>(){

            public boolean apply(AuditLogEntry entry) {
                return since.isBefore((ReadableInstant)new DateTime((Object)entry.getDate()));
            }
        };
    }
}

