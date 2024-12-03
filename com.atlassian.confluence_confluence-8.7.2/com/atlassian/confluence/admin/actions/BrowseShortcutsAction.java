/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.ParameterSafe
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.admin.actions;

import com.atlassian.confluence.content.render.xhtml.links.WebLink;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.renderer.ShortcutLinkConfig;
import com.atlassian.confluence.renderer.ShortcutLinksManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.ParameterSafe;
import com.atlassian.xwork.PermittedMethods;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

@WebSudoRequired
@AdminOnly
public class BrowseShortcutsAction
extends ConfluenceActionSupport {
    private ShortcutLinksManager shortcutLinksManager;
    private String key;
    private ShortcutLinkConfig shortcutLinkConfig;

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    @Override
    public void validate() {
        super.validate();
        if (this.shortcutLinkConfig != null && StringUtils.isNotEmpty((CharSequence)this.shortcutLinkConfig.getExpandedValue()) && !WebLink.isValidURL(this.shortcutLinkConfig.getExpandedValue())) {
            this.addFieldError("shortcutLinkConfig.expandedValue", this.getText("shortcut.expanded.value.invalid"));
        }
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @ParameterSafe
    public ShortcutLinkConfig getShortcutLinkConfig() {
        return this.shortcutLinkConfig;
    }

    public void setShortcutLinkConfig(ShortcutLinkConfig shortcutLinkConfig) {
        this.shortcutLinkConfig = shortcutLinkConfig;
    }

    public Map getShortcuts() {
        return this.shortcutLinksManager.getShortcutLinks();
    }

    public String doAdd() throws Exception {
        this.shortcutLinksManager.addShortcutLink(this.key, this.shortcutLinkConfig);
        return "success";
    }

    public String doRemove() throws Exception {
        this.shortcutLinksManager.removeShortcutLink(this.key);
        return "success";
    }

    public void setShortcutLinksManager(ShortcutLinksManager shortcutLinksManager) {
        this.shortcutLinksManager = shortcutLinksManager;
    }
}

