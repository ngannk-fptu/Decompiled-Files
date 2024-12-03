/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;

public interface LocaleResolver {
    public Locale resolveLocale(HttpServletRequest var1);

    public void setLocale(HttpServletRequest var1, @Nullable HttpServletResponse var2, @Nullable Locale var3);
}

