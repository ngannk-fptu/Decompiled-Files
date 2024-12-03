/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.mappings;

import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderUsersEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.AbstractRelationalPathBase;
import com.querydsl.core.types.dsl.StringPath;

public class ReminderUserTable
extends AbstractRelationalPathBase<ReminderUsersEntity> {
    public static final String TABLE_NAME = "AO_950DC3_TC_REMINDER_USERS";
    public final StringPath ID = this.createString("ID");
    public final StringPath SUB_CALENDAR_ID = this.createString("SUB_CALENDAR_ID");
    public final StringPath USER_KEY = this.createString("USER_KEY");

    public ReminderUserTable(DatabaseNameHelper databaseNameHelper, String alias) {
        super(ReminderUsersEntity.class, alias, "", TABLE_NAME, databaseNameHelper);
    }
}

