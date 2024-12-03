/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.agent.entity.GlobalEntityType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

public class NonSpaceTemplateConflictsInfo
implements Serializable {
    private static final long serialVersionUID = 6294277463264694411L;
    private Map<GlobalEntityType, Long> totalNumOfServerTemplates = new HashMap<GlobalEntityType, Long>();
    private final List<Conflict> conflicts = new ArrayList<Conflict>();

    public final void setTotalNumOfServerTemplates(GlobalEntityType templateType, long value) {
        this.totalNumOfServerTemplates.put(templateType, value);
    }

    public final long getTotalNumOfServerTemplates(GlobalEntityType templateType) {
        return this.totalNumOfServerTemplates.getOrDefault((Object)templateType, 0L);
    }

    public long getTotalNumOfConflictingTemplates(GlobalEntityType conflictType) {
        return this.conflicts.stream().filter(s -> conflictType.equals((Object)s.type)).count();
    }

    public long getTotalNumOfTemplatesMigrated(GlobalEntityType templateType) {
        return this.getTotalNumOfServerTemplates(templateType) - this.getTotalNumOfConflictingTemplates(templateType);
    }

    public void addConflict(Conflict conflict) {
        this.conflicts.add(conflict);
    }

    @Generated
    public void setTotalNumOfServerTemplates(Map<GlobalEntityType, Long> totalNumOfServerTemplates) {
        this.totalNumOfServerTemplates = totalNumOfServerTemplates;
    }

    @Generated
    public Map<GlobalEntityType, Long> getTotalNumOfServerTemplates() {
        return this.totalNumOfServerTemplates;
    }

    @Generated
    public List<Conflict> getConflicts() {
        return this.conflicts;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Conflict
    implements Serializable {
        private static final long serialVersionUID = 3971751004026826216L;
        public final GlobalEntityType type;
        public final String serverTemplateId;
        public final String cloudTemplateId;
        public final String serverTemplateName;
        public final String cloudTemplateName;
        public final String templatePluginKey;
        public final String templateModuleKey;

        @JsonCreator
        public Conflict(@JsonProperty(value="type") GlobalEntityType type, @JsonProperty(value="serverTemplateId") String serverTemplateId, @JsonProperty(value="cloudTemplateId") String cloudTemplateId, @JsonProperty(value="serverTemplateName") String serverTemplateName, @JsonProperty(value="cloudTemplateName") String cloudTemplateName, @JsonProperty(value="templatePluginKey") String templatePluginKey, @JsonProperty(value="templateModuleKey") String templateModuleKey) {
            this.type = type;
            this.serverTemplateId = serverTemplateId;
            this.cloudTemplateId = cloudTemplateId;
            this.serverTemplateName = serverTemplateName;
            this.cloudTemplateName = cloudTemplateName;
            this.templatePluginKey = templatePluginKey;
            this.templateModuleKey = templateModuleKey;
        }

        @Generated
        public static ConflictBuilder builder() {
            return new ConflictBuilder();
        }

        @Generated
        public static class ConflictBuilder {
            @Generated
            private GlobalEntityType type;
            @Generated
            private String serverTemplateId;
            @Generated
            private String cloudTemplateId;
            @Generated
            private String serverTemplateName;
            @Generated
            private String cloudTemplateName;
            @Generated
            private String templatePluginKey;
            @Generated
            private String templateModuleKey;

            @Generated
            ConflictBuilder() {
            }

            @Generated
            public ConflictBuilder type(GlobalEntityType type) {
                this.type = type;
                return this;
            }

            @Generated
            public ConflictBuilder serverTemplateId(String serverTemplateId) {
                this.serverTemplateId = serverTemplateId;
                return this;
            }

            @Generated
            public ConflictBuilder cloudTemplateId(String cloudTemplateId) {
                this.cloudTemplateId = cloudTemplateId;
                return this;
            }

            @Generated
            public ConflictBuilder serverTemplateName(String serverTemplateName) {
                this.serverTemplateName = serverTemplateName;
                return this;
            }

            @Generated
            public ConflictBuilder cloudTemplateName(String cloudTemplateName) {
                this.cloudTemplateName = cloudTemplateName;
                return this;
            }

            @Generated
            public ConflictBuilder templatePluginKey(String templatePluginKey) {
                this.templatePluginKey = templatePluginKey;
                return this;
            }

            @Generated
            public ConflictBuilder templateModuleKey(String templateModuleKey) {
                this.templateModuleKey = templateModuleKey;
                return this;
            }

            @Generated
            public Conflict build() {
                return new Conflict(this.type, this.serverTemplateId, this.cloudTemplateId, this.serverTemplateName, this.cloudTemplateName, this.templatePluginKey, this.templateModuleKey);
            }

            @Generated
            public String toString() {
                return "NonSpaceTemplateConflictsInfo.Conflict.ConflictBuilder(type=" + (Object)((Object)this.type) + ", serverTemplateId=" + this.serverTemplateId + ", cloudTemplateId=" + this.cloudTemplateId + ", serverTemplateName=" + this.serverTemplateName + ", cloudTemplateName=" + this.cloudTemplateName + ", templatePluginKey=" + this.templatePluginKey + ", templateModuleKey=" + this.templateModuleKey + ")";
            }
        }
    }
}

