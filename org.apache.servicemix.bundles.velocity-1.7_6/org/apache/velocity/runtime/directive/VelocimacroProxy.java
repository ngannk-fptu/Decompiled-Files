/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.Writer;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.context.ProxyVMContext;
import org.apache.velocity.exception.MacroOverflowException;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.Renderable;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class VelocimacroProxy
extends Directive {
    private String macroName;
    private String[] argArray = null;
    private String[] literalArgArray = null;
    private SimpleNode nodeTree = null;
    private int numMacroArgs = 0;
    private boolean strictArguments;
    private boolean localContextScope = false;
    private int maxCallDepth;
    private String bodyReference;

    public String getName() {
        return this.macroName;
    }

    public int getType() {
        return 2;
    }

    public void setName(String name) {
        this.macroName = name;
    }

    public void setArgArray(String[] arr) {
        this.argArray = arr;
        this.literalArgArray = new String[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            this.literalArgArray[i] = ".literal.$" + this.argArray[i];
        }
        this.numMacroArgs = this.argArray.length - 1;
    }

    public void setNodeTree(SimpleNode tree) {
        this.nodeTree = tree;
    }

    public int getNumArgs() {
        return this.numMacroArgs;
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, MethodInvocationException, MacroOverflowException {
        return this.render(context, writer, node, null);
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node, Renderable body) throws IOException, MethodInvocationException, MacroOverflowException {
        ProxyVMContext vmc = new ProxyVMContext(context, this.rsvc, this.localContextScope);
        int callArguments = node.jjtGetNumChildren();
        if (callArguments > 0) {
            for (int i = 1; i < this.argArray.length && i <= callArguments; ++i) {
                vmc.addVMProxyArg(context, this.argArray[i], this.literalArgArray[i], node.jjtGetChild(i - 1));
            }
        }
        if (body != null) {
            vmc.addVMProxyArg(context, this.bodyReference, "", body);
        }
        if (this.maxCallDepth > 0 && this.maxCallDepth == vmc.getCurrentMacroCallDepth()) {
            Object[] stack = vmc.getMacroNameStack();
            StringBuffer out = new StringBuffer(100).append("Max calling depth of ").append(this.maxCallDepth).append(" was exceeded in macro '").append(this.macroName).append("' with Call Stack:");
            for (int i = 0; i < stack.length; ++i) {
                if (i != 0) {
                    out.append("->");
                }
                out.append(stack[i]);
            }
            out.append(" at " + Log.formatFileString(this));
            this.rsvc.getLog().error(out.toString());
            while (vmc.getCurrentMacroCallDepth() > 0) {
                vmc.popCurrentMacroName();
            }
            throw new MacroOverflowException(out.toString());
        }
        try {
            vmc.pushCurrentMacroName(this.macroName);
            this.nodeTree.render(vmc, writer);
            vmc.popCurrentMacroName();
            return true;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            String msg = "VelocimacroProxy.render() : exception VM = #" + this.macroName + "()";
            this.rsvc.getLog().error(msg, e);
            throw new VelocityException(msg, e);
        }
    }

    public void init(RuntimeServices rs) {
        String key;
        Boolean alreadyWarned;
        this.rsvc = rs;
        this.strictArguments = rs.getConfiguration().getBoolean("velocimacro.arguments.strict", false);
        this.localContextScope = this.rsvc.getBoolean("velocimacro.context.localscope", false);
        if (this.localContextScope && this.rsvc.getLog().isWarnEnabled() && (alreadyWarned = (Boolean)this.rsvc.getApplicationAttribute(key = "velocimacro.context.localscope.warning")) == null) {
            this.rsvc.setApplicationAttribute(key, Boolean.TRUE);
            this.rsvc.getLog().warn("The velocimacro.context.localscope feature is deprecated and will be removed in Velocity 2.0. Instead, please use the $macro scope to store references that must be local to your macros (e.g. #set( $macro.foo = 'bar' ) and $macro.foo).  This $macro namespace is automatically created and destroyed for you at the beginning and end of the macro rendering.");
        }
        this.maxCallDepth = this.rsvc.getInt("velocimacro.max.depth");
        this.bodyReference = this.rsvc.getString("velocimacro.body.reference", "bodyContent");
    }

    private String buildErrorMsg(Node node, int numArgsProvided) {
        String msg = "VM #" + this.macroName + ": too " + (this.getNumArgs() > numArgsProvided ? "few" : "many") + " arguments to macro. Wanted " + this.getNumArgs() + " got " + numArgsProvided;
        return msg;
    }

    public void checkArgs(InternalContextAdapter context, Node node, boolean hasBody) {
        int i = node.jjtGetNumChildren();
        if (hasBody) {
            --i;
        }
        if (this.getNumArgs() != i) {
            if (this.strictArguments) {
                throw new TemplateInitException(this.buildErrorMsg(node, i), context.getCurrentTemplateName(), 0, 0);
            }
            if (this.rsvc.getLog().isDebugEnabled()) {
                this.rsvc.getLog().debug(this.buildErrorMsg(node, i));
                return;
            }
        }
    }
}

