/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util.counter;

public interface Counter {
    public long increment();

    public long decrement();

    public long getAndSet(long var1);

    public long getValue();

    public long increment(long var1);

    public long decrement(long var1);

    public void setValue(long var1);
}

