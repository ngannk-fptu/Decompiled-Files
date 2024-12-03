/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import org.apache.velocity.context.EvaluateContext;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.StopCommand;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.introspection.Info;

public class Evaluate
extends Directive {
    public String getName() {
        return "evaluate";
    }

    public int getType() {
        return 2;
    }

    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        super.init(rs, context, node);
        int argCount = node.jjtGetNumChildren();
        if (argCount == 0) {
            throw new TemplateInitException("#" + this.getName() + "() requires exactly one argument", context.getCurrentTemplateName(), node.getColumn(), node.getLine());
        }
        if (argCount > 1) {
            throw new TemplateInitException("#" + this.getName() + "() requires exactly one argument", context.getCurrentTemplateName(), node.jjtGetChild(1).getColumn(), node.jjtGetChild(1).getLine());
        }
        Node childNode = node.jjtGetChild(0);
        if (childNode.getType() != 8 && childNode.getType() != 18) {
            throw new TemplateInitException("#" + this.getName() + "()  argument must be a string literal or reference", context.getCurrentTemplateName(), childNode.getColumn(), childNode.getLine());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        Object value = node.jjtGetChild(0).value(context);
        String sourceText = value != null ? value.toString() : "";
        String templateName = context.getCurrentTemplateName();
        SimpleNode nodeTree = null;
        try {
            nodeTree = this.rsvc.parse(new StringReader(sourceText), templateName, false);
        }
        catch (ParseException pex) {
            Info info = new Info(templateName, node.getLine(), node.getColumn());
            throw new ParseErrorException(pex.getMessage(), info);
        }
        catch (TemplateInitException pex) {
            Info info = new Info(templateName, node.getLine(), node.getColumn());
            throw new ParseErrorException(pex.getMessage(), info);
        }
        if (nodeTree != null) {
            EvaluateContext ica = new EvaluateContext(context, this.rsvc);
            ica.pushCurrentTemplateName(templateName);
            try {
                try {
                    nodeTree.init(ica, this.rsvc);
                }
                catch (TemplateInitException pex) {
                    Info info = new Info(templateName, node.getLine(), node.getColumn());
                    throw new ParseErrorException(pex.getMessage(), info);
                }
                try {
                    this.preRender(ica);
                    nodeTree.render(ica, writer);
                }
                catch (StopCommand stop) {
                    if (!stop.isFor(this)) {
                        throw stop;
                    }
                    if (this.rsvc.getLog().isDebugEnabled()) {
                        this.rsvc.getLog().debug(stop.getMessage());
                    }
                }
                catch (ParseErrorException pex) {
                    Info info = new Info(templateName, node.getLine(), node.getColumn());
                    throw new ParseErrorException(pex.getMessage(), info);
                }
            }
            finally {
                ica.popCurrentTemplateName();
                this.postRender(ica);
            }
            return true;
        }
        return false;
    }
}

