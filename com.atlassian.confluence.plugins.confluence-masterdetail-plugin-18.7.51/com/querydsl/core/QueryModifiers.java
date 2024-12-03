/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  javax.annotation.Nonnegative
 *  javax.annotation.Nullable
 */
package com.querydsl.core;

import com.google.common.base.Objects;
import java.io.Serializable;
import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

public final class QueryModifiers
implements Serializable {
    private static final long serialVersionUID = 2934344588433680339L;
    public static final QueryModifiers EMPTY = new QueryModifiers();
    @Nullable
    private final Long limit;
    @Nullable
    private final Long offset;

    private static int toInt(Long l) {
        if (l <= Integer.MAX_VALUE) {
            return l.intValue();
        }
        return Integer.MAX_VALUE;
    }

    public static QueryModifiers limit(@Nonnegative long limit) {
        return new QueryModifiers(limit, null);
    }

    public static QueryModifiers offset(@Nonnegative long offset) {
        return new QueryModifiers(null, offset);
    }

    private QueryModifiers() {
        this.limit = null;
        this.offset = null;
    }

    public QueryModifiers(@Nullable Long limit, @Nullable Long offset) {
        this.limit = limit;
        if (limit != null && limit <= 0L) {
            throw new IllegalArgumentException("Limit must be greater than 0.");
        }
        this.offset = offset;
        if (offset != null && offset < 0L) {
            throw new IllegalArgumentException("Offset must not be negative.");
        }
    }

    public QueryModifiers(QueryModifiers modifiers) {
        this.limit = modifiers.getLimit();
        this.offset = modifiers.getOffset();
    }

    @Nullable
    public Long getLimit() {
        return this.limit;
    }

    @Nullable
    public Integer getLimitAsInteger() {
        return this.limit != null ? Integer.valueOf(QueryModifiers.toInt(this.limit)) : null;
    }

    @Nullable
    public Long getOffset() {
        return this.offset;
    }

    @Nullable
    public Integer getOffsetAsInteger() {
        return this.offset != null ? Integer.valueOf(QueryModifiers.toInt(this.offset)) : null;
    }

    public boolean isRestricting() {
        return this.limit != null || this.offset != null;
    }

    public <T> List<T> subList(List<T> list) {
        if (!list.isEmpty()) {
            int from = this.offset != null ? QueryModifiers.toInt(this.offset) : 0;
            int to = this.limit != null ? from + QueryModifiers.toInt(this.limit) : list.size();
            return list.subList(from, Math.min(to, list.size()));
        }
        return list;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof QueryModifiers) {
            QueryModifiers qm = (QueryModifiers)o;
            return Objects.equal((Object)qm.getLimit(), (Object)this.limit) && Objects.equal((Object)qm.getOffset(), (Object)this.offset);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.limit, this.offset});
    }
}

