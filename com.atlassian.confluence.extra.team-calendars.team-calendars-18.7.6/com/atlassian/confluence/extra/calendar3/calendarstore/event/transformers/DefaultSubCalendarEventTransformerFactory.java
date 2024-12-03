/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.extra.calendar3.SubCalendarColorRegistry;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.DefaultSubCalendarEventTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.JiraSubCalendarEventTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.ReminderSubCalendarEventTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventDescriptionHtmlCleaningTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventNotSupportingInviteesTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventSupportingInviteesTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.notification.ResourceDataHandler;
import com.atlassian.confluence.extra.calendar3.rest.validators.event.impl.UrlFieldValidator;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.extra.calendar3.util.HtmlCleaner;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DefaultSubCalendarEventTransformerFactory
implements SubCalendarEventTransformerFactory {
    private final SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> defaultSubCalendarEventTransformer;
    private final SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> subCalendarEventSupportingInviteesTransformer;
    private final SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> subCalendarEventNotSupportingInviteesTransformer;
    private final SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> subCalendarEventDescriptionHtmlCleaningTransformer;
    private final SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.JiraSubCalendarEventTransformParameters> jiraSubCalendarEventTransformer;
    private final SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> reminderSubCalendarEventTransformer;

    @Autowired
    public DefaultSubCalendarEventTransformerFactory(@ComponentImport SettingsManager settingsManager, @ComponentImport SpaceManager spaceManager, @ComponentImport PageManager pageManager, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, @ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport @Qualifier(value="cacheFactory") CacheFactory cacheFactory, BuildInformationManager buildInformationManager, HtmlCleaner htmlCleaner, SubCalendarColorRegistry subCalendarColorRegistry, ResourceDataHandler resourceDataHandler, UrlFieldValidator urlFieldValidator) {
        this.defaultSubCalendarEventTransformer = new DefaultSubCalendarEventTransformer(localeManager, i18NBeanFactory, settingsManager, spaceManager, pageManager, cacheFactory, urlFieldValidator);
        this.subCalendarEventSupportingInviteesTransformer = new SubCalendarEventSupportingInviteesTransformer(localeManager, i18NBeanFactory, buildInformationManager, settingsManager, webResourceUrlProvider, resourceDataHandler);
        this.subCalendarEventNotSupportingInviteesTransformer = new SubCalendarEventNotSupportingInviteesTransformer(localeManager, i18NBeanFactory, buildInformationManager, webResourceUrlProvider, cacheFactory);
        this.subCalendarEventDescriptionHtmlCleaningTransformer = new SubCalendarEventDescriptionHtmlCleaningTransformer(localeManager, i18NBeanFactory, htmlCleaner);
        this.jiraSubCalendarEventTransformer = new JiraSubCalendarEventTransformer(localeManager, i18NBeanFactory, buildInformationManager, webResourceUrlProvider, subCalendarColorRegistry);
        this.reminderSubCalendarEventTransformer = new ReminderSubCalendarEventTransformer(localeManager, i18NBeanFactory, buildInformationManager, webResourceUrlProvider);
    }

    @Override
    public SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> getDefaultTransformer() {
        return this.defaultSubCalendarEventTransformer;
    }

    @Override
    public SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> getInviteesTransformer() {
        return this.subCalendarEventSupportingInviteesTransformer;
    }

    @Override
    public SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> getNoInviteesTransformer() {
        return this.subCalendarEventNotSupportingInviteesTransformer;
    }

    @Override
    public SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> getDescriptionHtmlCleaningTransformer() {
        return this.subCalendarEventDescriptionHtmlCleaningTransformer;
    }

    @Override
    public SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.JiraSubCalendarEventTransformParameters> getJiraTransformer() {
        return this.jiraSubCalendarEventTransformer;
    }

    @Override
    public SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> getReminderTransformer() {
        return this.reminderSubCalendarEventTransformer;
    }
}

