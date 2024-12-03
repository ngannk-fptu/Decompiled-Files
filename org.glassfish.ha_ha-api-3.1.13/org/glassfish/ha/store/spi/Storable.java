/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.spi;

import java.util.Set;

public interface Storable {
    public long _getVersion();

    public Set<String> _getDirtyAttributeNames();

    public <T> T _getAttributeValue(String var1, Class<T> var2);

    public String _getOwnerInstanceName();
}

