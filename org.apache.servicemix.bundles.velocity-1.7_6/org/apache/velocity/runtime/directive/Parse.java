/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import org.apache.velocity.Template;
import org.apache.velocity.app.event.EventHandlerUtil;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.InputBase;
import org.apache.velocity.runtime.directive.StopCommand;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class Parse
extends InputBase {
    private int maxDepth;

    public String getName() {
        return "parse";
    }

    public String getScopeName() {
        return "template";
    }

    public int getType() {
        return 2;
    }

    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        super.init(rs, context, node);
        this.maxDepth = this.rsvc.getInt("directive.parse.max.depth", 10);
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        Object[] templateStack;
        String sourcearg;
        String arg;
        if (node.jjtGetNumChildren() == 0) {
            throw new VelocityException("#parse(): argument missing at " + Log.formatFileString(this));
        }
        Object value = node.jjtGetChild(0).value(context);
        if (value == null && this.rsvc.getLog().isDebugEnabled()) {
            this.rsvc.getLog().debug("#parse(): null argument at " + Log.formatFileString(this));
        }
        if ((arg = EventHandlerUtil.includeEvent(this.rsvc, context, sourcearg = value == null ? null : value.toString(), context.getCurrentTemplateName(), this.getName())) == null) {
            return true;
        }
        if (this.maxDepth > 0 && (templateStack = context.getTemplateNameStack()).length >= this.maxDepth) {
            StringBuffer path = new StringBuffer();
            for (int i = 0; i < templateStack.length; ++i) {
                path.append(" > " + templateStack[i]);
            }
            this.rsvc.getLog().error("Max recursion depth reached (" + templateStack.length + ')' + " File stack:" + path);
            return false;
        }
        Template t = null;
        try {
            t = this.rsvc.getTemplate(arg, this.getInputEncoding(context));
        }
        catch (ResourceNotFoundException rnfe) {
            this.rsvc.getLog().error("#parse(): cannot find template '" + arg + "', called at " + Log.formatFileString(this));
            throw rnfe;
        }
        catch (ParseErrorException pee) {
            this.rsvc.getLog().error("#parse(): syntax error in #parse()-ed template '" + arg + "', called at " + Log.formatFileString(this));
            throw pee;
        }
        catch (RuntimeException e) {
            this.rsvc.getLog().error("Exception rendering #parse(" + arg + ") at " + Log.formatFileString(this));
            throw e;
        }
        catch (Exception e) {
            String msg = "Exception rendering #parse(" + arg + ") at " + Log.formatFileString(this);
            this.rsvc.getLog().error(msg, e);
            throw new VelocityException(msg, e);
        }
        ArrayList<String> macroLibraries = context.getMacroLibraries();
        if (macroLibraries == null) {
            macroLibraries = new ArrayList<String>();
        }
        context.setMacroLibraries(macroLibraries);
        macroLibraries.add(arg);
        try {
            this.preRender(context);
            context.pushCurrentTemplateName(arg);
            ((SimpleNode)t.getData()).render(context, writer);
        }
        catch (StopCommand stop) {
            if (!stop.isFor(this)) {
                throw stop;
            }
        }
        catch (RuntimeException e) {
            this.rsvc.getLog().error("Exception rendering #parse(" + arg + ") at " + Log.formatFileString(this));
            throw e;
        }
        catch (Exception e) {
            String msg = "Exception rendering #parse(" + arg + ") at " + Log.formatFileString(this);
            this.rsvc.getLog().error(msg, e);
            throw new VelocityException(msg, e);
        }
        finally {
            context.popCurrentTemplateName();
            this.postRender(context);
        }
        return true;
    }
}

