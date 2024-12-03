/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.DirectiveConstants;
import org.apache.velocity.runtime.directive.Scope;
import org.apache.velocity.runtime.parser.node.Node;

public abstract class Directive
implements DirectiveConstants,
Cloneable {
    private int line = 0;
    private int column = 0;
    private boolean provideScope = false;
    private String templateName;
    protected RuntimeServices rsvc = null;

    public abstract String getName();

    public abstract int getType();

    public void setLocation(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public void setLocation(int line, int column, String templateName) {
        this.setLocation(line, column);
        this.templateName = templateName;
    }

    public int getLine() {
        return this.line;
    }

    public int getColumn() {
        return this.column;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public String getScopeName() {
        return this.getName();
    }

    public boolean isScopeProvided() {
        return this.provideScope;
    }

    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        this.rsvc = rs;
        String property = this.getScopeName() + '.' + "provide.scope.control";
        this.provideScope = this.rsvc.getBoolean(property, this.provideScope);
    }

    public abstract boolean render(InternalContextAdapter var1, Writer var2, Node var3) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException;

    protected void preRender(InternalContextAdapter context) {
        if (this.isScopeProvided()) {
            String name = this.getScopeName();
            Object previous = context.get(name);
            context.put(name, this.makeScope(previous));
        }
    }

    protected Scope makeScope(Object prev) {
        return new Scope(this, prev);
    }

    protected void postRender(InternalContextAdapter context) {
        if (this.isScopeProvided()) {
            String name = this.getScopeName();
            Object obj = context.get(name);
            try {
                Scope scope = (Scope)obj;
                if (scope.getParent() != null) {
                    context.put(name, scope.getParent());
                } else if (scope.getReplaced() != null) {
                    context.put(name, scope.getReplaced());
                } else {
                    context.remove(name);
                }
            }
            catch (ClassCastException cce) {
                // empty catch block
            }
        }
    }
}

