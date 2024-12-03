/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.util.Map;
import ognl.MethodFailedException;

public interface MethodAccessor {
    public Object callStaticMethod(Map var1, Class var2, String var3, Object[] var4) throws MethodFailedException;

    public Object callMethod(Map var1, Object var2, String var3, Object[] var4) throws MethodFailedException;
}

