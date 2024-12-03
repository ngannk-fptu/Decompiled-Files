/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 */
package com.atlassian.streams.confluence.changereport;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.streams.confluence.changereport.ActivityItem;
import com.atlassian.streams.confluence.changereport.AttachmentActivityItem;
import java.net.URI;

interface AttachmentActivityItemFactory {
    public ActivityItem newActivityItem(URI var1, Attachment var2);

    public ActivityItem newActivityItem(URI var1, Attachment var2, AttachmentActivityItem var3);
}

