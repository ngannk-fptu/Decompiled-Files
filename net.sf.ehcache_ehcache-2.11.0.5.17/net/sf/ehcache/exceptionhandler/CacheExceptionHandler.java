/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.exceptionhandler;

import net.sf.ehcache.Ehcache;

public interface CacheExceptionHandler {
    public void onException(Ehcache var1, Object var2, Exception var3);
}

