/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

import java.util.Collection;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.StoreException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Store<T> {
    public Collection<T> getMatches(Selector<T> var1) throws StoreException;
}

