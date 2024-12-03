/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.InternalRemoteDirectory
 */
package com.atlassian.crowd.directory.hybrid;

import com.atlassian.crowd.directory.InternalRemoteDirectory;

public abstract class InternalGroupHandler {
    public static final String SHADOW_ATTRIBUTE_KEY = InternalGroupHandler.class.getName() + ".shadow";
    private final InternalRemoteDirectory internalDirectory;
    private final boolean localGroupsEnabled;

    protected InternalGroupHandler(InternalRemoteDirectory internalDirectory) {
        this.internalDirectory = internalDirectory;
        this.localGroupsEnabled = Boolean.parseBoolean(internalDirectory.getValue("ldap.local.groups"));
    }

    protected InternalRemoteDirectory getInternalDirectory() {
        return this.internalDirectory;
    }

    public boolean isLocalGroupsEnabled() {
        return this.localGroupsEnabled;
    }
}

