/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.apache.commons.lang3.builder.ToStringStyle
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.troubleshooting.stp.zip;

import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ParametersAreNonnullByDefault
public class SupportZipRequest
implements Serializable {
    private static final long serialVersionUID = -2556329491065171726L;
    private final Integer fileConstraintSize;
    private final Boolean limitFileSizes;
    private final Integer fileConstraintLastModified;
    private Set<String> items;
    private final Set<String> nodeIds;
    private final Source source;
    private final String clusterTaskId;

    @JsonCreator
    public SupportZipRequest(@JsonProperty(value="items") @Nullable Set<String> items, @JsonProperty(value="limitFileSizes") @Nullable Boolean limitFileSizes, @JsonProperty(value="fileConstraintSize") @Nullable Integer fileConstraintSize, @JsonProperty(value="fileConstraintLastModified") @Nullable Integer fileConstraintLastModified, @JsonProperty(value="clusterTaskId") @Nullable String clusterTaskId, @JsonProperty(value="nodeIds") @Nullable Collection<String> nodeIds, @JsonProperty(value="source") @Nullable Source source) {
        this.items = items == null ? null : new HashSet<String>(items);
        this.limitFileSizes = limitFileSizes;
        this.fileConstraintSize = fileConstraintSize;
        this.fileConstraintLastModified = fileConstraintLastModified;
        this.clusterTaskId = clusterTaskId;
        this.nodeIds = nodeIds == null ? null : ImmutableSet.copyOf(nodeIds);
        this.source = source;
    }

    @Nonnull
    public static SupportZipRequest withDefaultSettings(SupportApplicationInfo applicationInfo, Source source) {
        return new SupportZipRequest(applicationInfo.getDefaultBundleKeys(), null, null, null, null, null, source);
    }

    @Nonnull
    public SupportZipRequest forCluster() {
        return new SupportZipRequest(this.items, this.limitFileSizes, this.fileConstraintSize, this.fileConstraintLastModified, UUID.randomUUID().toString(), this.nodeIds, this.source);
    }

    @Nonnull
    public SupportZipRequest withSource(Source source) {
        return new SupportZipRequest(this.items, this.limitFileSizes, this.fileConstraintSize, this.fileConstraintLastModified, this.clusterTaskId, this.nodeIds, source);
    }

    @Nonnull
    public Set<String> getItems() {
        return this.items;
    }

    public Boolean isLimitFileSizes() {
        return this.limitFileSizes;
    }

    public Integer getFileConstraintSize() {
        return this.fileConstraintSize;
    }

    public Integer getFileConstraintLastModified() {
        return this.fileConstraintLastModified;
    }

    @Nullable
    public String getClusterTaskId() {
        return this.clusterTaskId;
    }

    public boolean appliesToNode(String nodeId) {
        return this.nodeIds == null || this.nodeIds.contains(nodeId);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString((Object)this, (ToStringStyle)ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Nullable
    public Set<String> getNodeIds() {
        return this.nodeIds;
    }

    public boolean appliesToNoNodes() {
        return this.nodeIds != null && this.nodeIds.isEmpty();
    }

    @Nullable
    public Source getSource() {
        return this.source;
    }

    public void useDefaultItems(SupportApplicationInfo applicationInfo) {
        this.items = applicationInfo.getDefaultBundleKeys();
    }

    public static enum Source {
        REST_V1("rest-v1"),
        WEB_V1("web-v1"),
        WEB_V2("web-v2");

        private final String key;

        private Source(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
}

