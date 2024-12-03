/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.pipe;

import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.PipeClonerImpl;
import com.sun.xml.ws.api.pipe.TubeCloner;
import java.util.Map;

public abstract class PipeCloner
extends TubeCloner {
    public static Pipe clone(Pipe p) {
        return new PipeClonerImpl().copy(p);
    }

    PipeCloner(Map<Object, Object> master2copy) {
        super(master2copy);
    }

    @Override
    public abstract <T extends Pipe> T copy(T var1);

    public abstract void add(Pipe var1, Pipe var2);
}

