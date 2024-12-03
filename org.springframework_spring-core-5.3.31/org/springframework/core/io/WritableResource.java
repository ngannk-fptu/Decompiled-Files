/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import org.springframework.core.io.Resource;

public interface WritableResource
extends Resource {
    default public boolean isWritable() {
        return true;
    }

    public OutputStream getOutputStream() throws IOException;

    default public WritableByteChannel writableChannel() throws IOException {
        return Channels.newChannel(this.getOutputStream());
    }
}

