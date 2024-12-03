/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.FunctorUtils;

public class WhileClosure
implements Closure,
Serializable {
    private static final long serialVersionUID = -3110538116913760108L;
    private final Predicate iPredicate;
    private final Closure iClosure;
    private final boolean iDoLoop;
    static /* synthetic */ Class class$org$apache$commons$collections$functors$WhileClosure;

    public static Closure getInstance(Predicate predicate, Closure closure, boolean doLoop) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        if (closure == null) {
            throw new IllegalArgumentException("Closure must not be null");
        }
        return new WhileClosure(predicate, closure, doLoop);
    }

    public WhileClosure(Predicate predicate, Closure closure, boolean doLoop) {
        this.iPredicate = predicate;
        this.iClosure = closure;
        this.iDoLoop = doLoop;
    }

    public void execute(Object input) {
        if (this.iDoLoop) {
            this.iClosure.execute(input);
        }
        while (this.iPredicate.evaluate(input)) {
            this.iClosure.execute(input);
        }
    }

    public Predicate getPredicate() {
        return this.iPredicate;
    }

    public Closure getClosure() {
        return this.iClosure;
    }

    public boolean isDoLoop() {
        return this.iDoLoop;
    }

    private void writeObject(ObjectOutputStream os) throws IOException {
        FunctorUtils.checkUnsafeSerialization(class$org$apache$commons$collections$functors$WhileClosure == null ? (class$org$apache$commons$collections$functors$WhileClosure = WhileClosure.class$("org.apache.commons.collections.functors.WhileClosure")) : class$org$apache$commons$collections$functors$WhileClosure);
        os.defaultWriteObject();
    }

    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        FunctorUtils.checkUnsafeSerialization(class$org$apache$commons$collections$functors$WhileClosure == null ? (class$org$apache$commons$collections$functors$WhileClosure = WhileClosure.class$("org.apache.commons.collections.functors.WhileClosure")) : class$org$apache$commons$collections$functors$WhileClosure);
        is.defaultReadObject();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

