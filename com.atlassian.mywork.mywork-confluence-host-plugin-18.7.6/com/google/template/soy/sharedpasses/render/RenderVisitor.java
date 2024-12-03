/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package com.google.template.soy.sharedpasses.render;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyList;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.data.internal.AugmentedParamStore;
import com.google.template.soy.data.internal.BasicParamStore;
import com.google.template.soy.data.internal.ParamStore;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.data.restricted.UndefinedData;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.shared.SoyCssRenamingMap;
import com.google.template.soy.shared.SoyIdRenamingMap;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.sharedpasses.render.EvalVisitor;
import com.google.template.soy.sharedpasses.render.RenderException;
import com.google.template.soy.sharedpasses.render.RenderVisitorAssistantForMsgs;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamContentNode;
import com.google.template.soy.soytree.CallParamNode;
import com.google.template.soy.soytree.CallParamValueNode;
import com.google.template.soy.soytree.CssNode;
import com.google.template.soy.soytree.DebuggerNode;
import com.google.template.soy.soytree.ForNode;
import com.google.template.soy.soytree.ForeachNode;
import com.google.template.soy.soytree.ForeachNonemptyNode;
import com.google.template.soy.soytree.IfCondNode;
import com.google.template.soy.soytree.IfElseNode;
import com.google.template.soy.soytree.IfNode;
import com.google.template.soy.soytree.LetContentNode;
import com.google.template.soy.soytree.LetValueNode;
import com.google.template.soy.soytree.LogNode;
import com.google.template.soy.soytree.MsgFallbackGroupNode;
import com.google.template.soy.soytree.MsgHtmlTagNode;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SwitchCaseNode;
import com.google.template.soy.soytree.SwitchDefaultNode;
import com.google.template.soy.soytree.SwitchNode;
import com.google.template.soy.soytree.TemplateBasicNode;
import com.google.template.soy.soytree.TemplateDelegateNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.TemplateRegistry;
import com.google.template.soy.soytree.XidNode;
import com.google.template.soy.soytree.defn.TemplateParam;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

public class RenderVisitor
extends AbstractSoyNodeVisitor<Void> {
    protected final Map<String, SoyJavaPrintDirective> soyJavaDirectivesMap;
    protected final EvalVisitor.EvalVisitorFactory evalVisitorFactory;
    protected final TemplateRegistry templateRegistry;
    protected final SoyRecord data;
    protected final SoyRecord ijData;
    protected final Deque<Map<String, SoyValue>> env;
    protected final Set<String> activeDelPackageNames;
    protected final SoyMsgBundle msgBundle;
    protected final SoyIdRenamingMap xidRenamingMap;
    protected final SoyCssRenamingMap cssRenamingMap;
    private EvalVisitor evalVisitor;
    private RenderVisitorAssistantForMsgs assistantForMsgs;
    protected Deque<Appendable> outputBufStack;
    private Appendable currOutputBuf;

    protected RenderVisitor(Map<String, SoyJavaPrintDirective> soyJavaDirectivesMap, EvalVisitor.EvalVisitorFactory evalVisitorFactory, Appendable outputBuf, @Nullable TemplateRegistry templateRegistry, SoyRecord data, @Nullable SoyRecord ijData, @Nullable Deque<Map<String, SoyValue>> env, @Nullable Set<String> activeDelPackageNames, @Nullable SoyMsgBundle msgBundle, @Nullable SoyIdRenamingMap xidRenamingMap, @Nullable SoyCssRenamingMap cssRenamingMap) {
        Preconditions.checkNotNull((Object)data);
        this.soyJavaDirectivesMap = soyJavaDirectivesMap;
        this.evalVisitorFactory = evalVisitorFactory;
        this.templateRegistry = templateRegistry;
        this.data = data;
        this.ijData = ijData;
        this.env = env != null ? env : new ArrayDeque();
        this.activeDelPackageNames = activeDelPackageNames;
        this.msgBundle = msgBundle;
        this.xidRenamingMap = xidRenamingMap;
        this.cssRenamingMap = cssRenamingMap;
        this.evalVisitor = null;
        this.assistantForMsgs = null;
        this.outputBufStack = new ArrayDeque<Appendable>();
        this.pushOutputBuf(outputBuf);
    }

    protected RenderVisitor createHelperInstance(Appendable outputBuf, SoyRecord data) {
        return new RenderVisitor(this.soyJavaDirectivesMap, this.evalVisitorFactory, outputBuf, this.templateRegistry, data, this.ijData, null, this.activeDelPackageNames, this.msgBundle, this.xidRenamingMap, this.cssRenamingMap);
    }

    void visitForUseByAssistants(SoyNode node) {
        this.visit(node);
    }

    @Override
    protected void visitTemplateNode(TemplateNode node) {
        try {
            this.checkStrictParamTypes(node);
            this.visitBlockHelper(node);
        }
        catch (RenderException re) {
            throw re.completeStackTraceElement(node);
        }
    }

    @Override
    protected void visitRawTextNode(RawTextNode node) {
        RenderVisitor.append(this.currOutputBuf, node.getRawText());
    }

    @Override
    protected void visitMsgFallbackGroupNode(MsgFallbackGroupNode node) {
        if (this.assistantForMsgs == null) {
            this.assistantForMsgs = new RenderVisitorAssistantForMsgs(this, this.env, this.msgBundle);
        }
        this.assistantForMsgs.visitForUseByMaster(node);
    }

    @Override
    protected void visitMsgHtmlTagNode(MsgHtmlTagNode node) {
        throw new AssertionError();
    }

    @Override
    protected void visitPrintNode(PrintNode node) {
        SoyValue result = this.eval(node.getExprUnion().getExpr(), node);
        if (result instanceof UndefinedData) {
            throw new RenderException("In 'print' tag, expression \"" + node.getExprText() + "\" evaluates to undefined.").addPartialStackTraceElement(node.getSourceLocation());
        }
        for (PrintDirectiveNode directiveNode : node.getChildren()) {
            List<ExprRootNode<?>> argsExprs = directiveNode.getArgs();
            ArrayList argsSoyDatas = Lists.newArrayListWithCapacity((int)argsExprs.size());
            for (ExprRootNode<?> argExpr : argsExprs) {
                argsSoyDatas.add(this.eval(argExpr, directiveNode));
            }
            result = this.applyDirective(directiveNode.getName(), result, argsSoyDatas, node);
        }
        RenderVisitor.append(this.currOutputBuf, result.coerceToString());
    }

    @Override
    protected void visitXidNode(XidNode node) {
        String xid = node.getRenamedText(this.xidRenamingMap);
        RenderVisitor.append(this.currOutputBuf, xid);
    }

    @Override
    protected void visitCssNode(CssNode node) {
        ExprRootNode<?> componentNameExpr = node.getComponentNameExpr();
        if (componentNameExpr != null) {
            RenderVisitor.append(this.currOutputBuf, this.eval(componentNameExpr, node).toString());
            RenderVisitor.append(this.currOutputBuf, "-");
        }
        String className = node.getRenamedSelectorText(this.cssRenamingMap);
        RenderVisitor.append(this.currOutputBuf, className);
    }

    @Override
    protected void visitLetValueNode(LetValueNode node) {
        this.env.peek().put(node.getVarName(), this.eval(node.getValueExpr(), node));
    }

    @Override
    protected void visitLetContentNode(LetContentNode node) {
        SoyData renderedBlock = this.renderBlock(node);
        if (node.getContentKind() != null) {
            renderedBlock = UnsafeSanitizedContentOrdainer.ordainAsSafe(renderedBlock.stringValue(), node.getContentKind());
        }
        this.env.peek().put(node.getVarName(), renderedBlock);
    }

    @Override
    protected void visitIfNode(IfNode node) {
        for (SoyNode child : node.getChildren()) {
            if (child instanceof IfCondNode) {
                IfCondNode icn = (IfCondNode)child;
                if (!this.eval(icn.getExprUnion().getExpr(), node).coerceToBoolean()) continue;
                this.visit(icn);
                return;
            }
            if (child instanceof IfElseNode) {
                this.visit(child);
                return;
            }
            throw new AssertionError();
        }
    }

    @Override
    protected void visitSwitchNode(SwitchNode node) {
        SoyValue switchValue = this.eval(node.getExpr(), node);
        for (SoyNode child : node.getChildren()) {
            if (child instanceof SwitchCaseNode) {
                SwitchCaseNode scn = (SwitchCaseNode)child;
                for (ExprNode exprNode : scn.getExprList()) {
                    if (!switchValue.equals(this.eval(exprNode, scn))) continue;
                    this.visit(scn);
                    return;
                }
                continue;
            }
            if (child instanceof SwitchDefaultNode) {
                this.visit(child);
                return;
            }
            throw new AssertionError();
        }
    }

    @Override
    protected void visitForeachNode(ForeachNode node) {
        SoyValue dataRefValue = this.eval(node.getExpr(), node);
        if (!(dataRefValue instanceof SoyList)) {
            throw new RenderException("In 'foreach' command " + node.toSourceString() + ", the data reference does not resolve to a SoyList (encountered type " + dataRefValue.getClass().getName() + ").");
        }
        SoyList foreachList = (SoyList)dataRefValue;
        if (foreachList.length() > 0) {
            String varName = node.getVarName();
            HashMap newEnvFrame = Maps.newHashMap();
            newEnvFrame.put(varName + "__lastIndex", IntegerData.forValue(foreachList.length() - 1));
            this.env.push(newEnvFrame);
            for (int i = 0; i < foreachList.length(); ++i) {
                newEnvFrame.put(varName + "__index", IntegerData.forValue(i));
                newEnvFrame.put(varName, foreachList.get(i));
                this.visitChildren((ForeachNonemptyNode)node.getChild(0));
            }
            this.env.pop();
        } else if (node.numChildren() == 2) {
            this.visit((SoyNode)node.getChild(1));
        }
    }

    @Override
    protected void visitForNode(ForNode node) {
        ArrayList rangeArgValues = Lists.newArrayList();
        for (ExprNode exprNode : node.getRangeArgs()) {
            SoyValue rangeArgValue = this.eval(exprNode, node);
            if (!(rangeArgValue instanceof IntegerData)) {
                throw new RenderException("In 'for' command " + node.toSourceString() + ", the expression \"" + exprNode.toSourceString() + "\" does not resolve to an integer.");
            }
            rangeArgValues.add(((IntegerData)rangeArgValue).integerValue());
        }
        int increment = rangeArgValues.size() == 3 ? (Integer)rangeArgValues.remove(2) : 1;
        int n = rangeArgValues.size() == 2 ? (Integer)rangeArgValues.remove(0) : 0;
        int limit = (Integer)rangeArgValues.get(0);
        String localVarName = node.getVarName();
        HashMap newEnvFrame = Maps.newHashMap();
        this.env.push(newEnvFrame);
        for (int i = n; i < limit; i += increment) {
            newEnvFrame.put(localVarName, IntegerData.forValue(i));
            this.visitChildren(node);
        }
        this.env.pop();
    }

    @Override
    protected void visitCallBasicNode(CallBasicNode node) {
        TemplateBasicNode callee = this.templateRegistry.getBasicTemplate(node.getCalleeName());
        if (callee == null) {
            throw new RenderException("Attempting to render undefined template '" + node.getCalleeName() + "'.").addPartialStackTraceElement(node.getSourceLocation());
        }
        this.visitCallNodeHelper(node, callee);
    }

    @Override
    protected void visitCallDelegateNode(CallDelegateNode node) {
        TemplateDelegateNode callee;
        String variant;
        ExprRootNode<?> variantExpr = node.getDelCalleeVariantExpr();
        if (variantExpr == null) {
            variant = "";
        } else {
            try {
                SoyValue variantData = this.eval(variantExpr, node);
                variant = variantData instanceof IntegerData ? String.valueOf(variantData.longValue()) : variantData.stringValue();
            }
            catch (SoyDataException e) {
                throw new RenderException(String.format("Variant expression \"%s\" doesn't evaluate to a valid type (Only string and integer are supported).", variantExpr.toSourceString()), e).addPartialStackTraceElement(node.getSourceLocation());
            }
        }
        TemplateDelegateNode.DelTemplateKey delegateKey = new TemplateDelegateNode.DelTemplateKey(node.getDelCalleeName(), variant);
        try {
            callee = this.templateRegistry.selectDelTemplate(delegateKey, this.activeDelPackageNames);
        }
        catch (TemplateRegistry.DelegateTemplateConflictException e) {
            throw new RenderException(e.getMessage(), e).addPartialStackTraceElement(node.getSourceLocation());
        }
        if (callee == null) {
            if (node.allowsEmptyDefault()) {
                return;
            }
            throw new RenderException("Found no active impl for delegate call to '" + node.getDelCalleeName() + "' (and no attribute allowemptydefault=\"true\").").addPartialStackTraceElement(node.getSourceLocation());
        }
        this.visitCallNodeHelper(node, callee);
    }

    private void visitCallNodeHelper(CallNode node, TemplateNode callee) {
        SoyRecord callData;
        SoyRecord dataToPass;
        if (node.isPassingAllData()) {
            dataToPass = this.data;
        } else if (node.isPassingData()) {
            SoyValue dataRefValue = this.eval(node.getDataExpr(), node);
            if (!(dataRefValue instanceof SoyRecord)) {
                throw new RenderException("In 'call' command " + node.toSourceString() + ", the data reference does not resolve to a SoyRecord.").addPartialStackTraceElement(node.getSourceLocation());
            }
            dataToPass = (SoyRecord)dataRefValue;
        } else {
            dataToPass = null;
        }
        if (node.numChildren() == 0) {
            callData = dataToPass == null ? ParamStore.EMPTY_INSTANCE : dataToPass;
        } else {
            ParamStore mutableCallData = dataToPass == null ? new BasicParamStore() : new AugmentedParamStore(dataToPass);
            for (CallParamNode child : node.getChildren()) {
                if (child instanceof CallParamValueNode) {
                    mutableCallData.setField(child.getKey(), this.eval(((CallParamValueNode)child).getValueExprUnion().getExpr(), child));
                    continue;
                }
                if (child instanceof CallParamContentNode) {
                    CallParamContentNode childCpcn = (CallParamContentNode)child;
                    SoyData renderedBlock = this.renderBlock(childCpcn);
                    if (childCpcn.getContentKind() != null) {
                        renderedBlock = UnsafeSanitizedContentOrdainer.ordainAsSafe(renderedBlock.stringValue(), childCpcn.getContentKind());
                    }
                    mutableCallData.setField(child.getKey(), renderedBlock);
                    continue;
                }
                throw new AssertionError();
            }
            callData = mutableCallData;
        }
        if (node.getEscapingDirectiveNames().isEmpty()) {
            RenderVisitor rv = this.createHelperInstance(this.currOutputBuf, callData);
            try {
                rv.exec(callee);
            }
            catch (RenderException re) {
                throw re.addPartialStackTraceElement(node.getSourceLocation());
            }
        }
        StringBuilder calleeBuilder = new StringBuilder();
        RenderVisitor rv = this.createHelperInstance(calleeBuilder, callData);
        try {
            rv.exec(callee);
        }
        catch (RenderException re) {
            throw re.addPartialStackTraceElement(node.getSourceLocation());
        }
        SoyValue resultData = callee.getContentKind() != null ? UnsafeSanitizedContentOrdainer.ordainAsSafe(calleeBuilder.toString(), callee.getContentKind()) : StringData.forValue(calleeBuilder.toString());
        for (String directiveName : node.getEscapingDirectiveNames()) {
            resultData = this.applyDirective(directiveName, resultData, (List<SoyValue>)ImmutableList.of(), node);
        }
        RenderVisitor.append(this.currOutputBuf, ((Object)resultData).toString());
    }

    @Override
    protected void visitCallParamNode(CallParamNode node) {
        throw new AssertionError();
    }

    @Override
    protected void visitLogNode(LogNode node) {
        System.out.println(this.renderBlock(node));
    }

    @Override
    protected void visitDebuggerNode(DebuggerNode node) {
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            if (node instanceof SoyNode.BlockNode) {
                this.visitBlockHelper((SoyNode.BlockNode)node);
            } else {
                this.visitChildren((SoyNode.ParentSoyNode)node);
            }
        }
    }

    private void pushOutputBuf(Appendable outputBuf) {
        this.outputBufStack.push(outputBuf);
        this.currOutputBuf = outputBuf;
    }

    private Appendable popOutputBuf() {
        Appendable poppedOutputBuf = this.outputBufStack.pop();
        this.currOutputBuf = this.outputBufStack.peek();
        return poppedOutputBuf;
    }

    Appendable getCurrOutputBufForUseByAssistants() {
        return this.currOutputBuf;
    }

    private void visitBlockHelper(SoyNode.BlockNode node) {
        if (node.needsEnvFrameDuringInterp() != Boolean.FALSE) {
            this.env.push(Maps.newHashMap());
            this.visitChildren(node);
            this.env.pop();
        } else {
            this.visitChildren(node);
        }
    }

    private StringData renderBlock(SoyNode.BlockNode block) {
        this.pushOutputBuf(new StringBuilder());
        this.visitBlockHelper(block);
        Appendable outputBuf = this.popOutputBuf();
        return StringData.forValue(outputBuf.toString());
    }

    private SoyValue eval(ExprNode expr, SoyNode node) {
        if (expr == null) {
            throw new RenderException("Cannot evaluate expression in V1 syntax.").addPartialStackTraceElement(node.getSourceLocation());
        }
        if (this.evalVisitor == null) {
            this.evalVisitor = this.evalVisitorFactory.create(this.data, this.ijData, this.env);
        }
        try {
            return (SoyValue)this.evalVisitor.exec(expr);
        }
        catch (Exception e) {
            throw new RenderException("When evaluating \"" + expr.toSourceString() + "\": " + e.getMessage(), e).addPartialStackTraceElement(node.getSourceLocation());
        }
    }

    SoyValue evalForUseByAssistants(ExprNode expr, SoyNode node) {
        return this.eval(expr, node);
    }

    static void append(Appendable outputBuf, CharSequence cs) {
        try {
            outputBuf.append(cs);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SoyValue applyDirective(String directiveName, SoyValue value, List<SoyValue> args, SoyNode node) {
        SoyJavaPrintDirective directive = this.soyJavaDirectivesMap.get(directiveName);
        if (directive == null) {
            throw new RenderException("Failed to find Soy print directive with name '" + directiveName + "' (tag " + node.toSourceString() + ")").addPartialStackTraceElement(node.getSourceLocation());
        }
        if (!directive.getValidArgsSizes().contains(args.size())) {
            throw new RenderException("Print directive '" + directiveName + "' used with the wrong number of arguments (tag " + node.toSourceString() + ").").addPartialStackTraceElement(node.getSourceLocation());
        }
        try {
            return directive.applyForJava(value, args);
        }
        catch (RuntimeException e) {
            throw new RenderException(String.format("Failed in applying directive '%s' in tag \"%s\" due to exception: %s", directiveName, node.toSourceString(), e.getMessage()), e).addPartialStackTraceElement(node.getSourceLocation());
        }
    }

    private void checkStrictParamTypes(TemplateNode node) {
        for (TemplateParam param : node.getParams()) {
            SoyValue paramValue = this.data.getField(param.name());
            if (paramValue == null) {
                paramValue = NullData.INSTANCE;
            }
            if (param.type().isInstance(paramValue)) continue;
            throw new RenderException("Parameter type mismatch: attempt to bind value '" + (paramValue instanceof UndefinedData ? "(undefined)" : paramValue) + "' to parameter '" + param.name() + "' which has declared type '" + param.type().toString() + "'.").addPartialStackTraceElement(node.getSourceLocation());
        }
    }
}

