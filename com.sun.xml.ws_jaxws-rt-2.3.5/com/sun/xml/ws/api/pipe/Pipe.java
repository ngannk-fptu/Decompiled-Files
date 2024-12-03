/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.pipe;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.PipeCloner;

public interface Pipe {
    public Packet process(Packet var1);

    public void preDestroy();

    public Pipe copy(PipeCloner var1);
}

