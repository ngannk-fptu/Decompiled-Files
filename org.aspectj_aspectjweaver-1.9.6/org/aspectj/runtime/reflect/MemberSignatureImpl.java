/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.MemberSignature;
import org.aspectj.runtime.reflect.SignatureImpl;

abstract class MemberSignatureImpl
extends SignatureImpl
implements MemberSignature {
    MemberSignatureImpl(int modifiers, String name, Class declaringType) {
        super(modifiers, name, declaringType);
    }

    public MemberSignatureImpl(String stringRep) {
        super(stringRep);
    }
}

