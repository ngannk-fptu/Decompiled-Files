/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto;

import com.atlassian.migration.agent.dto.AppsProgressDto;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;

public class AppMigrationProgressResponse {
    @JsonProperty
    public final List<AppsProgressDto.App> progress;

    public AppMigrationProgressResponse(List<AppsProgressDto.App> progress) {
        this.progress = progress;
    }
}

