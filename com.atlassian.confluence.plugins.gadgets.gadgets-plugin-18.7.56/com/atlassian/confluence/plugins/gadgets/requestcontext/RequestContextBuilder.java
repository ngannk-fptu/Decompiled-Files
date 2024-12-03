/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.GadgetRequestContext$Builder
 *  com.atlassian.gadgets.GadgetRequestContext$User
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.gadgets.requestcontext;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.user.User;

public class RequestContextBuilder {
    private final LocaleManager localeManager;

    public RequestContextBuilder(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public GadgetRequestContext buildRequestContext(boolean ignoreCache) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        GadgetRequestContext.Builder contextBuilder = GadgetRequestContext.Builder.gadgetRequestContext().ignoreCache(ignoreCache);
        if (user != null) {
            contextBuilder.locale(this.localeManager.getLocale((User)user)).user(new GadgetRequestContext.User(user.getKey().getStringValue(), user.getName()));
        } else {
            contextBuilder.locale(this.localeManager.getLocale(null));
        }
        return contextBuilder.build();
    }
}

