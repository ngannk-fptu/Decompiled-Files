/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.api.OperationType;
import com.atlassian.crowd.embedded.api.UserCapabilities;

public class DirectoryUserCapabilities
implements UserCapabilities {
    private static final UserCapabilities NO_CAPABILITIES = new UserCapabilities(){

        @Override
        public boolean canResetPassword() {
            return false;
        }
    };
    private final Directory directory;

    private DirectoryUserCapabilities(Directory directory) {
        this.directory = directory;
    }

    @Override
    public boolean canResetPassword() {
        return this.directory != null && this.directory.getAllowedOperations().contains((Object)OperationType.UPDATE_USER) && this.directory.getType() != DirectoryType.DELEGATING;
    }

    public static UserCapabilities none() {
        return NO_CAPABILITIES;
    }

    public static UserCapabilities fromDirectory(Directory directory) {
        return new DirectoryUserCapabilities(directory);
    }
}

