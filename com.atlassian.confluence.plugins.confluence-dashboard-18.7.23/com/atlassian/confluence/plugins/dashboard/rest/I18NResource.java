/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.dashboard.rest;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/i18n")
@AnonymousAllowed
public class I18NResource {
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;

    public I18NResource(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory) {
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @POST
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    @Path(value="/")
    public Response postI18N(ArrayList<String> i18NKeys) {
        Locale locale = this.getCurrentLocale();
        Map<String, String> i18NProperties = this.loadI18N(i18NKeys, locale);
        return Response.ok(i18NProperties).build();
    }

    private Map<String, String> loadI18N(List<String> i18NKeys, Locale locale) {
        HashMap<String, String> i18NProperties = new HashMap<String, String>();
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(locale);
        for (String i18NKey : i18NKeys) {
            i18NProperties.put(i18NKey, i18NBean.getText(i18NKey));
        }
        return i18NProperties;
    }

    private Locale getCurrentLocale() {
        return this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get());
    }
}

