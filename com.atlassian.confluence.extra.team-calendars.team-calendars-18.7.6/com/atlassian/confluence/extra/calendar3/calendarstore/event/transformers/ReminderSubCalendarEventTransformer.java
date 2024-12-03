/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers;

import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.WebResourceDependentSubCalendarEventTransformer;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.ReminderEvent;
import com.atlassian.confluence.extra.calendar3.model.ReminderSubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.extra.calendar3.util.EmailNotificationHelper;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;

public class ReminderSubCalendarEventTransformer
extends WebResourceDependentSubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> {
    private final WebResourceUrlProvider webResourceUrlProvider;

    public ReminderSubCalendarEventTransformer(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, BuildInformationManager buildInformationManager, WebResourceUrlProvider webResourceUrlProvider) {
        super(localeManager, i18NBeanFactory, buildInformationManager);
        this.webResourceUrlProvider = webResourceUrlProvider;
    }

    @Override
    public SubCalendarEvent transform(SubCalendarEvent toBeTransformed, ConfluenceUser forUser, SubCalendarEventTransformerFactory.TransformParameters transformParameters) {
        if (!(toBeTransformed instanceof ReminderSubCalendarEvent)) {
            return toBeTransformed;
        }
        if (!(transformParameters instanceof SubCalendarEventTransformerFactory.ReminderTransformParameters)) {
            return toBeTransformed;
        }
        ReminderSubCalendarEvent reminderSubCalendarEventToBeTransformed = (ReminderSubCalendarEvent)toBeTransformed;
        SubCalendarEventTransformerFactory.ReminderTransformParameters reminderTransformParameters = (SubCalendarEventTransformerFactory.ReminderTransformParameters)transformParameters;
        SubCalendarEventTransformerFactory subCalendarEventTransformerFactory = reminderTransformParameters.getSubCalendarEventTransformerFactory();
        toBeTransformed = subCalendarEventTransformerFactory.getDefaultTransformer().transform(toBeTransformed, forUser, transformParameters);
        CustomEventType customEventType = this.findCustomEventTypeById(toBeTransformed.getCustomEventTypeId(), reminderTransformParameters.getAvailableCustomEventTypes());
        ReminderEvent reminderEvent = reminderSubCalendarEventToBeTransformed.getReminderEvent();
        boolean needReformatResourceUrl = false;
        if (CalendarUtil.isJiraStoreKey(reminderEvent.getStoreKey())) {
            reminderSubCalendarEventToBeTransformed.setIconUrl(reminderEvent.getIconUrl());
            reminderSubCalendarEventToBeTransformed.setMediumIconUrl(reminderEvent.getMediumIconUrl());
            reminderSubCalendarEventToBeTransformed.setEventTypeIconUrl(reminderEvent.getIconUrl());
            needReformatResourceUrl = true;
        } else if (toBeTransformed.getInvitees() != null && toBeTransformed.getInvitees().size() > 0) {
            toBeTransformed = subCalendarEventTransformerFactory.getInviteesTransformer().transform(toBeTransformed, forUser, transformParameters);
            needReformatResourceUrl = true;
        } else if (customEventType == null) {
            toBeTransformed = subCalendarEventTransformerFactory.getNoInviteesTransformer().transform(toBeTransformed, forUser, transformParameters);
            needReformatResourceUrl = true;
        } else {
            toBeTransformed.setIconUrl(String.format("com/atlassian/confluence/extra/calendar3/img/customeventtype/%s_48.png", customEventType.getIcon()));
            toBeTransformed.setMediumIconUrl(String.format("com/atlassian/confluence/extra/calendar3/img/customeventtype/%s_24.png", customEventType.getIcon()));
            reminderSubCalendarEventToBeTransformed.setEventTypeIconUrl(toBeTransformed.getIconUrl());
        }
        if (StringUtils.isNotEmpty((CharSequence)toBeTransformed.getIconUrl()) && needReformatResourceUrl) {
            toBeTransformed.setIconUrl(String.format("com/atlassian/confluence/extra/calendar3/%s", EmailNotificationHelper.getSubIconUrl(toBeTransformed.getIconUrl())));
            toBeTransformed.setMediumIconUrl(String.format("com/atlassian/confluence/extra/calendar3/%s", EmailNotificationHelper.getSubIconUrl(toBeTransformed.getIconUrl())));
        }
        return toBeTransformed;
    }

    private CustomEventType findCustomEventTypeById(String currentCustomEventTypeId, Collection<CustomEventType> customEventTypes) {
        if (StringUtils.isEmpty((CharSequence)currentCustomEventTypeId)) {
            return null;
        }
        for (CustomEventType customEventType : customEventTypes) {
            if (!currentCustomEventTypeId.equals(customEventType.getCustomEventTypeId())) continue;
            return customEventType;
        }
        return null;
    }
}

