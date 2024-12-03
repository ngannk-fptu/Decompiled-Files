/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.lang.reflect;

import org.aspectj.lang.Signature;

public interface CatchClauseSignature
extends Signature {
    public Class getParameterType();

    public String getParameterName();
}

