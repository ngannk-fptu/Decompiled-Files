/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3;

import com.atlassian.confluence.user.ConfluenceUser;

public interface PrivateCalendarUrlManager {
    public String getTokenFor(ConfluenceUser var1, String var2);

    public ConfluenceUser getUserFor(String var1);

    public String getCalendarId(String var1);

    public void resetPrivateUrlsFor(ConfluenceUser var1, String var2);

    public void resetAllPrivateUrls();
}

