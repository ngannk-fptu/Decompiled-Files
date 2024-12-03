/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 *  org.codehaus.jackson.map.annotate.JsonSerialize$Inclusion
 */
package com.atlassian.troubleshooting.stp.rest.dto;

import com.atlassian.troubleshooting.stp.rest.dto.ClusterNodeDto;
import com.atlassian.troubleshooting.stp.rest.dto.SupportZipItemDto;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class SupportZipInfoDto {
    @JsonProperty
    private final Collection<ClusterNodeDto> nodes;
    @JsonProperty
    private final List<SupportZipItemDto> itemOptions;
    @JsonProperty
    private final String instanceTitle;
    @JsonProperty
    private final Integer fileConstraintSize;
    @JsonProperty
    private final Integer fileConstraintLastModified;

    @JsonCreator
    public SupportZipInfoDto(@Nonnull @JsonProperty(value="itemOptions") List<SupportZipItemDto> itemOptions, @Nullable @JsonProperty(value="nodes") Collection<ClusterNodeDto> nodes, @Nonnull @JsonProperty(value="instanceTitle") String instanceTitle, @Nullable @JsonProperty(value="fileConstraintSize") Integer fileConstraintSize, @Nullable @JsonProperty(value="fileConstraintLastModified") Integer fileConstraintLastModified) {
        this.nodes = nodes;
        this.itemOptions = Objects.requireNonNull(itemOptions);
        this.instanceTitle = Objects.requireNonNull(instanceTitle);
        this.fileConstraintSize = fileConstraintSize;
        this.fileConstraintLastModified = fileConstraintLastModified;
    }

    @Nullable
    public Collection<ClusterNodeDto> getNodes() {
        return this.nodes;
    }

    @Nonnull
    public List<SupportZipItemDto> getItemOptions() {
        return this.itemOptions;
    }

    @Nonnull
    public String getInstanceTitle() {
        return this.instanceTitle;
    }

    @Nullable
    public Integer getFileConstraintSize() {
        return this.fileConstraintSize;
    }

    @Nullable
    public Integer getFileConstraintLastModified() {
        return this.fileConstraintLastModified;
    }
}

