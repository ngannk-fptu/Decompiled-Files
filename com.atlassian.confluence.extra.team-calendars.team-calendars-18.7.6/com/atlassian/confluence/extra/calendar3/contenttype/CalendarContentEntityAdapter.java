/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentEntityAdapterParent
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Optional
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.contenttype;

import com.atlassian.confluence.content.ContentEntityAdapterParent;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.fugue.Option;
import com.google.common.base.Optional;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CalendarContentEntityAdapter
extends ContentEntityAdapterParent {
    CalendarManager calendarManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(CalendarContentEntityAdapter.class);

    @Autowired
    public CalendarContentEntityAdapter(CalendarManager calendarManager) {
        this.calendarManager = calendarManager;
    }

    public Option<String> getUrlPath(CustomContentEntityObject cceo) {
        if ("com.atlassian.confluence.extra.team-calendars:calendar-content-type".equals(cceo.getPluginModuleKey())) {
            String subCalendarId = this.getSubCalendarId(cceo);
            Optional<PersistedSubCalendar> subCalendarOption = this.calendarManager.getPersistedSubCalendar(subCalendarId);
            if (subCalendarOption.isPresent() && StringUtils.isNotBlank(((PersistedSubCalendar)subCalendarOption.get()).getSpaceKey())) {
                PersistedSubCalendar subCalendar = (PersistedSubCalendar)subCalendarOption.get();
                return Option.some((Object)("/display/" + subCalendar.getSpaceKey() + "/calendar/" + subCalendarId + "?calendarName=" + subCalendar.getName()));
            }
            return Option.some((Object)("/calendar/previewcalendar.action?subCalendarId=" + subCalendarId));
        }
        return Option.some((Object)("/display/" + this.getSpaceKey(cceo) + "/calendars"));
    }

    public Option<String> getDisplayTitle(CustomContentEntityObject pluginContentEntityObject) {
        return Option.some((Object)pluginContentEntityObject.getTitle());
    }

    public BodyType getDefaultBodyType(CustomContentEntityObject pluginContentEntityObject) {
        return BodyType.XHTML;
    }

    public Option<String> getExcerpt(CustomContentEntityObject pluginContentEntityObject) {
        return Option.some((Object)pluginContentEntityObject.getBodyAsString());
    }

    public boolean isIndexable(CustomContentEntityObject pluginContentEntityObject, boolean isDefaultIndexable) {
        return true;
    }

    public String getSubCalendarId(CustomContentEntityObject pluginContentEntityObject) {
        return pluginContentEntityObject.getProperties().getStringProperty("subCalendarId");
    }

    private String getSpaceKey(CustomContentEntityObject pluginContentEntityObject) {
        return pluginContentEntityObject.getProperties().getStringProperty("spaceKey");
    }
}

