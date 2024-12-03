/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.pipe.Codec;
import com.sun.xml.ws.api.server.WSEndpoint;

public interface EndpointAwareCodec
extends Codec {
    public void setEndpoint(@NotNull WSEndpoint var1);
}

