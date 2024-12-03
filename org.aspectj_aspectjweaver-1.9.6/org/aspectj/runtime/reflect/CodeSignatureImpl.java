/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.runtime.reflect.MemberSignatureImpl;

abstract class CodeSignatureImpl
extends MemberSignatureImpl
implements CodeSignature {
    Class[] parameterTypes;
    String[] parameterNames;
    Class[] exceptionTypes;

    CodeSignatureImpl(int modifiers, String name, Class declaringType, Class[] parameterTypes, String[] parameterNames, Class[] exceptionTypes) {
        super(modifiers, name, declaringType);
        this.parameterTypes = parameterTypes;
        this.parameterNames = parameterNames;
        this.exceptionTypes = exceptionTypes;
    }

    CodeSignatureImpl(String stringRep) {
        super(stringRep);
    }

    @Override
    public Class[] getParameterTypes() {
        if (this.parameterTypes == null) {
            this.parameterTypes = this.extractTypes(3);
        }
        return this.parameterTypes;
    }

    @Override
    public String[] getParameterNames() {
        if (this.parameterNames == null) {
            this.parameterNames = this.extractStrings(4);
        }
        return this.parameterNames;
    }

    @Override
    public Class[] getExceptionTypes() {
        if (this.exceptionTypes == null) {
            this.exceptionTypes = this.extractTypes(5);
        }
        return this.exceptionTypes;
    }
}

