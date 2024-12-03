/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.Transformer;

public class FactoryTransformer
implements Transformer,
Serializable {
    private static final long serialVersionUID = -6817674502475353160L;
    private final Factory iFactory;

    public static Transformer getInstance(Factory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Factory must not be null");
        }
        return new FactoryTransformer(factory);
    }

    public FactoryTransformer(Factory factory) {
        this.iFactory = factory;
    }

    public Object transform(Object input) {
        return this.iFactory.create();
    }

    public Factory getFactory() {
        return this.iFactory;
    }
}

