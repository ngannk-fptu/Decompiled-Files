/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.meetingnotes;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.BusinessBlueprintsContextProviderHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.user.User;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class MeetingNotesContextProvider
extends AbstractBlueprintContextProvider {
    private static final String TEMPLATE_PROVIDER_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-business-blueprints:meeting-notes-resources";
    private static final String TEMPLATE_NAME = "Confluence.Templates.Meeting.Notes.userMention.soy";
    private static final String USER_MENTION_KEY = "userkey";
    private TemplateRenderer templateRenderer;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final BusinessBlueprintsContextProviderHelper helper;

    public MeetingNotesContextProvider(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, TemplateRenderer templateRenderer, BusinessBlueprintsContextProviderHelper helper) {
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
        this.templateRenderer = templateRenderer;
        this.helper = helper;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        String pageTitle = this.i18nBean().getText("meeting.notes.blueprint.page.title", Arrays.asList(this.helper.getFormattedLocalDate("yyyy-MM-dd")));
        StringBuilder userMention = new StringBuilder();
        HashMap<String, String> soyContext = new HashMap<String, String>();
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user != null) {
            soyContext.put(USER_MENTION_KEY, user.getKey().getStringValue());
        }
        this.templateRenderer.renderTo((Appendable)userMention, TEMPLATE_PROVIDER_PLUGIN_KEY, TEMPLATE_NAME, soyContext);
        context.put("documentOwner", (Object)userMention.toString());
        context.put("currentDate", (Object)this.helper.getFormattedLocalDate(null));
        context.put("currentDateLozenge", (Object)this.helper.createStorageFormatForToday());
        context.setTitle(pageTitle);
        return context;
    }

    private Locale getLocale() {
        return this.localeManager.getLocale(this.getUser());
    }

    private User getUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    private I18NBean i18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.getLocale());
    }
}

