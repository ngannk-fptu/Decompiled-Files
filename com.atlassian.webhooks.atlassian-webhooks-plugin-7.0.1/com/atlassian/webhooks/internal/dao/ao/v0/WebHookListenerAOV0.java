/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.schema.NotNull
 *  net.java.ao.schema.StringLength
 *  net.java.ao.schema.Table
 */
package com.atlassian.webhooks.internal.dao.ao.v0;

import java.util.Date;
import net.java.ao.Entity;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

@Table(value="WEB_HOOK_LISTENER_AO")
public interface WebHookListenerAOV0
extends Entity {
    @NotNull
    @StringLength(value=-1)
    public String getUrl();

    public void setUrl(String var1);

    @NotNull
    @StringLength(value=-1)
    public String getName();

    public void setName(String var1);

    @StringLength(value=-1)
    public String getDescription();

    public void setDescription(String var1);

    public String getLastUpdatedUser();

    public void setLastUpdatedUser(String var1);

    @NotNull
    public Date getLastUpdated();

    public void setLastUpdated(Date var1);

    public boolean isExcludeBody();

    public void setExcludeBody(boolean var1);

    @StringLength(value=-1)
    public String getFilters();

    public void setFilters(String var1);

    @StringLength(value=-1)
    public String getParameters();

    public void setParameters(String var1);

    @NotNull
    public String getRegistrationMethod();

    public void setRegistrationMethod(String var1);

    @StringLength(value=-1)
    public String getEvents();

    public void setEvents(String var1);

    public boolean isEnabled();

    public void setEnabled(boolean var1);
}

