/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.CalendarRenderer;
import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class UserCalendarPreference
implements JsonSerializable,
Serializable {
    private static final long serialVersionUID = -5737665625011659462L;
    private static final Logger LOG = LoggerFactory.getLogger(UserCalendarPreference.class);
    public static final String MESSAGE_KEY_TIMEZONE_SETUP = "MESSAGE_KEY_TIMEZONE_SETUP";
    public static final String MESAGE_KEY_PREFIX_WHATS_NEW = "MESSAGE_KEY_PREFIX_WHATSNEW_";
    public static final String MESSAGE_KEY_SHOW_CALENDAR_DISCOVERY = "MESSAGE_KEY_CALENDAR_DISCOVERY";
    public static final String MESSAGE_KEY_FULL_SIZED_EMPTY_UPCOMING_EVENTS = "MESSAGE_KEY_FULL_SIZED_EMPTY_UPCOMING_EVENTS";
    @XmlElement(name="view")
    private String calendarView;
    @XmlElement
    private Set<String> subCalendarsInView;
    @XmlElement
    private Set<String> watchedSubCalendars;
    @XmlElement
    private Set<String> disabledMessageKeys;
    @XmlElement
    private Set<String> disabledSubCalendars;
    @XmlElement
    private Set<String> showOnboardingSpaces;

    public UserCalendarPreference() {
        this.setCalendarView(CalendarRenderer.CalendarView.month.toString());
        this.setSubCalendarsInView(new HashSet<String>());
        this.setWatchedSubCalendars(new HashSet<String>());
        this.setDisabledMessageKeys(new HashSet<String>());
        this.setDisabledSubCalendars(new HashSet<String>());
        this.setShowOnboardingSpaces(new HashSet<String>());
    }

    public UserCalendarPreference(UserCalendarPreference userCalendarPreference) {
        if (null != userCalendarPreference) {
            this.setCalendarView(userCalendarPreference.getCalendarView());
            this.setSubCalendarsInView(userCalendarPreference.getSubCalendarsInView());
            this.setWatchedSubCalendars(userCalendarPreference.getWatchedSubCalendars());
            this.setDisabledMessageKeys(userCalendarPreference.getDisabledMessageKeys());
            this.setDisabledSubCalendars(userCalendarPreference.getDisabledSubCalendars());
            this.setShowOnboardingSpaces(userCalendarPreference.getShowOnboardingSpaces());
        }
    }

    public String getCalendarView() {
        return this.calendarView;
    }

    public void setCalendarView(String calendarView) {
        this.calendarView = calendarView;
    }

    public Set<String> getSubCalendarsInView() {
        return this.subCalendarsInView;
    }

    public void setSubCalendarsInView(Set<String> subCalendarsInView) {
        this.subCalendarsInView = subCalendarsInView;
    }

    public Set<String> getWatchedSubCalendars() {
        return this.watchedSubCalendars;
    }

    public void setWatchedSubCalendars(Set<String> watchedSubCalendars) {
        this.watchedSubCalendars = watchedSubCalendars;
    }

    public Set<String> getDisabledMessageKeys() {
        return this.disabledMessageKeys;
    }

    public void setDisabledMessageKeys(Set<String> disabledMessageKeys) {
        this.disabledMessageKeys = disabledMessageKeys;
    }

    public Set<String> getDisabledSubCalendars() {
        return this.disabledSubCalendars;
    }

    public void setDisabledSubCalendars(Set<String> disabledSubCalendars) {
        this.disabledSubCalendars = disabledSubCalendars;
    }

    public Set<String> getShowOnboardingSpaces() {
        return this.showOnboardingSpaces;
    }

    public void setShowOnboardingSpaces(Set<String> showOnboardingSpaces) {
        this.showOnboardingSpaces = showOnboardingSpaces;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            Set<String> set;
            Set<String> disabledMessageKeys;
            Set<String> watchedSubCalendars;
            thisObject.put("view", (Object)this.getCalendarView());
            Set<String> subCalendarsInView = this.getSubCalendarsInView();
            if (null != subCalendarsInView && !subCalendarsInView.isEmpty()) {
                JSONArray subCalendarsInViewArray = new JSONArray();
                for (String string : subCalendarsInView) {
                    subCalendarsInViewArray.put((Object)string);
                }
                thisObject.put("subCalendarsInView", (Object)subCalendarsInViewArray);
            }
            if (null != (watchedSubCalendars = this.getWatchedSubCalendars()) && !watchedSubCalendars.isEmpty()) {
                JSONArray watchedSubCalendarsArray = new JSONArray();
                for (String string : watchedSubCalendars) {
                    watchedSubCalendarsArray.put((Object)string);
                }
                thisObject.put("watchedSubCalendars", (Object)watchedSubCalendarsArray);
            }
            if (null != (disabledMessageKeys = this.getDisabledMessageKeys()) && !disabledMessageKeys.isEmpty()) {
                JSONArray jSONArray = new JSONArray();
                for (String messageKey : disabledMessageKeys) {
                    jSONArray.put((Object)messageKey);
                }
                thisObject.put("disabledMessageKeys", (Object)jSONArray);
            }
            if (null != (set = this.getDisabledSubCalendars()) && !set.isEmpty()) {
                JSONArray jSONArray = new JSONArray();
                for (String subCalendarId : set) {
                    jSONArray.put((Object)subCalendarId);
                }
                thisObject.put("disabledSubCalendars", (Object)jSONArray);
            }
        }
        catch (JSONException e) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)e);
        }
        return thisObject;
    }
}

