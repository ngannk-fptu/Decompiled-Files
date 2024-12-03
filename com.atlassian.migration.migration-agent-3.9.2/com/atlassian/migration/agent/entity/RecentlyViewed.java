/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Column
 *  javax.persistence.Entity
 *  javax.persistence.Id
 *  javax.persistence.Table
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.ProductEntity;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@ProductEntity
@Entity
@Table(name="AO_92296B_AORECENTLY_VIEWED")
public class RecentlyViewed {
    @Id
    @Column(name="CONTENT_ID")
    private long contentId;
    @Column(name="LAST_VIEW_DATE")
    private Timestamp lastViewDate;
    @Column(name="SPACE_KEY")
    private String spaceKey;
}

