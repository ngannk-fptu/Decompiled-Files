/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.CatchClauseSignature;
import org.aspectj.runtime.reflect.SignatureImpl;
import org.aspectj.runtime.reflect.StringMaker;

class CatchClauseSignatureImpl
extends SignatureImpl
implements CatchClauseSignature {
    Class parameterType;
    String parameterName;

    CatchClauseSignatureImpl(Class declaringType, Class parameterType, String parameterName) {
        super(0, "catch", declaringType);
        this.parameterType = parameterType;
        this.parameterName = parameterName;
    }

    CatchClauseSignatureImpl(String stringRep) {
        super(stringRep);
    }

    @Override
    public Class getParameterType() {
        if (this.parameterType == null) {
            this.parameterType = this.extractType(3);
        }
        return this.parameterType;
    }

    @Override
    public String getParameterName() {
        if (this.parameterName == null) {
            this.parameterName = this.extractString(4);
        }
        return this.parameterName;
    }

    @Override
    protected String createToString(StringMaker sm) {
        return "catch(" + sm.makeTypeName(this.getParameterType()) + ")";
    }
}

