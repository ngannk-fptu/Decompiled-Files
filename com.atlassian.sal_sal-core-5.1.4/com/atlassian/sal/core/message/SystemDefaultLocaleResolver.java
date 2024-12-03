/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserKey
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.core.message;

import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public class SystemDefaultLocaleResolver
implements LocaleResolver {
    public Locale getLocale(HttpServletRequest request) {
        return this.getLocale();
    }

    public Locale getLocale() {
        return Locale.getDefault();
    }

    public Locale getLocale(UserKey userKey) {
        return Locale.getDefault();
    }

    public Locale getApplicationLocale() {
        return Locale.getDefault();
    }

    public Set<Locale> getSupportedLocales() {
        return new HashSet<Locale>(Collections.singletonList(Locale.getDefault()));
    }
}

