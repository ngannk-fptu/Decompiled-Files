/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Transformer;

public class ClosureTransformer
implements Transformer,
Serializable {
    private static final long serialVersionUID = 478466901448617286L;
    private final Closure iClosure;

    public static Transformer getInstance(Closure closure) {
        if (closure == null) {
            throw new IllegalArgumentException("Closure must not be null");
        }
        return new ClosureTransformer(closure);
    }

    public ClosureTransformer(Closure closure) {
        this.iClosure = closure;
    }

    public Object transform(Object input) {
        this.iClosure.execute(input);
        return input;
    }

    public Closure getClosure() {
        return this.iClosure;
    }
}

