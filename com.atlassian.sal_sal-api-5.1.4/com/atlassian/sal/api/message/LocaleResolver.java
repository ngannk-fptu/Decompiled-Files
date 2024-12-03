/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.api.message;

import com.atlassian.sal.api.user.UserKey;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

public interface LocaleResolver {
    public Locale getLocale(HttpServletRequest var1);

    public Locale getLocale();

    public Locale getLocale(@Nullable UserKey var1);

    public Locale getApplicationLocale();

    public Set<Locale> getSupportedLocales();
}

