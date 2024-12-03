/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.confluence.extra.calendar3.model.persistence;

import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Preload
@Table(value="tc_subcals_props")
public interface ExtraSubCalendarPropertyEntity
extends Entity {
    @NotNull
    public void setSubCalendar(SubCalendarEntity var1);

    public SubCalendarEntity getSubCalendar();

    @NotNull
    @Indexed
    public String getKey();

    public void setKey(String var1);

    @StringLength(value=-1)
    @NotNull
    public String getValue();

    public void setValue(String var1);
}

