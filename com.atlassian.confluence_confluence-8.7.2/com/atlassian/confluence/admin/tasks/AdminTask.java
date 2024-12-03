/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.confluence.admin.tasks;

import com.atlassian.confluence.admin.criteria.AdminConfigurationCriteria;
import com.atlassian.confluence.admin.tasks.AdminTaskConfig;
import com.atlassian.confluence.admin.tasks.AdminTaskData;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AdminTask {
    private final AdminTaskConfig adminTaskConfig;
    private final AdminTaskData adminTaskData;

    public AdminTask(AdminTaskConfig adminTaskConfig, AdminTaskData adminTaskData) {
        this.adminTaskConfig = adminTaskConfig;
        this.adminTaskData = adminTaskData;
    }

    public AdminTaskConfig getAdminTaskConfig() {
        return this.adminTaskConfig;
    }

    public AdminTaskData getAdminTaskData() {
        return this.adminTaskData;
    }

    public String getKey() {
        return this.adminTaskConfig.getKey();
    }

    public String getTitleKey() {
        return this.adminTaskConfig.getTitleKey();
    }

    public String getActionTextKey() {
        return this.adminTaskConfig.getActionTextKey();
    }

    public String getDescriptionKey() {
        return this.adminTaskConfig.getDescriptionKey();
    }

    public String getConfigurationCurrentValueKey() {
        return this.adminTaskConfig.getConfigurationCurrentValueKey();
    }

    public String getFirstConfigurationUri() {
        return this.adminTaskConfig.getFirstConfigurationUri();
    }

    public List<String> getAllConfigurationUris() {
        return this.adminTaskConfig.getAllConfigurationUris();
    }

    public boolean isIgnorable() {
        return this.getHasSuccessCriteria();
    }

    public boolean isIgnored() {
        return this.isIgnorable() && this.adminTaskConfig.getAdminConfigurationCriteria().getIgnored();
    }

    public void setIgnored(boolean value) {
        if (this.isIgnorable()) {
            this.adminTaskConfig.getAdminConfigurationCriteria().setIgnored(value);
        }
    }

    public boolean getHasValue() {
        AdminConfigurationCriteria adminConfigurationCriteria = this.adminTaskConfig.getAdminConfigurationCriteria();
        return adminConfigurationCriteria != null && adminConfigurationCriteria.hasValue();
    }

    public boolean getIsCompleted() {
        return this.adminTaskData.isCompleted();
    }

    public Date getCompletedAt() {
        return this.adminTaskData.getCompletedAt();
    }

    public String getCompletedByFullName() {
        return this.adminTaskData.getCompletedByFullName();
    }

    public String getCompletedByUsername() {
        return this.adminTaskData.getCompletedByUsername();
    }

    public boolean getHasSuccessCriteria() {
        return this.adminTaskConfig.getAdminConfigurationCriteria() != null;
    }

    public String getConfiguredValue() {
        String value = null;
        if (this.getIsCompleted()) {
            value = this.adminTaskData.getSignedOffValue();
        }
        if (this.getHasSuccessCriteria() && (this.adminTaskConfig.getAdminConfigurationCriteria().hasLiveValue() || StringUtils.isBlank((CharSequence)value))) {
            value = this.adminTaskConfig.getAdminConfigurationCriteria().getValue();
        }
        return value;
    }

    public boolean getIsCriteriaMet() {
        boolean result = false;
        if (this.getHasSuccessCriteria()) {
            result = this.adminTaskConfig.getAdminConfigurationCriteria().isMet();
        }
        return result;
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("key", (Object)this.getKey()).toString();
    }
}

