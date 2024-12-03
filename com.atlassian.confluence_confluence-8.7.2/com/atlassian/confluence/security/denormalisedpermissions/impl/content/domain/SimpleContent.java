/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.content.domain;

import com.atlassian.confluence.core.NotExportable;
import com.atlassian.confluence.pages.Page;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;

public class SimpleContent
implements Serializable,
NotExportable {
    private long id;
    private String title;
    private Instant creationDate;
    private Instant lastModificationDate;
    private ContentStatus status;
    private Integer position;
    private Long parentId;
    private Long spaceId;

    public SimpleContent() {
    }

    public SimpleContent(long id, Long spaceId, Long parentId, String title, Instant creationDate, Instant lastModificationDate, String status, Integer position) {
        this.id = id;
        this.spaceId = spaceId;
        this.parentId = parentId;
        this.title = title;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.status = ContentStatus.fromString(status);
        this.position = position;
    }

    public SimpleContent(long id, Long spaceId, Long parentId, String title, Date creationDate, Date lastModificationDate, String status, Integer position) {
        this.id = id;
        this.spaceId = spaceId;
        this.parentId = parentId;
        this.title = title;
        this.creationDate = creationDate != null ? creationDate.toInstant() : null;
        this.lastModificationDate = lastModificationDate != null ? lastModificationDate.toInstant() : null;
        this.status = ContentStatus.fromString(status);
        this.position = position;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getLastModificationDate() {
        return this.lastModificationDate;
    }

    public void setLastModificationDate(Instant lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public ContentStatus getStatus() {
        return this.status;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public Integer getPosition() {
        return this.position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getSpaceId() {
        return this.spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public void mergeFieldsFrom(SimpleContent other) {
        this.creationDate = other.creationDate;
        this.lastModificationDate = other.lastModificationDate;
        this.parentId = other.parentId;
        this.position = other.position;
        this.spaceId = other.spaceId;
        this.status = other.status;
        this.title = other.title;
    }

    public static SimpleContent from(Page page) {
        return new SimpleContent(page.getId(), (Long)page.getSpace().getId(), page.getParent() != null ? Long.valueOf(page.getParent().getId()) : null, page.getTitle(), page.getCreationDate() != null ? page.getCreationDate().toInstant() : null, page.getLastModificationDate() != null ? page.getLastModificationDate().toInstant() : null, page.getContentStatus(), page.getPosition());
    }

    public static enum ContentStatus {
        CURRENT("current"),
        TRASHED("deleted"),
        OTHER(null);

        private final String status;

        private ContentStatus(String status) {
            this.status = status;
        }

        public static ContentStatus fromString(String status) {
            if (ContentStatus.CURRENT.status.equals(status)) {
                return CURRENT;
            }
            if (ContentStatus.TRASHED.status.equals(status)) {
                return TRASHED;
            }
            return OTHER;
        }
    }
}

