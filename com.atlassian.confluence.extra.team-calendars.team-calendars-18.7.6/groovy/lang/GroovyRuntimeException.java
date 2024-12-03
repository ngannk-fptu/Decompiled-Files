/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ModuleNode;

public class GroovyRuntimeException
extends RuntimeException {
    private ModuleNode module;
    private ASTNode node;

    public GroovyRuntimeException() {
    }

    public GroovyRuntimeException(String message) {
        super(message);
    }

    public GroovyRuntimeException(String message, ASTNode node) {
        super(message);
        this.node = node;
    }

    public GroovyRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GroovyRuntimeException(Throwable t) {
        this.initCause(t);
    }

    public void setModule(ModuleNode module) {
        this.module = module;
    }

    public ModuleNode getModule() {
        return this.module;
    }

    @Override
    public String getMessage() {
        return this.getMessageWithoutLocationText() + this.getLocationText();
    }

    public ASTNode getNode() {
        return this.node;
    }

    public String getMessageWithoutLocationText() {
        return super.getMessage();
    }

    protected String getLocationText() {
        String answer = ". ";
        if (this.node != null) {
            answer = answer + "At [" + this.node.getLineNumber() + ":" + this.node.getColumnNumber() + "] ";
        }
        if (this.module != null) {
            answer = answer + this.module.getDescription();
        }
        if (answer.equals(". ")) {
            return "";
        }
        return answer;
    }
}

