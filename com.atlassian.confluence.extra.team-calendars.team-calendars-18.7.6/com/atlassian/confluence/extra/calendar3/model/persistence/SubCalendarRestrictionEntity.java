/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.schema.Indexed
 *  net.java.ao.schema.NotNull
 */
package com.atlassian.confluence.extra.calendar3.model.persistence;

import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;

public interface SubCalendarRestrictionEntity {
    public static final String TYPE_VIEW = "VIEW";
    public static final String TYPE_EDIT = "EDIT";

    @Indexed
    @NotNull
    public String getType();

    public void setType(String var1);
}

