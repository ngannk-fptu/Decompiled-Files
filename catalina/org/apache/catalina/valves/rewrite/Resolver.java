/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.valves.rewrite;

import java.nio.charset.Charset;

public abstract class Resolver {
    public abstract String resolve(String var1);

    public String resolveEnv(String key) {
        return System.getProperty(key);
    }

    public abstract String resolveSsl(String var1);

    public abstract String resolveHttp(String var1);

    public abstract boolean resolveResource(int var1, String var2);

    public abstract Charset getUriCharset();
}

