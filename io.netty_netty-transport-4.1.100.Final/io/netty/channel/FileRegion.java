/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.ReferenceCounted
 */
package io.netty.channel;

import io.netty.util.ReferenceCounted;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;

public interface FileRegion
extends ReferenceCounted {
    public long position();

    @Deprecated
    public long transfered();

    public long transferred();

    public long count();

    public long transferTo(WritableByteChannel var1, long var2) throws IOException;

    public FileRegion retain();

    public FileRegion retain(int var1);

    public FileRegion touch();

    public FileRegion touch(Object var1);
}

