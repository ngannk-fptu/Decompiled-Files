/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.modifier;

import com.atlassian.confluence.plugins.gatekeeper.exception.PermissionModificationException;
import com.atlassian.confluence.plugins.gatekeeper.model.modification.Modification;
import com.atlassian.confluence.plugins.gatekeeper.model.modification.ModificationResult;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.confluence.util.i18n.I18NBean;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Modifier {
    private static final Logger logger = LoggerFactory.getLogger(Modifier.class);
    private ConfluenceService confluenceService;
    private final I18NBean i18NBean;

    public Modifier(ConfluenceService confluenceService, I18NBean i18NBean) {
        this.confluenceService = confluenceService;
        this.i18NBean = i18NBean;
    }

    public ModificationResult setPermissions(Modification modification) {
        ModificationResult result = new ModificationResult();
        Map<TinyOwner, PermissionSet> permissions = modification.getPermissions();
        for (String spaceKey : modification.getSpaces()) {
            TinySpace space = this.confluenceService.getSpace(spaceKey);
            if (space == null) {
                result.addFailed(spaceKey, "", this.i18NBean.getText("com.atlassian.confluence.plugins.gatekeeper.space-not-found.error"));
                continue;
            }
            if (!this.confluenceService.canCurrentUserSetPermissions(spaceKey)) {
                result.addFailed(spaceKey, space.getName(), this.i18NBean.getText("com.atlassian.confluence.plugins.gatekeeper.not-permitted-to-modify-permissions.error"));
                continue;
            }
            try {
                this.confluenceService.setPermissions(spaceKey, permissions);
                result.addSuccessful(spaceKey, space.getName());
            }
            catch (PermissionModificationException e) {
                logger.error("Setting permissions for space failed", (Object)e.getMessage());
                result.addFailed(spaceKey, space.getName(), this.i18NBean.getText("server.error.message"));
            }
        }
        result.finish();
        return result;
    }
}

