/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.runtime.reflect;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.runtime.reflect.CodeSignatureImpl;
import org.aspectj.runtime.reflect.StringMaker;

class MethodSignatureImpl
extends CodeSignatureImpl
implements MethodSignature {
    private Method method;
    Class returnType;

    MethodSignatureImpl(int modifiers, String name, Class declaringType, Class[] parameterTypes, String[] parameterNames, Class[] exceptionTypes, Class returnType) {
        super(modifiers, name, declaringType, parameterTypes, parameterNames, exceptionTypes);
        this.returnType = returnType;
    }

    MethodSignatureImpl(String stringRep) {
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
        buf.append(sm.makeModifiersString(this.getModifiers()));
        if (sm.includeArgs) {
            buf.append(sm.makeTypeName(this.getReturnType()));
        }
        if (sm.includeArgs) {
            buf.append(" ");
        }
        buf.append(sm.makePrimaryTypeName(this.getDeclaringType(), this.getDeclaringTypeName()));
        buf.append(".");
        buf.append(this.getName());
        sm.addSignature(buf, this.getParameterTypes());
        sm.addThrows(buf, this.getExceptionTypes());
        return buf.toString();
    }

    @Override
    public Method getMethod() {
        if (this.method == null) {
            Class dtype = this.getDeclaringType();
            try {
                this.method = dtype.getDeclaredMethod(this.getName(), this.getParameterTypes());
            }
            catch (NoSuchMethodException nsmEx) {
                HashSet<Class> searched = new HashSet<Class>();
                searched.add(dtype);
                this.method = this.search(dtype, this.getName(), this.getParameterTypes(), searched);
            }
        }
        return this.method;
    }

    private Method search(Class type, String name, Class[] params, Set searched) {
        Method m;
        if (type == null) {
            return null;
        }
        if (!searched.contains(type)) {
            searched.add(type);
            try {
                return type.getDeclaredMethod(name, params);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
        }
        if ((m = this.search(type.getSuperclass(), name, params, searched)) != null) {
            return m;
        }
        Class<?>[] superinterfaces = type.getInterfaces();
        if (superinterfaces != null) {
            for (int i = 0; i < superinterfaces.length; ++i) {
                m = this.search(superinterfaces[i], name, params, searched);
                if (m == null) continue;
                return m;
            }
        }
        return null;
    }
}

