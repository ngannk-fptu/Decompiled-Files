/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.check.csv;

import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.service.NonSpaceTemplateConflictsInfo;
import com.atlassian.migration.agent.service.check.csv.AbstractCheckResultCSVBean;
import lombok.Generated;

public class GlobalDataTemplateCSVBean
implements AbstractCheckResultCSVBean {
    private String globalTemplateType;
    private String serverTemplateName;
    private String cloudTemplateName;
    private String templateModuleKey;

    public GlobalDataTemplateCSVBean(NonSpaceTemplateConflictsInfo.Conflict conflictsInfo) {
        this.globalTemplateType = this.getGlobalTemplateTypeForCSV(conflictsInfo.type);
        this.serverTemplateName = conflictsInfo.serverTemplateName;
        this.cloudTemplateName = conflictsInfo.cloudTemplateName;
        this.templateModuleKey = conflictsInfo.templateModuleKey;
    }

    private String getGlobalTemplateTypeForCSV(GlobalEntityType globalEntityType) {
        if (globalEntityType.equals((Object)GlobalEntityType.SYSTEM_TEMPLATES)) {
            return "Custom system template";
        }
        if (globalEntityType.equals((Object)GlobalEntityType.GLOBAL_TEMPLATES)) {
            return "Global page template";
        }
        return "Invalid global template type found";
    }

    @Generated
    public String getGlobalTemplateType() {
        return this.globalTemplateType;
    }

    @Generated
    public String getServerTemplateName() {
        return this.serverTemplateName;
    }

    @Generated
    public String getCloudTemplateName() {
        return this.cloudTemplateName;
    }

    @Generated
    public String getTemplateModuleKey() {
        return this.templateModuleKey;
    }
}

