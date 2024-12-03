/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.conversion.api.ConversionType
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.files.entities;

import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class FileContentEntity {
    @JsonProperty
    private final long id;
    @JsonProperty
    private final ConversionType conversionType;
    @JsonProperty
    private final String contentType;
    @JsonProperty
    private final String downloadUrl;

    @JsonCreator
    public FileContentEntity(@JsonProperty(value="id") long id, @JsonProperty(value="conversionType") ConversionType conversionType, @JsonProperty(value="contentType") String contentType, @JsonProperty(value="downloadUrl") String downloadUrl) {
        this.id = id;
        this.conversionType = conversionType;
        this.contentType = contentType;
        this.downloadUrl = downloadUrl;
    }

    public long getId() {
        return this.id;
    }

    @Nullable
    public ConversionType getConversionType() {
        return this.conversionType;
    }

    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    @Nullable
    public String getDownloadUrl() {
        return this.downloadUrl;
    }
}

