/*
 * Decompiled with CFR 0.152.
 */
package org.aopalliance.intercept;

import org.aopalliance.intercept.Joinpoint;

public interface Invocation
extends Joinpoint {
    public Object[] getArguments();
}

