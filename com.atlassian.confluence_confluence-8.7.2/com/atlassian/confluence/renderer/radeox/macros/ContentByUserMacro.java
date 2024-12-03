/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.radeox.macro.parameter.MacroParameter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.renderer.radeox.macros;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.renderer.radeox.macros.AbstractHtmlGeneratingMacro;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.radeox.macro.parameter.MacroParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentByUserMacro
extends AbstractHtmlGeneratingMacro {
    private static final Logger log = LoggerFactory.getLogger(ContentByUserMacro.class);
    private String[] myParamDescription = new String[]{"1: user"};
    private UserAccessor userAccessor;
    private ContentEntityObjectDao contentEntityObjectDao;
    private PermissionManager permissionManager;

    public String getName() {
        return "content-by-user";
    }

    public String[] getParamDescription() {
        return this.myParamDescription;
    }

    @Override
    public String getHtml(MacroParameter macroParameter) throws IllegalArgumentException, IOException {
        String username = StringUtils.defaultString((String)macroParameter.get("user", 0)).trim();
        Map<String, Object> contextMap = MacroUtils.defaultVelocityContext();
        ConfluenceUser user = this.fetchUser(username);
        List content = this.fetchContent(username);
        Collections.sort(content);
        contextMap.put("user", user);
        contextMap.put("content", content);
        try {
            return VelocityUtils.getRenderedTemplate("templates/macros/contentbyuser.vm", contextMap);
        }
        catch (Exception e) {
            log.error("Error while trying to assemble the ContentByUser result!", (Throwable)e);
            throw new IOException(e.getMessage());
        }
    }

    private ConfluenceUser fetchUser(String username) {
        return this.userAccessor.getUserByName(username);
    }

    private List fetchContent(String username) {
        List<ContentEntityObject> result = this.contentEntityObjectDao.getContentAuthoredByUser(username);
        if (result == null || result.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return this.permissionManager.getPermittedEntities(AuthenticatedUserThreadLocal.get(), Permission.VIEW, result);
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setContentEntityObjectDao(ContentEntityObjectDao contentEntityObjectDao) {
        this.contentEntityObjectDao = contentEntityObjectDao;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

