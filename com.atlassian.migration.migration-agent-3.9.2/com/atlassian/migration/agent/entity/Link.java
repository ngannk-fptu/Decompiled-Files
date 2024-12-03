/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.JoinColumn
 *  javax.persistence.ManyToOne
 *  javax.persistence.Table
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.Content;
import com.atlassian.migration.agent.entity.ProductEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Generated;

@ProductEntity
@Entity
@Table(name="LINKS")
public class Link {
    @Id
    @Column(name="LINKID")
    private long id;
    @Column(name="DESTPAGETITLE")
    private String destPageTitle;
    @Column(name="LOWERDESTPAGETITLE")
    private String lowerDestPageTitle;
    @Column(name="DESTSPACEKEY")
    private String destSpaceKey;
    @Column(name="LOWERDESTSPACEKEY")
    private String lowerDestSpaceKey;
    @ManyToOne
    @JoinColumn(name="CONTENTID")
    private Content content;

    @Generated
    public long getId() {
        return this.id;
    }

    @Generated
    public String getDestPageTitle() {
        return this.destPageTitle;
    }

    @Generated
    public String getLowerDestPageTitle() {
        return this.lowerDestPageTitle;
    }

    @Generated
    public String getDestSpaceKey() {
        return this.destSpaceKey;
    }

    @Generated
    public String getLowerDestSpaceKey() {
        return this.lowerDestSpaceKey;
    }

    @Generated
    public Content getContent() {
        return this.content;
    }
}

