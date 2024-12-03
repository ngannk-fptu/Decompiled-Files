/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ConstraintMode
 *  javax.persistence.DiscriminatorValue
 *  javax.persistence.Entity
 *  javax.persistence.ForeignKey
 *  javax.persistence.JoinColumn
 *  javax.persistence.OneToMany
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.AttachmentMigration;
import com.atlassian.migration.agent.entity.SpaceContent;
import java.util.List;
import javax.persistence.ConstraintMode;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue(value="ATTACHMENT")
public class Attachment
extends SpaceContent {
    @OneToMany
    @JoinColumn(name="attachmentId", nullable=false, updatable=false, insertable=false, foreignKey=@ForeignKey(value=ConstraintMode.NO_CONSTRAINT))
    private List<AttachmentMigration> migrations;
}

