/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.functors.FunctorUtils;
import org.apache.commons.collections.functors.NOPClosure;

public class ForClosure
implements Closure,
Serializable {
    private static final long serialVersionUID = -1190120533393621674L;
    private final int iCount;
    private final Closure iClosure;
    static /* synthetic */ Class class$org$apache$commons$collections$functors$ForClosure;

    public static Closure getInstance(int count, Closure closure) {
        if (count <= 0 || closure == null) {
            return NOPClosure.INSTANCE;
        }
        if (count == 1) {
            return closure;
        }
        return new ForClosure(count, closure);
    }

    public ForClosure(int count, Closure closure) {
        this.iCount = count;
        this.iClosure = closure;
    }

    public void execute(Object input) {
        for (int i = 0; i < this.iCount; ++i) {
            this.iClosure.execute(input);
        }
    }

    public Closure getClosure() {
        return this.iClosure;
    }

    public int getCount() {
        return this.iCount;
    }

    private void writeObject(ObjectOutputStream os) throws IOException {
        FunctorUtils.checkUnsafeSerialization(class$org$apache$commons$collections$functors$ForClosure == null ? (class$org$apache$commons$collections$functors$ForClosure = ForClosure.class$("org.apache.commons.collections.functors.ForClosure")) : class$org$apache$commons$collections$functors$ForClosure);
        os.defaultWriteObject();
    }

    private void readObject(ObjectInputStream is) throws ClassNotFoundException, IOException {
        FunctorUtils.checkUnsafeSerialization(class$org$apache$commons$collections$functors$ForClosure == null ? (class$org$apache$commons$collections$functors$ForClosure = ForClosure.class$("org.apache.commons.collections.functors.ForClosure")) : class$org$apache$commons$collections$functors$ForClosure);
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

