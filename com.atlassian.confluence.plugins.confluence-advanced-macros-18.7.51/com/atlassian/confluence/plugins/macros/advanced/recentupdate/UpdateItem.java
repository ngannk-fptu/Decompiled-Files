/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.confluence.plugins.macros.advanced.recentupdate.Updater;
import com.atlassian.velocity.htmlsafe.HtmlSafe;

public interface UpdateItem {
    @HtmlSafe
    public Updater getUpdater();

    @HtmlSafe
    public String getUpdateTargetTitle();

    @HtmlSafe
    public String getBody();

    @HtmlSafe
    public String getFormattedDate();

    @HtmlSafe
    public String getIconClass();

    @HtmlSafe
    public String getDescriptionAndDate();

    @HtmlSafe
    public String getDescriptionAndAuthor();
}

