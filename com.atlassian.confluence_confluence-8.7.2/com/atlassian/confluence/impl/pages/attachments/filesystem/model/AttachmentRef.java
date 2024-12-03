/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem.model;

import java.util.Optional;

public interface AttachmentRef {
    public long getId();

    public Container getContainer();

    public int getVersion();

    public static interface Space {
        public long getId();
    }

    public static interface Container {
        public long getId();

        public Optional<Space> getSpace();

        default public boolean tryRenameOnMove() {
            return true;
        }
    }
}

