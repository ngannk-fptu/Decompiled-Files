/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.voorhees;

import java.util.List;

public interface RpcMethodMapper {
    public boolean methodExists(String var1);

    public boolean methodExists(String var1, int var2);

    public List<Class[]> getPossibleArgumentTypes(String var1, int var2);

    public Object call(String var1, Class[] var2, Object[] var3) throws Exception;
}

