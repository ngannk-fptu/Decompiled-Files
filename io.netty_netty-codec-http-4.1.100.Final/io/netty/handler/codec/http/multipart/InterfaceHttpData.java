/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.ReferenceCounted
 */
package io.netty.handler.codec.http.multipart;

import io.netty.util.ReferenceCounted;

public interface InterfaceHttpData
extends Comparable<InterfaceHttpData>,
ReferenceCounted {
    public String getName();

    public HttpDataType getHttpDataType();

    public InterfaceHttpData retain();

    public InterfaceHttpData retain(int var1);

    public InterfaceHttpData touch();

    public InterfaceHttpData touch(Object var1);

    public static enum HttpDataType {
        Attribute,
        FileUpload,
        InternalAttribute;

    }
}

