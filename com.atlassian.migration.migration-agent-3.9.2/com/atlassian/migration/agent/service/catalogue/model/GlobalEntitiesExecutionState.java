/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.catalogue.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.annotation.Nullable;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class GlobalEntitiesExecutionState {
    @JsonProperty
    private Long totalGlobalPageTemplates;
    @JsonProperty
    private Long totalEditedSystemTemplates;
    @Nullable
    @JsonProperty
    private Long totalGlobalPageTemplatesExported;
    @Nullable
    @JsonProperty
    private Long totalEditedSystemTemplatesExported;

    @Generated
    public GlobalEntitiesExecutionState(Long totalGlobalPageTemplates, Long totalEditedSystemTemplates, @Nullable Long totalGlobalPageTemplatesExported, @Nullable Long totalEditedSystemTemplatesExported) {
        this.totalGlobalPageTemplates = totalGlobalPageTemplates;
        this.totalEditedSystemTemplates = totalEditedSystemTemplates;
        this.totalGlobalPageTemplatesExported = totalGlobalPageTemplatesExported;
        this.totalEditedSystemTemplatesExported = totalEditedSystemTemplatesExported;
    }

    @Generated
    public GlobalEntitiesExecutionState() {
    }

    @Generated
    public Long getTotalGlobalPageTemplates() {
        return this.totalGlobalPageTemplates;
    }

    @Generated
    public Long getTotalEditedSystemTemplates() {
        return this.totalEditedSystemTemplates;
    }

    @Nullable
    @Generated
    public Long getTotalGlobalPageTemplatesExported() {
        return this.totalGlobalPageTemplatesExported;
    }

    @Nullable
    @Generated
    public Long getTotalEditedSystemTemplatesExported() {
        return this.totalEditedSystemTemplatesExported;
    }

    @Generated
    public void setTotalGlobalPageTemplates(Long totalGlobalPageTemplates) {
        this.totalGlobalPageTemplates = totalGlobalPageTemplates;
    }

    @Generated
    public void setTotalEditedSystemTemplates(Long totalEditedSystemTemplates) {
        this.totalEditedSystemTemplates = totalEditedSystemTemplates;
    }

    @Generated
    public void setTotalGlobalPageTemplatesExported(@Nullable Long totalGlobalPageTemplatesExported) {
        this.totalGlobalPageTemplatesExported = totalGlobalPageTemplatesExported;
    }

    @Generated
    public void setTotalEditedSystemTemplatesExported(@Nullable Long totalEditedSystemTemplatesExported) {
        this.totalEditedSystemTemplatesExported = totalEditedSystemTemplatesExported;
    }
}

