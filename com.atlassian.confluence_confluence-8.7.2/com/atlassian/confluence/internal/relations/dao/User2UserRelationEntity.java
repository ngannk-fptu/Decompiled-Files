/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.relations.dao;

import com.atlassian.confluence.internal.relations.RelatableEntityTypeEnum;
import com.atlassian.confluence.internal.relations.dao.RelationEntity;
import com.atlassian.confluence.user.ConfluenceUser;

public class User2UserRelationEntity
extends RelationEntity<ConfluenceUser, ConfluenceUser> {
    @Override
    public RelatableEntityTypeEnum getSourceType() {
        return RelatableEntityTypeEnum.USER;
    }

    @Override
    public RelatableEntityTypeEnum getTargetType() {
        return RelatableEntityTypeEnum.USER;
    }
}

