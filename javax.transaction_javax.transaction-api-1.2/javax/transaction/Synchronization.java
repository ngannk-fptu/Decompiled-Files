/*
 * Decompiled with CFR 0.152.
 */
package javax.transaction;

public interface Synchronization {
    public void beforeCompletion();

    public void afterCompletion(int var1);
}

