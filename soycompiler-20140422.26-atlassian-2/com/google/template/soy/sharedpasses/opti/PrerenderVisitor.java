/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses.opti;

import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.shared.restricted.SoyPurePrintDirective;
import com.google.template.soy.sharedpasses.opti.PreevalVisitorFactory;
import com.google.template.soy.sharedpasses.render.RenderException;
import com.google.template.soy.sharedpasses.render.RenderVisitor;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.CssNode;
import com.google.template.soy.soytree.DebuggerNode;
import com.google.template.soy.soytree.LogNode;
import com.google.template.soy.soytree.MsgFallbackGroupNode;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.TemplateRegistry;
import com.google.template.soy.soytree.jssrc.GoogMsgDefNode;
import com.google.template.soy.soytree.jssrc.GoogMsgRefNode;
import java.util.Deque;
import java.util.Map;
import javax.annotation.Nullable;

class PrerenderVisitor
extends RenderVisitor {
    PrerenderVisitor(Map<String, SoyJavaPrintDirective> soyJavaDirectivesMap, PreevalVisitorFactory preevalVisitorFactory, Appendable outputBuf, @Nullable TemplateRegistry templateRegistry, SoyRecord data, @Nullable Deque<Map<String, SoyValue>> env) {
        super(soyJavaDirectivesMap, preevalVisitorFactory, outputBuf, templateRegistry, data, null, env, null, null, null, null);
    }

    @Override
    protected PrerenderVisitor createHelperInstance(Appendable outputBuf, SoyRecord data) {
        return new PrerenderVisitor(this.soyJavaDirectivesMap, (PreevalVisitorFactory)this.evalVisitorFactory, outputBuf, this.templateRegistry, data, null);
    }

    @Override
    public Void exec(SoyNode soyNode) {
        try {
            return (Void)super.exec(soyNode);
        }
        catch (RenderException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw new RenderException("Failed prerender due to exception: " + e.getMessage(), e);
        }
    }

    @Override
    protected void visitMsgFallbackGroupNode(MsgFallbackGroupNode node) {
        throw new RenderException("Cannot prerender MsgFallbackGroupNode.");
    }

    @Override
    protected void visitGoogMsgDefNode(GoogMsgDefNode node) {
        throw new RenderException("Cannot prerender GoogMsgDefNode.");
    }

    @Override
    protected void visitGoogMsgRefNode(GoogMsgRefNode node) {
        throw new RenderException("Cannot prerender GoogMsgRefNode.");
    }

    @Override
    protected void visitCssNode(CssNode node) {
        throw new RenderException("Cannot prerender CssNode.");
    }

    @Override
    protected void visitCallDelegateNode(CallDelegateNode node) {
        throw new RenderException("Cannot prerender CallDelegateNode.");
    }

    @Override
    protected void visitLogNode(LogNode node) {
        throw new RenderException("Cannot prerender LogNode.");
    }

    @Override
    protected void visitDebuggerNode(DebuggerNode node) {
        throw new RenderException("Cannot prerender DebuggerNode.");
    }

    @Override
    protected void visitPrintNode(PrintNode node) {
        for (PrintDirectiveNode directiveNode : node.getChildren()) {
            if (this.isSoyPurePrintDirective(directiveNode)) continue;
            throw new RenderException("Cannot prerender a node with some impure print directive.");
        }
        super.visitPrintNode(node);
    }

    @Override
    protected void visitPrintDirectiveNode(PrintDirectiveNode node) {
        if (!this.isSoyPurePrintDirective(node)) {
            throw new RenderException("Cannot prerender impure print directive.");
        }
        super.visitPrintDirectiveNode(node);
    }

    private boolean isSoyPurePrintDirective(PrintDirectiveNode node) {
        SoyJavaPrintDirective directive = (SoyJavaPrintDirective)this.soyJavaDirectivesMap.get(node.getName());
        return directive != null && directive.getClass().isAnnotationPresent(SoyPurePrintDirective.class);
    }
}

