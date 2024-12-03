/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.spaces.actions.ViewSpaceAction;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;

public abstract class LegacySpaceRssFeedAction
extends ViewSpaceAction {
    private String rssType;

    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public abstract String getRssParameters();

    public String getRssType() {
        if (this.rssType == null) {
            return "rss";
        }
        return this.rssType;
    }

    public void setRssType(String rssType) {
        this.rssType = rssType;
    }
}

