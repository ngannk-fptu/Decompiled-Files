/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.text.StrBuilder
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.Renderable;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.StopCommand;
import org.apache.velocity.runtime.directive.VelocimacroProxy;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.util.introspection.Info;

public class RuntimeMacro
extends Directive {
    private String macroName;
    private String literal = null;
    private Node node = null;
    protected boolean strictRef = false;
    private String badArgsErrorMsg = null;

    public RuntimeMacro(String macroName) {
        if (macroName == null) {
            throw new IllegalArgumentException("Null arguments");
        }
        this.macroName = macroName.intern();
    }

    public String getName() {
        return this.macroName;
    }

    public String getScopeName() {
        return "macro";
    }

    public int getType() {
        return 2;
    }

    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) {
        super.init(rs, context, node);
        this.rsvc = rs;
        this.node = node;
        Token t = node.getLastToken();
        if (t.image.startsWith(")") || t.image.startsWith("#end")) {
            this.strictRef = this.rsvc.getBoolean("runtime.references.strict", false);
        }
        for (int n = 0; n < node.jjtGetNumChildren(); ++n) {
            Node child = node.jjtGetChild(n);
            if (child.getType() != 10) continue;
            this.badArgsErrorMsg = "Invalid arg '" + child.getFirstToken().image + "' in macro #" + this.macroName + " at " + Log.formatFileString(child);
            if (!this.strictRef) continue;
            throw new TemplateInitException(this.badArgsErrorMsg, context.getCurrentTemplateName(), 0, 0);
        }
    }

    private String getLiteral() {
        if (this.literal == null) {
            StrBuilder buffer = new StrBuilder();
            Token t = this.node.getFirstToken();
            while (t != null && t != this.node.getLastToken()) {
                buffer.append(t.image);
                t = t.next;
            }
            if (t != null) {
                buffer.append(t.image);
            }
            this.literal = buffer.toString();
        }
        return this.literal;
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        return this.render(context, writer, node, null);
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node, Renderable body) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        List macroLibraries;
        VelocimacroProxy vmProxy = null;
        String renderingTemplate = context.getCurrentTemplateName();
        Directive o = this.rsvc.getVelocimacro(this.macroName, this.getTemplateName(), renderingTemplate);
        if (o != null) {
            vmProxy = (VelocimacroProxy)o;
        }
        if (vmProxy == null && (macroLibraries = context.getMacroLibraries()) != null) {
            for (int i = macroLibraries.size() - 1; i >= 0; --i) {
                o = this.rsvc.getVelocimacro(this.macroName, (String)macroLibraries.get(i), renderingTemplate);
                if (o == null) continue;
                vmProxy = (VelocimacroProxy)o;
                break;
            }
        }
        if (vmProxy != null) {
            try {
                vmProxy.checkArgs(context, node, body != null);
            }
            catch (TemplateInitException die) {
                throw new ParseErrorException(die.getMessage() + " at " + Log.formatFileString(node), new Info(node));
            }
            if (this.badArgsErrorMsg != null) {
                throw new TemplateInitException(this.badArgsErrorMsg, context.getCurrentTemplateName(), node.getColumn(), node.getLine());
            }
            try {
                this.preRender(context);
                boolean die = vmProxy.render(context, writer, node, body);
                return die;
            }
            catch (StopCommand stop) {
                if (!stop.isFor(this)) {
                    throw stop;
                }
                boolean bl = true;
                return bl;
            }
            catch (RuntimeException e) {
                this.rsvc.getLog().error("Exception in macro #" + this.macroName + " called at " + Log.formatFileString(node));
                throw e;
            }
            catch (IOException e) {
                this.rsvc.getLog().error("Exception in macro #" + this.macroName + " called at " + Log.formatFileString(node));
                throw e;
            }
            finally {
                this.postRender(context);
            }
        }
        if (this.strictRef) {
            throw new VelocityException("Macro '#" + this.macroName + "' is not defined at " + Log.formatFileString(node));
        }
        writer.write(this.getLiteral());
        return true;
    }
}

