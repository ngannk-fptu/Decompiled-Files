/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.google.template.soy.msgs.internal;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.msgs.internal.SoyMsgIdComputer;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import com.google.template.soy.msgs.restricted.SoyMsgPlaceholderPart;
import com.google.template.soy.msgs.restricted.SoyMsgPluralCaseSpec;
import com.google.template.soy.msgs.restricted.SoyMsgPluralPart;
import com.google.template.soy.msgs.restricted.SoyMsgPluralRemainderPart;
import com.google.template.soy.msgs.restricted.SoyMsgRawTextPart;
import com.google.template.soy.msgs.restricted.SoyMsgSelectPart;
import com.google.template.soy.soytree.CaseOrDefaultNode;
import com.google.template.soy.soytree.MsgNode;
import com.google.template.soy.soytree.MsgPlaceholderNode;
import com.google.template.soy.soytree.MsgPluralCaseNode;
import com.google.template.soy.soytree.MsgPluralDefaultNode;
import com.google.template.soy.soytree.MsgPluralNode;
import com.google.template.soy.soytree.MsgPluralRemainderNode;
import com.google.template.soy.soytree.MsgSelectCaseNode;
import com.google.template.soy.soytree.MsgSelectDefaultNode;
import com.google.template.soy.soytree.MsgSelectNode;
import com.google.template.soy.soytree.RawTextNode;
import com.google.template.soy.soytree.SoyNode;

public class MsgUtils {
    private MsgUtils() {
    }

    public static ImmutableList<SoyMsgPart> buildMsgParts(MsgNode msgNode) {
        return MsgUtils.buildMsgPartsForChildren(msgNode, msgNode);
    }

    public static MsgPartsAndIds buildMsgPartsAndComputeMsgIdForDualFormat(MsgNode msgNode) {
        if (msgNode.isPlrselMsg()) {
            MsgPartsAndIds mpai = MsgUtils.buildMsgPartsAndComputeMsgIds(msgNode, true);
            return new MsgPartsAndIds(mpai.parts, mpai.idUsingBracedPhs, -1L);
        }
        return MsgUtils.buildMsgPartsAndComputeMsgIds(msgNode, false);
    }

    public static long computeMsgIdForDualFormat(MsgNode msgNode) {
        return msgNode.isPlrselMsg() ? MsgUtils.computeMsgIdUsingBracedPhs(msgNode) : MsgUtils.computeMsgId(msgNode);
    }

    private static MsgPartsAndIds buildMsgPartsAndComputeMsgIds(MsgNode msgNode, boolean doComputeMsgIdUsingBracedPhs) {
        ImmutableList<SoyMsgPart> msgParts = MsgUtils.buildMsgParts(msgNode);
        long msgId = SoyMsgIdComputer.computeMsgId(msgParts, msgNode.getMeaning(), msgNode.getContentType());
        long msgIdUsingBracedPhs = doComputeMsgIdUsingBracedPhs ? SoyMsgIdComputer.computeMsgIdUsingBracedPhs(msgParts, msgNode.getMeaning(), msgNode.getContentType()) : -1L;
        return new MsgPartsAndIds(msgParts, msgId, msgIdUsingBracedPhs);
    }

    private static long computeMsgId(MsgNode msgNode) {
        return SoyMsgIdComputer.computeMsgId(MsgUtils.buildMsgParts(msgNode), msgNode.getMeaning(), msgNode.getContentType());
    }

    private static long computeMsgIdUsingBracedPhs(MsgNode msgNode) {
        return SoyMsgIdComputer.computeMsgIdUsingBracedPhs(MsgUtils.buildMsgParts(msgNode), msgNode.getMeaning(), msgNode.getContentType());
    }

    private static ImmutableList<SoyMsgPart> buildMsgPartsForChildren(SoyNode.BlockNode parent, MsgNode msgNode) {
        ImmutableList.Builder msgParts = ImmutableList.builder();
        for (SoyNode.StandaloneNode child : parent.getChildren()) {
            if (child instanceof RawTextNode) {
                String rawText = ((RawTextNode)child).getRawText();
                msgParts.add((Object)SoyMsgRawTextPart.of(rawText));
                continue;
            }
            if (child instanceof MsgPlaceholderNode) {
                String placeholderName = msgNode.getPlaceholderName((MsgPlaceholderNode)child);
                msgParts.add((Object)new SoyMsgPlaceholderPart(placeholderName));
                continue;
            }
            if (child instanceof MsgPluralRemainderNode) {
                msgParts.add((Object)new SoyMsgPluralRemainderPart(msgNode.getPluralVarName(child.getNearestAncestor(MsgPluralNode.class))));
                continue;
            }
            if (child instanceof MsgPluralNode) {
                msgParts.add((Object)MsgUtils.buildMsgPartForPlural((MsgPluralNode)child, msgNode));
                continue;
            }
            if (!(child instanceof MsgSelectNode)) continue;
            msgParts.add((Object)MsgUtils.buildMsgPartForSelect((MsgSelectNode)child, msgNode));
        }
        return msgParts.build();
    }

    private static SoyMsgPluralPart buildMsgPartForPlural(MsgPluralNode msgPluralNode, MsgNode msgNode) {
        ImmutableList.Builder pluralCases = ImmutableList.builder();
        for (CaseOrDefaultNode child : msgPluralNode.getChildren()) {
            SoyMsgPluralCaseSpec caseSpec;
            ImmutableList<SoyMsgPart> caseMsgParts = MsgUtils.buildMsgPartsForChildren(child, msgNode);
            if (child instanceof MsgPluralCaseNode) {
                caseSpec = new SoyMsgPluralCaseSpec(((MsgPluralCaseNode)child).getCaseNumber());
            } else if (child instanceof MsgPluralDefaultNode) {
                caseSpec = new SoyMsgPluralCaseSpec("other");
            } else {
                throw new AssertionError((Object)"Unidentified node under a plural node.");
            }
            pluralCases.add(Pair.of(caseSpec, caseMsgParts));
        }
        return new SoyMsgPluralPart(msgNode.getPluralVarName(msgPluralNode), msgPluralNode.getOffset(), (ImmutableList<Pair<SoyMsgPluralCaseSpec, ImmutableList<SoyMsgPart>>>)pluralCases.build());
    }

    private static SoyMsgSelectPart buildMsgPartForSelect(MsgSelectNode msgSelectNode, MsgNode msgNode) {
        ImmutableList.Builder selectCases = ImmutableList.builder();
        for (CaseOrDefaultNode child : msgSelectNode.getChildren()) {
            String caseValue;
            ImmutableList<SoyMsgPart> caseMsgParts = MsgUtils.buildMsgPartsForChildren(child, msgNode);
            if (child instanceof MsgSelectCaseNode) {
                caseValue = ((MsgSelectCaseNode)child).getCaseValue();
            } else if (child instanceof MsgSelectDefaultNode) {
                caseValue = null;
            } else {
                throw new AssertionError((Object)"Unidentified node under a select node.");
            }
            selectCases.add(Pair.of(caseValue, caseMsgParts));
        }
        return new SoyMsgSelectPart(msgNode.getSelectVarName(msgSelectNode), (ImmutableList<Pair<String, ImmutableList<SoyMsgPart>>>)selectCases.build());
    }

    public static class MsgPartsAndIds {
        public final ImmutableList<SoyMsgPart> parts;
        public final long id;
        public final long idUsingBracedPhs;

        private MsgPartsAndIds(ImmutableList<SoyMsgPart> parts, long id, long idUsingBracedPhs) {
            this.parts = parts;
            this.id = id;
            this.idUsingBracedPhs = idUsingBracedPhs;
        }
    }
}

