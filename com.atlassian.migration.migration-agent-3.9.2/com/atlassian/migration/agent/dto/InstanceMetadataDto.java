/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.ProductDto;
import lombok.Generated;

public class InstanceMetadataDto {
    ProductDto product;
    String sen;
    String serverId;
    String instanceTimezone;
    String assessmentDate;

    @Generated
    public ProductDto getProduct() {
        return this.product;
    }

    @Generated
    public String getSen() {
        return this.sen;
    }

    @Generated
    public String getServerId() {
        return this.serverId;
    }

    @Generated
    public String getInstanceTimezone() {
        return this.instanceTimezone;
    }

    @Generated
    public String getAssessmentDate() {
        return this.assessmentDate;
    }

    @Generated
    public InstanceMetadataDto(ProductDto product, String sen, String serverId, String instanceTimezone, String assessmentDate) {
        this.product = product;
        this.sen = sen;
        this.serverId = serverId;
        this.instanceTimezone = instanceTimezone;
        this.assessmentDate = assessmentDate;
    }
}

