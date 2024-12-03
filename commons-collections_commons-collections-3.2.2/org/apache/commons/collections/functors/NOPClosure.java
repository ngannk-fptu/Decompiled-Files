/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Closure;

public class NOPClosure
implements Closure,
Serializable {
    private static final long serialVersionUID = 3518477308466486130L;
    public static final Closure INSTANCE = new NOPClosure();

    public static Closure getInstance() {
        return INSTANCE;
    }

    private NOPClosure() {
    }

    public void execute(Object input) {
    }
}

