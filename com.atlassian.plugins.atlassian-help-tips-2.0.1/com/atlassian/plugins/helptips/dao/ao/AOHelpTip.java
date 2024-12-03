/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.java.ao.Entity
 *  net.java.ao.Preload
 *  net.java.ao.schema.Indexed
 */
package com.atlassian.plugins.helptips.dao.ao;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;

@Preload
public interface AOHelpTip
extends Entity {
    public static final String USER_KEY = "USER_KEY";
    public static final String DISMISSED_HELP_TIP = "DISMISSED_HELP_TIP";

    @Indexed
    public String getUserKey();

    public void setUserKey(String var1);

    @Indexed
    public String getDismissedHelpTip();

    public void setDismissedHelpTip(String var1);
}

