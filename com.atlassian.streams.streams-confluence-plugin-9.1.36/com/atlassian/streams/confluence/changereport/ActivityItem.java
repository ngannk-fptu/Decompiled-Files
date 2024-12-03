/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.streams.api.ActivityVerb
 *  com.atlassian.streams.api.StreamsEntry$ActivityObject
 *  com.atlassian.streams.api.StreamsEntry$Renderer
 *  com.atlassian.streams.api.common.Option
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.streams.api.ActivityVerb;
import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.api.common.Option;
import java.util.Date;

public interface ActivityItem {
    public Long getId();

    public String getUrlPath();

    public Option<String> getSpaceKey();

    public String getChangedBy();

    public Date getModified();

    public String getType();

    public String getIconPath();

    public boolean isAcceptingCommentsFromUser(String var1);

    public String getContentType();

    public Iterable<StreamsEntry.ActivityObject> getActivityObjects();

    public ActivityVerb getVerb();

    public Option<StreamsEntry.ActivityObject> getTarget();

    public StreamsEntry.Renderer getRenderer();

    public int getVersion();

    public ConfluenceEntityObject getEntity();
}

