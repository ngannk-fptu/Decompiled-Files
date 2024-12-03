/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers;

import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.AbstractSubCalendarEventTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;

public abstract class WebResourceDependentSubCalendarEventTransformer<T extends SubCalendarEventTransformerFactory.TransformParameters>
extends AbstractSubCalendarEventTransformer<T> {
    private final String calendarResourcesModuleKey;

    protected WebResourceDependentSubCalendarEventTransformer(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, BuildInformationManager buildInformationManager) {
        super(localeManager, i18NBeanFactory);
        this.calendarResourcesModuleKey = buildInformationManager.getPluginKey() + ":calendar-resources";
    }

    protected String getCalendarResourceModuleKey() {
        return this.calendarResourcesModuleKey;
    }
}

