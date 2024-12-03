/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufHolder
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;

interface PemEncoded
extends ByteBufHolder {
    public boolean isSensitive();

    public PemEncoded copy();

    public PemEncoded duplicate();

    public PemEncoded retainedDuplicate();

    public PemEncoded replace(ByteBuf var1);

    public PemEncoded retain();

    public PemEncoded retain(int var1);

    public PemEncoded touch();

    public PemEncoded touch(Object var1);
}

