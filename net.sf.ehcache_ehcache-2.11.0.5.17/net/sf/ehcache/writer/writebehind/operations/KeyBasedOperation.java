/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer.writebehind.operations;

public interface KeyBasedOperation {
    public Object getKey();

    public long getCreationTime();
}

