/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import java.lang.reflect.Constructor;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.runtime.reflect.CodeSignatureImpl;
import org.aspectj.runtime.reflect.StringMaker;

class ConstructorSignatureImpl
extends CodeSignatureImpl
implements ConstructorSignature {
    private Constructor constructor;

    ConstructorSignatureImpl(int modifiers, Class declaringType, Class[] parameterTypes, String[] parameterNames, Class[] exceptionTypes) {
        super(modifiers, "<init>", declaringType, parameterTypes, parameterNames, exceptionTypes);
    }

    ConstructorSignatureImpl(String stringRep) {
        super(stringRep);
    }

    @Override
    public String getName() {
        return "<init>";
    }

    @Override
    protected String createToString(StringMaker sm) {
        StringBuffer buf = new StringBuffer();
        buf.append(sm.makeModifiersString(this.getModifiers()));
        buf.append(sm.makePrimaryTypeName(this.getDeclaringType(), this.getDeclaringTypeName()));
        sm.addSignature(buf, this.getParameterTypes());
        sm.addThrows(buf, this.getExceptionTypes());
        return buf.toString();
    }

    @Override
    public Constructor getConstructor() {
        if (this.constructor == null) {
            try {
                this.constructor = this.getDeclaringType().getDeclaredConstructor(this.getParameterTypes());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return this.constructor;
    }
}

