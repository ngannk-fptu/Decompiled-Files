/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.ReferenceCounted
 */
package io.netty.handler.ssl;

import io.netty.util.ReferenceCounted;
import java.security.cert.X509Certificate;

interface OpenSslKeyMaterial
extends ReferenceCounted {
    public X509Certificate[] certificateChain();

    public long certificateChainAddress();

    public long privateKeyAddress();

    public OpenSslKeyMaterial retain();

    public OpenSslKeyMaterial retain(int var1);

    public OpenSslKeyMaterial touch();

    public OpenSslKeyMaterial touch(Object var1);

    public boolean release();

    public boolean release(int var1);
}

