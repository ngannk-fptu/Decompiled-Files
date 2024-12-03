/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.CallableMemberDescriptor;
import freemarker.ext.beans._MethodUtil;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

final class ReflectionCallableMemberDescriptor
extends CallableMemberDescriptor {
    private final Member member;
    final Class[] paramTypes;

    ReflectionCallableMemberDescriptor(Method member, Class[] paramTypes) {
        this.member = member;
        this.paramTypes = paramTypes;
    }

    ReflectionCallableMemberDescriptor(Constructor member, Class[] paramTypes) {
        this.member = member;
        this.paramTypes = paramTypes;
    }

    @Override
    TemplateModel invokeMethod(BeansWrapper bw, Object obj, Object[] args) throws TemplateModelException, InvocationTargetException, IllegalAccessException {
        return bw.invokeMethod(obj, (Method)this.member, args);
    }

    @Override
    Object invokeConstructor(BeansWrapper bw, Object[] args) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return ((Constructor)this.member).newInstance(args);
    }

    @Override
    String getDeclaration() {
        return _MethodUtil.toString(this.member);
    }

    @Override
    boolean isConstructor() {
        return this.member instanceof Constructor;
    }

    @Override
    boolean isStatic() {
        return (this.member.getModifiers() & 8) != 0;
    }

    @Override
    boolean isVarargs() {
        return _MethodUtil.isVarargs(this.member);
    }

    @Override
    Class[] getParamTypes() {
        return this.paramTypes;
    }

    @Override
    String getName() {
        return this.member.getName();
    }
}

