/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.manager.directory;

import com.atlassian.crowd.embedded.api.Directory;

class DirectorySynchronisationUtils {
    private static final String LOCK_PREFIX = Directory.class.getName() + ":";

    DirectorySynchronisationUtils() {
    }

    static String getLockName(long directoryId) {
        return LOCK_PREFIX + directoryId;
    }
}

