/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package com.google.template.soy.parsepasses;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.AbstractSoyNodeVisitor;
import com.google.template.soy.soytree.MsgNode;
import com.google.template.soy.soytree.MsgPluralNode;
import com.google.template.soy.soytree.MsgSelectCaseNode;
import com.google.template.soy.soytree.MsgSelectDefaultNode;
import com.google.template.soy.soytree.MsgSelectNode;
import com.google.template.soy.soytree.MsgSubstUnitBaseVarNameUtils;
import com.google.template.soy.soytree.SoyNode;
import com.google.template.soy.soytree.SoySyntaxExceptionUtils;
import com.google.template.soy.soytree.SoytreeUtils;
import java.util.List;
import javax.annotation.Nullable;

public class RewriteGenderMsgsVisitor
extends AbstractSoyNodeVisitor<Void> {
    public static final String FALLBACK_BASE_SELECT_VAR_NAME = "GENDER";
    private IdGenerator nodeIdGen;

    public RewriteGenderMsgsVisitor(IdGenerator nodeIdGen) {
        this.nodeIdGen = (IdGenerator)Preconditions.checkNotNull((Object)nodeIdGen);
    }

    @Override
    protected void visitMsgNode(MsgNode msg) {
        List<String> baseSelectVarNames;
        List genderExprs = msg.getAndRemoveGenderExprs();
        if (genderExprs == null) {
            return;
        }
        if (msg.getChild(0) instanceof MsgSelectNode) {
            throw SoySyntaxExceptionUtils.createWithNode("Cannot mix 'genders' attribute with 'select' command in the same message. Please use one or the other only.", msg);
        }
        if (msg.getChild(0) instanceof MsgPluralNode && genderExprs.size() > 2) {
            throw SoySyntaxExceptionUtils.createWithNode("In a msg with 'plural', the 'genders' attribute can contain at most 2 expressions (otherwise, combinatorial explosion would cause a gigantic generated message).", msg);
        }
        genderExprs = Lists.reverse(genderExprs);
        try {
            baseSelectVarNames = MsgSubstUnitBaseVarNameUtils.genNoncollidingBaseNamesForExprs(genderExprs, FALLBACK_BASE_SELECT_VAR_NAME);
        }
        catch (SoySyntaxException sse) {
            throw SoySyntaxExceptionUtils.associateNode(sse, msg);
        }
        for (int i = 0; i < genderExprs.size(); ++i) {
            ExprRootNode genderExpr = (ExprRootNode)genderExprs.get(i);
            String baseSelectVarName = baseSelectVarNames.get(i);
            if (MsgSubstUnitBaseVarNameUtils.genNaiveBaseNameForExpr(genderExpr, FALLBACK_BASE_SELECT_VAR_NAME).equals(baseSelectVarName) && MsgSubstUnitBaseVarNameUtils.genShortestBaseNameForExpr(genderExpr, FALLBACK_BASE_SELECT_VAR_NAME).equals(baseSelectVarName)) {
                baseSelectVarName = null;
            }
            this.splitMsgForGender(msg, genderExpr, baseSelectVarName);
        }
    }

    private void splitMsgForGender(MsgNode msg, ExprRootNode<?> genderExpr, @Nullable String baseSelectVarName) {
        ImmutableList origChildren = ImmutableList.copyOf(msg.getChildren());
        msg.clearChildren();
        MsgSelectCaseNode femaleCase = new MsgSelectCaseNode(this.nodeIdGen.genId(), "'female'");
        femaleCase.addChildren(SoytreeUtils.cloneListWithNewIds(origChildren, this.nodeIdGen));
        MsgSelectCaseNode maleCase = new MsgSelectCaseNode(this.nodeIdGen.genId(), "'male'");
        maleCase.addChildren(SoytreeUtils.cloneListWithNewIds(origChildren, this.nodeIdGen));
        MsgSelectDefaultNode defaultCase = new MsgSelectDefaultNode(this.nodeIdGen.genId());
        defaultCase.addChildren(SoytreeUtils.cloneListWithNewIds(origChildren, this.nodeIdGen));
        MsgSelectNode selectNode = new MsgSelectNode(this.nodeIdGen.genId(), genderExpr, baseSelectVarName);
        selectNode.addChild(femaleCase);
        selectNode.addChild(maleCase);
        selectNode.addChild(defaultCase);
        msg.addChild(selectNode);
    }

    @Override
    protected void visitSoyNode(SoyNode node) {
        if (node instanceof SoyNode.ParentSoyNode) {
            this.visitChildrenAllowingConcurrentModification((SoyNode.ParentSoyNode)node);
        }
    }
}

