/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.support;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.PatternMatchUtils;

public class NameMatchMethodPointcut
extends StaticMethodMatcherPointcut
implements Serializable {
    private List<String> mappedNames = new ArrayList<String>();

    public void setMappedName(String mappedName) {
        this.setMappedNames(mappedName);
    }

    public void setMappedNames(String ... mappedNames) {
        this.mappedNames = new ArrayList<String>(Arrays.asList(mappedNames));
    }

    public NameMatchMethodPointcut addMethodName(String name) {
        this.mappedNames.add(name);
        return this;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        for (String mappedName : this.mappedNames) {
            if (!mappedName.equals(method.getName()) && !this.isMatch(method.getName(), mappedName)) continue;
            return true;
        }
        return false;
    }

    protected boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }

    public boolean equals(@Nullable Object other) {
        return this == other || other instanceof NameMatchMethodPointcut && this.mappedNames.equals(((NameMatchMethodPointcut)other).mappedNames);
    }

    public int hashCode() {
        return this.mappedNames.hashCode();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.mappedNames;
    }
}

