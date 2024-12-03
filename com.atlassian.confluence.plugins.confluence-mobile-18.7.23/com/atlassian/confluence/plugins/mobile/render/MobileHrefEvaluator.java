/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.mobile.render;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.links.HrefEvaluator;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.user.User;

public class MobileHrefEvaluator
implements HrefEvaluator {
    private static final String PAGE_LINK_ROUTE_PREFIX = "#content/view/";
    private static final String USER_PROFILE_LINK_ROUTE_PREFIX = "#profile/";

    public String createHref(ConversionContext context, Object destination, String anchor) {
        if (destination instanceof AbstractPage) {
            return PAGE_LINK_ROUTE_PREFIX + ((AbstractPage)destination).getId();
        }
        if (destination instanceof User) {
            User user = (User)destination;
            String userName = GeneralUtil.isAllAscii((String)user.getName()) ? HtmlUtil.urlEncode((String)user.getName()) : GeneralUtil.doubleUrlEncode((String)user.getName());
            return USER_PROFILE_LINK_ROUTE_PREFIX + userName;
        }
        return "#";
    }
}

