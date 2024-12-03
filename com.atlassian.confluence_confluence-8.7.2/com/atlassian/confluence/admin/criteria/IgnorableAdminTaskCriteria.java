/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.admin.criteria;

import com.atlassian.confluence.admin.criteria.AdminConfigurationCriteria;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.util.HashSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IgnorableAdminTaskCriteria
implements AdminConfigurationCriteria {
    private static final Logger log = LoggerFactory.getLogger(IgnorableAdminTaskCriteria.class);
    private final SettingsManager settingsManager;
    private final String key;
    private final AdminConfigurationCriteria delegate;

    public IgnorableAdminTaskCriteria(String key, SettingsManager settingsManager) {
        this.key = (String)Preconditions.checkNotNull((Object)key);
        this.settingsManager = (SettingsManager)Preconditions.checkNotNull((Object)settingsManager);
        this.delegate = null;
    }

    public IgnorableAdminTaskCriteria(String key, SettingsManager settingsManager, AdminConfigurationCriteria alternativeCriteria) {
        this.key = (String)Preconditions.checkNotNull((Object)key);
        this.settingsManager = (SettingsManager)Preconditions.checkNotNull((Object)settingsManager);
        this.delegate = alternativeCriteria;
    }

    @Override
    public boolean getIgnored() {
        String ignoredTasks = this.settingsManager.getGlobalSettings().getIgnoredAdminTasks();
        if (StringUtils.isBlank((CharSequence)ignoredTasks)) {
            return false;
        }
        HashSet ignoredTaskList = Sets.newHashSet((Object[])ignoredTasks.split(","));
        if (ignoredTaskList.contains(this.key)) {
            return true;
        }
        return this.delegate == null ? false : this.delegate.getIgnored();
    }

    @Override
    public void setIgnored(boolean ignore) {
        String ignoredTasks = this.settingsManager.getGlobalSettings().getIgnoredAdminTasks();
        if (StringUtils.isBlank((CharSequence)ignoredTasks)) {
            if (ignore) {
                Settings settings = this.settingsManager.getGlobalSettings();
                settings.setIgnoredAdminTasks(this.key);
                this.settingsManager.updateGlobalSettings(settings);
            }
        } else {
            HashSet ignoredTaskList = Sets.newHashSet((Object[])ignoredTasks.split(","));
            boolean update = false;
            if (ignore) {
                if (!ignoredTaskList.contains(this.key)) {
                    ignoredTaskList.add(this.key);
                    update = true;
                }
            } else if (ignoredTaskList.contains(this.key)) {
                ignoredTaskList.remove(this.key);
                update = true;
            }
            if (update) {
                Settings settings = this.settingsManager.getGlobalSettings();
                settings.setIgnoredAdminTasks(StringUtils.join((Iterable)ignoredTaskList, (char)','));
                this.settingsManager.updateGlobalSettings(settings);
            }
        }
    }

    @Override
    public boolean isMet() {
        return this.delegate == null ? false : this.delegate.isMet();
    }

    @Override
    public boolean hasValue() {
        return this.delegate == null ? false : this.delegate.hasValue();
    }

    @Override
    public String getValue() {
        return this.delegate == null ? null : this.delegate.getValue();
    }

    @Override
    public boolean hasLiveValue() {
        return this.delegate == null ? false : this.delegate.hasLiveValue();
    }
}

