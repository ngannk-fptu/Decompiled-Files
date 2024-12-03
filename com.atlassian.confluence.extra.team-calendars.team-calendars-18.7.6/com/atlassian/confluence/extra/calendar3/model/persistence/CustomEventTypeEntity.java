/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Preload
 *  net.java.ao.RawEntity
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.PrimaryKey
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.extra.calendar3.model.persistence;

import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import net.java.ao.Preload;
import net.java.ao.RawEntity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;

@Preload
@Table(value="tc_custom_ev_types")
public interface CustomEventTypeEntity
extends RawEntity<String> {
    @PrimaryKey
    @NotNull
    public String getID();

    @NotNull
    public String getIcon();

    public void setIcon(String var1);

    @NotNull
    public String getTitle();

    public void setTitle(String var1);

    public String getCreated();

    public void setCreated(String var1);

    public SubCalendarEntity getBelongSubCalendar();

    public void setBelongSubCalendar(SubCalendarEntity var1);
}

