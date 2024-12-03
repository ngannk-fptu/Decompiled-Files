/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

public interface Interceptor {
    public Object beforeInvoke(Object var1, String var2, Object[] var3);

    public Object afterInvoke(Object var1, String var2, Object[] var3, Object var4);

    public boolean doInvoke();
}

