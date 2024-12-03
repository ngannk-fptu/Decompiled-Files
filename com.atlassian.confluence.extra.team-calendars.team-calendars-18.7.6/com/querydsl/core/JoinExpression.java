/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableSet
 */
package com.querydsl.core;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.querydsl.core.JoinFlag;
import com.querydsl.core.JoinType;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import java.io.Serializable;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class JoinExpression
implements Serializable {
    private static final long serialVersionUID = -1131755765747174886L;
    @Nullable
    private final Predicate condition;
    private final ImmutableSet<JoinFlag> flags;
    private final Expression<?> target;
    private final JoinType type;

    public JoinExpression(JoinType type, Expression<?> target) {
        this(type, target, null, (Set<JoinFlag>)ImmutableSet.of());
    }

    public JoinExpression(JoinType type, Expression<?> target, @Nullable Predicate condition, Set<JoinFlag> flags) {
        this.type = type;
        this.target = target;
        this.condition = condition;
        this.flags = ImmutableSet.copyOf(flags);
    }

    @Nullable
    public Predicate getCondition() {
        return this.condition;
    }

    public Expression<?> getTarget() {
        return this.target;
    }

    public JoinType getType() {
        return this.type;
    }

    public boolean hasFlag(JoinFlag flag) {
        return this.flags.contains((Object)flag);
    }

    public Set<JoinFlag> getFlags() {
        return this.flags;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append((Object)this.type).append(" ").append(this.target);
        if (this.condition != null) {
            builder.append(" on ").append(this.condition);
        }
        return builder.toString();
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.condition, this.target, this.type});
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof JoinExpression) {
            JoinExpression j = (JoinExpression)o;
            return Objects.equal((Object)this.condition, (Object)j.condition) && Objects.equal(this.target, j.target) && Objects.equal((Object)((Object)this.type), (Object)((Object)j.type));
        }
        return false;
    }
}

