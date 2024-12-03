/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.directory.BeforeGroupRemoval
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.manager.directory.BeforeGroupRemoval;

public class NoopBeforeGroupRemoval
implements BeforeGroupRemoval {
    public void beforeRemoveGroup(long directoryId, String groupName) {
    }
}

