/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.reminder;

import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.reminder.RemindingSettingHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="remindingSettingHelper")
public class DefaultRemindingSettingHelper
implements RemindingSettingHelper {
    private static Logger LOGGER = LoggerFactory.getLogger(DefaultRemindingSettingHelper.class);
    private final CalendarManager calendarManager;
    private final UserAccessor userAccessor;

    @Autowired
    public DefaultRemindingSettingHelper(CalendarManager calendarManager, @ComponentImport UserAccessor userAccessor) {
        this.calendarManager = calendarManager;
        this.userAccessor = userAccessor;
    }

    @Override
    public void enableRemindingFor(ConfluenceUser user, PersistedSubCalendar parentSubCalendar) {
        Collection<PersistedSubCalendar> childSubCalendarHasReminder = this.calendarManager.getRemindedChildSubCalendar(parentSubCalendar);
        for (PersistedSubCalendar childSubCalendar : childSubCalendarHasReminder) {
            try {
                this.calendarManager.setReminderFor(childSubCalendar, user, true);
            }
            catch (Exception e) {
                LOGGER.error("Could not enable reminding me option for {} on sub calendar {}", (Object)user.getFullName(), (Object)childSubCalendar.getName());
                LOGGER.error("Exception during enable reminding me", (Throwable)e);
            }
        }
    }

    @Override
    public void enableRemindingForWatcher(Collection<ConfluenceUser> subscribers, PersistedSubCalendar childSubCalendar) {
        ConfluenceUser creator = this.userAccessor.getUserByKey(new UserKey(childSubCalendar.getCreator()));
        if (null != creator) {
            subscribers.add(creator);
        }
        for (ConfluenceUser subscriber : subscribers) {
            try {
                this.calendarManager.setReminderFor(childSubCalendar, subscriber, true);
            }
            catch (Exception e) {
                LOGGER.error("Could not enable reminding me option for {} on sub calendar {}", (Object)subscriber.getFullName(), (Object)childSubCalendar.getName());
                LOGGER.error("Exception during enable reminding me", (Throwable)e);
            }
        }
    }
}

