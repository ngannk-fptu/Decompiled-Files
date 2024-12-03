/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.server.WSEndpoint;
import java.net.URI;

public interface BoundEndpoint
extends Component {
    @NotNull
    public WSEndpoint getEndpoint();

    @NotNull
    public URI getAddress();

    @NotNull
    public URI getAddress(String var1);
}

