/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.rules;

import com.atlassian.confluence.impl.retention.rules.ContentType;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

public class HistoricalVersion {
    private final Long id;
    private final Long originalId;
    private final Long spaceId;
    private final Integer version;
    private final Instant lastModificationDate;
    private final ContentType contentType;

    public HistoricalVersion(Long id, Long originalId, Long spaceId, Integer version, Date lastModificationDate, String contentType) {
        this.id = id;
        this.originalId = originalId;
        this.spaceId = spaceId;
        this.version = version;
        this.lastModificationDate = lastModificationDate.toInstant();
        this.contentType = ContentType.valueOf(contentType);
    }

    public Long getId() {
        return this.id;
    }

    public Long getOriginalId() {
        return this.originalId;
    }

    public Long getSpaceId() {
        return this.spaceId;
    }

    public Integer getVersion() {
        return this.version;
    }

    public Instant getLastModificationDate() {
        return this.lastModificationDate;
    }

    public ContentType getContentType() {
        return this.contentType;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof HistoricalVersion)) {
            return false;
        }
        HistoricalVersion historicalVersion = (HistoricalVersion)obj;
        return Objects.equals(historicalVersion.id, this.id) && Objects.equals(historicalVersion.originalId, this.originalId) && Objects.equals(historicalVersion.spaceId, this.spaceId) && Objects.equals(historicalVersion.version, this.version) && Objects.equals(historicalVersion.lastModificationDate, this.lastModificationDate) && Objects.equals((Object)historicalVersion.contentType, (Object)this.contentType);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.id, this.originalId, this.spaceId, this.version, this.lastModificationDate, this.contentType});
    }

    public String toString() {
        return "HistoricalVersion{id=" + this.id + ", originalId=" + this.originalId + ", spaceId=" + this.spaceId + ", version=" + this.version + ", lastModificationDate=" + this.lastModificationDate + ", contentType=" + this.contentType + "}";
    }
}

