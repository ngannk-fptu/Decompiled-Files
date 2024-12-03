/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import groovy.lang.Closure;
import org.codehaus.groovy.binding.SourceBinding;

public class ClosureSourceBinding
implements SourceBinding {
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    Closure closure;
    Object[] arguments;

    public ClosureSourceBinding(Closure closure) {
        this(closure, EMPTY_OBJECT_ARRAY);
    }

    public ClosureSourceBinding(Closure closure, Object[] arguments) {
        this.closure = closure;
        this.arguments = arguments;
    }

    public Closure getClosure() {
        return this.closure;
    }

    public void setClosure(Closure closure) {
        this.closure = closure;
    }

    @Override
    public Object getSourceValue() {
        return this.closure.call(this.arguments);
    }

    public void setClosureArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public void setClosureArgument(Object argument) {
        this.arguments = new Object[]{argument};
    }
}

