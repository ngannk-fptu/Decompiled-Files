/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.velocity.runtime.directive;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.context.ProxyVMContext;
import org.apache.velocity.exception.MacroOverflowException;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class VelocimacroProxy
extends Directive {
    private String macroName;
    private String[] argArray = null;
    private String[] literalArgArray = null;
    private SimpleNode nodeTree = null;
    private int numMacroArgs = 0;
    private boolean preInit = false;
    private boolean strictArguments;
    private boolean localContextScope = false;
    private int maxCallDepth;

    @Override
    public String getName() {
        return this.macroName;
    }

    @Override
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

    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, MethodInvocationException, MacroOverflowException {
        ProxyVMContext vmc = new ProxyVMContext(context, this.rsvc, this.localContextScope);
        int callArguments = node.jjtGetNumChildren();
        if (callArguments > 0) {
            for (int i = 1; i < this.argArray.length && i <= callArguments; ++i) {
                Node macroCallArgument = node.jjtGetChild(i - 1);
                vmc.addVMProxyArg(context, this.argArray[i], this.literalArgArray[i], macroCallArgument);
            }
        }
        if (this.maxCallDepth > 0 && this.maxCallDepth == vmc.getCurrentMacroCallDepth()) {
            String templateName = vmc.getCurrentTemplateName();
            Object[] stack = vmc.getMacroNameStack();
            StringBuffer out = new StringBuffer(100).append("Max calling depth of ").append(this.maxCallDepth).append(" was exceeded in Template:").append(templateName).append(" and Macro:").append(this.macroName).append(" with Call Stack:");
            for (int i = 0; i < stack.length; ++i) {
                if (i != 0) {
                    out.append("->");
                }
                out.append(stack[i]);
            }
            this.rsvc.getLog().error(out.toString());
            try {
                throw new MacroOverflowException(out.toString());
            }
            catch (Throwable throwable) {
                while (vmc.getCurrentMacroCallDepth() > 0) {
                    vmc.popCurrentMacroName();
                }
                throw throwable;
            }
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void init(RuntimeServices rs, InternalContextAdapter context, Node node) throws TemplateInitException {
        VelocimacroProxy velocimacroProxy = this;
        synchronized (velocimacroProxy) {
            if (!this.preInit) {
                super.init(rs, context, node);
                this.strictArguments = rs.getConfiguration().getBoolean("velocimacro.arguments.strict", false);
                this.localContextScope = this.rsvc.getBoolean("velocimacro.context.localscope", false);
                this.maxCallDepth = this.rsvc.getInt("velocimacro.max.depth");
                this.nodeTree.init(context, rs);
                this.preInit = true;
            }
        }
        int i = node.jjtGetNumChildren();
        if (this.getNumArgs() != i) {
            for (Node parent = node.jjtGetParent(); parent != null; parent = parent.jjtGetParent()) {
                if (!(parent instanceof ASTDirective) || !StringUtils.equals((CharSequence)((ASTDirective)parent).getDirectiveName(), (CharSequence)"macro")) continue;
                return;
            }
            String msg = "VM #" + this.macroName + ": too " + (this.getNumArgs() > i ? "few" : "many") + " arguments to macro. Wanted " + this.getNumArgs() + " got " + i;
            if (this.strictArguments) {
                throw new TemplateInitException(msg, context.getCurrentTemplateName(), 0, 0);
            }
            this.rsvc.getLog().debug(msg);
            return;
        }
        for (int n = 0; n < i; ++n) {
            Node child = node.jjtGetChild(n);
            if (child.getType() != 9) continue;
            throw new TemplateInitException("Invalid arg #" + n + " in VM #" + this.macroName, context.getCurrentTemplateName(), 0, 0);
        }
    }
}

