/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.text.StrBuilder
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.directive.VelocimacroProxy;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.Token;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.util.introspection.Info;

public class RuntimeMacro
extends Directive {
    private String macroName;
    private String sourceTemplate;
    private String literal = null;
    private Node node = null;
    protected boolean strictRef = false;

    public RuntimeMacro(String macroName, String sourceTemplate) {
        if (macroName == null || sourceTemplate == null) {
            throw new IllegalArgumentException("Null arguments");
        }
        this.macroName = macroName;
        this.sourceTemplate = sourceTemplate;
    }

    @Override
    public String getName() {
        return this.macroName;
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) {
        super.init(rs, context, node);
        this.rsvc = rs;
        this.node = node;
        Token t = node.getLastToken();
        if (t.image.charAt(0) == ')') {
            this.strictRef = this.rsvc.getBoolean("runtime.references.strict", false);
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

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        List macroLibraries;
        VelocimacroProxy vmProxy = null;
        String renderingTemplate = context.getCurrentTemplateName();
        Directive o = this.rsvc.getVelocimacro(this.macroName, this.sourceTemplate, renderingTemplate);
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
                vmProxy.init(this.rsvc, context, node);
            }
            catch (TemplateInitException die) {
                Info info = new Info(this.sourceTemplate, node.getLine(), node.getColumn());
                throw new ParseErrorException(die.getMessage() + " at " + Log.formatFileString(info), info);
            }
            try {
                return vmProxy.render(context, writer, node);
            }
            catch (RuntimeException e) {
                this.rsvc.getLog().error("Exception in macro #" + this.macroName + " at " + Log.formatFileString(this.sourceTemplate, this.getLine(), this.getColumn()));
                throw e;
            }
            catch (IOException e) {
                this.rsvc.getLog().error("Exception in macro #" + this.macroName + " at " + Log.formatFileString(this.sourceTemplate, this.getLine(), this.getColumn()));
                throw e;
            }
        }
        if (this.strictRef) {
            Info info = new Info(this.sourceTemplate, node.getLine(), node.getColumn());
            throw new ParseErrorException("Macro '#" + this.macroName + "' is not defined at " + Log.formatFileString(info), info);
        }
        writer.write(this.getLiteral());
        return true;
    }
}

