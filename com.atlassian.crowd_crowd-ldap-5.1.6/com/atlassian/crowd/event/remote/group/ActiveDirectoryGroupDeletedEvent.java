/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.event.remote.group;

import com.atlassian.crowd.event.remote.ActiveDirectoryEntityDeletedEvent;
import com.atlassian.crowd.event.remote.group.RemoteGroupEvent;
import com.atlassian.crowd.model.Tombstone;

public class ActiveDirectoryGroupDeletedEvent
extends ActiveDirectoryEntityDeletedEvent
implements RemoteGroupEvent {
    public ActiveDirectoryGroupDeletedEvent(Object source, long directoryID, Tombstone tombstone) {
        super(source, directoryID, tombstone);
    }
}

