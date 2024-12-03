/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.pipe.helper;

import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.PipeCloner;

public abstract class AbstractPipeImpl
implements Pipe {
    protected AbstractPipeImpl() {
    }

    protected AbstractPipeImpl(Pipe that, PipeCloner cloner) {
        cloner.add(that, this);
    }

    @Override
    public void preDestroy() {
    }
}

