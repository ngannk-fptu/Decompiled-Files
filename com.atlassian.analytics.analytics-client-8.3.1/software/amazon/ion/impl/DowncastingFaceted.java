/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import software.amazon.ion.facet.Faceted;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class DowncastingFaceted
implements Faceted {
    DowncastingFaceted() {
    }

    @Override
    public final <T> T asFacet(Class<T> type) {
        if (!type.isInstance(this)) {
            return null;
        }
        return type.cast(this);
    }
}

