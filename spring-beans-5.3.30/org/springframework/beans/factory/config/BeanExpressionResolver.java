/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.lang.Nullable;

public interface BeanExpressionResolver {
    @Nullable
    public Object evaluate(@Nullable String var1, BeanExpressionContext var2) throws BeansException;
}

