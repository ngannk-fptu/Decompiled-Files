/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.logging.Logger
 */
package com.sun.xml.ws.assembler;

import com.sun.istack.logging.Logger;
import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.helper.PipeAdapter;
import com.sun.xml.ws.assembler.dev.TubelineAssemblyContext;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class TubelineAssemblyContextImpl
implements TubelineAssemblyContext {
    private static final Logger LOGGER = Logger.getLogger(TubelineAssemblyContextImpl.class);
    private Tube head;
    private Pipe adaptedHead;
    private List<Tube> tubes = new LinkedList<Tube>();

    @Override
    public Tube getTubelineHead() {
        return this.head;
    }

    @Override
    public Pipe getAdaptedTubelineHead() {
        if (this.adaptedHead == null) {
            this.adaptedHead = PipeAdapter.adapt(this.head);
        }
        return this.adaptedHead;
    }

    public boolean setTubelineHead(Tube newHead) {
        if (newHead == this.head || newHead == this.adaptedHead) {
            return false;
        }
        this.head = newHead;
        this.tubes.add(this.head);
        this.adaptedHead = null;
        if (LOGGER.isLoggable(Level.FINER)) {
            LOGGER.finer(MessageFormat.format("Added ''{0}'' tube instance to the tubeline.", newHead == null ? null : newHead.getClass().getName()));
        }
        return true;
    }

    @Override
    public <T> T getImplementation(Class<T> type) {
        for (Tube tube : this.tubes) {
            if (!type.isInstance(tube)) continue;
            return type.cast(tube);
        }
        return null;
    }
}

