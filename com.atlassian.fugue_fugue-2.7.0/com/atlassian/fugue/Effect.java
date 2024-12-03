/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.fugue;

public interface Effect<A> {
    public void apply(A var1);

    public static interface Applicant<A> {
        public void foreach(Effect<? super A> var1);
    }
}

