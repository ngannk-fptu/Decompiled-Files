/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.Snapshot
 */
package com.atlassian.dc.filestore.common.snapshot;

import com.atlassian.dc.filestore.api.Snapshot;
import java.io.IOException;
import java.nio.file.Path;

public final class EmptySnapshot
implements Snapshot {
    public static final Snapshot INSTANCE = new EmptySnapshot();

    private EmptySnapshot() {
    }

    public void unpack(Path destination) throws IOException {
    }

    public void close() {
    }
}

