/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.GroovyRuntimeException;

public class MissingFieldException
extends GroovyRuntimeException {
    private final String field;
    private final Class type;

    public MissingFieldException(String field, Class type) {
        super("No such field: " + field + " for class: " + type.getName());
        this.field = field;
        this.type = type;
    }

    public MissingFieldException(String field, Class type, Throwable e) {
        super("No such field: " + field + " for class: " + type.getName() + ". Reason: " + e, e);
        this.field = field;
        this.type = type;
    }

    public MissingFieldException(String message, String field, Class type) {
        super(message);
        this.field = field;
        this.type = type;
    }

    public String getField() {
        return this.field;
    }

    public Class getType() {
        return this.type;
    }
}

