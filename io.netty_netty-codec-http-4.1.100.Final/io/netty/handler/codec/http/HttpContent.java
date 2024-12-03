/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufHolder
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.http.HttpObject;

public interface HttpContent
extends HttpObject,
ByteBufHolder {
    public HttpContent copy();

    public HttpContent duplicate();

    public HttpContent retainedDuplicate();

    public HttpContent replace(ByteBuf var1);

    public HttpContent retain();

    public HttpContent retain(int var1);

    public HttpContent touch();

    public HttpContent touch(Object var1);
}

