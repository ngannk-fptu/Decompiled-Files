/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.LegacySpaceRssFeedAction;
import com.atlassian.confluence.util.HtmlUtil;

public class SpaceCommentRssFeedAction
extends LegacySpaceRssFeedAction {
    @Override
    public String getRssParameters() {
        Space space = this.getSpace();
        Object title = "New+Comments+Feed";
        if (space != null) {
            title = HtmlUtil.urlEncode(space.getName()) + "+" + (String)title;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("spaces=").append(this.getSpaceKey()).append("&types=comment&sort=modified&maxResults=15&publicFeed=true&rssType=").append(this.getRssType()).append("&title=").append((String)title);
        return sb.toString();
    }
}

