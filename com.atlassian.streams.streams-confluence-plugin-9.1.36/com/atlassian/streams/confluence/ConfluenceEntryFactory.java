/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.StreamsEntry
 */
package com.atlassian.streams.confluence;

import com.atlassian.streams.api.StreamsEntry;
import com.atlassian.streams.confluence.changereport.ActivityItem;
import java.net.URI;

public interface ConfluenceEntryFactory {
    public StreamsEntry buildStreamsEntry(URI var1, ActivityItem var2);
}

