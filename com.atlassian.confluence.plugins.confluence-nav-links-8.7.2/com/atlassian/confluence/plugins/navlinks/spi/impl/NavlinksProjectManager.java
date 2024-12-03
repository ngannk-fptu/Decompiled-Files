/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.navlink.spi.Project
 *  com.atlassian.plugins.navlink.spi.ProjectManager
 *  com.atlassian.plugins.navlink.spi.ProjectNotFoundException
 */
package com.atlassian.confluence.plugins.navlinks.spi.impl;

import com.atlassian.plugins.navlink.spi.Project;
import com.atlassian.plugins.navlink.spi.ProjectManager;
import com.atlassian.plugins.navlink.spi.ProjectNotFoundException;

public class NavlinksProjectManager
implements ProjectManager {
    public Project getProjectByKey(String s) throws ProjectNotFoundException {
        throw new ProjectNotFoundException(s);
    }
}

