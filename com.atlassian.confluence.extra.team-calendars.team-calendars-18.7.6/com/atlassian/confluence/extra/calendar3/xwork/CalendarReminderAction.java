/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.extra.calendar3.xwork;

import com.atlassian.confluence.extra.calendar3.events.RemindingOnByEmail;
import com.atlassian.confluence.extra.calendar3.exception.CalendarException;
import com.atlassian.confluence.extra.calendar3.model.CustomEventType;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.atlassian.confluence.extra.calendar3.xwork.CalendarPreviewAction;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Option;
import com.google.common.base.Preconditions;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

public class CalendarReminderAction
extends CalendarPreviewAction {
    private String childSubCalendarId;
    private String isReminder;
    private PreviewStatus previewStatus = PreviewStatus.NORMAL;
    private boolean toggleStatus;
    private EventPublisher eventPublisher;
    private String eventTypeName;

    public PreviewStatus getPreviewStatus() {
        return this.previewStatus;
    }

    public void setPreviewStatus(PreviewStatus previewStatus) {
        this.previewStatus = previewStatus;
    }

    public String getChildSubCalendarId() {
        return this.childSubCalendarId;
    }

    public String getIsReminder() {
        return this.isReminder;
    }

    public void setIsReminder(String isReminder) {
        this.isReminder = isReminder;
    }

    public void setChildSubCalendarId(String childSubCalendarId) {
        this.childSubCalendarId = childSubCalendarId;
    }

    public boolean isToggleStatus() {
        return this.toggleStatus;
    }

    public void setToggleStatus(boolean toggleStatus) {
        this.toggleStatus = toggleStatus;
    }

    public EventPublisher getEventPublisher() {
        return this.eventPublisher;
    }

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public String getChildSubEventTypeName() {
        return StringUtils.defaultIfBlank(this.eventTypeName, "Unknown");
    }

    public boolean isPermitted() {
        return super.isPermitted() && this.getCalendarPermissionManager().hasEditSubCalendarPrivilege(this.getAuthenticatedUser());
    }

    public String toggleReminderOptionForChildSubCalendar() {
        String childSubCalendarId = this.getChildSubCalendarId();
        try {
            Preconditions.checkState((!StringUtils.isEmpty(childSubCalendarId) ? 1 : 0) != 0, (Object)"ChildSubCalendarId parameter should not be null");
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            PersistedSubCalendar childSubCalendar = this.getCalendarManager().getSubCalendar(childSubCalendarId);
            Set<CustomEventType> customEventTypes = childSubCalendar.getCustomEventTypes();
            if (customEventTypes == null || customEventTypes.size() == 0) {
                this.eventTypeName = this.getText(CalendarUtil.getEventTypePropertyFromStoreKey(childSubCalendar.getStoreKey()));
            } else {
                Option firstItem = Iterables.first(customEventTypes);
                this.eventTypeName = ((CustomEventType)firstItem.get()).getTitle();
            }
            Preconditions.checkNotNull((Object)childSubCalendar, (Object)"Could not found an instance of ChildSubCalendar");
            Preconditions.checkNotNull((Object)user, (Object)"Could not found user");
            boolean isReminding = Boolean.parseBoolean(this.getIsReminder());
            this.toggleStatus = this.getCalendarManager().setReminderFor(childSubCalendar, user, isReminding);
            if (this.toggleStatus == isReminding) {
                RemindingOnByEmail event = this.toggleStatus ? new RemindingOnByEmail((Object)this.toggleStatus, user, childSubCalendar) : new RemindingOnByEmail((Object)this.toggleStatus, user, childSubCalendar);
                this.eventPublisher.publish((Object)event);
            }
            this.setPreviewStatus(PreviewStatus.OK);
        }
        catch (IllegalStateException ex) {
            this.setPreviewStatus(PreviewStatus.MISSING_PARAMETERS);
        }
        catch (NullPointerException ex) {
            this.setPreviewStatus(PreviewStatus.MISSING_OBJECTS);
        }
        catch (CalendarException ce) {
            this.setPreviewStatus(PreviewStatus.CALENDAR_EXCEPTION);
        }
        catch (Exception ex) {
            this.setPreviewStatus(PreviewStatus.GENERAL_EXCEPTION);
        }
        return "input";
    }

    public boolean isMissingParameters() {
        return this.previewStatus == PreviewStatus.MISSING_PARAMETERS;
    }

    public boolean isMissingObjects() {
        return this.previewStatus == PreviewStatus.MISSING_OBJECTS;
    }

    public boolean isGeneralException() {
        return this.previewStatus == PreviewStatus.GENERAL_EXCEPTION;
    }

    public boolean isToggleReminderSuccess() {
        return this.previewStatus == PreviewStatus.OK;
    }

    public String getToggleStatusString() {
        return this.toggleStatus ? "on" : "off";
    }

    public boolean isCalendarException() {
        return this.previewStatus == PreviewStatus.CALENDAR_EXCEPTION;
    }

    private static enum PreviewStatus {
        NORMAL,
        OK,
        MISSING_PARAMETERS,
        MISSING_OBJECTS,
        GENERAL_EXCEPTION,
        CALENDAR_EXCEPTION;

    }
}

