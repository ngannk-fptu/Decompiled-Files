/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.server;

import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.server.WSEndpoint;

public interface EndpointAwareTube
extends Tube {
    public void setEndpoint(WSEndpoint<?> var1);
}

