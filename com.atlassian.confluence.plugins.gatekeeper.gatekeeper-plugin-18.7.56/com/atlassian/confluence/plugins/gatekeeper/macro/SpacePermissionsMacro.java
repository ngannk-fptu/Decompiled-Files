/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.user.GroupManager
 *  com.atlassian.user.UserManager
 */
package com.atlassian.confluence.plugins.gatekeeper.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.gatekeeper.macro.BasePermissionMacro;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.user.GroupManager;
import com.atlassian.user.UserManager;
import java.util.Map;

public class SpacePermissionsMacro
extends BasePermissionMacro
implements Macro {
    public SpacePermissionsMacro(PageManager pageManager, SpaceManager spaceManager, UserManager userManager, GroupManager groupManager, ContextPathHolder contextPathHolder) {
        super(pageManager, spaceManager, userManager, groupManager, contextPathHolder);
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        return "";
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }
}

