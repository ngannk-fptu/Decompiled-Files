/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.OneToMany
 *  net.java.ao.Preload
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.extra.calendar3.model.persistence;

import com.atlassian.confluence.extra.calendar3.model.persistence.EventRecurrenceExclusionEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.InviteeEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.ReminderSettingEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import net.java.ao.Entity;
import net.java.ao.OneToMany;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Preload
@Table(value="tc_events")
public interface EventEntity
extends Entity {
    @Indexed
    @NotNull
    public String getVeventUid();

    public void setVeventUid(String var1);

    @NotNull
    public SubCalendarEntity getSubCalendar();

    public void setSubCalendar(SubCalendarEntity var1);

    @Indexed
    @NotNull
    public long getStart();

    public void setStart(long var1);

    @Indexed
    @NotNull
    public long getEnd();

    public void setEnd(long var1);

    @Indexed
    public long getUtcStart();

    public void setUtcStart(long var1);

    @Indexed
    public long getUtcEnd();

    public void setUtcEnd(long var1);

    @NotNull
    public boolean isAllDay();

    public void setAllDay(boolean var1);

    @StringLength(value=-1)
    public String getSummary();

    public void setSummary(String var1);

    @StringLength(value=-1)
    public String getDescription();

    public void setDescription(String var1);

    @StringLength(value=-1)
    public String getLocation();

    public void setLocation(String var1);

    @StringLength(value=-1)
    public String getUrl();

    public void setUrl(String var1);

    public String getOrganiser();

    public void setOrganiser(String var1);

    public String getRecurrenceRule();

    public void setRecurrenceRule(String var1);

    public Long getRecurrenceIdTimestamp();

    public void setRecurrenceIdTimestamp(Long var1);

    @OneToMany(reverse="getEvent")
    public InviteeEntity[] getInvitees();

    @OneToMany(reverse="getEvent")
    public EventRecurrenceExclusionEntity[] getExclusions();

    @NotNull
    public long getCreated();

    public void setCreated(long var1);

    public long getLastModified();

    public void setLastModified(long var1);

    @NotNull
    public int getSequence();

    public void setSequence(int var1);

    public ReminderSettingEntity getReminderSetting();

    public void setReminderSetting(ReminderSettingEntity var1);
}

