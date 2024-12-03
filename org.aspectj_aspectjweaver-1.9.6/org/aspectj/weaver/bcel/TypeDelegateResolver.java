/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;

public interface TypeDelegateResolver {
    public ReferenceTypeDelegate getDelegate(ReferenceType var1);
}

