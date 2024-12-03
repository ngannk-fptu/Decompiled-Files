/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.pipe;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.ServerPipeAssemblerContext;

public interface PipelineAssembler {
    @NotNull
    public Pipe createClient(@NotNull ClientPipeAssemblerContext var1);

    @NotNull
    public Pipe createServer(@NotNull ServerPipeAssemblerContext var1);
}

