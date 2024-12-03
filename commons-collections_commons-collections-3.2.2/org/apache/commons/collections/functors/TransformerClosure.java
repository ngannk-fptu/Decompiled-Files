/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.NOPClosure;

public class TransformerClosure
implements Closure,
Serializable {
    private static final long serialVersionUID = -5194992589193388969L;
    private final Transformer iTransformer;

    public static Closure getInstance(Transformer transformer) {
        if (transformer == null) {
            return NOPClosure.INSTANCE;
        }
        return new TransformerClosure(transformer);
    }

    public TransformerClosure(Transformer transformer) {
        this.iTransformer = transformer;
    }

    public void execute(Object input) {
        this.iTransformer.transform(input);
    }

    public Transformer getTransformer() {
        return this.iTransformer;
    }
}

