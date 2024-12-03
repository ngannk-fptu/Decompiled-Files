/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dc.filestore.api;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

public interface Snapshot
extends Closeable {
    public void unpack(Path var1) throws IOException;
}

