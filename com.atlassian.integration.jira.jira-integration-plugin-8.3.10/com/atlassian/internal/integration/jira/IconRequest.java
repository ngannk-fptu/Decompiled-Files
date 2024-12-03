/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.internal.integration.jira;

import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;

public class IconRequest
extends LinkedHashMap<String, Object> {
    public static final String PARAM_PID = "pid";
    private static final String PARAM_SERVER_ID = "serverId";
    private static final String PARAM_ICON_TYPE = "iconType";
    private static final String PARAM_PROJECT_ID = "projectId";
    private static final String PARAM_AVATAR_ID = "avatarId";

    public IconRequest(HttpServletRequest request) {
        this.put(PARAM_SERVER_ID, request.getParameter(PARAM_SERVER_ID));
        this.put(PARAM_ICON_TYPE, request.getParameter(PARAM_ICON_TYPE));
        this.put(PARAM_PROJECT_ID, request.getParameter(PARAM_PID));
        this.put(PARAM_AVATAR_ID, request.getParameter(PARAM_AVATAR_ID));
    }

    public String getServerId() {
        return (String)this.get(PARAM_SERVER_ID);
    }

    public String getIconType() {
        return (String)this.get(PARAM_ICON_TYPE);
    }

    public String getProjectId() {
        return (String)this.get(PARAM_PROJECT_ID);
    }

    public String getAvatarId() {
        return (String)this.get(PARAM_AVATAR_ID);
    }
}

