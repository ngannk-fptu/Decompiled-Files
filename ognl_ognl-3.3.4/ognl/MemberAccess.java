/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.Member;
import java.util.Map;

public interface MemberAccess {
    public Object setup(Map var1, Object var2, Member var3, String var4);

    public void restore(Map var1, Object var2, Member var3, String var4, Object var5);

    public boolean isAccessible(Map var1, Object var2, Member var3, String var4);
}

