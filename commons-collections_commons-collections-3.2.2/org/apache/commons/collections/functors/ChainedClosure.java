/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.functors.FunctorUtils;
import org.apache.commons.collections.functors.NOPClosure;

public class ChainedClosure
implements Closure,
Serializable {
    private static final long serialVersionUID = -3520677225766901240L;
    private final Closure[] iClosures;

    public static Closure getInstance(Closure[] closures) {
        FunctorUtils.validate(closures);
        if (closures.length == 0) {
            return NOPClosure.INSTANCE;
        }
        closures = FunctorUtils.copy(closures);
        return new ChainedClosure(closures);
    }

    public static Closure getInstance(Collection closures) {
        if (closures == null) {
            throw new IllegalArgumentException("Closure collection must not be null");
        }
        if (closures.size() == 0) {
            return NOPClosure.INSTANCE;
        }
        Closure[] cmds = new Closure[closures.size()];
        int i = 0;
        Iterator it = closures.iterator();
        while (it.hasNext()) {
            cmds[i++] = (Closure)it.next();
        }
        FunctorUtils.validate(cmds);
        return new ChainedClosure(cmds);
    }

    public static Closure getInstance(Closure closure1, Closure closure2) {
        if (closure1 == null || closure2 == null) {
            throw new IllegalArgumentException("Closures must not be null");
        }
        Closure[] closures = new Closure[]{closure1, closure2};
        return new ChainedClosure(closures);
    }

    public ChainedClosure(Closure[] closures) {
        this.iClosures = closures;
    }

    public void execute(Object input) {
        for (int i = 0; i < this.iClosures.length; ++i) {
            this.iClosures[i].execute(input);
        }
    }

    public Closure[] getClosures() {
        return this.iClosures;
    }
}

