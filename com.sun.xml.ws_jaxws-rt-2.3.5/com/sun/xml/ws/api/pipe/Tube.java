/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.pipe;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.TubeCloner;

public interface Tube {
    @NotNull
    public NextAction processRequest(@NotNull Packet var1);

    @NotNull
    public NextAction processResponse(@NotNull Packet var1);

    @NotNull
    public NextAction processException(@NotNull Throwable var1);

    public void preDestroy();

    public Tube copy(TubeCloner var1);
}

