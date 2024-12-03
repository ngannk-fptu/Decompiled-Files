/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.notifications;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.notifications.ConfluenceUserRole;

@ExperimentalApi
public class SystemUserRole
extends ConfluenceUserRole {
    public static SystemUserRole INSTANCE = new SystemUserRole();

    private SystemUserRole() {
        super("SYSTEM");
    }
}

