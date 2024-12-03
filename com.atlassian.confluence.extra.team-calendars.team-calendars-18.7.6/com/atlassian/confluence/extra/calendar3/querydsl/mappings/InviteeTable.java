/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.mappings;

import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.AbstractRelationalPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

public class InviteeTable
extends AbstractRelationalPathBase<InviteeEntity> {
    public static final String TABLE_NAME = "AO_950DC3_TC_EVENTS_INVITEES";
    public final NumberPath<Integer> ID = this.createNumber("ID", Integer.class);
    public final NumberPath<Integer> EVENT_ID = this.createNumber("EVENT_ID", Integer.class);
    public final StringPath INVITEE_ID = this.createString("INVITEE_ID");

    public InviteeTable(DatabaseNameHelper databaseNameHelper, String alias) {
        super(InviteeEntity.class, alias, "", TABLE_NAME, databaseNameHelper);
    }
}

