/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Closure;

final class TrampolineClosure<V>
extends Closure<V> {
    private final Closure<V> original;

    TrampolineClosure(Closure<V> original) {
        super(original.getOwner(), original.getDelegate());
        this.original = original;
    }

    @Override
    public int getMaximumNumberOfParameters() {
        return this.original.maximumNumberOfParameters;
    }

    @Override
    public Class[] getParameterTypes() {
        return this.original.parameterTypes;
    }

    @Override
    public V call() {
        return this.loop(this.original.call());
    }

    @Override
    public V call(Object arguments) {
        return this.loop(this.original.call(arguments));
    }

    @Override
    public V call(Object ... args) {
        return this.loop(this.original.call(args));
    }

    private V loop(Object lastResult) {
        Object result = lastResult;
        while (result instanceof TrampolineClosure) {
            result = ((TrampolineClosure)result).original.call();
        }
        return (V)result;
    }

    @Override
    public Closure<V> trampoline(Object ... args) {
        return new TrampolineClosure<V>(this.original.curry(args));
    }

    @Override
    public Closure<V> trampoline() {
        return this;
    }
}

