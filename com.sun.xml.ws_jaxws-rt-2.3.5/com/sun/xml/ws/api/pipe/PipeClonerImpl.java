/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.pipe;

import com.sun.xml.ws.api.pipe.Pipe;
import com.sun.xml.ws.api.pipe.PipeCloner;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PipeClonerImpl
extends PipeCloner {
    private static final Logger LOGGER = Logger.getLogger(PipeClonerImpl.class.getName());

    public PipeClonerImpl() {
        super(new HashMap<Object, Object>());
    }

    protected PipeClonerImpl(Map<Object, Object> master2copy) {
        super(master2copy);
    }

    @Override
    public <T extends Pipe> T copy(T p) {
        Pipe r = (Pipe)this.master2copy.get(p);
        if (r == null) {
            r = p.copy(this);
            assert (this.master2copy.get(p) == r) : "the pipe must call the add(...) method to register itself before start copying other pipes, but " + p + " hasn't done so";
        }
        return (T)r;
    }

    @Override
    public void add(Pipe original, Pipe copy) {
        assert (!this.master2copy.containsKey(original));
        assert (original != null && copy != null);
        this.master2copy.put(original, copy);
    }

    public void add(AbstractTubeImpl original, AbstractTubeImpl copy) {
        this.add((Tube)original, (Tube)copy);
    }

    @Override
    public void add(Tube original, Tube copy) {
        assert (!this.master2copy.containsKey(original));
        assert (original != null && copy != null);
        this.master2copy.put(original, copy);
    }

    @Override
    public <T extends Tube> T copy(T t) {
        Tube r = (Tube)this.master2copy.get(t);
        if (r == null) {
            if (t != null) {
                r = t.copy(this);
            } else if (LOGGER.isLoggable(Level.FINER)) {
                LOGGER.fine("WARNING, tube passed to 'copy' in " + this + " was null, so no copy was made");
            }
        }
        return (T)r;
    }
}

