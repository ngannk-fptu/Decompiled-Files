/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.metamodel;

import java.util.Map;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.Type;

public interface MapAttribute<X, K, V>
extends PluralAttribute<X, Map<K, V>, V> {
    public Class<K> getKeyJavaType();

    public Type<K> getKeyType();
}

