/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.persistence.Column
 *  javax.persistence.DiscriminatorColumn
 *  javax.persistence.DiscriminatorType
 *  javax.persistence.Entity
 *  javax.persistence.FetchType
 *  javax.persistence.Id
 *  javax.persistence.JoinColumn
 *  javax.persistence.ManyToOne
 *  javax.persistence.OneToMany
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.ContentProperty;
import com.atlassian.migration.agent.entity.ProductEntity;
import com.google.common.base.MoreObjects;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@ProductEntity
@Entity
@Table(name="CONTENT")
@DiscriminatorColumn(discriminatorType=DiscriminatorType.STRING, name="CONTENTTYPE")
public abstract class Content {
    @Id
    @Column(name="CONTENTID")
    private long id;
    @Column(name="VERSION")
    private int version;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="PAGEID")
    private Content container;
    @Column(name="TITLE")
    private String title;
    @Column(name="CONTENTTYPE", insertable=false, updatable=false)
    private String type;
    @Column(name="CONTENT_STATUS")
    private String status;
    @Column(name="CREATIONDATE")
    private Date creationDate;
    @OneToMany
    @JoinColumn(name="CONTENTID")
    private List<ContentProperty> properties;
    @Column(name="PREVVER")
    private Long previousVersion;
    @Column(name="PARENTID")
    private Long parentId;
    @Column(name="SPACEID", insertable=false, updatable=false)
    private Long spaceId;
    @Column(name="LASTMODDATE")
    private Timestamp lastModDate;

    public long getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
    }

    public Content getContainer() {
        return this.container;
    }

    public String getTitle() {
        return this.title;
    }

    public String getType() {
        return this.type;
    }

    public String getStatus() {
        return this.status;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public Long getPreviousVersion() {
        return this.previousVersion;
    }

    public Long getSpaceId() {
        return this.spaceId;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("id", this.id).add("version", this.version).add("container", (Object)this.container).add("title", (Object)this.title).add("type", (Object)this.type).add("status", (Object)this.status).add("creationDate", (Object)this.creationDate).add("previousVersion", (Object)this.previousVersion).add("spaceId", (Object)this.spaceId).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Content that = (Content)o;
        return this.id == that.id;
    }

    public int hashCode() {
        return Objects.hash(this.id);
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}

