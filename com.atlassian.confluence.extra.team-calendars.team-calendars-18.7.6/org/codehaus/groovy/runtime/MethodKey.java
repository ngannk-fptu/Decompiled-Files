/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.codehaus.groovy.runtime.DefaultMethodKey;

public abstract class MethodKey {
    private int hash;
    private String name;
    private Class sender;
    private boolean isCallToSuper;

    public MethodKey(Class sender, String name, boolean isCallToSuper) {
        this.sender = sender;
        this.name = name;
        this.isCallToSuper = isCallToSuper;
    }

    public MethodKey createCopy() {
        int size = this.getParameterCount();
        Class[] paramTypes = new Class[size];
        for (int i = 0; i < size; ++i) {
            paramTypes[i] = this.getParameterType(i);
        }
        return new DefaultMethodKey(this.sender, this.name, paramTypes, this.isCallToSuper);
    }

    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that instanceof MethodKey) {
            return this.equals((MethodKey)that);
        }
        return false;
    }

    public boolean equals(MethodKey that) {
        if (this.sender != that.sender) {
            return false;
        }
        if (this.isCallToSuper != that.isCallToSuper) {
            return false;
        }
        if (!this.name.equals(that.name)) {
            return false;
        }
        int size = this.getParameterCount();
        if (size != that.getParameterCount()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (this.getParameterType(i) == that.getParameterType(i)) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        if (this.hash == 0) {
            this.hash = this.createHashCode();
            if (this.hash == 0) {
                this.hash = -889275714;
            }
        }
        return this.hash;
    }

    public String toString() {
        return super.toString() + "[name:" + this.name + "; params:" + this.getParamterTypes();
    }

    public String getName() {
        return this.name;
    }

    public List getParamterTypes() {
        int size = this.getParameterCount();
        if (size <= 0) {
            return Collections.EMPTY_LIST;
        }
        ArrayList<Class> params = new ArrayList<Class>(size);
        for (int i = 0; i < size; ++i) {
            params.add(this.getParameterType(i));
        }
        return params;
    }

    public abstract int getParameterCount();

    public abstract Class getParameterType(int var1);

    protected int createHashCode() {
        int answer = this.name.hashCode();
        int size = this.getParameterCount();
        for (int i = 0; i < size; ++i) {
            answer *= 37;
            answer += 1 + this.getParameterType(i).hashCode();
        }
        answer *= 37;
        answer += this.isCallToSuper ? 1 : 0;
        answer *= 37;
        return answer += 1 + this.sender.hashCode();
    }
}

