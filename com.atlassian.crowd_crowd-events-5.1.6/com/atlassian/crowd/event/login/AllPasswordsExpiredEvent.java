/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.event.login;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.DirectoryEvent;

public class AllPasswordsExpiredEvent
extends DirectoryEvent {
    public AllPasswordsExpiredEvent(Object source, Directory directory) {
        super(source, directory);
    }
}

