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
public class SiteBackupSettings {
    @JsonProperty
    private boolean skipAttachments;
    @JsonProperty
    private boolean keepPermanently;
    @JsonProperty
    private String fileNamePrefix;

    public boolean isSkipAttachments() {
        return this.skipAttachments;
    }

    public void setSkipAttachments(boolean skipAttachments) {
        this.skipAttachments = skipAttachments;
    }

    public boolean isKeepPermanently() {
        return this.keepPermanently;
    }

    public void setKeepPermanently(boolean keepPermanently) {
        this.keepPermanently = keepPermanently;
    }

    public String getFileNamePrefix() {
        return this.fileNamePrefix;
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }
}

