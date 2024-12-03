/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.UserManager
 */
package com.atlassian.confluence.plugins.gatekeeper.macro;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.user.GroupManager;
import com.atlassian.user.UserManager;

class BasePermissionMacro {
    protected static final String PLUGIN_KEY = "hu.metainf.confluence.macro.ultimatepermissions";
    protected static final String RESOURCE_KEY = "effective-permissions-resources";
    protected final PageManager pageManager;
    protected final SpaceManager spaceManager;
    protected final UserManager userManager;
    protected final GroupManager groupManager;
    protected final ContextPathHolder contextPathHolder;
    private String imagePath;
    private String imageCheckPath;
    private String imageErrorPath;

    public BasePermissionMacro(PageManager pageManager, SpaceManager spaceManager, UserManager userManager, GroupManager groupManager, ContextPathHolder contextPathHolder) {
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.contextPathHolder = contextPathHolder;
        this.imagePath = contextPathHolder.getContextPath() + "/download/resources/hu.metainf.confluence.macro.ultimatepermissions:effective-permissions-resources/images/";
        this.imageCheckPath = "<img src=\"" + this.imagePath + "check.png\">";
        this.imageErrorPath = "<img src=\"" + this.imagePath + "error.png\">";
    }

    protected String generateImageUrl(boolean enabled) {
        return enabled ? this.imageCheckPath : this.imageErrorPath;
    }
}

