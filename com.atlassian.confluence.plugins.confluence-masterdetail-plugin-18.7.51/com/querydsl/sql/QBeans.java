/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.querydsl.sql;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.util.ArrayUtils;
import com.querydsl.sql.Beans;
import com.querydsl.sql.RelationalPath;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QBeans
extends FactoryExpressionBase<Beans> {
    private static final long serialVersionUID = -4411839816134215923L;
    private final ImmutableMap<RelationalPath<?>, QBean<?>> qBeans;
    private final ImmutableList<Expression<?>> expressions;

    public QBeans(RelationalPath<?> ... beanPaths) {
        super(Beans.class);
        try {
            ImmutableList.Builder listBuilder = ImmutableList.builder();
            ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
            for (RelationalPath<?> path : beanPaths) {
                LinkedHashMap bindings = new LinkedHashMap();
                for (Path<?> column : path.getColumns()) {
                    bindings.put(column.getMetadata().getName(), column);
                    listBuilder.add(column);
                }
                mapBuilder.put(path, Projections.bean(path.getType(), bindings));
            }
            this.expressions = listBuilder.build();
            this.qBeans = mapBuilder.build();
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <R, C> R accept(Visitor<R, C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public List<Expression<?>> getArgs() {
        return this.expressions;
    }

    @Override
    public Beans newInstance(Object ... args) {
        int offset = 0;
        HashMap beans = new HashMap();
        for (Map.Entry entry : this.qBeans.entrySet()) {
            RelationalPath path = (RelationalPath)entry.getKey();
            QBean qBean = (QBean)entry.getValue();
            int argsSize = qBean.getArgs().size();
            Object[] subArgs = ArrayUtils.subarray(args, offset, offset + argsSize);
            beans.put(path, qBean.newInstance(subArgs));
            offset += argsSize;
        }
        return new Beans(beans);
    }
}

