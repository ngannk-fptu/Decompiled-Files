/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.UnlockSignature;
import org.aspectj.runtime.reflect.SignatureImpl;
import org.aspectj.runtime.reflect.StringMaker;

class UnlockSignatureImpl
extends SignatureImpl
implements UnlockSignature {
    private Class parameterType;

    UnlockSignatureImpl(Class c) {
        super(8, "unlock", c);
        this.parameterType = c;
    }

    UnlockSignatureImpl(String stringRep) {
        super(stringRep);
    }

    @Override
    protected String createToString(StringMaker sm) {
        if (this.parameterType == null) {
            this.parameterType = this.extractType(3);
        }
        return "unlock(" + sm.makeTypeName(this.parameterType) + ")";
    }

    public Class getParameterType() {
        if (this.parameterType == null) {
            this.parameterType = this.extractType(3);
        }
        return this.parameterType;
    }
}

