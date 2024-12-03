/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import org.apache.commons.collections.Factory;

public class ConstantFactory
implements Factory,
Serializable {
    private static final long serialVersionUID = -3520677225766901240L;
    public static final Factory NULL_INSTANCE = new ConstantFactory(null);
    private final Object iConstant;

    public static Factory getInstance(Object constantToReturn) {
        if (constantToReturn == null) {
            return NULL_INSTANCE;
        }
        return new ConstantFactory(constantToReturn);
    }

    public ConstantFactory(Object constantToReturn) {
        this.iConstant = constantToReturn;
    }

    public Object create() {
        return this.iConstant;
    }

    public Object getConstant() {
        return this.iConstant;
    }
}

