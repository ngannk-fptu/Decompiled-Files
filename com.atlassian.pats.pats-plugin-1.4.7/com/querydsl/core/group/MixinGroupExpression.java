/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.AbstractGroupExpression;
import com.querydsl.core.group.GroupCollector;
import com.querydsl.core.group.GroupExpression;

public class MixinGroupExpression<E, F, R>
extends AbstractGroupExpression<E, R> {
    private static final long serialVersionUID = -5419707469727395643L;
    private final GroupExpression<F, R> mixin;
    private final GroupExpression<E, F> groupExpression;

    public MixinGroupExpression(GroupExpression<E, F> groupExpression, GroupExpression<F, R> mixin) {
        super(mixin.getType(), groupExpression.getExpression());
        this.mixin = mixin;
        this.groupExpression = groupExpression;
    }

    @Override
    public GroupCollector<E, R> createGroupCollector() {
        return new GroupCollectorImpl();
    }

    private class GroupCollectorImpl
    implements GroupCollector<E, R> {
        private final GroupCollector<F, R> mixinGroupCollector;
        private GroupCollector<E, F> groupCollector;

        public GroupCollectorImpl() {
            this.mixinGroupCollector = MixinGroupExpression.this.mixin.createGroupCollector();
        }

        @Override
        public void add(E input) {
            if (this.groupCollector == null) {
                this.groupCollector = MixinGroupExpression.this.groupExpression.createGroupCollector();
            }
            this.groupCollector.add(input);
        }

        @Override
        public R get() {
            if (this.groupCollector != null) {
                Object output = this.groupCollector.get();
                this.mixinGroupCollector.add(output);
                this.groupCollector = null;
            }
            return this.mixinGroupCollector.get();
        }
    }
}

