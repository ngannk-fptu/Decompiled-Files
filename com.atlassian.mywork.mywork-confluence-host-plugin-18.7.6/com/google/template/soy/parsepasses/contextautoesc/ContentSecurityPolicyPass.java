/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.VarDefn;
import com.google.template.soy.exprtree.VarRefNode;
import com.google.template.soy.parsepasses.contextautoesc.Context;
import com.google.template.soy.parsepasses.contextautoesc.SlicedRawTextNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.IfCondNode;
import com.google.template.soy.soytree.IfNode;
import com.google.template.soy.soytree.PrintNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyFileSetNode;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.types.primitive.StringType;
import java.util.Collections;
import java.util.List;

public final class ContentSecurityPolicyPass {
    public static final String CSP_NONCE_VARIABLE_NAME = "csp_nonce";
    private static final String NONCE_ATTR_BEFORE_VALUE = " nonce=\"";
    private static final String ATTR_AFTER_VALUE = "\"";
    private static final Predicate<? super Context> IN_SCRIPT_OR_STYLE_TAG_PREDICATE = new Predicate<Context>(){

        public boolean apply(Context c) {
            return (c.elType == Context.ElementType.SCRIPT || c.elType == Context.ElementType.STYLE) && c.state == Context.State.HTML_TAG && c.attrType == Context.AttributeType.NONE;
        }
    };
    private static final Predicate<? super Context> IN_SCRIPT_OR_STYLE_BODY_PREDICATE = new Predicate<Context>(){

        public boolean apply(Context c) {
            return c.attrType == Context.AttributeType.NONE && (c.state == Context.State.JS || c.state == Context.State.CSS);
        }
    };
    private static final Predicate<? super Context> IN_SCRIPT_OR_STYLE_ATTR_VALUE = new Predicate<Context>(){

        public boolean apply(Context c) {
            return c.elType != Context.ElementType.NONE && (this.isScriptAttr(c) || this.isStyleAttr(c));
        }

        private boolean isScriptAttr(Context c) {
            return c.attrType == Context.AttributeType.SCRIPT && c.state == Context.State.JS;
        }

        private boolean isStyleAttr(Context c) {
            return c.attrType == Context.AttributeType.STYLE && c.state == Context.State.CSS;
        }
    };

    private ContentSecurityPolicyPass() {
    }

    public static void blessAuthorSpecifiedScripts(Iterable<? extends SlicedRawTextNode> slicedRawTextNodes) {
        ImmutableList.Builder injectedSoyGenerators = ImmutableList.builder();
        ContentSecurityPolicyPass.findCompleteInlineEventHandlers(slicedRawTextNodes, (ImmutableList.Builder<InjectedSoyGenerator>)injectedSoyGenerators);
        ContentSecurityPolicyPass.findNonceAttrLocations(slicedRawTextNodes, (ImmutableList.Builder<InjectedSoyGenerator>)injectedSoyGenerators);
        List<InjectedSoyGenerator> groupedInjectedAttrs = ContentSecurityPolicyPass.sortAndGroup((List<InjectedSoyGenerator>)injectedSoyGenerators.build());
        ContentSecurityPolicyPass.generateAndInsertSoyNodesWrappedInIfNode(groupedInjectedAttrs);
    }

    private static void findCompleteInlineEventHandlers(Iterable<? extends SlicedRawTextNode> slicedRawTextNodes, ImmutableList.Builder<InjectedSoyGenerator> out) {
        List<SlicedRawTextNode.RawTextSlice> valueSlices = SlicedRawTextNode.find(slicedRawTextNodes, null, IN_SCRIPT_OR_STYLE_ATTR_VALUE, null);
        for (SlicedRawTextNode.RawTextSlice valueSlice : valueSlices) {
            Context.AttributeEndDelimiter delimType = valueSlice.context.delimType;
            if (delimType != Context.AttributeEndDelimiter.DOUBLE_QUOTE && delimType != Context.AttributeEndDelimiter.SINGLE_QUOTE) continue;
            out.add((Object)new InlineContentPrefixGenerator(valueSlice.slicedRawTextNode.getRawTextNode(), valueSlice.getStartOffset()));
        }
    }

    private static void findNonceAttrLocations(Iterable<? extends SlicedRawTextNode> slicedRawTextNodes, ImmutableList.Builder<InjectedSoyGenerator> out) {
        for (SlicedRawTextNode.RawTextSlice slice : SlicedRawTextNode.find(slicedRawTextNodes, null, IN_SCRIPT_OR_STYLE_TAG_PREDICATE, IN_SCRIPT_OR_STYLE_BODY_PREDICATE)) {
            int rawTextLen;
            String rawText = slice.getRawText();
            if (rawText.charAt((rawTextLen = rawText.length()) - 1) != '>') {
                throw new IllegalStateException("Invalid tag end: " + rawText);
            }
            int insertionPoint = rawTextLen - 1;
            if (insertionPoint - 1 >= 0 && rawText.charAt(insertionPoint - 1) == '/') {
                --insertionPoint;
            }
            out.add((Object)new NonceAttrGenerator(slice.slicedRawTextNode.getRawTextNode(), slice.getStartOffset() + insertionPoint));
        }
    }

    private static List<InjectedSoyGenerator> sortAndGroup(List<InjectedSoyGenerator> ungrouped) {
        ungrouped = Lists.newArrayList(ungrouped);
        Collections.sort(ungrouped);
        ImmutableList.Builder grouped = ImmutableList.builder();
        int n = ungrouped.size();
        int i = 0;
        while (i < n) {
            int end;
            InjectedSoyGenerator firstGroupMember = (InjectedSoyGenerator)ungrouped.get(i);
            for (end = i + 1; end < n && ((InjectedSoyGenerator)ungrouped.get((int)end)).rawTextNode == firstGroupMember.rawTextNode && ((InjectedSoyGenerator)ungrouped.get((int)end)).offset == firstGroupMember.offset; ++end) {
            }
            grouped.add((Object)new GroupOfInjectedSoyGenerator(ungrouped.subList(i, end)));
            i = end;
        }
        return grouped.build();
    }

    private static void generateAndInsertSoyNodesWrappedInIfNode(List<? extends InjectedSoyGenerator> injectedSoyGenerators) {
        int n = injectedSoyGenerators.size();
        int i = 0;
        while (i < n) {
            int end;
            InjectedSoyGenerator first = injectedSoyGenerators.get(i);
            for (end = i + 1; end < n; ++end) {
                InjectedSoyGenerator atEnd = injectedSoyGenerators.get(end);
                if (first.rawTextNode != atEnd.rawTextNode) break;
            }
            RawTextNode rawTextNode = first.rawTextNode;
            String rawText = rawTextNode.getRawText();
            SoyNode.BlockNode parent = rawTextNode.getParent();
            IdGenerator idGenerator = parent.getNearestAncestor(SoyFileSetNode.class).getNodeIdGenerator();
            int textStart = 0;
            int childIndex = parent.getChildIndex(rawTextNode);
            parent.removeChild(rawTextNode);
            for (InjectedSoyGenerator injectedSoyGenerator : injectedSoyGenerators.subList(i, end)) {
                int offset = injectedSoyGenerator.offset;
                if (offset != textStart) {
                    RawTextNode textBefore = new RawTextNode(idGenerator.genId(), rawText.substring(textStart, offset));
                    parent.addChild(childIndex, textBefore);
                    ++childIndex;
                    textStart = offset;
                }
                IfNode ifNode = new IfNode(idGenerator.genId());
                IfCondNode ifCondNode = new IfCondNode(idGenerator.genId(), "if", new ExprUnion(new ExprRootNode<VarRefNode>(ContentSecurityPolicyPass.makeReferenceToInjectedCspNonce())));
                parent.addChild(childIndex, ifNode);
                ++childIndex;
                ifNode.addChild(ifCondNode);
                ImmutableList.Builder newChildren = ImmutableList.builder();
                injectedSoyGenerator.addNodesToInject(idGenerator, (ImmutableList.Builder<? super SoyNode.StandaloneNode>)newChildren);
                ifCondNode.addChildren(newChildren.build());
            }
            if (textStart != rawText.length()) {
                RawTextNode textTail = new RawTextNode(idGenerator.genId(), rawText.substring(textStart));
                parent.addChild(childIndex, textTail);
            }
            i = end;
        }
    }

    private static VarRefNode makeReferenceToInjectedCspNonce() {
        return new VarRefNode(CSP_NONCE_VARIABLE_NAME, true, false, ImplicitCspNonceDefn.SINGLETON);
    }

    private static PrintNode makeInjectedCspNoncePrintNode(IdGenerator idGenerator) {
        return new PrintNode(idGenerator.genId(), true, new ExprUnion(new ExprRootNode<VarRefNode>(ContentSecurityPolicyPass.makeReferenceToInjectedCspNonce())), null);
    }

    private static final class ImplicitCspNonceDefn
    implements VarDefn {
        public static final ImplicitCspNonceDefn SINGLETON = new ImplicitCspNonceDefn();

        private ImplicitCspNonceDefn() {
        }

        @Override
        public VarDefn.Kind kind() {
            return VarDefn.Kind.IJ_PARAM;
        }

        @Override
        public String name() {
            return ContentSecurityPolicyPass.CSP_NONCE_VARIABLE_NAME;
        }

        @Override
        public StringType type() {
            return StringType.getInstance();
        }
    }

    private static final class GroupOfInjectedSoyGenerator
    extends InjectedSoyGenerator {
        final ImmutableList<InjectedSoyGenerator> members;

        GroupOfInjectedSoyGenerator(List<? extends InjectedSoyGenerator> group) {
            super(group.get((int)0).rawTextNode, group.get((int)0).offset);
            this.members = ImmutableList.copyOf(group);
            for (InjectedSoyGenerator member : this.members) {
                if (member.rawTextNode == this.rawTextNode && member.offset == this.offset) continue;
                throw new IllegalArgumentException("Invalid group member");
            }
        }

        @Override
        void addNodesToInject(IdGenerator idGenerator, ImmutableList.Builder<? super SoyNode.StandaloneNode> out) {
            for (InjectedSoyGenerator member : this.members) {
                member.addNodesToInject(idGenerator, out);
            }
        }
    }

    private static final class InlineContentPrefixGenerator
    extends InjectedSoyGenerator {
        InlineContentPrefixGenerator(RawTextNode rawTextNode, int offset) {
            super(rawTextNode, offset);
        }

        @Override
        void addNodesToInject(IdGenerator idGenerator, ImmutableList.Builder<? super SoyNode.StandaloneNode> out) {
            out.add((Object)new RawTextNode(idGenerator.genId(), "/*"));
            out.add((Object)ContentSecurityPolicyPass.makeInjectedCspNoncePrintNode(idGenerator));
            out.add((Object)new RawTextNode(idGenerator.genId(), "*/"));
        }
    }

    private static final class NonceAttrGenerator
    extends InjectedSoyGenerator {
        NonceAttrGenerator(RawTextNode rawTextNode, int offset) {
            super(rawTextNode, offset);
        }

        @Override
        void addNodesToInject(IdGenerator idGenerator, ImmutableList.Builder<? super SoyNode.StandaloneNode> out) {
            out.add((Object)new RawTextNode(idGenerator.genId(), ContentSecurityPolicyPass.NONCE_ATTR_BEFORE_VALUE));
            out.add((Object)ContentSecurityPolicyPass.makeInjectedCspNoncePrintNode(idGenerator));
            out.add((Object)new RawTextNode(idGenerator.genId(), ContentSecurityPolicyPass.ATTR_AFTER_VALUE));
        }
    }

    private static abstract class InjectedSoyGenerator
    implements Comparable<InjectedSoyGenerator> {
        final RawTextNode rawTextNode;
        final int offset;

        InjectedSoyGenerator(RawTextNode rawTextNode, int offset) {
            Preconditions.checkElementIndex((int)offset, (int)rawTextNode.getRawText().length(), (String)"text offset");
            this.rawTextNode = rawTextNode;
            this.offset = offset;
        }

        abstract void addNodesToInject(IdGenerator var1, ImmutableList.Builder<? super SoyNode.StandaloneNode> var2);

        @Override
        public final int compareTo(InjectedSoyGenerator other) {
            int delta = this.rawTextNode.getId() - other.rawTextNode.getId();
            if (delta == 0) {
                delta = this.offset - other.offset;
            }
            return delta;
        }
    }
}

