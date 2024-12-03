/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.impl.xs.identity.Field;
import org.apache.xerces.xs.ShortList;

public interface ValueStore {
    public void addValue(Field var1, boolean var2, Object var3, short var4, ShortList var5);

    public void reportError(String var1, Object[] var2);
}

