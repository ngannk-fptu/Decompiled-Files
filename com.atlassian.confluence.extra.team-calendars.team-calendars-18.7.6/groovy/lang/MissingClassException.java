/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;

public class MissingClassException
extends GroovyRuntimeException {
    private final String type;

    public MissingClassException(String type, ASTNode node, String message) {
        super("No such class: " + type + " " + message, node);
        this.type = type;
    }

    public MissingClassException(ClassNode type, String message) {
        super("No such class: " + type.getName() + " " + message);
        this.type = type.getName();
    }

    public String getType() {
        return this.type;
    }
}

