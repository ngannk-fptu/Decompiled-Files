/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Closure;
import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.runtime.InvokerHelper;

public class IncorrectClosureArgumentsException
extends GroovyRuntimeException {
    private final Closure closure;
    private final Object arguments;
    private final Class[] expected;

    public IncorrectClosureArgumentsException(Closure closure, Object arguments, Class[] expected) {
        super("Incorrect arguments to closure: " + closure + ". Expected: " + InvokerHelper.toString(expected) + ", actual: " + InvokerHelper.toString(arguments));
        this.closure = closure;
        this.arguments = arguments;
        this.expected = expected;
    }

    public Object getArguments() {
        return this.arguments;
    }

    public Closure getClosure() {
        return this.closure;
    }

    public Class[] getExpected() {
        return this.expected;
    }
}

