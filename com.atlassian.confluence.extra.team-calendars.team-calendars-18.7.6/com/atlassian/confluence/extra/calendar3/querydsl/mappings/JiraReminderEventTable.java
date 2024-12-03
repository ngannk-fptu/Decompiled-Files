/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.mappings;

import com.atlassian.confluence.extra.calendar3.model.persistence.JiraReminderEventEntity;
import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.AbstractRelationalPathBase;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.sql.PrimaryKey;

public class JiraReminderEventTable
extends AbstractRelationalPathBase<JiraReminderEventEntity> {
    public static final String TABLE_NAME = "AO_950DC3_TC_JIRA_REMI_EVENTS";
    public final NumberPath<Integer> ID = this.createNumber("ID", Integer.class);
    public final PrimaryKey<JiraReminderEventEntity> TC_JIRA_EVENTS_PK = this.createPrimaryKey(this.ID);
    public final StringPath KEY_ID = this.createString("KEY_ID");
    public final StringPath SUB_CALENDAR_ID = this.createString("SUB_CALENDAR_ID");
    public final StringPath REMINDER_SETTING_ID = this.createString("REMINDER_SETTING_ID");
    public final StringPath USER_ID = this.createString("USER_ID");
    public final StringPath JQL = this.createString("JQL");
    public final StringPath TICKET_ID = this.createString("TICKET_ID");
    public final StringPath ASSIGNEE = this.createString("ASSIGNEE");
    public final StringPath STATUS = this.createString("STATUS");
    public final StringPath TITLE = this.createString("TITLE");
    public final StringPath SUMMARY = this.createString("SUMMARY");
    public final StringPath DESCRIPTION = this.createString("DESCRIPTION");
    public final StringPath EVENT_TYPE = this.createString("EVENT_TYPE");
    public final NumberPath<Long> UTC_START = this.createNumber("UTC_START", Long.class);
    public final NumberPath<Long> UTC_END = this.createNumber("UTC_END", Long.class);
    public final BooleanPath ALL_DAY = this.createBoolean("ALL_DAY");
    public final StringPath ISSUE_LINK = this.createString("ISSUE_LINK");
    public final StringPath ISSUE_ICON_URL = this.createString("ISSUE_ICON_URL");

    public JiraReminderEventTable(DatabaseNameHelper databaseNameHelper, String alias) {
        super(JiraReminderEventEntity.class, alias, "", TABLE_NAME, databaseNameHelper);
    }
}

