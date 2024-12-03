/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.group.Group;
import com.querydsl.core.group.GroupByIterate;
import com.querydsl.core.group.GroupByList;
import com.querydsl.core.group.GroupByMap;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GroupByBuilder<K> {
    private final Expression<K> key;

    public GroupByBuilder(Expression<K> key) {
        this.key = key;
    }

    public ResultTransformer<Map<K, Group>> as(Expression<?> ... expressions) {
        return new GroupByMap(this.key, expressions);
    }

    public ResultTransformer<CloseableIterator<Group>> iterate(Expression<?> ... expressions) {
        return new GroupByIterate(this.key, expressions);
    }

    public ResultTransformer<List<Group>> list(Expression<?> ... expressions) {
        return new GroupByList(this.key, expressions);
    }

    public <V> ResultTransformer<Map<K, V>> as(Expression<V> expression) {
        final Expression<V> lookup = this.getLookup(expression);
        return new GroupByMap<K, V>(this.key, new Expression[]{expression}){

            @Override
            protected Map<K, V> transform(Map<K, Group> groups) {
                LinkedHashMap results = new LinkedHashMap((int)Math.ceil((double)groups.size() / 0.75), 0.75f);
                for (Map.Entry entry : groups.entrySet()) {
                    results.put(entry.getKey(), entry.getValue().getOne(lookup));
                }
                return results;
            }
        };
    }

    public <V> ResultTransformer<CloseableIterator<V>> iterate(Expression<V> expression) {
        final Expression<V> lookup = this.getLookup(expression);
        return new GroupByIterate<K, V>(this.key, new Expression[]{expression}){

            @Override
            protected V transform(Group group) {
                return group.getOne(lookup);
            }
        };
    }

    public <V> ResultTransformer<List<V>> list(Expression<V> expression) {
        final Expression<V> lookup = this.getLookup(expression);
        return new GroupByList<K, V>(this.key, new Expression[]{expression}){

            @Override
            protected V transform(Group group) {
                return group.getOne(lookup);
            }
        };
    }

    private <V> Expression<V> getLookup(Expression<V> expression) {
        if (expression instanceof GroupExpression) {
            GroupExpression groupExpression = (GroupExpression)expression;
            return groupExpression.getExpression();
        }
        return expression;
    }

    public <V> ResultTransformer<Map<K, V>> as(FactoryExpression<V> expression) {
        final FactoryExpression<V> transformation = FactoryExpressionUtils.wrap(expression);
        List<Expression<?>> args = transformation.getArgs();
        return new GroupByMap<K, V>(this.key, args.toArray(new Expression[args.size()])){

            @Override
            protected Map<K, V> transform(Map<K, Group> groups) {
                LinkedHashMap results = new LinkedHashMap((int)Math.ceil((double)groups.size() / 0.75), 0.75f);
                for (Map.Entry entry : groups.entrySet()) {
                    results.put(entry.getKey(), this.transform(entry.getValue()));
                }
                return results;
            }

            protected V transform(Group group) {
                ArrayList args = new ArrayList(this.groupExpressions.size() - 1);
                for (int i = 1; i < this.groupExpressions.size(); ++i) {
                    args.add(group.getGroup((GroupExpression)this.groupExpressions.get(i)));
                }
                return transformation.newInstance(args.toArray());
            }
        };
    }

    public <V> ResultTransformer<CloseableIterator<V>> iterate(FactoryExpression<V> expression) {
        final FactoryExpression<V> transformation = FactoryExpressionUtils.wrap(expression);
        List<Expression<?>> args = transformation.getArgs();
        return new GroupByIterate<K, V>(this.key, args.toArray(new Expression[args.size()])){

            @Override
            protected V transform(Group group) {
                ArrayList args = new ArrayList(this.groupExpressions.size() - 1);
                for (int i = 1; i < this.groupExpressions.size(); ++i) {
                    args.add(group.getGroup((GroupExpression)this.groupExpressions.get(i)));
                }
                return transformation.newInstance(args.toArray());
            }
        };
    }

    public <V> ResultTransformer<List<V>> list(FactoryExpression<V> expression) {
        final FactoryExpression<V> transformation = FactoryExpressionUtils.wrap(expression);
        List<Expression<?>> args = transformation.getArgs();
        return new GroupByList<K, V>(this.key, args.toArray(new Expression[args.size()])){

            @Override
            protected V transform(Group group) {
                ArrayList args = new ArrayList(this.groupExpressions.size() - 1);
                for (int i = 1; i < this.groupExpressions.size(); ++i) {
                    args.add(group.getGroup((GroupExpression)this.groupExpressions.get(i)));
                }
                return transformation.newInstance(args.toArray());
            }
        };
    }
}

