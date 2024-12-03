/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.Language
 *  com.atlassian.confluence.languages.LanguageManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Option
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.sal.confluence.message;

import com.atlassian.confluence.languages.Language;
import com.atlassian.confluence.languages.LanguageManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Option;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

public class ConfluenceLocaleResolver
implements LocaleResolver {
    private final Option<UserAccessor> userAccessor;
    private final LanguageManager languageManager;
    private final LocaleManager localeManager;

    public ConfluenceLocaleResolver(LocaleManager localeManager, UserAccessor userAccessor, LanguageManager languageManager) {
        this.localeManager = localeManager;
        this.userAccessor = Option.some((Object)userAccessor);
        this.languageManager = languageManager;
    }

    public ConfluenceLocaleResolver(LanguageManager languageManager, LocaleManager localeManager) {
        this.languageManager = languageManager;
        this.localeManager = localeManager;
        this.userAccessor = Option.none();
    }

    public Locale getLocale(HttpServletRequest request) {
        if (this.userAccessor.isDefined()) {
            return this.localeManager.getLocale((User)((UserAccessor)this.userAccessor.get()).getUserByName(request.getRemoteUser()));
        }
        return this.getLocale();
    }

    public Locale getLocale() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return this.localeManager.getLocale((User)user);
    }

    public Locale getLocale(UserKey userKey) {
        ConfluenceUser user = null;
        if (this.userAccessor.isDefined()) {
            user = ((UserAccessor)this.userAccessor.get()).getExistingUserByKey(userKey);
        }
        return this.localeManager.getLocale(user);
    }

    public Locale getApplicationLocale() {
        return this.localeManager.getSiteDefaultLocale();
    }

    public Set<Locale> getSupportedLocales() {
        List langs = this.languageManager.getLanguages();
        HashSet<Locale> ret = new HashSet<Locale>();
        for (Language lang : langs) {
            ret.add(lang.getLocale());
        }
        return Collections.unmodifiableSet(ret);
    }
}

