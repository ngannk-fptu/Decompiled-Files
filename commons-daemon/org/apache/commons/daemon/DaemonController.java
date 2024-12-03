/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.daemon;

public interface DaemonController {
    public void shutdown() throws IllegalStateException;

    public void reload() throws IllegalStateException;

    public void fail() throws IllegalStateException;

    public void fail(String var1) throws IllegalStateException;

    public void fail(Exception var1) throws IllegalStateException;

    public void fail(String var1, Exception var2) throws IllegalStateException;
}

