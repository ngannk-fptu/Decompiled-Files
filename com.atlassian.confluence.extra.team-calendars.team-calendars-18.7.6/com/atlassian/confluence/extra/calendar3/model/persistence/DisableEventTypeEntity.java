/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.extra.calendar3.model.persistence;

import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

@Preload
@Table(value="tc_disable_ev_types")
public interface DisableEventTypeEntity
extends Entity {
    @NotNull
    public SubCalendarEntity getSubCalendar();

    public void setSubCalendar(SubCalendarEntity var1);

    @NotNull
    public String getEventKey();

    public void setEventKey(String var1);
}

