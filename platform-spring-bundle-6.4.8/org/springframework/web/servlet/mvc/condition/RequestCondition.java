/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.mvc.condition;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

public interface RequestCondition<T> {
    public T combine(T var1);

    @Nullable
    public T getMatchingCondition(HttpServletRequest var1);

    public int compareTo(T var1, HttpServletRequest var2);
}

