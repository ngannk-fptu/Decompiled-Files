/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.spaces.actions.LegacySpaceRssFeedAction;

public class SpaceRssFeedAction
extends LegacySpaceRssFeedAction {
    @Override
    public String getRssParameters() {
        StringBuilder sb = new StringBuilder();
        sb.append("spaces=").append(this.getSpaceKey()).append("&types=page&sort=modified&maxResults=15&publicFeed=true&rssType=").append(this.getRssType());
        return sb.toString();
    }
}

