/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.util.Constants
 *  org.apache.logging.log4j.util.EnglishEnums
 */
package org.apache.logging.log4j.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.EnglishEnums;

public interface Filter
extends LifeCycle {
    public static final Filter[] EMPTY_ARRAY = new Filter[0];
    public static final String ELEMENT_TYPE = "filter";

    public Result getOnMismatch();

    public Result getOnMatch();

    public Result filter(Logger var1, Level var2, Marker var3, String var4, Object ... var5);

    public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5);

    public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6);

    public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7);

    public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8);

    public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9);

    public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10);

    public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11);

    public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12);

    public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13);

    public Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14);

    public Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5);

    public Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5);

    default public Result filter(Logger logger, Level level, Marker marker, String msg) {
        return this.filter(logger, level, marker, msg, Constants.EMPTY_OBJECT_ARRAY);
    }

    public Result filter(LogEvent var1);

    public static enum Result {
        ACCEPT,
        NEUTRAL,
        DENY;


        public static Result toResult(String name) {
            return Result.toResult(name, null);
        }

        public static Result toResult(String name, Result defaultResult) {
            return (Result)EnglishEnums.valueOf(Result.class, (String)name, (Enum)defaultResult);
        }
    }
}

