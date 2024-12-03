/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.mobile.dto.metadata;

import com.atlassian.confluence.plugins.mobile.dto.metadata.AbstractActionMetadataDto;
import org.codehaus.jackson.annotate.JsonProperty;

public class ShareActionMetadataDto
extends AbstractActionMetadataDto {
    @JsonProperty
    private String message;
    @JsonProperty
    private String groupName;

    public ShareActionMetadataDto(String message) {
        this.message = message;
    }

    public ShareActionMetadataDto(String message, String groupName) {
        this.message = message;
        this.groupName = groupName;
    }

    public String getMessage() {
        return this.message;
    }

    public String getGroupName() {
        return this.groupName;
    }
}

