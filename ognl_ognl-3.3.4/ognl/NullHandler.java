/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Map;

public interface NullHandler {
    public Object nullMethodResult(Map var1, Object var2, String var3, Object[] var4);

    public Object nullPropertyValue(Map var1, Object var2, Object var3);
}

