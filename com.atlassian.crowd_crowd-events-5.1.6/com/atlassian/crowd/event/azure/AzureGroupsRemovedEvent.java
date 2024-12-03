/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.event.azure;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;
import java.util.Set;

public class AzureGroupsRemovedEvent
extends DirectoryEvent {
    private final Set<String> externalIds;

    public AzureGroupsRemovedEvent(Object source, Directory directory, Set<String> externalIds) {
        super(source, directory);
        this.externalIds = externalIds;
    }

    public Set<String> getExternalIds() {
        return this.externalIds;
    }
}

