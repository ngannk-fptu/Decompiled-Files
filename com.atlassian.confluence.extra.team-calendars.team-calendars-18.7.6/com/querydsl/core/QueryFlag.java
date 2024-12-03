/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.querydsl.core;

import com.google.common.base.Objects;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import java.io.Serializable;

public class QueryFlag
implements Serializable {
    private static final long serialVersionUID = -7131081607441961628L;
    private final Position position;
    private final Expression<?> flag;

    public QueryFlag(Position position, String flag) {
        this(position, ExpressionUtils.template(Object.class, flag, new Object[0]));
    }

    public QueryFlag(Position position, Expression<?> flag) {
        this.position = position;
        this.flag = flag;
    }

    public Position getPosition() {
        return this.position;
    }

    public Expression<?> getFlag() {
        return this.flag;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.position, this.flag});
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof QueryFlag) {
            QueryFlag other = (QueryFlag)obj;
            return other.position.equals((Object)this.position) && other.flag.equals(this.flag);
        }
        return false;
    }

    public String toString() {
        return (Object)((Object)this.position) + " : " + this.flag;
    }

    public static enum Position {
        WITH,
        START,
        START_OVERRIDE,
        AFTER_SELECT,
        AFTER_PROJECTION,
        BEFORE_FILTERS,
        AFTER_FILTERS,
        BEFORE_GROUP_BY,
        AFTER_GROUP_BY,
        BEFORE_HAVING,
        AFTER_HAVING,
        BEFORE_ORDER,
        AFTER_ORDER,
        END;

    }
}

