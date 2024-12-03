/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.confluence.admin.tasks;

import com.atlassian.confluence.admin.criteria.AdminConfigurationCriteria;
import com.atlassian.confluence.admin.tasks.AdminTaskConfig;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class DefaultAdminTaskConfig
implements AdminTaskConfig {
    private final String key;
    private final AdminConfigurationCriteria adminConfigurationCriteria;
    private List<String> configurationUris;

    public DefaultAdminTaskConfig(String key) {
        this(key, null);
    }

    public DefaultAdminTaskConfig(String key, AdminConfigurationCriteria adminConfigurationCriteria) throws IllegalArgumentException {
        this(key, adminConfigurationCriteria, (String)null);
    }

    public DefaultAdminTaskConfig(String key, AdminConfigurationCriteria adminConfigurationCriteria, String configurationUri) throws IllegalArgumentException {
        this(key, adminConfigurationCriteria, (List<String>)(StringUtils.isNotBlank((CharSequence)configurationUri) ? ImmutableList.of((Object)configurationUri) : ImmutableList.of()));
    }

    public DefaultAdminTaskConfig(String key, AdminConfigurationCriteria adminConfigurationCriteria, List<String> configurationUris) throws IllegalArgumentException {
        Validate.notEmpty((CharSequence)key, (String)"key must be a non-emtpy string", (Object[])new Object[0]);
        key = ((String)key).toLowerCase();
        if (!StringUtils.startsWith((CharSequence)key, (CharSequence)"admintask")) {
            key = "admintask." + (String)key;
        }
        this.key = key;
        this.adminConfigurationCriteria = adminConfigurationCriteria;
        this.configurationUris = ImmutableList.copyOf(configurationUris);
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getTitleKey() {
        return this.getKey() + ".title";
    }

    @Override
    public String getDescriptionKey() {
        return this.getKey() + ".description";
    }

    @Override
    public String getActionTextKey() {
        return this.getKey() + ".actiontext";
    }

    @Override
    public String getConfigurationCurrentValueKey() {
        return this.getKey() + ".configuration.current.value";
    }

    @Override
    public AdminConfigurationCriteria getAdminConfigurationCriteria() {
        return this.adminConfigurationCriteria;
    }

    @Override
    public String getFirstConfigurationUri() {
        return this.configurationUris.isEmpty() ? null : this.configurationUris.get(0);
    }

    @Override
    public List<String> getAllConfigurationUris() {
        return this.configurationUris;
    }
}

