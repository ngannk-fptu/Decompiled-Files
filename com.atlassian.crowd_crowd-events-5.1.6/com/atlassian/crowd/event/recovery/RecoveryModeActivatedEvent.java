/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.model.directory.ImmutableDirectory
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.event.recovery;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.directory.ImmutableDirectory;
import com.google.common.base.Preconditions;

public class RecoveryModeActivatedEvent {
    private final String recoveryUsername;
    private final ImmutableDirectory recoveryDirectory;

    public RecoveryModeActivatedEvent(String recoveryUsername, Directory recoveryDirectory) {
        this.recoveryUsername = (String)Preconditions.checkNotNull((Object)recoveryUsername, (Object)"recoveryUsername");
        this.recoveryDirectory = ImmutableDirectory.from((Directory)recoveryDirectory);
    }

    public String getUsername() {
        return this.recoveryUsername;
    }

    public Directory getDirectory() {
        return this.recoveryDirectory;
    }

    public String toString() {
        return "RecoveryModeActivatedEvent{recoveryUsername='" + this.recoveryUsername + "'}";
    }
}

