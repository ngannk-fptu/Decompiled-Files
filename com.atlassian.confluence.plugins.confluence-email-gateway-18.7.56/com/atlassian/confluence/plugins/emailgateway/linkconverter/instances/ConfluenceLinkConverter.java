/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.QueryStringUtil
 *  com.atlassian.confluence.xhtml.api.Link
 *  com.atlassian.confluence.xhtml.api.LinkBody
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.plugins.emailgateway.linkconverter.instances;

import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.plugins.emailgateway.api.BaseLinkConverter;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.QueryStringUtil;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import com.atlassian.sal.api.user.UserKey;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class ConfluenceLinkConverter
extends BaseLinkConverter<Object> {
    private final SettingsManager settingsManager;
    private final UserAccessor userAccessor;

    public ConfluenceLinkConverter(SettingsManager settingsManager, UserAccessor userAccessor) {
        this.settingsManager = settingsManager;
        this.userAccessor = userAccessor;
    }

    @Override
    public Link convert(URL link, LinkBody<Object> linkBody) {
        Map params;
        String baseUrlStr = this.settingsManager.getGlobalSettings().getBaseUrl();
        URL baseUrl = null;
        try {
            baseUrl = new URL(baseUrlStr);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        if (!baseUrl.getHost().equals(link.getHost()) || baseUrl.getPort() != link.getPort() || !baseUrl.getAuthority().equals(link.getAuthority())) {
            return null;
        }
        String baseContext = baseUrl.getPath();
        if (!link.getPath().startsWith(baseContext)) {
            return null;
        }
        String path = link.getPath().substring(baseContext.length() + 1);
        String[] parts = path.split("/");
        ResourceIdentifier ri = this.getResourceIdentifier(parts, params = QueryStringUtil.extractParams((URL)link));
        if (ri == null) {
            return null;
        }
        String anchor = this.processAnchor(link.getRef());
        return DefaultLink.builder().withDestinationResourceIdentifier(ri).withAnchor(anchor).build();
    }

    private ResourceIdentifier getResourceIdentifier(String[] parts, Map<String, String> params) {
        if (params.containsKey("focusedCommentId")) {
            return new ContentEntityResourceIdentifier((long)Integer.parseInt(params.get("focusedCommentId")));
        }
        if (params.containsKey("pageId")) {
            return new ContentEntityResourceIdentifier((long)Integer.parseInt(params.get("pageId")));
        }
        if (parts[0].equals("display")) {
            if (parts.length == 2 && parts[1].startsWith("~")) {
                return this.createUserResourceIdentifier(parts[1].substring(1));
            }
            if (parts.length == 3) {
                String title = parts[2].replaceAll("\\+", " ");
                return new PageResourceIdentifier(parts[1], title);
            }
            if (parts.length == 4 && parts[1].equals("status")) {
                return new ContentEntityResourceIdentifier((long)Integer.parseInt(parts[3]));
            }
            if (parts.length == 6) {
                String title = parts[5].replaceAll("\\+", " ");
                int year = Integer.parseInt(parts[2]);
                int month = Integer.parseInt(parts[3]) - 1;
                int day = Integer.parseInt(parts[4]);
                GregorianCalendar postingDay = new GregorianCalendar(year, month, day);
                return new BlogPostResourceIdentifier(parts[1], title, (Calendar)postingDay);
            }
        }
        if (parts[0].equals("users") && parts.length == 2) {
            if (parts[1].equals("viewuserprofile.action")) {
                return this.createUserResourceIdentifier(params.get("username"));
            }
            if (parts[1].equals("viewmyprofile.action")) {
                ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
                return UserResourceIdentifier.createFromUsernameSource((UserKey)currentUser.getKey(), (String)currentUser.getName());
            }
        }
        return null;
    }

    private UserResourceIdentifier createUserResourceIdentifier(String username) {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        if (user != null) {
            return UserResourceIdentifier.createFromUsernameSource((UserKey)user.getKey(), (String)user.getName());
        }
        return UserResourceIdentifier.createForNonExistentUser((String)username);
    }

    private String processAnchor(String anchor) {
        if (anchor == null || anchor.indexOf("comment-") == 0) {
            return null;
        }
        return anchor.substring(anchor.indexOf("-") + 1);
    }
}

