/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.LabelParser
 *  com.atlassian.confluence.labels.ParsedLabelName
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FavouritePagesMacro
extends BaseMacro {
    private static final String TEMPLATE_NAME = "com/atlassian/confluence/plugins/macros/advanced/favpages.vm";
    private LabelManager labelManager;
    private PermissionManager permissionManager;

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        Map<String, Object> contextMap = this.getDefaultVelocityContext();
        List<Object> contents = new LinkedList<ContentEntityObject>();
        int maxResults = 5;
        try {
            maxResults = Integer.parseInt((String)parameters.get("maxResults"));
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        contextMap.put("maxResults", maxResults);
        this.addContentForLabelCollection(contents, "my:favourite");
        this.addContentForLabelCollection(contents, "my:favorite");
        contents = this.permissionManager.getPermittedEntities((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, contents);
        contents = this.filterByContentType(contents, Arrays.asList("page", "blogpost"));
        contextMap.put("contents", contents);
        return this.getRenderedTemplate(contextMap);
    }

    private void addContentForLabelCollection(Collection<ContentEntityObject> contents, String labelName) {
        Label label;
        ParsedLabelName ref = this.parseLabel(labelName);
        if (ref != null && (label = this.labelManager.getLabel(ref)) != null) {
            contents.addAll(this.labelManager.getContentForLabel(0, -1, label).getList());
        }
    }

    protected ParsedLabelName parseLabel(String labelName) {
        return LabelParser.parse((String)labelName, (User)AuthenticatedUserThreadLocal.get());
    }

    private List<ContentEntityObject> filterByContentType(List<ContentEntityObject> original, List<String> types) {
        ArrayList<ContentEntityObject> result = new ArrayList<ContentEntityObject>();
        for (ContentEntityObject contentEntityObject : original) {
            if (!types.contains(contentEntityObject.getType())) continue;
            result.add(contentEntityObject);
        }
        return result;
    }

    public void setLabelManager(LabelManager manager) {
        this.labelManager = manager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    protected String getRenderedTemplate(Map<String, Object> contextMap) {
        return VelocityUtils.getRenderedTemplate((String)TEMPLATE_NAME, contextMap);
    }

    protected Map<String, Object> getDefaultVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }
}

