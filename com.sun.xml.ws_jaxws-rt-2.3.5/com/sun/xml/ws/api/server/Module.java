/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.server.BoundEndpoint;
import java.util.List;

public abstract class Module
implements Component {
    @NotNull
    public abstract List<BoundEndpoint> getBoundEndpoints();

    @Override
    @Nullable
    public <S> S getSPI(@NotNull Class<S> spiType) {
        return null;
    }
}

