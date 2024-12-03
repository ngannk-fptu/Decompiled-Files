/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.spaces.actions.LegacySpaceRssFeedAction;

public class BlogPostRssFeedAction
extends LegacySpaceRssFeedAction {
    @Override
    public String getRssParameters() {
        StringBuilder sb = new StringBuilder();
        sb.append("spaces=").append(this.getSpaceKey()).append("&amp;types=blogpost&amp;sort=modified&amp;maxResults=15&amp;publicFeed=true&amp;rssType=").append(this.getRssType());
        return sb.toString();
    }
}

