/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;

public final class CurriedClosure<V>
extends Closure<V> {
    private final Object[] curriedParams;
    private final int minParamsExpected;
    private int index;
    private Class varargType = null;

    public CurriedClosure(int index, Closure<V> uncurriedClosure, Object ... arguments) {
        super(uncurriedClosure.clone());
        Class lastType;
        this.curriedParams = arguments;
        this.index = index;
        int origMaxLen = uncurriedClosure.getMaximumNumberOfParameters();
        this.maximumNumberOfParameters = origMaxLen - arguments.length;
        Class[] classes = uncurriedClosure.getParameterTypes();
        Class clazz = lastType = classes.length == 0 ? null : classes[classes.length - 1];
        if (lastType != null && lastType.isArray()) {
            this.varargType = lastType;
        }
        if (!this.isVararg()) {
            if (index < 0) {
                this.index += origMaxLen;
                this.minParamsExpected = 0;
            } else {
                this.minParamsExpected = index + arguments.length;
            }
            if (this.maximumNumberOfParameters < 0) {
                throw new IllegalArgumentException("Can't curry " + arguments.length + " arguments for a closure with " + origMaxLen + " parameters.");
            }
            if (index < 0) {
                if (index < -origMaxLen || index > -arguments.length) {
                    throw new IllegalArgumentException("To curry " + arguments.length + " argument(s) expect index range " + -origMaxLen + ".." + -arguments.length + " but found " + index);
                }
            } else if (index > this.maximumNumberOfParameters) {
                throw new IllegalArgumentException("To curry " + arguments.length + " argument(s) expect index range 0.." + this.maximumNumberOfParameters + " but found " + index);
            }
        } else {
            this.minParamsExpected = 0;
        }
    }

    public CurriedClosure(Closure<V> uncurriedClosure, Object ... arguments) {
        this(0, uncurriedClosure, arguments);
    }

    public Object[] getUncurriedArguments(Object ... arguments) {
        if (this.isVararg()) {
            int normalizedIndex;
            int n = normalizedIndex = this.index < 0 ? this.index + arguments.length + this.curriedParams.length : this.index;
            if (normalizedIndex < 0 || normalizedIndex > arguments.length) {
                throw new IllegalArgumentException("When currying expected index range between " + (-arguments.length - this.curriedParams.length) + ".." + (arguments.length + this.curriedParams.length) + " but found " + this.index);
            }
            return this.createNewCurriedParams(normalizedIndex, arguments);
        }
        if (this.curriedParams.length + arguments.length < this.minParamsExpected) {
            throw new IllegalArgumentException("When currying expected at least " + this.index + " argument(s) to be supplied before known curried arguments but found " + arguments.length);
        }
        int newIndex = Math.min(this.index, this.curriedParams.length + arguments.length - 1);
        newIndex = Math.min(newIndex, arguments.length);
        return this.createNewCurriedParams(newIndex, arguments);
    }

    private Object[] createNewCurriedParams(int normalizedIndex, Object[] arguments) {
        Object[] newCurriedParams = new Object[this.curriedParams.length + arguments.length];
        System.arraycopy(arguments, 0, newCurriedParams, 0, normalizedIndex);
        System.arraycopy(this.curriedParams, 0, newCurriedParams, normalizedIndex, this.curriedParams.length);
        if (arguments.length - normalizedIndex > 0) {
            System.arraycopy(arguments, normalizedIndex, newCurriedParams, this.curriedParams.length + normalizedIndex, arguments.length - normalizedIndex);
        }
        return newCurriedParams;
    }

    @Override
    public void setDelegate(Object delegate) {
        ((Closure)this.getOwner()).setDelegate(delegate);
    }

    @Override
    public Object getDelegate() {
        return ((Closure)this.getOwner()).getDelegate();
    }

    @Override
    public void setResolveStrategy(int resolveStrategy) {
        ((Closure)this.getOwner()).setResolveStrategy(resolveStrategy);
    }

    @Override
    public int getResolveStrategy() {
        return ((Closure)this.getOwner()).getResolveStrategy();
    }

    @Override
    public Object clone() {
        Closure uncurriedClosure = (Closure)((Closure)this.getOwner()).clone();
        return new CurriedClosure<V>(this.index, uncurriedClosure, this.curriedParams);
    }

    @Override
    public Class[] getParameterTypes() {
        Class[] oldParams = ((Closure)this.getOwner()).getParameterTypes();
        int extraParams = 0;
        int gobbledParams = this.curriedParams.length;
        if (this.isVararg()) {
            int numNonVarargs = oldParams.length - 1;
            if (this.index < 0) {
                int i;
                int newNumNonVarargs;
                int absIndex = -this.index;
                if (absIndex > numNonVarargs) {
                    gobbledParams = numNonVarargs;
                }
                if (absIndex - this.curriedParams.length > (newNumNonVarargs = numNonVarargs - gobbledParams)) {
                    extraParams = absIndex - this.curriedParams.length - newNumNonVarargs;
                }
                int keptParams = Math.max(numNonVarargs - absIndex, 0);
                Class[] newParams = new Class[keptParams + newNumNonVarargs + extraParams + 1];
                System.arraycopy(oldParams, 0, newParams, 0, keptParams);
                for (i = 0; i < newNumNonVarargs; ++i) {
                    newParams[keptParams + i] = Object.class;
                }
                for (i = 0; i < extraParams; ++i) {
                    newParams[keptParams + newNumNonVarargs + i] = this.varargType.getComponentType();
                }
                newParams[newParams.length - 1] = this.varargType;
                return newParams;
            }
            int leadingKept = Math.min(this.index, numNonVarargs);
            int trailingKept = Math.max(numNonVarargs - leadingKept - this.curriedParams.length, 0);
            if (this.index > leadingKept) {
                extraParams = this.index - leadingKept;
            }
            Class[] newParams = new Class[leadingKept + trailingKept + extraParams + 1];
            System.arraycopy(oldParams, 0, newParams, 0, leadingKept);
            if (trailingKept > 0) {
                System.arraycopy(oldParams, leadingKept + this.curriedParams.length, newParams, leadingKept, trailingKept);
            }
            for (int i = 0; i < extraParams; ++i) {
                newParams[leadingKept + trailingKept + i] = this.varargType.getComponentType();
            }
            newParams[newParams.length - 1] = this.varargType;
            return newParams;
        }
        Class[] newParams = new Class[oldParams.length - gobbledParams + extraParams];
        System.arraycopy(oldParams, 0, newParams, 0, this.index);
        if (newParams.length - this.index > 0) {
            System.arraycopy(oldParams, this.curriedParams.length + this.index, newParams, this.index, newParams.length - this.index);
        }
        return newParams;
    }

    private boolean isVararg() {
        return this.varargType != null;
    }
}

