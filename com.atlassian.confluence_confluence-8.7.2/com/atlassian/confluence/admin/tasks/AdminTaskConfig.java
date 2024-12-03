/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.admin.tasks;

import com.atlassian.confluence.admin.criteria.AdminConfigurationCriteria;
import java.util.List;

public interface AdminTaskConfig {
    public static final String TASK_PREFIX = "admintask";

    public String getKey();

    public String getTitleKey();

    public String getDescriptionKey();

    public String getActionTextKey();

    public String getConfigurationCurrentValueKey();

    public AdminConfigurationCriteria getAdminConfigurationCriteria();

    public String getFirstConfigurationUri();

    public List<String> getAllConfigurationUris();
}

