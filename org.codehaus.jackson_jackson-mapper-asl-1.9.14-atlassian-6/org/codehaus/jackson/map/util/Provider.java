/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

import java.util.Collection;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Provider<T> {
    public Collection<T> provide();
}

