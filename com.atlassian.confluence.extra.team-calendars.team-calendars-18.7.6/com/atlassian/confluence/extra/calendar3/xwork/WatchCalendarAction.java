/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Collections2
 */
package com.atlassian.confluence.extra.calendar3.xwork;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.calendar3.CalendarManager;
import com.atlassian.confluence.extra.calendar3.CalendarPermissionManager;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarSummary;
import com.atlassian.confluence.extra.calendar3.model.SubscribingSubCalendarSummary;
import com.atlassian.confluence.user.ConfluenceUser;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;

public class WatchCalendarAction
extends ConfluenceActionSupport {
    private CalendarManager calendarManager;
    private CalendarPermissionManager calendarPermissionManager;
    private String subscriptionId;
    private boolean addWatch;

    public boolean isPermitted() {
        return super.isPermitted() && this.calendarPermissionManager.hasEditSubCalendarPrivilege(this.getAuthenticatedUser());
    }

    public void setCalendarManager(CalendarManager calendarManager) {
        this.calendarManager = calendarManager;
    }

    public void setCalendarPermissionManager(CalendarPermissionManager calendarPermissionManager) {
        this.calendarPermissionManager = calendarPermissionManager;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void setAddWatch(boolean addWatch) {
        this.addWatch = addWatch;
    }

    public String execute() {
        if (this.addWatch) {
            this.addWatch();
        } else {
            this.removeWatch();
        }
        return "success";
    }

    private void addWatch() {
        Collection summariesOfSubCalendarsInView = Collections2.filter((Collection)Collections2.transform(this.calendarManager.getSubCalendarsInView(this.getAuthenticatedUser()), subCalendarId -> this.calendarManager.getSubCalendarSummary((String)subCalendarId)), (Predicate)Predicates.notNull());
        for (SubCalendarSummary subCalendarSummary : summariesOfSubCalendarsInView) {
            if (!StringUtils.equals(this.subscriptionId, subCalendarSummary.getId()) && (!(subCalendarSummary instanceof SubscribingSubCalendarSummary) || !StringUtils.equals(this.subscriptionId, ((SubscribingSubCalendarSummary)subCalendarSummary).getSubscriptionId()))) continue;
            this.calendarManager.watchSubCalendar(this.calendarManager.getSubCalendar(subCalendarSummary.getId()), this.getAuthenticatedUser());
        }
    }

    private void removeWatch() {
        Collection summaryOfSubCalendarsToUnwatch = Collections2.filter((Collection)Collections2.transform(this.calendarManager.getSubCalendarsInView(this.getAuthenticatedUser()), subCalendarId -> this.calendarManager.getSubCalendarSummary((String)subCalendarId)), (Predicate)Predicates.and((Predicate)Predicates.notNull(), subCalendarSummary -> StringUtils.equals(subCalendarSummary.getId(), this.subscriptionId) || subCalendarSummary instanceof SubscribingSubCalendarSummary && StringUtils.equals(((SubscribingSubCalendarSummary)subCalendarSummary).getSubscriptionId(), this.subscriptionId)));
        ConfluenceUser loggedInUser = this.getAuthenticatedUser();
        for (SubCalendarSummary subCalendarSummary2 : summaryOfSubCalendarsToUnwatch) {
            this.calendarManager.unwatchSubCalendar(this.calendarManager.getSubCalendar(subCalendarSummary2.getId()), loggedInUser);
        }
    }
}

