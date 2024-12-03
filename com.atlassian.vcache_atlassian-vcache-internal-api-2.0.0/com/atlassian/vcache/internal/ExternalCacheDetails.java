/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.ExternalCacheSettings
 */
package com.atlassian.vcache.internal;

import com.atlassian.vcache.ExternalCacheSettings;

public interface ExternalCacheDetails {
    public String getName();

    public BufferPolicy getPolicy();

    public ExternalCacheSettings getSettings();

    public static enum BufferPolicy {
        NEVER(false, false),
        READ_ONLY(true, false),
        FULLY(true, true);

        private final boolean readBuffered;
        private final boolean writeBuffered;

        private BufferPolicy(boolean readBuffered, boolean writeBuffered) {
            this.readBuffered = readBuffered;
            this.writeBuffered = writeBuffered;
        }

        boolean isReadBuffered() {
            return this.readBuffered;
        }

        boolean isWriteBuffered() {
            return this.writeBuffered;
        }
    }
}

