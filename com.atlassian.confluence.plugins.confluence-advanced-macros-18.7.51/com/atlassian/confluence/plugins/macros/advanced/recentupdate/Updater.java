/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

import com.atlassian.velocity.htmlsafe.HtmlSafe;

public interface Updater {
    @HtmlSafe
    public String getLinkedProfilePicture();

    @HtmlSafe
    public String getLinkedFullName();

    @HtmlSafe
    public String getUsername();
}

