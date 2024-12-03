/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ConstantTransformer;

public final class MapTransformer
implements Transformer,
Serializable {
    private static final long serialVersionUID = 862391807045468939L;
    private final Map iMap;

    public static Transformer getInstance(Map map) {
        if (map == null) {
            return ConstantTransformer.NULL_INSTANCE;
        }
        return new MapTransformer(map);
    }

    private MapTransformer(Map map) {
        this.iMap = map;
    }

    public Object transform(Object input) {
        return this.iMap.get(input);
    }

    public Map getMap() {
        return this.iMap;
    }
}

