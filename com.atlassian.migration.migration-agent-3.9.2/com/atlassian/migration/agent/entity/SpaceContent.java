/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Entity
 *  javax.persistence.FetchType
 *  javax.persistence.JoinColumn
 *  javax.persistence.ManyToOne
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.Content;
import com.atlassian.migration.agent.entity.ProductEntity;
import com.atlassian.migration.agent.entity.Space;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@ProductEntity
@Entity
public abstract class SpaceContent
extends Content {
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="SPACEID", nullable=false)
    private Space space;

    public Space getSpace() {
        return this.space;
    }
}

