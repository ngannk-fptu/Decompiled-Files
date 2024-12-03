/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.assembler.dev;

import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.Tube;

public interface TubelineAssemblyContext {
    public Pipe getAdaptedTubelineHead();

    public <T> T getImplementation(Class<T> var1);

    public Tube getTubelineHead();
}

