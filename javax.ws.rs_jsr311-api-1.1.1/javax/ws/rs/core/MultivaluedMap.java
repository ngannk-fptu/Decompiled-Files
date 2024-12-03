/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface MultivaluedMap<K, V>
extends Map<K, List<V>> {
    public void putSingle(K var1, V var2);

    public void add(K var1, V var2);

    public V getFirst(K var1);
}

