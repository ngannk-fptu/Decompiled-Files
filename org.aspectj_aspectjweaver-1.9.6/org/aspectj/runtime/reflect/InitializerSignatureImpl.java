/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.aspectj.lang.reflect.InitializerSignature;
import org.aspectj.runtime.reflect.CodeSignatureImpl;
import org.aspectj.runtime.reflect.StringMaker;

class InitializerSignatureImpl
extends CodeSignatureImpl
implements InitializerSignature {
    private Constructor constructor;

    InitializerSignatureImpl(int modifiers, Class declaringType) {
        super(modifiers, Modifier.isStatic(modifiers) ? "<clinit>" : "<init>", declaringType, EMPTY_CLASS_ARRAY, EMPTY_STRING_ARRAY, EMPTY_CLASS_ARRAY);
    }

    InitializerSignatureImpl(String stringRep) {
        super(stringRep);
    }

    @Override
    public String getName() {
        return Modifier.isStatic(this.getModifiers()) ? "<clinit>" : "<init>";
    }

    @Override
    protected String createToString(StringMaker sm) {
        StringBuffer buf = new StringBuffer();
        buf.append(sm.makeModifiersString(this.getModifiers()));
        buf.append(sm.makePrimaryTypeName(this.getDeclaringType(), this.getDeclaringTypeName()));
        buf.append(".");
        buf.append(this.getName());
        return buf.toString();
    }

    @Override
    public Constructor getInitializer() {
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

