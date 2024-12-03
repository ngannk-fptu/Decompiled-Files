/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.LockSignature;
import org.aspectj.runtime.reflect.SignatureImpl;
import org.aspectj.runtime.reflect.StringMaker;

class LockSignatureImpl
extends SignatureImpl
implements LockSignature {
    private Class parameterType;

    LockSignatureImpl(Class c) {
        super(8, "lock", c);
        this.parameterType = c;
    }

    LockSignatureImpl(String stringRep) {
        super(stringRep);
    }

    @Override
    protected String createToString(StringMaker sm) {
        if (this.parameterType == null) {
            this.parameterType = this.extractType(3);
        }
        return "lock(" + sm.makeTypeName(this.parameterType) + ")";
    }

    public Class getParameterType() {
        if (this.parameterType == null) {
            this.parameterType = this.extractType(3);
        }
        return this.parameterType;
    }
}

