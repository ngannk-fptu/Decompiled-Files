/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FunctorException;

public final class ExceptionFactory
implements Factory,
Serializable {
    private static final long serialVersionUID = 7179106032121985545L;
    public static final Factory INSTANCE = new ExceptionFactory();

    public static Factory getInstance() {
        return INSTANCE;
    }

    private ExceptionFactory() {
    }

    public Object create() {
        throw new FunctorException("ExceptionFactory invoked");
    }
}

