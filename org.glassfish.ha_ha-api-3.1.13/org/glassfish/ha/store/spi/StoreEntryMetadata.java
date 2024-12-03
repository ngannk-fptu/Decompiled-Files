/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.spi;

import java.util.Collection;
import org.glassfish.ha.store.spi.AttributeMetadata;

public interface StoreEntryMetadata<S> {
    public AttributeMetadata<S, ?> getAttributeMetadata(String var1);

    public Collection<AttributeMetadata<S, ?>> getAllAttributeMetadata();
}

