/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.core;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import java.io.Serializable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class JoinFlag
implements Serializable {
    private static final long serialVersionUID = -688265393547206465L;
    private final Expression<?> flag;
    private final Position position;

    public JoinFlag(String flag) {
        this(ExpressionUtils.template(Object.class, flag, new Object[0]), Position.BEFORE_TARGET);
    }

    public JoinFlag(String flag, Position position) {
        this(ExpressionUtils.template(Object.class, flag, new Object[0]), position);
    }

    public JoinFlag(Expression<?> flag) {
        this(flag, Position.BEFORE_TARGET);
    }

    public JoinFlag(Expression<?> flag, Position position) {
        this.flag = flag;
        this.position = position;
    }

    public int hashCode() {
        return this.flag.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof JoinFlag) {
            return ((JoinFlag)obj).flag.equals(this.flag);
        }
        return false;
    }

    public String toString() {
        return this.flag.toString();
    }

    public Expression<?> getFlag() {
        return this.flag;
    }

    public Position getPosition() {
        return this.position;
    }

    public static enum Position {
        START,
        OVERRIDE,
        BEFORE_TARGET,
        BEFORE_CONDITION,
        END;

    }
}

