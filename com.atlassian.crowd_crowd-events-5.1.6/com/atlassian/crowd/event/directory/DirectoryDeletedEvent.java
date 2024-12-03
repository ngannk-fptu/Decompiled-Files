/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.event.directory;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;

public class DirectoryDeletedEvent
extends DirectoryEvent {
    public DirectoryDeletedEvent(Object source, Directory directory) {
        super(source, directory);
    }
}

