/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.backuprestore;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SpaceRestoreSettings {
    @JsonProperty
    private String fileName;
    @JsonProperty
    private Boolean skipReindex;

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getSkipReindex() {
        return this.skipReindex;
    }

    public void setSkipReindex(Boolean skipReindex) {
        this.skipReindex = skipReindex;
    }
}

