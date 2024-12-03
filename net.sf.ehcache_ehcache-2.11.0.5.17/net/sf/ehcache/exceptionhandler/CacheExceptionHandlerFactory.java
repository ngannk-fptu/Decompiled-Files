/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.exceptionhandler;

import java.util.Properties;
import net.sf.ehcache.exceptionhandler.CacheExceptionHandler;

public abstract class CacheExceptionHandlerFactory {
    public abstract CacheExceptionHandler createExceptionHandler(Properties var1);
}

