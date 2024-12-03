/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.FunctorException;

public final class ExceptionClosure
implements Closure,
Serializable {
    private static final long serialVersionUID = 7179106032121985545L;
    public static final Closure INSTANCE = new ExceptionClosure();

    public static Closure getInstance() {
        return INSTANCE;
    }

    private ExceptionClosure() {
    }

    public void execute(Object input) {
        throw new FunctorException("ExceptionClosure invoked");
    }
}

