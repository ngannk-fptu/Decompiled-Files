/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.pipe;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.TransportTubeFactory;
import com.sun.xml.ws.api.pipe.helper.PipeAdapter;

public abstract class TransportPipeFactory {
    public abstract Pipe doCreate(@NotNull ClientPipeAssemblerContext var1);

    public static Pipe create(@Nullable ClassLoader classLoader, @NotNull ClientPipeAssemblerContext context) {
        return PipeAdapter.adapt(TransportTubeFactory.create(classLoader, context));
    }
}

