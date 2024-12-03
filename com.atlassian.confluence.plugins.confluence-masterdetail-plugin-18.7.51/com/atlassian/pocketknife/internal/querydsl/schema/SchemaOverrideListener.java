/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.pocketknife.internal.querydsl.schema;

import com.atlassian.pocketknife.internal.querydsl.listener.AbstractDetailedLoggingListener;
import com.atlassian.pocketknife.internal.querydsl.schema.RelationPathsInQueryMetadata;
import com.atlassian.pocketknife.internal.querydsl.schema.SchemaOverrider;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLListenerContext;
import java.util.Set;

public class SchemaOverrideListener
extends AbstractDetailedLoggingListener {
    private final Configuration configuration;
    private final SchemaOverrider schemaOverrider;

    public SchemaOverrideListener(Configuration configuration, SchemaOverrider schemaOverrider) {
        this.configuration = configuration;
        this.schemaOverrider = schemaOverrider;
    }

    private void visit(SQLListenerContext context) {
        Set<RelationalPath<?>> relationalPaths = new RelationPathsInQueryMetadata().capture(context.getMetadata());
        this.schemaOverrider.registerOverrides(context.getConnection(), this.configuration, relationalPaths);
    }

    @Override
    public void start(SQLListenerContext context) {
        this.visit(context);
    }
}

