/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;

public interface NameValueExpression<T> {
    public String getName();

    @Nullable
    public T getValue();

    public boolean isNegated();
}

