/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.interceptor;

import java.io.Serializable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class RollbackRuleAttribute
implements Serializable {
    public static final RollbackRuleAttribute ROLLBACK_ON_RUNTIME_EXCEPTIONS = new RollbackRuleAttribute(RuntimeException.class);
    private final String exceptionPattern;

    public RollbackRuleAttribute(Class<?> exceptionType) {
        Assert.notNull(exceptionType, (String)"'exceptionType' cannot be null");
        if (!Throwable.class.isAssignableFrom(exceptionType)) {
            throw new IllegalArgumentException("Cannot construct rollback rule from [" + exceptionType.getName() + "]: it's not a Throwable");
        }
        this.exceptionPattern = exceptionType.getName();
    }

    public RollbackRuleAttribute(String exceptionPattern) {
        Assert.hasText((String)exceptionPattern, (String)"'exceptionPattern' cannot be null or empty");
        this.exceptionPattern = exceptionPattern;
    }

    public String getExceptionName() {
        return this.exceptionPattern;
    }

    public int getDepth(Throwable exception) {
        return this.getDepth(exception.getClass(), 0);
    }

    private int getDepth(Class<?> exceptionType, int depth) {
        if (exceptionType.getName().contains(this.exceptionPattern)) {
            return depth;
        }
        if (exceptionType == Throwable.class) {
            return -1;
        }
        return this.getDepth(exceptionType.getSuperclass(), depth + 1);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RollbackRuleAttribute)) {
            return false;
        }
        RollbackRuleAttribute rhs = (RollbackRuleAttribute)other;
        return this.exceptionPattern.equals(rhs.exceptionPattern);
    }

    public int hashCode() {
        return this.exceptionPattern.hashCode();
    }

    public String toString() {
        return "RollbackRuleAttribute with pattern [" + this.exceptionPattern + "]";
    }
}

