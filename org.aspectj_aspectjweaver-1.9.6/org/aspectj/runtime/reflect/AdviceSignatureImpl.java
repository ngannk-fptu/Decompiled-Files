/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import java.lang.reflect.Method;
import java.util.StringTokenizer;
import org.aspectj.lang.reflect.AdviceSignature;
import org.aspectj.runtime.reflect.CodeSignatureImpl;
import org.aspectj.runtime.reflect.StringMaker;

class AdviceSignatureImpl
extends CodeSignatureImpl
implements AdviceSignature {
    Class returnType;
    private Method adviceMethod = null;

    AdviceSignatureImpl(int modifiers, String name, Class declaringType, Class[] parameterTypes, String[] parameterNames, Class[] exceptionTypes, Class returnType) {
        super(modifiers, name, declaringType, parameterTypes, parameterNames, exceptionTypes);
        this.returnType = returnType;
    }

    AdviceSignatureImpl(String stringRep) {
        super(stringRep);
    }

    @Override
    public Class getReturnType() {
        if (this.returnType == null) {
            this.returnType = this.extractType(6);
        }
        return this.returnType;
    }

    @Override
    protected String createToString(StringMaker sm) {
        StringBuffer buf = new StringBuffer();
        if (sm.includeArgs) {
            buf.append(sm.makeTypeName(this.getReturnType()));
        }
        if (sm.includeArgs) {
            buf.append(" ");
        }
        buf.append(sm.makePrimaryTypeName(this.getDeclaringType(), this.getDeclaringTypeName()));
        buf.append(".");
        buf.append(this.toAdviceName(this.getName()));
        sm.addSignature(buf, this.getParameterTypes());
        sm.addThrows(buf, this.getExceptionTypes());
        return buf.toString();
    }

    private String toAdviceName(String methodName) {
        if (methodName.indexOf(36) == -1) {
            return methodName;
        }
        StringTokenizer strTok = new StringTokenizer(methodName, "$");
        while (strTok.hasMoreTokens()) {
            String token = strTok.nextToken();
            if (!token.startsWith("before") && !token.startsWith("after") && !token.startsWith("around")) continue;
            return token;
        }
        return methodName;
    }

    @Override
    public Method getAdvice() {
        if (this.adviceMethod == null) {
            try {
                this.adviceMethod = this.getDeclaringType().getDeclaredMethod(this.getName(), this.getParameterTypes());
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return this.adviceMethod;
    }
}

