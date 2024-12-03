/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.relations.dao;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.internal.relations.RelatableEntity;
import com.atlassian.confluence.internal.relations.RelatableEntityTypeEnum;
import java.io.Serializable;

public abstract class RelationEntity<S extends RelatableEntity, T extends RelatableEntity>
extends ConfluenceEntityObject
implements Serializable {
    private long id;
    private S sourceContent;
    private T targetContent;
    private RelatableEntityTypeEnum targetType;
    private RelatableEntityTypeEnum sourceType;
    private String relationName;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public T getTargetContent() {
        return this.targetContent;
    }

    public void setTargetContent(T targetContent) {
        this.targetContent = targetContent;
    }

    public S getSourceContent() {
        return this.sourceContent;
    }

    public void setSourceContent(S sourceContent) {
        this.sourceContent = sourceContent;
    }

    public RelatableEntityTypeEnum getTargetType() {
        return this.targetType;
    }

    public void setTargetType(RelatableEntityTypeEnum targetType) {
        this.targetType = targetType;
    }

    public RelatableEntityTypeEnum getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(RelatableEntityTypeEnum sourceType) {
        this.sourceType = sourceType;
    }

    public String getRelationName() {
        return this.relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }
}

