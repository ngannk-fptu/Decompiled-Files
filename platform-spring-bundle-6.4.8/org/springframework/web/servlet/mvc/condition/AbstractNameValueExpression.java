/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.mvc.condition;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.mvc.condition.NameValueExpression;

abstract class AbstractNameValueExpression<T>
implements NameValueExpression<T> {
    protected final String name;
    @Nullable
    protected final T value;
    protected final boolean isNegated;

    AbstractNameValueExpression(String expression) {
        int separator = expression.indexOf(61);
        if (separator == -1) {
            this.isNegated = expression.startsWith("!");
            this.name = this.isNegated ? expression.substring(1) : expression;
            this.value = null;
        } else {
            this.isNegated = separator > 0 && expression.charAt(separator - 1) == '!';
            this.name = this.isNegated ? expression.substring(0, separator - 1) : expression.substring(0, separator);
            this.value = this.parseValue(expression.substring(separator + 1));
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    @Nullable
    public T getValue() {
        return this.value;
    }

    @Override
    public boolean isNegated() {
        return this.isNegated;
    }

    public final boolean match(HttpServletRequest request) {
        boolean isMatch = this.value != null ? this.matchValue(request) : this.matchName(request);
        return this.isNegated != isMatch;
    }

    protected abstract boolean isCaseSensitiveName();

    protected abstract T parseValue(String var1);

    protected abstract boolean matchName(HttpServletRequest var1);

    protected abstract boolean matchValue(HttpServletRequest var1);

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        AbstractNameValueExpression that = (AbstractNameValueExpression)other;
        return (this.isCaseSensitiveName() ? this.name.equals(that.name) : this.name.equalsIgnoreCase(that.name)) && ObjectUtils.nullSafeEquals(this.value, that.value) && this.isNegated == that.isNegated;
    }

    public int hashCode() {
        int result = this.isCaseSensitiveName() ? this.name.hashCode() : this.name.toLowerCase().hashCode();
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        result = 31 * result + (this.isNegated ? 1 : 0);
        return result;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.value != null) {
            builder.append(this.name);
            if (this.isNegated) {
                builder.append('!');
            }
            builder.append('=');
            builder.append(this.value);
        } else {
            if (this.isNegated) {
                builder.append('!');
            }
            builder.append(this.name);
        }
        return builder.toString();
    }
}

