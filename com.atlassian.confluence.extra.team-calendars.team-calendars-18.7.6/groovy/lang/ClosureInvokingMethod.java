/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Closure;

public interface ClosureInvokingMethod {
    public Closure getClosure();

    public boolean isStatic();

    public String getName();
}

