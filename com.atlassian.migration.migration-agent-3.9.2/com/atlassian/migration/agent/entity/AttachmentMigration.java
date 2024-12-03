/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.ConstraintMode
 *  javax.persistence.Entity
 *  javax.persistence.FetchType
 *  javax.persistence.ForeignKey
 *  javax.persistence.Id
 *  javax.persistence.IdClass
 *  javax.persistence.JoinColumn
 *  javax.persistence.ManyToOne
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.Attachment;
import com.atlassian.migration.agent.entity.AttachmentMigrationId;
import com.atlassian.migration.agent.entity.CloudSite;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name="MIG_ATTACHMENT")
@Entity
@IdClass(value=AttachmentMigrationId.class)
public class AttachmentMigration {
    @Id
    @Column(name="cloudId", nullable=false)
    private String cloudId;
    @Id
    @Column(name="attachmentId", nullable=false)
    private long attachmentId;
    @Column(name="version", nullable=false)
    private int version;
    @ManyToOne
    @JoinColumn(name="cloudId", nullable=false, updatable=false, insertable=false)
    private CloudSite cloudSite;
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="attachmentId", nullable=false, updatable=false, insertable=false, foreignKey=@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
    private Attachment attachment;
    @Column(name="mediaId")
    private String mediaId;

    public CloudSite getCloudSite() {
        return this.cloudSite;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    public String getCloudId() {
        return this.cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public long getAttachmentId() {
        return this.attachmentId;
    }

    public void setAttachmentId(long attachmentId) {
        this.attachmentId = attachmentId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }
}

