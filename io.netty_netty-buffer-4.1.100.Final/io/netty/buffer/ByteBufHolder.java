/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.ReferenceCounted
 */
package io.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCounted;

public interface ByteBufHolder
extends ReferenceCounted {
    public ByteBuf content();

    public ByteBufHolder copy();

    public ByteBufHolder duplicate();

    public ByteBufHolder retainedDuplicate();

    public ByteBufHolder replace(ByteBuf var1);

    public ByteBufHolder retain();

    public ByteBufHolder retain(int var1);

    public ByteBufHolder touch();

    public ByteBufHolder touch(Object var1);
}

