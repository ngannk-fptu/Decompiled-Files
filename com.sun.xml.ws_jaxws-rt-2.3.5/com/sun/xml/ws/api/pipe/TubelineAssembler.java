/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.pipe;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.ws.api.pipe.Tube;

public interface TubelineAssembler {
    @NotNull
    public Tube createClient(@NotNull ClientTubeAssemblerContext var1);

    @NotNull
    public Tube createServer(@NotNull ServerTubeAssemblerContext var1);
}

