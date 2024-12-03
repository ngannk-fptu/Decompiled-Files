/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.mappings.internal;

import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.AbstractRelationalPathBase;
import com.atlassian.confluence.extra.calendar3.querydsl.mappings.internal.CwdUser;
import com.querydsl.core.types.dsl.StringPath;

public class CwdUserTable
extends AbstractRelationalPathBase<CwdUser> {
    public static final String TABLE_NAME = "cwd_user";
    public final StringPath USER_NAME = this.createString(this.columnName("user_name"));

    public CwdUserTable(DatabaseNameHelper databaseNameHelper, String alias) {
        super(CwdUser.class, alias, "", TABLE_NAME, databaseNameHelper);
    }
}

