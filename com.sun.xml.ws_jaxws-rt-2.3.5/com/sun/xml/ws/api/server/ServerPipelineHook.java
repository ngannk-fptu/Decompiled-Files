/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.server;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.ServerPipeAssemblerContext;

public abstract class ServerPipelineHook {
    @NotNull
    public Pipe createMonitoringPipe(ServerPipeAssemblerContext ctxt, @NotNull Pipe tail) {
        return tail;
    }

    @NotNull
    public Pipe createSecurityPipe(ServerPipeAssemblerContext ctxt, @NotNull Pipe tail) {
        return tail;
    }
}

