/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  javax.annotation.Nullable
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.internalutils.NodeContentKinds;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.parsepasses.contextautoesc.Context;
import com.google.template.soy.parsepasses.contextautoesc.DerivedTemplateUtils;
import com.google.template.soy.parsepasses.contextautoesc.EscapingMode;
import com.google.template.soy.parsepasses.contextautoesc.Inferences;
import com.google.template.soy.parsepasses.contextautoesc.RawTextContextUpdater;
import com.google.template.soy.parsepasses.contextautoesc.SlicedRawTextNode;
import com.google.template.soy.parsepasses.contextautoesc.SoyAutoescapeException;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.AutoescapeMode;
import com.google.template.soy.soytree.CallBasicNode;
import com.google.template.soy.soytree.CallDelegateNode;
import com.google.template.soy.soytree.CallNode;
import com.google.template.soy.soytree.CallParamContentNode;
import com.google.template.soy.soytree.CssNode;
import com.google.template.soy.soytree.ForNode;
import com.google.template.soy.soytree.ForeachIfemptyNode;
import com.google.template.soy.soytree.ForeachNode;
import com.google.template.soy.soytree.ForeachNonemptyNode;
import com.google.template.soy.soytree.IfElseNode;
import com.google.template.soy.soytree.IfNode;
import com.google.template.soy.soytree.LetContentNode;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SwitchDefaultNode;
import com.google.template.soy.soytree.SwitchNode;
import com.google.template.soy.soytree.TemplateNode;
import com.google.template.soy.soytree.XidNode;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

final class InferenceEngine {
    private final AutoescapeMode autoescapeMode;
    private final AutoescapeMode templateAutoescapeMode;
    private final Inferences inferences;
    @Nullable
    private final EscapingMode defaultEscapingMode;
    private final Set<String> autoescapeCancellingDirectives;
    private final ImmutableList.Builder<SlicedRawTextNode> slicedRawTextNodesBuilder;

    public static Context inferTemplateEndContext(TemplateNode templateNode, Context startContext, Inferences inferences, Set<String> autoescapeCancellingDirectives, ImmutableList.Builder<SlicedRawTextNode> slicedRawTextNodesBuilder) throws SoyAutoescapeException {
        Context endContext;
        try {
            AutoescapeMode autoescapeMode = templateNode.getAutoescapeMode();
            InferenceEngine inferenceEngine = new InferenceEngine(autoescapeMode, autoescapeMode, inferences, autoescapeCancellingDirectives, slicedRawTextNodesBuilder);
            endContext = inferenceEngine.infer(templateNode, startContext);
            inferences.recordTemplateEndContext(templateNode.getTemplateName(), endContext);
        }
        catch (SoyAutoescapeException e) {
            throw e.maybeAssociateNode(templateNode);
        }
        return endContext;
    }

    private InferenceEngine(AutoescapeMode autoescapeMode, AutoescapeMode templateAutoescapeMode, Inferences inferences, Set<String> autoescapeCancellingDirectives, ImmutableList.Builder<SlicedRawTextNode> slicedRawTextNodesBuilder) {
        this.autoescapeMode = autoescapeMode;
        this.templateAutoescapeMode = templateAutoescapeMode;
        this.inferences = inferences;
        this.autoescapeCancellingDirectives = autoescapeCancellingDirectives;
        this.slicedRawTextNodesBuilder = slicedRawTextNodesBuilder;
        this.defaultEscapingMode = autoescapeMode != AutoescapeMode.FALSE ? EscapingMode.ESCAPE_HTML : null;
    }

    private Context infer(SoyNode node, Context context) {
        return new ContextPropagatingVisitor(context).exec(node);
    }

    private Context inferChildren(SoyNode node, Context context) {
        ContextPropagatingVisitor contextPropagatingVisitor = new ContextPropagatingVisitor(context);
        return contextPropagatingVisitor.execChildren(node);
    }

    private static Context getContextAfterDynamicValue(SoyNode node, Context startContext) {
        return InferenceEngine.getContextAfterEscaping(node, startContext, startContext.getContextBeforeDynamicValue().getEscapingModes());
    }

    private static Context getContextAfterEscaping(SoyNode node, Context startContext, List<EscapingMode> escapingModes) {
        Context endContext = startContext.getContextAfterEscaping(escapingModes.isEmpty() ? null : escapingModes.get(0));
        if (endContext.isErrorContext()) {
            if (startContext.uriPart == Context.UriPart.UNKNOWN || startContext.uriPart == Context.UriPart.UNKNOWN_PRE_FRAGMENT) {
                throw SoyAutoescapeException.createWithNode("Cannot determine which part of the URL " + node.toSourceString() + " is in.", node);
            }
            throw SoyAutoescapeException.createWithNode("Don't put {print} or {call} inside comments : " + node.toSourceString(), node);
        }
        return endContext;
    }

    private final class ContextPropagatingVisitor
    extends AbstractSoyNodeVisitor<Context> {
        private Context context;

        public ContextPropagatingVisitor(Context context) {
            this.context = context;
        }

        @Override
        public Context exec(SoyNode node) {
            this.visit(node);
            return this.context;
        }

        public Context execChildren(SoyNode node) {
            if (node instanceof SoyNode.ParentSoyNode) {
                this.visitChildren((SoyNode.ParentSoyNode)node);
            }
            return this.context;
        }

        @Override
        protected void visitTemplateNode(TemplateNode templateNode) {
            Preconditions.checkState((templateNode.getAutoescapeMode() == InferenceEngine.this.autoescapeMode ? 1 : 0) != 0, (Object)"Same ContextPropagatingVisitor cannot be reused for multiple escaping modes.");
            if (InferenceEngine.this.autoescapeMode == AutoescapeMode.STRICT) {
                Preconditions.checkState((boolean)Context.isValidStartContextForContentKind(templateNode.getContentKind(), this.context), (Object)"Strict templates may only be visited in the context for their declared content kind.");
                this.context = Context.getStartContextForContentKind(templateNode.getContentKind());
            }
            this.visitChildren(templateNode);
            if (InferenceEngine.this.autoescapeMode == AutoescapeMode.STRICT) {
                this.checkStrictBlockEndContext(templateNode, this.context);
            }
        }

        @Override
        protected void visitRawTextNode(RawTextNode rawTextNode) {
            Context newContext;
            String rawText = rawTextNode.getRawText();
            try {
                SlicedRawTextNode sliced = RawTextContextUpdater.processRawText(rawTextNode, this.context);
                newContext = sliced.getEndContext();
                InferenceEngine.this.slicedRawTextNodesBuilder.add((Object)sliced);
            }
            catch (SoyAutoescapeException ex) {
                throw ex.maybeAssociateNode(rawTextNode);
            }
            if (newContext.isErrorContext()) {
                throw SoyAutoescapeException.createWithNode("Failed to compute an output context for raw text `" + rawText + "` starting in context " + this.context, rawTextNode);
            }
            this.context = newContext;
        }

        @Override
        protected void visitCallNode(CallNode callNode) {
            try {
                String calleeName = callNode instanceof CallBasicNode ? ((CallBasicNode)callNode).getCalleeName() : ((CallDelegateNode)callNode).getDelCalleeName();
                Pair<String, Context> derivedNameAndContext = this.inferCallSite(callNode, this.context, calleeName, InferenceEngine.this.inferences);
                String derivedCalleeName = (String)derivedNameAndContext.first;
                if (!calleeName.equals(derivedCalleeName)) {
                    InferenceEngine.this.inferences.retargetCall(callNode, derivedCalleeName);
                }
                this.context = (Context)derivedNameAndContext.second;
            }
            catch (SoyAutoescapeException ex) {
                throw ex.maybeAssociateNode(callNode);
            }
            this.visitChildren(callNode);
        }

        @Override
        protected void visitCallParamContentNode(CallParamContentNode node) {
            if (node.getContentKind() != null) {
                this.inferInStrictMode(node);
            } else if (InferenceEngine.this.autoescapeMode == AutoescapeMode.CONTEXTUAL) {
                this.inferInContextualModeForHtml(node);
            } else {
                Preconditions.checkState((InferenceEngine.this.autoescapeMode != AutoescapeMode.STRICT ? 1 : 0) != 0);
            }
        }

        @Override
        protected void visitXidNode(XidNode node) {
            this.context = this.context.getContextBeforeDynamicValue();
        }

        @Override
        protected void visitCssNode(CssNode node) {
            this.context = this.context.getContextBeforeDynamicValue();
        }

        @Override
        protected void visitLetContentNode(LetContentNode node) {
            if (node.getContentKind() == null) {
                super.visitLetContentNode(node);
            } else {
                this.inferInStrictMode(node);
            }
        }

        @Override
        protected void visitIfNode(IfNode ifNode) {
            this.propagateAcrossDisjunction(ifNode);
        }

        @Override
        protected void visitSwitchNode(SwitchNode switchNode) {
            this.propagateAcrossDisjunction(switchNode);
        }

        @Override
        protected void visitForNode(ForNode forNode) {
            try {
                Context afterBody = this.context;
                for (SoyNode child : forNode.getChildren()) {
                    afterBody = InferenceEngine.this.infer(child, afterBody);
                }
                Context combined = Context.union(this.context, afterBody);
                if (combined.isErrorContext()) {
                    throw SoyAutoescapeException.createWithNode("{for} command changes context so it cannot be reentered : " + forNode.toSourceString(), forNode);
                }
                this.context = combined;
            }
            catch (SoyAutoescapeException ex) {
                throw ex.maybeAssociateNode(forNode);
            }
        }

        @Override
        protected void visitForeachNode(ForeachNode foreachNode) {
            ForeachIfemptyNode ieNode;
            List foreachChildren = foreachNode.getChildren();
            ForeachNonemptyNode neNode = (ForeachNonemptyNode)foreachChildren.get(0);
            if (foreachChildren.size() == 2) {
                ieNode = (ForeachIfemptyNode)foreachChildren.get(1);
            } else if (foreachChildren.size() == 1) {
                ieNode = null;
            } else {
                throw new AssertionError();
            }
            try {
                Context ifemptyContext;
                Context combined;
                Context afterBody = this.context;
                if (neNode != null) {
                    afterBody = InferenceEngine.this.infer(neNode, this.context);
                    Context elseContext = InferenceEngine.this.infer(neNode, afterBody);
                    combined = Context.union(elseContext, afterBody);
                    if (combined.isErrorContext()) {
                        throw SoyAutoescapeException.createWithNode("{foreach} body does not end in the same context after repeated entries : " + neNode.toSourceString(), neNode);
                    }
                    afterBody = combined;
                }
                if ((combined = Context.union(ifemptyContext = ieNode != null ? InferenceEngine.this.infer(ieNode, this.context) : this.context, afterBody)).isErrorContext()) {
                    throw SoyAutoescapeException.createWithNode((ieNode == null ? "{foreach} body changes context : " : "{foreach} body does not end in the same context as {ifempty} : ") + foreachNode.toSourceString(), ieNode == null ? foreachNode : ieNode);
                }
                this.context = combined;
            }
            catch (SoyAutoescapeException ex) {
                throw ex.maybeAssociateNode(foreachNode);
            }
        }

        @Override
        protected void visitPrintNode(PrintNode printNode) {
            try {
                if (InferenceEngine.this.autoescapeMode == AutoescapeMode.STRICT && !this.context.equals(Context.TEXT)) {
                    for (PrintDirectiveNode printDirective : printNode.getChildren()) {
                        if (printDirective.getName().equals("|noAutoescape")) {
                            if (InferenceEngine.this.templateAutoescapeMode != AutoescapeMode.STRICT) continue;
                            SanitizedContent.ContentKind recommendedKind = this.context.getMostAppropriateContentKind();
                            String recommendedKindStr = recommendedKind == SanitizedContent.ContentKind.TEXT ? "appropriate kind=\"...\"" : "kind=\"" + NodeContentKinds.toAttributeValue(recommendedKind) + "\"";
                            throw SoyAutoescapeException.createWithNode("noAutoescape is not allowed in strict autoescaping mode. Instead, pass in a {param} with " + recommendedKindStr + " or SanitizedContent.", printNode);
                        }
                        if (!InferenceEngine.this.autoescapeCancellingDirectives.contains(printDirective.getName())) continue;
                        throw SoyAutoescapeException.createWithNode("Autoescape-cancelling print directives like " + printDirective.getName() + " are only allowed in kind=\"text\" blocks. If you really want to over-escape, try using a let block: {let $foo kind=\"text\"}" + printNode.toSourceString() + "{/let}{$foo}.", printNode);
                    }
                }
                Object escapingModes = InferenceEngine.this.inferences.getEscapingMode(printNode);
                this.context = this.context.getContextBeforeDynamicValue();
                if (escapingModes.isEmpty()) {
                    List<EscapingMode> escapingModesToSet = null;
                    switch (InferenceEngine.this.autoescapeMode) {
                        case STRICT: 
                        case CONTEXTUAL: {
                            escapingModesToSet = this.context.getEscapingModes();
                            escapingModes = escapingModesToSet;
                            break;
                        }
                        case FALSE: {
                            break;
                        }
                        case TRUE: {
                            escapingModes = ImmutableList.of((Object)((Object)InferenceEngine.this.defaultEscapingMode));
                        }
                    }
                    InferenceEngine.this.inferences.setEscapingDirectives(printNode, this.context, escapingModesToSet);
                } else if (!this.context.isCompatibleWith((EscapingMode)((Object)escapingModes.get(0)))) {
                    throw SoyAutoescapeException.createWithNode("Escaping modes " + escapingModes + " not compatible with " + this.context + " : " + printNode.toSourceString(), printNode);
                }
                this.context = !escapingModes.isEmpty() || InferenceEngine.this.autoescapeMode == AutoescapeMode.CONTEXTUAL || InferenceEngine.this.autoescapeMode == AutoescapeMode.STRICT ? InferenceEngine.getContextAfterEscaping(printNode, this.context, (List)escapingModes) : RawTextContextUpdater.processRawText(new RawTextNode(-1, "z"), this.context).getEndContext();
            }
            catch (SoyAutoescapeException ex) {
                throw ex.maybeAssociateNode(printNode);
            }
        }

        @Override
        protected void visitSoyNode(SoyNode node) {
            if (node instanceof SoyNode.ParentSoyNode) {
                this.visitChildren((SoyNode.ParentSoyNode)node);
            }
        }

        private SanitizedContent.ContentKind getCommonContentKindIfStrict(List<TemplateNode> templates) {
            if (templates == null || templates.isEmpty()) {
                return null;
            }
            SanitizedContent.ContentKind contentKind = templates.get(0).getContentKind();
            for (TemplateNode template : templates) {
                Preconditions.checkArgument((template.getContentKind() == contentKind ? 1 : 0) != 0);
            }
            return contentKind;
        }

        private Pair<String, Context> inferCallSite(CallNode callNode, Context startContext, String templateName, Inferences inferences) throws SoyAutoescapeException {
            inferences.recordTemplateChecked(templateName);
            List<TemplateNode> targets = inferences.lookupTemplates(templateName);
            SanitizedContent.ContentKind calleeStrictContentKind = this.getCommonContentKindIfStrict(targets);
            if (InferenceEngine.this.autoescapeMode == AutoescapeMode.STRICT) {
                if (calleeStrictContentKind != null && Context.isValidStartContextForContentKind(calleeStrictContentKind, startContext)) {
                    return Pair.of(templateName, InferenceEngine.getContextAfterDynamicValue(callNode, startContext));
                }
                if (calleeStrictContentKind != null || targets == null || targets.isEmpty()) {
                    inferences.setEscapingDirectives(callNode, this.context, this.context.getEscapingModes());
                    return Pair.of(templateName, InferenceEngine.getContextAfterDynamicValue(callNode, startContext));
                }
                if (startContext.equals(Context.TEXT)) {
                    return this.contextualizeCallee(callNode, Context.TEXT, templateName, inferences);
                }
                throw SoyAutoescapeException.createWithNode("Soy strict autoescaping currently forbids calls to non-strict templates, unless the context is kind=\"text\", since there's no guarantee the callee is safe: " + callNode.getTagString(), callNode);
            }
            if (targets == null || targets.isEmpty()) {
                return Pair.of(templateName, startContext);
            }
            if (calleeStrictContentKind != null) {
                if (!Context.isValidStartContextForContentKindLoose(calleeStrictContentKind, startContext)) {
                    throw SoyAutoescapeException.createWithNode("Cannot call strictly autoescaped template " + templateName + " of kind=\"" + NodeContentKinds.toAttributeValue(calleeStrictContentKind) + "\" from incompatible context " + startContext + ". Strict templates generate extra code to safely call templates of other content kinds, but non-strict templates do not: " + callNode.getTagString(), callNode);
                }
                return Pair.of(templateName, startContext);
            }
            return this.contextualizeCallee(callNode, startContext, templateName, inferences);
        }

        private Pair<String, Context> contextualizeCallee(CallNode callNode, Context startContext, String calleeName, Inferences inferences) {
            String suffix = DerivedTemplateUtils.getSuffix(startContext);
            String baseName = DerivedTemplateUtils.getBaseName(calleeName);
            String newCalleeName = baseName + suffix;
            if (inferences.lookupTemplates(newCalleeName) == null) {
                inferences.cloneTemplates(baseName, newCalleeName);
            }
            try {
                Context endContext = this.determineContextualization(startContext, newCalleeName, inferences);
                return Pair.of(newCalleeName, endContext);
            }
            catch (SoyAutoescapeException e) {
                throw SoyAutoescapeException.createCausedWithNode("Error while re-contextualizing template " + calleeName + " in context " + startContext + ":", e, callNode);
            }
        }

        private Context determineContextualization(Context startContext, String calleeName, Inferences inferences) {
            Context endContext = inferences.getTemplateEndContext(calleeName);
            if (endContext != null) {
                return endContext;
            }
            List<TemplateNode> templateNodes = inferences.lookupTemplates(calleeName);
            Pair<Inferences, Context> hypothesis = this.hypothesizeContextualization(startContext, startContext, calleeName, templateNodes, inferences);
            endContext = (Context)hypothesis.second;
            Inferences subInferences = (Inferences)hypothesis.first;
            if (!endContext.equals(startContext) && subInferences.wasTemplateChecked(calleeName)) {
                Pair<Inferences, Context> secondHypothesis = this.hypothesizeContextualization(startContext, endContext, calleeName, templateNodes, inferences);
                if ((endContext = Context.union((Context)secondHypothesis.second, endContext)).isErrorContext()) {
                    throw SoyAutoescapeException.createWithNode("Cannot determine end context for recursive template " + calleeName, templateNodes.get(0));
                }
            }
            subInferences.recordTemplateEndContext(calleeName, endContext);
            subInferences.foldIntoParent();
            return endContext;
        }

        private Pair<Inferences, Context> hypothesizeContextualization(Context startContext, Context hypotheticalEndContext, String calleeName, List<TemplateNode> templateNodes, Inferences parentInferences) {
            Inferences inferences = new Inferences(parentInferences);
            Context endContext = null;
            inferences.recordTemplateEndContext(calleeName, hypotheticalEndContext);
            for (TemplateNode templateNode : templateNodes) {
                Context c = InferenceEngine.inferTemplateEndContext(templateNode, startContext, inferences, InferenceEngine.this.autoescapeCancellingDirectives, (ImmutableList.Builder<SlicedRawTextNode>)InferenceEngine.this.slicedRawTextNodesBuilder);
                endContext = endContext != null ? Context.union(endContext, c) : c;
            }
            return Pair.of(inferences, endContext);
        }

        private void propagateAcrossDisjunction(SoyNode.ParentSoyNode<?> node) {
            try {
                Iterator childIt = node.getChildren().iterator();
                SoyNode firstBranch = (SoyNode)childIt.next();
                Context out = InferenceEngine.this.infer(firstBranch, this.context);
                boolean sawElseOrDefault = false;
                while (childIt.hasNext()) {
                    SoyNode branch = (SoyNode)childIt.next();
                    Context brOut = InferenceEngine.this.infer(branch, this.context);
                    Context combined = Context.union(out, brOut);
                    if (combined.isErrorContext()) {
                        throw SoyAutoescapeException.createWithNode((node instanceof IfNode ? "{if} command branch ends in a different context than preceding branches: " : "{switch} command case ends in a different context than preceding cases: ") + branch.toSourceString(), branch);
                    }
                    out = combined;
                    if (!(branch instanceof IfElseNode) && !(branch instanceof SwitchDefaultNode)) continue;
                    sawElseOrDefault = true;
                }
                if (!sawElseOrDefault) {
                    Context combined = Context.union(this.context, out);
                    if (combined.isErrorContext()) {
                        throw SoyAutoescapeException.createWithNode((node instanceof IfNode ? "{if} command without {else} changes context : " : "{switch} command without {default} changes context : ") + node.toSourceString(), node);
                    }
                    out = combined;
                }
                this.context = out;
            }
            catch (SoyAutoescapeException ex) {
                throw ex.maybeAssociateNode(node);
            }
        }

        private void inferInStrictMode(SoyNode.RenderUnitNode node) {
            Context endContext = new InferenceEngine(AutoescapeMode.STRICT, InferenceEngine.this.templateAutoescapeMode, InferenceEngine.this.inferences, InferenceEngine.this.autoescapeCancellingDirectives, InferenceEngine.this.slicedRawTextNodesBuilder).inferChildren(node, Context.getStartContextForContentKind(node.getContentKind()));
            this.checkStrictBlockEndContext(node, endContext);
        }

        private void checkStrictBlockEndContext(SoyNode.RenderUnitNode node, Context endContext) {
            if (!Context.isValidEndContextForContentKind(node.getContentKind(), endContext)) {
                throw SoyAutoescapeException.createWithNode("A strict block of kind=\"" + NodeContentKinds.toAttributeValue(node.getContentKind()) + "\" cannot end in context " + endContext + ". Likely cause is " + Context.getLikelyEndContextMismatchCause(node.getContentKind(), endContext) + ": " + node.getTagString(), node);
            }
        }

        private void inferInContextualModeForHtml(SoyNode.CommandNode node) {
            Context paramContentNodeEndContext = new InferenceEngine(AutoescapeMode.CONTEXTUAL, InferenceEngine.this.templateAutoescapeMode, InferenceEngine.this.inferences, InferenceEngine.this.autoescapeCancellingDirectives, InferenceEngine.this.slicedRawTextNodesBuilder).inferChildren(node, Context.HTML_PCDATA);
            if (!paramContentNodeEndContext.equals(Context.HTML_PCDATA)) {
                throw SoyAutoescapeException.createWithNode("Blocks should start and end in HTML context: " + node.getTagString(), node);
            }
        }
    }
}

