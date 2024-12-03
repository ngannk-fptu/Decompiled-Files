/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.spaces.SpaceDescription
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.streams.confluence.changereport.ActivityItem;
import java.net.URI;

interface ContentEntityActivityItemFactory {
    public ActivityItem newActivityItem(URI var1, AbstractPage var2);

    public ActivityItem newActivityItem(URI var1, Comment var2);

    public ActivityItem newActivityItem(SpaceDescription var1, boolean var2);
}

