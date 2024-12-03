/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.navlink.spi.Project
 *  com.atlassian.plugins.navlink.spi.ProjectPermissionManager
 */
package com.atlassian.confluence.plugins.navlinks.spi.impl;

import com.atlassian.plugins.navlink.spi.Project;
import com.atlassian.plugins.navlink.spi.ProjectPermissionManager;

public class NavlinksProjectPermissionManager
implements ProjectPermissionManager {
    public boolean canAdminister(Project project, String s) {
        return false;
    }
}

