/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers;

import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.AbstractSubCalendarEventTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.util.HtmlCleaner;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import net.fortuna.ical4j.model.property.Description;
import org.apache.commons.lang.StringUtils;

public class SubCalendarEventDescriptionHtmlCleaningTransformer
extends AbstractSubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> {
    private final HtmlCleaner htmlCleaner;

    public SubCalendarEventDescriptionHtmlCleaningTransformer(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, HtmlCleaner htmlCleaner) {
        super(localeManager, i18NBeanFactory);
        this.htmlCleaner = htmlCleaner;
    }

    @Override
    public SubCalendarEvent transform(SubCalendarEvent toBeTransformed, ConfluenceUser forUser, SubCalendarEventTransformerFactory.TransformParameters transformParameters) {
        String eventDescValue;
        Description eventDescProperty = transformParameters.getRawEvent().getDescription();
        if (null != eventDescProperty && StringUtils.isNotBlank(eventDescValue = eventDescProperty.getValue())) {
            toBeTransformed.setDescriptionRendered(this.htmlCleaner.clean(eventDescValue));
        }
        return toBeTransformed;
    }
}

