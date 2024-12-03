/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.model.stats;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@ParametersAreNonnullByDefault
public final class AttachmentStats {
    public static final AttachmentStats ZERO = AttachmentStats.builder().totalSize(0L).build();
    @JsonProperty
    private final long averageSize;
    @JsonProperty
    private final long minimumSize;
    @JsonProperty
    private final long maximumSize;
    @JsonProperty
    private final Long totalSize;

    @JsonCreator
    private AttachmentStats(@JsonProperty(value="averageSize") long averageSize, @JsonProperty(value="minimumSize") long minimumSize, @JsonProperty(value="maximumSize") long maximumSize, @JsonProperty(value="totalSize") Long totalSize) {
        this.averageSize = averageSize;
        this.minimumSize = minimumSize;
        this.maximumSize = maximumSize;
        this.totalSize = totalSize;
    }

    public long getAverageSize() {
        return this.averageSize;
    }

    public long getMinimumSize() {
        return this.minimumSize;
    }

    public long getMaximumSize() {
        return this.maximumSize;
    }

    public Long getTotalSize() {
        return this.totalSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AttachmentStats)) {
            return false;
        }
        AttachmentStats other = (AttachmentStats)o;
        if (this.getAverageSize() != other.getAverageSize()) {
            return false;
        }
        if (this.getMinimumSize() != other.getMinimumSize()) {
            return false;
        }
        if (this.getMaximumSize() != other.getMaximumSize()) {
            return false;
        }
        Long this$totalSize = this.getTotalSize();
        Long other$totalSize = other.getTotalSize();
        return !(this$totalSize == null ? other$totalSize != null : !((Object)this$totalSize).equals(other$totalSize));
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $averageSize = this.getAverageSize();
        result = result * 59 + (int)($averageSize >>> 32 ^ $averageSize);
        long $minimumSize = this.getMinimumSize();
        result = result * 59 + (int)($minimumSize >>> 32 ^ $minimumSize);
        long $maximumSize = this.getMaximumSize();
        result = result * 59 + (int)($maximumSize >>> 32 ^ $maximumSize);
        Long $totalSize = this.getTotalSize();
        result = result * 59 + ($totalSize == null ? 43 : ((Object)$totalSize).hashCode());
        return result;
    }

    public static final class Builder {
        private long averageSize;
        private long minimumSize;
        private long maximumSize;
        private Long totalSize;

        private Builder() {
        }

        @Nonnull
        public Builder averageSize(long averageSize) {
            this.averageSize = averageSize;
            return this;
        }

        @Nonnull
        public Builder minimumSize(long minimumSize) {
            this.minimumSize = minimumSize;
            return this;
        }

        @Nonnull
        public Builder maximumSize(long maximumSize) {
            this.maximumSize = maximumSize;
            return this;
        }

        @Nonnull
        public Builder totalSize(Long totalSize) {
            this.totalSize = totalSize;
            return this;
        }

        @Nonnull
        public AttachmentStats build() {
            return new AttachmentStats(this.averageSize, this.minimumSize, this.maximumSize, this.totalSize);
        }
    }
}

