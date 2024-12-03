/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.runtime.MethodRankHelper;

public class MissingPropertyException
extends GroovyRuntimeException {
    public static final Object MPE = new Object();
    private final String property;
    private final Class type;

    public MissingPropertyException(String property, Class type) {
        this.property = property;
        this.type = type;
    }

    public MissingPropertyException(String property, Class type, Throwable t) {
        super(t);
        this.property = property;
        this.type = type;
    }

    public MissingPropertyException(String message) {
        super(message);
        this.property = null;
        this.type = null;
    }

    public MissingPropertyException(String message, String property, Class type) {
        super(message);
        this.property = property;
        this.type = type;
    }

    @Override
    public String getMessageWithoutLocationText() {
        Throwable cause = this.getCause();
        if (cause == null) {
            if (super.getMessageWithoutLocationText() != null) {
                return super.getMessageWithoutLocationText();
            }
            return "No such property: " + this.property + " for class: " + this.type.getName() + MethodRankHelper.getPropertySuggestionString(this.property, this.type);
        }
        return "No such property: " + this.property + " for class: " + this.type.getName() + ". Reason: " + cause;
    }

    public String getProperty() {
        return this.property;
    }

    public Class getType() {
        return this.type;
    }
}

