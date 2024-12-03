/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pocketknife.internal.querydsl.dialect;

import com.atlassian.pocketknife.api.querydsl.schema.DialectProvider;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLTemplates;

public interface DialectConfiguration
extends DialectProvider {
    public SQLTemplates.Builder enrich(SQLTemplates.Builder var1);

    public Configuration enrich(Configuration var1);
}

