/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.FunctorException;
import org.apache.commons.collections.Transformer;

public final class ExceptionTransformer
implements Transformer,
Serializable {
    private static final long serialVersionUID = 7179106032121985545L;
    public static final Transformer INSTANCE = new ExceptionTransformer();

    public static Transformer getInstance() {
        return INSTANCE;
    }

    private ExceptionTransformer() {
    }

    public Object transform(Object input) {
        throw new FunctorException("ExceptionTransformer invoked");
    }
}

