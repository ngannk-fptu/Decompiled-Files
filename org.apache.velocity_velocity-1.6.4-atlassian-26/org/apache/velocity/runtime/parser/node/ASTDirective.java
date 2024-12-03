/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package org.apache.velocity.runtime.parser.node;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.RuntimeMacro;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.node.ParserVisitor;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.ExceptionUtils;

public class ASTDirective
extends SimpleNode {
    private Directive directive = null;
    private String directiveName = "";
    private boolean isDirective;
    private boolean isInitialized;

    public ASTDirective(int id) {
        super(id);
    }

    public ASTDirective(Parser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public synchronized Object init(InternalContextAdapter context, Object data) throws TemplateInitException {
        if (!this.isInitialized) {
            super.init(context, data);
            if (this.parser.isDirective(this.directiveName)) {
                this.isDirective = true;
                try {
                    this.directive = (Directive)this.parser.getDirective(this.directiveName).getClass().newInstance();
                }
                catch (InstantiationException e) {
                    throw ExceptionUtils.createRuntimeException("Couldn't initialize directive of class " + this.parser.getDirective(this.directiveName).getClass().getName(), e);
                }
                catch (IllegalAccessException e) {
                    throw ExceptionUtils.createRuntimeException("Couldn't initialize directive of class " + this.parser.getDirective(this.directiveName).getClass().getName(), e);
                }
                this.directive.setLocation(this.getLine(), this.getColumn(), this.getTemplateName());
                this.directive.init(this.rsvc, context, this);
            } else {
                this.directive = new RuntimeMacro(this.directiveName, this.getTemplateName());
                this.directive.setLocation(this.getLine(), this.getColumn(), this.getTemplateName());
                try {
                    this.directive.init(this.rsvc, context, this);
                }
                catch (TemplateInitException die) {
                    throw new TemplateInitException(die.getMessage(), (ParseException)die.getWrappedThrowable(), die.getTemplateName(), die.getColumnNumber() + this.getColumn(), die.getLineNumber() + this.getLine());
                }
                this.isDirective = true;
            }
            this.isInitialized = true;
        }
        return data;
    }

    @Override
    public boolean render(InternalContextAdapter context, Writer writer) throws IOException, MethodInvocationException, ResourceNotFoundException, ParseErrorException {
        if (this.isDirective) {
            this.directive.render(context, writer, this);
        } else if (context.getAllowRendering()) {
            writer.write("#");
            writer.write(this.directiveName);
        }
        return true;
    }

    public void setDirectiveName(String str) {
        this.directiveName = str;
    }

    public String getDirectiveName() {
        return this.directiveName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder((Object)this).appendSuper(super.toString()).append("directiveName", (Object)this.getDirectiveName()).toString();
    }
}

