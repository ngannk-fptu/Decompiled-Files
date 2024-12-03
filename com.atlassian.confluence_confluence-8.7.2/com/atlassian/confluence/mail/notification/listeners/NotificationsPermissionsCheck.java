/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.mail.notification.listeners;

import com.atlassian.user.User;
import java.io.Serializable;

public interface NotificationsPermissionsCheck
extends Serializable {
    public boolean shouldNotify(User var1);
}

