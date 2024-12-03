/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Accessor
 *  net.java.ao.Entity
 *  net.java.ao.Mutator
 *  net.java.ao.OneToMany
 *  net.java.ao.schema.StringLength
 */
package com.atlassian.plugin.notifications.config.ao;

import com.atlassian.plugin.notifications.config.ao.ServerParam;
import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.OneToMany;
import net.java.ao.schema.StringLength;

public interface ServerConfig
extends Entity {
    public String getNotificationMediumKey();

    @Accessor(value="SERVER_NAME")
    public String getName();

    @Mutator(value="SERVER_NAME")
    public void setName(String var1);

    public boolean isEnabledForAllUsers();

    public void setEnabledForAllUsers(boolean var1);

    @OneToMany
    public ServerParam[] getServerParams();

    public String getDefaultUserIdTemplate();

    public void setDefaultUserIdTemplate(String var1);

    @StringLength(value=-1)
    public String getCustomTemplatePath();

    public void setCustomTemplatePath(String var1);

    @StringLength(value=-1)
    public String getGroupsWithAccess();

    public void setGroupsWithAccess(String var1);
}

