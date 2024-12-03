/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.mappings.internal;

import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.AbstractRelationalPathBase;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.internal.UserMapping;
import com.querydsl.core.types.dsl.StringPath;

public final class UserMappingTable
extends AbstractRelationalPathBase<UserMapping> {
    public static final String TABLE_NAME = "user_mapping";
    public final StringPath USER_KEY = this.createString(this.columnName("user_key"));
    public final StringPath USERNAME = this.createString(this.columnName("username"));

    public UserMappingTable(DatabaseNameHelper databaseNameHelper, String alias) {
        super(UserMapping.class, alias, "", TABLE_NAME, databaseNameHelper);
    }
}

