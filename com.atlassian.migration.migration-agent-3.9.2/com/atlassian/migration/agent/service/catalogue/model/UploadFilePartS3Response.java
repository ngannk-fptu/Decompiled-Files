/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.catalogue.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UploadFilePartS3Response {
    @JsonProperty(value="partNumber")
    private final Integer partNumber;
    @JsonProperty(value="etag")
    private final String etag;

    public UploadFilePartS3Response(@JsonProperty(value="partNumber") Integer partNumber, @JsonProperty(value="etag") String etag) {
        this.partNumber = partNumber;
        this.etag = etag;
    }

    public Integer getPartNumber() {
        return this.partNumber;
    }

    public String getEtag() {
        return this.etag;
    }
}

