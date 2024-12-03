/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.concurrent;

public interface Effect<A> {
    public void apply(A var1);

    public static interface Applicant<A> {
        public void foreach(Effect<A> var1);
    }
}

