/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Ordering
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.extra.calendar3.calendarstore.InternalSubscriptionCalendarDataStore;
import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import com.atlassian.confluence.extra.calendar3.model.rest.GeneralResponseEntity;
import com.atlassian.confluence.extra.calendar3.util.CalendarUtil;
import com.google.common.collect.Ordering;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class SubCalendarsResponseEntity
extends GeneralResponseEntity {
    private static final Logger LOG = LoggerFactory.getLogger(SubCalendarsResponseEntity.class);
    @XmlElement
    private List<ExtendedSubCalendar> payload;
    @XmlElement
    private String modifiedSubCalendarId;

    public SubCalendarsResponseEntity() {
        this.setSuccess(true);
    }

    public SubCalendarsResponseEntity(List<ExtendedSubCalendar> payload) {
        this();
        this.setPayload(payload);
    }

    public SubCalendarsResponseEntity(List<ExtendedSubCalendar> payload, String modifiedSubCalendarId) {
        this();
        this.setPayload(payload);
        this.setModifiedSubCalendarId(modifiedSubCalendarId);
    }

    public void setModifiedSubCalendarId(String modifiedSubCalendarId) {
        this.modifiedSubCalendarId = modifiedSubCalendarId;
    }

    public String getModifiedSubCalendarId() {
        return this.modifiedSubCalendarId;
    }

    public void setPayload(List<ExtendedSubCalendar> payload) {
        this.payload = payload;
    }

    public List<ExtendedSubCalendar> getPayload() {
        return this.payload;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObj = super.toJson();
        if (null != this.getPayload() && !this.getPayload().isEmpty()) {
            JSONArray subCalendarArray = new JSONArray();
            for (ExtendedSubCalendar subCalendar : this.getPayload()) {
                subCalendarArray.put((Object)subCalendar.toJson());
            }
            try {
                thisObj.put("payload", (Object)subCalendarArray);
            }
            catch (JSONException jsonE) {
                LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
            }
        }
        if (StringUtils.isNotBlank(this.getModifiedSubCalendarId())) {
            try {
                thisObj.put("modifiedSubCalendarId", (Object)this.getModifiedSubCalendarId());
            }
            catch (JSONException jsonE) {
                LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
            }
        }
        return thisObj;
    }

    @XmlRootElement
    public static class ExtendedSubCalendar
    implements JsonSerializable {
        private static final Logger LOG = LoggerFactory.getLogger(ExtendedSubCalendar.class);
        @XmlElement
        private PersistedSubCalendar subCalendar;
        @XmlElement
        private boolean eventsViewable;
        @XmlElement
        private boolean reloadable;
        @XmlElement
        private boolean editable;
        @XmlElement
        private boolean eventsEditable;
        @XmlElement
        private boolean hidden;
        @XmlElement
        private boolean watched;
        @XmlElement
        private boolean reminderMe;
        @XmlElement
        private boolean watchedViaContent;
        @XmlElement
        private int subscriberCount;
        @XmlElement
        private boolean subscribedByCurrentUser;
        @XmlElement
        private boolean eventsHidden;
        @XmlElement
        private boolean deletable;
        @XmlElement
        private boolean administrable;
        @XmlElement
        private List<PermittedUser> usersPermittedToView;
        @XmlElement
        private List<String> groupsPermittedToView;
        @XmlElement
        private List<PermittedUser> usersPermittedToEdit;
        @XmlElement
        private List<String> groupsPermittedToEdit;
        @XmlElement
        private List<String> warnings;
        @XmlElement
        private List<ExtendedSubCalendar> childSubCalendars;

        public ExtendedSubCalendar(PersistedSubCalendar subCalendar, boolean eventsViewable, boolean reloadable, boolean editable, boolean eventsEditable, boolean hidden, boolean watched, boolean watchedViaContent, boolean eventsHidden, boolean deletable, boolean administrable, Set<PermittedUser> usersPermittedToView, Set<String> groupsPermittedToView, Set<PermittedUser> usersPermittedToEdit, Set<String> groupsPermittedToEdit, Set<String> warnings, boolean reminderMe) {
            this(subCalendar, eventsViewable, reloadable, editable, eventsEditable, hidden, watched, watchedViaContent, 0, false, eventsHidden, deletable, administrable, usersPermittedToView, groupsPermittedToView, usersPermittedToEdit, groupsPermittedToEdit, warnings, reminderMe);
        }

        public ExtendedSubCalendar(PersistedSubCalendar subCalendar, boolean eventsViewable, boolean reloadable, boolean editable, boolean eventsEditable, boolean hidden, boolean watched, boolean watchedViaContent, int subscriberCount, boolean subscribedByCurrentUser, boolean eventsHidden, boolean deletable, boolean administrable, Set<PermittedUser> usersPermittedToView, Set<String> groupsPermittedToView, Set<PermittedUser> usersPermittedToEdit, Set<String> groupsPermittedToEdit, Set<String> warnings, boolean reminderMe) {
            this.setEventsViewable(eventsViewable);
            this.setSubCalendar(subCalendar);
            this.setReloadable(reloadable);
            this.setEditable(editable);
            this.setEventsEditable(eventsEditable);
            this.setHidden(hidden);
            this.setWatched(watched);
            this.setWatchedViaContent(watchedViaContent);
            this.setSubscriberCount(subscriberCount);
            this.setSubscribedByCurrentUser(subscribedByCurrentUser);
            this.setEventsHidden(eventsHidden);
            this.setDeletable(deletable);
            this.setAdministrable(administrable);
            this.setUsersPermittedToView(new ArrayList<PermittedUser>(usersPermittedToView));
            this.setGroupsPermittedToView(new ArrayList<String>(groupsPermittedToView));
            this.setUsersPermittedToEdit(new ArrayList<PermittedUser>(usersPermittedToEdit));
            this.setGroupsPermittedToEdit(new ArrayList<String>(groupsPermittedToEdit));
            this.setWarnings(new ArrayList<String>(warnings));
            this.setReminderMe(reminderMe);
        }

        public ExtendedSubCalendar() {
            this(null, false, false, false, false, false, false, false, false, true, true, Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), false);
        }

        public PersistedSubCalendar getSubCalendar() {
            return this.subCalendar;
        }

        public void setSubCalendar(PersistedSubCalendar subCalendar) {
            this.subCalendar = subCalendar;
        }

        public boolean isEventsViewable() {
            return this.eventsViewable;
        }

        public void setEventsViewable(boolean eventsViewable) {
            this.eventsViewable = eventsViewable;
        }

        public boolean isReloadable() {
            return this.reloadable;
        }

        public void setReloadable(boolean reloadable) {
            this.reloadable = reloadable;
        }

        public boolean isEditable() {
            return this.editable;
        }

        public void setEditable(boolean editable) {
            this.editable = editable;
        }

        public boolean isEventsEditable() {
            return this.eventsEditable;
        }

        public void setEventsEditable(boolean eventsEditable) {
            this.eventsEditable = eventsEditable;
        }

        public boolean isHidden() {
            return this.hidden;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }

        public boolean isWatched() {
            return this.watched;
        }

        public void setWatched(boolean watched) {
            this.watched = watched;
        }

        public boolean isWatchedViaContent() {
            return this.watchedViaContent;
        }

        public void setWatchedViaContent(boolean watchedViaContent) {
            this.watchedViaContent = watchedViaContent;
        }

        public int getSubscriberCount() {
            return this.subscriberCount;
        }

        public void setSubscriberCount(int subscriberCount) {
            this.subscriberCount = subscriberCount;
        }

        public boolean isSubscribedByCurrentUser() {
            return this.subscribedByCurrentUser;
        }

        public void setSubscribedByCurrentUser(boolean subscribedByCurrentUser) {
            this.subscribedByCurrentUser = subscribedByCurrentUser;
        }

        public boolean isEventsHidden() {
            return this.eventsHidden;
        }

        public void setEventsHidden(boolean eventsHidden) {
            this.eventsHidden = eventsHidden;
        }

        public boolean isDeletable() {
            return this.deletable;
        }

        public void setDeletable(boolean deletable) {
            this.deletable = deletable;
        }

        public boolean isAdministrable() {
            return this.administrable;
        }

        public void setAdministrable(boolean administrable) {
            this.administrable = administrable;
        }

        public List<PermittedUser> getUsersPermittedToView() {
            return new ArrayList<PermittedUser>(Ordering.from((Comparator)new PermittedUserComparator()).sortedCopy(this.usersPermittedToView));
        }

        public void setUsersPermittedToView(List<PermittedUser> usersPermittedToView) {
            this.usersPermittedToView = usersPermittedToView;
        }

        public List<String> getGroupsPermittedToView() {
            return new ArrayList<String>(Ordering.from((Comparator)String.CASE_INSENSITIVE_ORDER).sortedCopy(this.groupsPermittedToView));
        }

        public void setGroupsPermittedToView(List<String> groupsPermittedToView) {
            this.groupsPermittedToView = groupsPermittedToView;
        }

        public List<PermittedUser> getUsersPermittedToEdit() {
            return new ArrayList<PermittedUser>(Ordering.from((Comparator)new PermittedUserComparator()).sortedCopy(this.usersPermittedToEdit));
        }

        public void setUsersPermittedToEdit(List<PermittedUser> usersPermittedToEdit) {
            this.usersPermittedToEdit = usersPermittedToEdit;
        }

        public List<String> getGroupsPermittedToEdit() {
            return new ArrayList<String>(Ordering.from((Comparator)String.CASE_INSENSITIVE_ORDER).sortedCopy(this.groupsPermittedToEdit));
        }

        public void setGroupsPermittedToEdit(List<String> groupsPermittedToEdit) {
            this.groupsPermittedToEdit = groupsPermittedToEdit;
        }

        public List<String> getWarnings() {
            return this.warnings;
        }

        public void setWarnings(List<String> warnings) {
            this.warnings = warnings;
        }

        public List<ExtendedSubCalendar> getChildSubCalendars() {
            return this.childSubCalendars;
        }

        public void setChildSubCalendars(List<ExtendedSubCalendar> childSubCalendars) {
            this.childSubCalendars = childSubCalendars;
        }

        public boolean isReminderMe() {
            return this.reminderMe;
        }

        public void setReminderMe(boolean reminderMe) {
            this.reminderMe = reminderMe;
        }

        @Override
        public JSONObject toJson() {
            JSONObject thisObject = new JSONObject();
            try {
                PersistedSubCalendar subCalendar = this.getSubCalendar();
                if (null != subCalendar) {
                    PersistedSubCalendar sourceSubCalendar;
                    if (subCalendar instanceof InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar && (sourceSubCalendar = ((InternalSubscriptionCalendarDataStore.InternalSubscriptionSubCalendar)subCalendar).getSourceSubCalendar()) != null && CalendarUtil.isJiraSubCalendarType(sourceSubCalendar.getType())) {
                        thisObject.put("sourceSubCalendar", (Object)sourceSubCalendar.toJson());
                    }
                    thisObject.put("subCalendar", (Object)subCalendar.toJson());
                }
                thisObject.put("eventsViewable", this.isEventsViewable());
                thisObject.put("reloadable", this.isReloadable());
                thisObject.put("editable", this.isEditable());
                thisObject.put("eventsEditable", this.isEventsEditable());
                thisObject.put("hidden", this.isHidden());
                thisObject.put("watched", this.isWatched());
                thisObject.put("watchedViaContent", this.isWatchedViaContent());
                thisObject.put("subscriberCount", this.getSubscriberCount());
                thisObject.put("subscribedByCurrentUser", this.isSubscribedByCurrentUser());
                thisObject.put("eventsHidden", this.isEventsHidden());
                thisObject.put("deletable", this.isDeletable());
                thisObject.put("administrable", this.isAdministrable());
                thisObject.put("reminderMe", this.isReminderMe());
                JSONArray usersPermittedToViewArray = new JSONArray();
                for (PermittedUser permittedUser : this.getUsersPermittedToView()) {
                    usersPermittedToViewArray.put((Object)permittedUser.toJson());
                }
                thisObject.put("usersPermittedToView", (Object)usersPermittedToViewArray);
                JSONArray groupsPermittedToViewArray = new JSONArray();
                for (String string : this.getGroupsPermittedToView()) {
                    groupsPermittedToViewArray.put((Object)string);
                }
                thisObject.put("groupsPermittedToView", (Object)groupsPermittedToViewArray);
                JSONArray jSONArray = new JSONArray();
                for (PermittedUser permittedUser : this.getUsersPermittedToEdit()) {
                    jSONArray.put((Object)permittedUser.toJson());
                }
                thisObject.put("usersPermittedToEdit", (Object)jSONArray);
                JSONArray jSONArray2 = new JSONArray();
                for (String string : this.getGroupsPermittedToEdit()) {
                    jSONArray2.put((Object)string);
                }
                thisObject.put("groupsPermittedToEdit", (Object)jSONArray2);
                JSONArray jSONArray3 = new JSONArray();
                for (String warning : this.getWarnings()) {
                    jSONArray3.put((Object)warning);
                }
                thisObject.put("warnings", (Object)jSONArray3);
                List<ExtendedSubCalendar> list = this.getChildSubCalendars();
                if (null != list && !list.isEmpty()) {
                    JSONArray childSubCalendarsArray = new JSONArray();
                    for (ExtendedSubCalendar childSubCalendar : list) {
                        childSubCalendarsArray.put((Object)childSubCalendar.toJson());
                    }
                    thisObject.put("childSubCalendars", (Object)childSubCalendarsArray);
                }
            }
            catch (JSONException jsone) {
                LOG.error("Unable to create a JSON object based on this object", (Throwable)jsone);
            }
            return thisObject;
        }

        private static class PermittedUserComparator
        implements Comparator<PermittedUser> {
            private PermittedUserComparator() {
            }

            @Override
            public int compare(PermittedUser leftUser, PermittedUser rightUser) {
                int result = leftUser.getFullName().compareTo(rightUser.getFullName());
                if (0 == result) {
                    result = leftUser.getId().compareTo(rightUser.getId());
                }
                return result;
            }
        }

        @XmlRootElement
        public static class PermittedUser
        implements JsonSerializable {
            @XmlElement
            private String id;
            @XmlElement
            private String name;
            @XmlElement
            private String fullName;
            @XmlElement
            private String avatarUrl;

            public PermittedUser() {
                this(null, null, null, null);
            }

            public PermittedUser(String id, String name, String fullName, String avatarUrl) {
                this.setId(id);
                this.setName(name);
                this.setFullName(fullName);
                this.setAvatarUrl(avatarUrl);
            }

            public String getId() {
                return this.id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return this.name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getFullName() {
                return this.fullName;
            }

            public void setFullName(String fullName) {
                this.fullName = fullName;
            }

            public String getAvatarUrl() {
                return this.avatarUrl;
            }

            public void setAvatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
            }

            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (o == null || this.getClass() != o.getClass()) {
                    return false;
                }
                PermittedUser that = (PermittedUser)o;
                return this.id != null ? this.id.equals(that.id) : that.id == null;
            }

            public int hashCode() {
                return this.id != null ? this.id.hashCode() : 0;
            }

            @Override
            public JSONObject toJson() {
                JSONObject thisObject = new JSONObject();
                try {
                    thisObject.put("id", (Object)this.getId());
                    thisObject.put("name", (Object)this.getName());
                    thisObject.put("fullName", (Object)this.getFullName());
                    thisObject.put("avatarUrl", (Object)this.getAvatarUrl());
                }
                catch (JSONException jsonE) {
                    LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
                }
                return thisObject;
            }
        }
    }
}

