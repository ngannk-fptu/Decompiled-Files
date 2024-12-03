/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.extra.calendar3.model.persistence;

import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarRestrictionEntity;
import net.java.ao.Entity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.Table;

@Table(value="tc_subcals_priv_usr")
public interface SubCalendarUserRestrictionEntity
extends SubCalendarRestrictionEntity,
Entity {
    @NotNull
    public SubCalendarEntity getSubCalendar();

    public void setSubCalendar(SubCalendarEntity var1);

    @NotNull
    public String getUserKey();

    public void setUserKey(String var1);
}

