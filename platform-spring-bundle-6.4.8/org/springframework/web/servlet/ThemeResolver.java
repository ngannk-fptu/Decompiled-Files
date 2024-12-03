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
import org.springframework.lang.Nullable;

public interface ThemeResolver {
    public String resolveThemeName(HttpServletRequest var1);

    public void setThemeName(HttpServletRequest var1, @Nullable HttpServletResponse var2, @Nullable String var3);
}

