/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.backuprestore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SpaceBackupSettings {
    @JsonProperty
    private final Set<String> spaceKeys = new HashSet<String>();
    @JsonProperty
    private boolean keepPermanently;
    @JsonProperty
    private String fileNamePrefix;

    public Set<String> getSpaceKeys() {
        return this.spaceKeys;
    }

    public void setSpaceKeys(Collection<String> spaceKeys) {
        this.spaceKeys.addAll(spaceKeys);
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

