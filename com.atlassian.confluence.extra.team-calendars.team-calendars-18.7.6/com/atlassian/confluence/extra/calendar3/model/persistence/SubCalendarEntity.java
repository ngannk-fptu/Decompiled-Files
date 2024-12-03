/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.OneToMany
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.extra.calendar3.model.persistence;

import com.atlassian.confluence.extra.calendar3.model.persistence.CustomEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.DisableEventTypeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ExtraSubCalendarPropertyEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderUsersEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarGroupRestrictionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarUserRestrictionEntity;
import net.java.ao.OneToMany;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Preload
@Table(value="tc_subcals")
public interface SubCalendarEntity
extends RawEntity<String> {
    @PrimaryKey
    @NotNull
    public String getID();

    @NotNull
    @Indexed
    public void setStoreKey(String var1);

    public String getStoreKey();

    public SubCalendarEntity getParent();

    public void setParent(SubCalendarEntity var1);

    @OneToMany(reverse="getParent")
    public SubCalendarEntity[] getChildSubCalendarEntities();

    @StringLength(value=-1)
    @NotNull
    public String getName();

    public void setName(String var1);

    @StringLength(value=-1)
    public String getDescription();

    public void setDescription(String var1);

    public String getColour();

    public void setColour(String var1);

    public String getCreator();

    public void setCreator(String var1);

    @Indexed
    public String getSpaceKey();

    public void setSpaceKey(String var1);

    public String getTimeZoneId();

    public void setTimeZoneId(String var1);

    public SubCalendarEntity getSubscription();

    public void setSubscription(SubCalendarEntity var1);

    @OneToMany(reverse="getSubscription")
    public SubCalendarEntity[] getSubscribers();

    public long getCreated();

    public void setCreated(long var1);

    public long getLastModified();

    public void setLastModified(long var1);

    @OneToMany(reverse="getSubCalendar")
    public ExtraSubCalendarPropertyEntity[] getExtraProperties();

    @OneToMany(reverse="getSubCalendar")
    public EventEntity[] getEvents();

    @OneToMany(reverse="getSubCalendar")
    public SubCalendarUserRestrictionEntity[] getPrivilegedUsers();

    @OneToMany(reverse="getSubCalendar")
    public SubCalendarGroupRestrictionEntity[] getPrivilegedGroups();

    @OneToMany(reverse="getSubCalendar")
    public DisableEventTypeEntity[] getDisableEventTypes();

    @OneToMany(reverse="getSubCalendar")
    public ReminderUsersEntity[] getReminderUsers();

    @OneToMany(reverse="getBelongSubCalendar")
    public CustomEventTypeEntity[] getAvailableCustomEventTypes();

    public String getUsingCustomEventTypeId();

    public void setUsingCustomEventTypeId(String var1);
}

