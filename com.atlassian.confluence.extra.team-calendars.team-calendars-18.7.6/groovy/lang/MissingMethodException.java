/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MethodRankHelper;

public class MissingMethodException
extends GroovyRuntimeException {
    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private final String method;
    private final Class type;
    private final boolean isStatic;
    private final Object[] arguments;

    public Object[] getArguments() {
        return this.arguments;
    }

    public MissingMethodException(String method, Class type, Object[] arguments) {
        this(method, type, arguments, false);
    }

    public MissingMethodException(String method, Class type, Object[] arguments, boolean isStatic) {
        this.method = method;
        this.type = type;
        this.isStatic = isStatic;
        this.arguments = arguments == null ? EMPTY_OBJECT_ARRAY : arguments;
    }

    @Override
    public String getMessage() {
        return "No signature of method: " + (this.isStatic ? "static " : "") + this.type.getName() + "." + this.method + "() is applicable for argument types: (" + InvokerHelper.toTypeString(this.arguments) + ") values: " + InvokerHelper.toArrayString(this.arguments, 60, true) + MethodRankHelper.getMethodSuggestionString(this.method, this.type, this.arguments);
    }

    public String getMethod() {
        return this.method;
    }

    public Class getType() {
        return this.type;
    }

    public boolean isStatic() {
        return this.isStatic;
    }
}

