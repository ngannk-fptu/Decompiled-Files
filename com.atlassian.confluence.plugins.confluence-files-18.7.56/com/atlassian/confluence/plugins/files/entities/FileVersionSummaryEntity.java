/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Version
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.files.entities;

import com.atlassian.confluence.api.model.content.Version;
import java.sql.Timestamp;
import java.util.Date;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class FileVersionSummaryEntity {
    @JsonProperty
    private final long id;
    @Nullable
    @JsonProperty
    private final Long latestVersionId;
    @JsonProperty
    private final String title;
    @JsonProperty
    private final Version version;
    @JsonProperty
    private final int countComments;

    public FileVersionSummaryEntity(long id, Long latestVersionId, int version, String title, Timestamp lastModificationDate, String comment, int countComments) {
        this.id = id;
        this.latestVersionId = latestVersionId;
        this.title = title;
        this.version = Version.builder().message(comment).number(version).when((Date)lastModificationDate).build();
        this.countComments = countComments;
    }

    @JsonCreator
    public FileVersionSummaryEntity(@JsonProperty(value="id") long id, @JsonProperty(value="latestVersionId") @Nullable Long latestVersionId, @JsonProperty(value="title") String title, @JsonProperty(value="countComments") int countComments, @JsonProperty(value="version") Version version) {
        this.id = id;
        this.latestVersionId = latestVersionId;
        this.title = title;
        this.version = version;
        this.countComments = countComments;
    }

    public long getId() {
        return this.id;
    }

    @Nullable
    public Long getLatestVersionId() {
        return this.latestVersionId;
    }

    public String getTitle() {
        return this.title;
    }

    public Version getVersion() {
        return this.version;
    }

    public int getCountComments() {
        return this.countComments;
    }
}

