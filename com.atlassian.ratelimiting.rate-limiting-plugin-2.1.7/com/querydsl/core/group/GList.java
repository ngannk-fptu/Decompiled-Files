/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.AbstractGroupExpression;
import com.querydsl.core.group.GroupCollector;
import com.querydsl.core.types.Expression;
import java.util.ArrayList;
import java.util.List;

class GList<T>
extends AbstractGroupExpression<T, List<T>> {
    private static final long serialVersionUID = -5613861506383727078L;

    public GList(Expression<T> expr) {
        super(List.class, expr);
    }

    @Override
    public GroupCollector<T, List<T>> createGroupCollector() {
        return new GroupCollector<T, List<T>>(){
            private final List<T> list = new ArrayList();

            @Override
            public void add(T o) {
                if (o != null) {
                    this.list.add(o);
                }
            }

            @Override
            public List<T> get() {
                return this.list;
            }
        };
    }
}

