/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.mvc.condition;

import java.util.Collection;
import java.util.StringJoiner;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

public abstract class AbstractRequestCondition<T extends AbstractRequestCondition<T>>
implements RequestCondition<T> {
    public boolean isEmpty() {
        return this.getContent().isEmpty();
    }

    protected abstract Collection<?> getContent();

    protected abstract String getToStringInfix();

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        return this.getContent().equals(((AbstractRequestCondition)other).getContent());
    }

    public int hashCode() {
        return this.getContent().hashCode();
    }

    public String toString() {
        String infix = this.getToStringInfix();
        StringJoiner joiner = new StringJoiner(infix, "[", "]");
        for (Object expression : this.getContent()) {
            joiner.add(expression.toString());
        }
        return joiner.toString();
    }
}

