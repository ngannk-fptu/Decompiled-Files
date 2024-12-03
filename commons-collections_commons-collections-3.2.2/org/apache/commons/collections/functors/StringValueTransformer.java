/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Transformer;

public final class StringValueTransformer
implements Transformer,
Serializable {
    private static final long serialVersionUID = 7511110693171758606L;
    public static final Transformer INSTANCE = new StringValueTransformer();

    public static Transformer getInstance() {
        return INSTANCE;
    }

    private StringValueTransformer() {
    }

    public Object transform(Object input) {
        return String.valueOf(input);
    }
}

