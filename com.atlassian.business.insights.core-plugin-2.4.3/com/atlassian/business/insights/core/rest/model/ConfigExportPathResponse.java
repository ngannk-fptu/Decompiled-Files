/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.business.insights.core.service.ExportPathHolder;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ConfigExportPathResponse {
    private String rootExportPath;
    private boolean customPathSet;

    public ConfigExportPathResponse() {
    }

    @JsonCreator
    public ConfigExportPathResponse(@Nonnull @JsonProperty(value="rootExportPath") String rootExportPath, @JsonProperty(value="customPathSet") boolean customPathSet) {
        this.rootExportPath = Objects.requireNonNull(rootExportPath, "rootExportPath must not be null");
        this.customPathSet = customPathSet;
    }

    public static ConfigExportPathResponse from(@Nonnull ExportPathHolder exportPathHolder) {
        return new ConfigExportPathResponse(exportPathHolder.getRootExportPath().toAbsolutePath().toString(), exportPathHolder.isCustom());
    }

    @JsonProperty(value="rootExportPath")
    public String getRootExportPath() {
        return this.rootExportPath;
    }

    @JsonProperty(value="customPathSet")
    public boolean isCustomPathSet() {
        return this.customPathSet;
    }
}

