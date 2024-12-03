/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.Keywords;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLServerTemplates;
import com.querydsl.sql.SQLTemplates;
import java.util.Map;
import java.util.Set;

public class SQLServer2012Templates
extends SQLServerTemplates {
    public static final SQLServer2012Templates DEFAULT = new SQLServer2012Templates();
    private String topTemplate = "top {0s} ";
    private String limitOffsetTemplate = "\noffset {1} rows fetch next {0} rows only";
    private String offsetTemplate = "\noffset {0} rows";

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new SQLServer2012Templates(escape, quote);
            }
        };
    }

    public SQLServer2012Templates() {
        this(Keywords.SQLSERVER2012, '\\', false);
    }

    public SQLServer2012Templates(boolean quote) {
        this(Keywords.SQLSERVER2012, '\\', quote);
    }

    public SQLServer2012Templates(char escape, boolean quote) {
        this(Keywords.SQLSERVER2012, escape, quote);
    }

    protected SQLServer2012Templates(Set<String> keywords, char escape, boolean quote) {
        super(keywords, escape, quote);
    }

    @Override
    public void serialize(QueryMetadata metadata, boolean forCountRow, SQLSerializer context) {
        if (!forCountRow && metadata.getModifiers().isRestricting() && metadata.getOrderBy().isEmpty() && !metadata.getJoins().isEmpty()) {
            QueryModifiers mod = (metadata = metadata.clone()).getModifiers();
            if (mod.getOffset() == null) {
                metadata.addFlag(new QueryFlag(QueryFlag.Position.AFTER_SELECT, Expressions.template(Integer.class, this.topTemplate, mod.getLimit())));
            } else {
                metadata.addOrderBy(Expressions.ONE.asc());
            }
        }
        context.serializeForQuery(metadata, forCountRow);
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    @Override
    public void serializeDelete(QueryMetadata metadata, RelationalPath<?> entity, SQLSerializer context) {
        QueryModifiers mod = metadata.getModifiers();
        if (mod.isRestricting()) {
            metadata = metadata.clone();
            metadata.addFlag(new QueryFlag(QueryFlag.Position.AFTER_SELECT, Expressions.template(Integer.class, this.topTemplate, mod.getLimit())));
        }
        context.serializeForDelete(metadata, entity);
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    @Override
    public void serializeUpdate(QueryMetadata metadata, RelationalPath<?> entity, Map<Path<?>, Expression<?>> updates, SQLSerializer context) {
        QueryModifiers mod = metadata.getModifiers();
        if (mod.isRestricting()) {
            metadata = metadata.clone();
            metadata.addFlag(new QueryFlag(QueryFlag.Position.AFTER_SELECT, Expressions.template(Integer.class, this.topTemplate, mod.getLimit())));
        }
        context.serializeForUpdate(metadata, entity, updates);
        if (!metadata.getFlags().isEmpty()) {
            context.serialize(QueryFlag.Position.END, metadata.getFlags());
        }
    }

    @Override
    protected void serializeModifiers(QueryMetadata metadata, SQLSerializer context) {
        if (!metadata.getOrderBy().isEmpty()) {
            QueryModifiers mod = metadata.getModifiers();
            if (mod.getLimit() == null) {
                context.handle(this.offsetTemplate, mod.getOffset());
            } else if (mod.getOffset() == null) {
                context.handle(this.limitOffsetTemplate, mod.getLimit(), 0);
            } else {
                context.handle(this.limitOffsetTemplate, mod.getLimit(), mod.getOffset());
            }
        }
    }
}

