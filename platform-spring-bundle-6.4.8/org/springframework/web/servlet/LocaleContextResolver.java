/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.LocaleResolver;

public interface LocaleContextResolver
extends LocaleResolver {
    public LocaleContext resolveLocaleContext(HttpServletRequest var1);

    public void setLocaleContext(HttpServletRequest var1, @Nullable HttpServletResponse var2, @Nullable LocaleContext var3);
}

