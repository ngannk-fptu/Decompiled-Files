/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.AppendingFactoryExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.Keywords;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLSerializer;
import com.querydsl.sql.SQLServerTemplates;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.WindowFunction;
import java.util.Map;
import java.util.Set;

public class SQLServer2005Templates
extends SQLServerTemplates {
    public static final SQLServer2005Templates DEFAULT = new SQLServer2005Templates();
    private String topTemplate = "top ({0}) ";
    private String outerQueryStart = "select * from (\n  ";
    private String outerQueryEnd = ") a where ";
    private String limitOffsetTemplate = "rn > {0} and rn <= {1}";
    private String offsetTemplate = "rn > {0}";
    private String outerQuerySuffix = " order by rn";

    public static SQLTemplates.Builder builder() {
        return new SQLTemplates.Builder(){

            @Override
            protected SQLTemplates build(char escape, boolean quote) {
                return new SQLServer2005Templates(escape, quote);
            }
        };
    }

    public SQLServer2005Templates() {
        this(Keywords.SQLSERVER2005, '\\', false);
    }

    public SQLServer2005Templates(boolean quote) {
        this(Keywords.SQLSERVER2005, '\\', quote);
    }

    public SQLServer2005Templates(char escape, boolean quote) {
        this(Keywords.SQLSERVER2005, escape, quote);
    }

    protected SQLServer2005Templates(Set<String> keywords, char escape, boolean quote) {
        super(keywords, escape, quote);
        this.add(Ops.MathOps.LOG, "(LOG({0}) / LOG({1}))");
    }

    @Override
    public void serialize(QueryMetadata metadata, boolean forCountRow, SQLSerializer context) {
        if (!forCountRow && metadata.getModifiers().isRestricting() && !metadata.getJoins().isEmpty()) {
            QueryModifiers mod = metadata.getModifiers();
            if (mod.getOffset() == null) {
                metadata = metadata.clone();
                metadata.addFlag(new QueryFlag(QueryFlag.Position.AFTER_SELECT, Expressions.template(Integer.class, this.topTemplate, mod.getLimit())));
                context.serializeForQuery(metadata, forCountRow);
            } else {
                context.append(this.outerQueryStart);
                metadata = metadata.clone();
                WindowFunction<Long> rn = SQLExpressions.rowNumber().over();
                for (OrderSpecifier<?> os : metadata.getOrderBy()) {
                    rn.orderBy(os);
                }
                if (metadata.getOrderBy().isEmpty()) {
                    rn.orderBy((OrderSpecifier<?>)Expressions.currentTimestamp().asc());
                }
                AppendingFactoryExpression<?> pr = Projections.appending(metadata.getProjection(), rn.as("rn"));
                metadata.setProjection(FactoryExpressionUtils.wrap(pr));
                metadata.clearOrderBy();
                context.serializeForQuery(metadata, forCountRow);
                context.append(this.outerQueryEnd);
                if (mod.getLimit() == null) {
                    context.handle(this.offsetTemplate, mod.getOffset());
                } else {
                    context.handle(this.limitOffsetTemplate, mod.getOffset(), mod.getLimit() + mod.getOffset());
                }
                context.append(this.outerQuerySuffix);
            }
        } else {
            context.serializeForQuery(metadata, forCountRow);
        }
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
}

