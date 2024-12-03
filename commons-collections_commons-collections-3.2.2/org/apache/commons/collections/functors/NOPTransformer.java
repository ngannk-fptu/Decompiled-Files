/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Transformer;

public class NOPTransformer
implements Transformer,
Serializable {
    private static final long serialVersionUID = 2133891748318574490L;
    public static final Transformer INSTANCE = new NOPTransformer();

    public static Transformer getInstance() {
        return INSTANCE;
    }

    private NOPTransformer() {
    }

    public Object transform(Object input) {
        return input;
    }
}

