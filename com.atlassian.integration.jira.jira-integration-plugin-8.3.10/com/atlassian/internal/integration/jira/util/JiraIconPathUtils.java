/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.internal.integration.jira.util;

import com.atlassian.internal.integration.jira.IconRequest;
import com.google.common.collect.ImmutableMap;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

public class JiraIconPathUtils {
    private static final Map<String, String> ICON_PATHS = ImmutableMap.of((Object)"project", (Object)"/secure/projectavatar?pid={projectId}&avatarId={avatarId}");

    public static final String getIconPath(IconRequest request) throws UnsupportedEncodingException {
        String iconPath = ICON_PATHS.get(request.getIconType());
        if (iconPath == null) {
            throw new IllegalArgumentException("Icon type " + request.getIconType() + " is not supported");
        }
        UriBuilder uriBuilder = UriBuilder.fromPath((String)iconPath);
        return URLDecoder.decode(uriBuilder.buildFromMap((Map)request).toString(), "UTF-8");
    }
}

