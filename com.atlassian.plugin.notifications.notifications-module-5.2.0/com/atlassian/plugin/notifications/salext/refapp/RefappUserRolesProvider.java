/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.salext.refapp;

import com.atlassian.plugin.notifications.spi.AbstractUserRolesProvider;
import com.atlassian.plugin.notifications.spi.DefaultUserRole;

public class RefappUserRolesProvider
extends AbstractUserRolesProvider {
    public RefappUserRolesProvider() {
        this.addRole(new DefaultUserRole("WATCHER"));
        this.addRole(new DefaultUserRole("OTHER"));
    }
}

