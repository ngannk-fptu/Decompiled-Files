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

import com.atlassian.confluence.extra.calendar3.model.persistence.EventEntity;
import net.java.ao.OneToMany;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Preload
@Table(value="tc_reminder_settings")
public interface ReminderSettingEntity
extends RawEntity<String> {
    @PrimaryKey
    @NotNull
    @StringLength(value=255)
    public String getID();

    public void setID(String var1);

    @NotNull
    @Indexed
    @StringLength(value=255)
    public String getStoreKey();

    public void setStoreKey(String var1);

    @Indexed
    @NotNull
    public long getPeriod();

    public void setPeriod(long var1);

    @NotNull
    @Indexed
    @StringLength(value=255)
    public String getSubCalendarID();

    public void setSubCalendarID(String var1);

    @Indexed
    @StringLength(value=255)
    public String getCustomEventTypeID();

    public void setCustomEventTypeID(String var1);

    @OneToMany(reverse="getReminderSetting")
    public EventEntity[] getEventEntitys();

    @StringLength(value=255)
    public String getLastModifier();

    public void setLastModifier(String var1);
}

