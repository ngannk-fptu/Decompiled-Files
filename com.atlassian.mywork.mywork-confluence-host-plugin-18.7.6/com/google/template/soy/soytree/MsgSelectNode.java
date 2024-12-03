/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.AbstractParentCommandNode;
import com.google.template.soy.soytree.CaseOrDefaultNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.MsgSubstUnitBaseVarNameUtils;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;
import javax.annotation.Nullable;

public class MsgSelectNode
extends AbstractParentCommandNode<CaseOrDefaultNode>
implements SoyNode.MsgSubstUnitNode,
SoyNode.SplitLevelTopNode<CaseOrDefaultNode>,
SoyNode.ExprHolderNode {
    public static final String FALLBACK_BASE_SELECT_VAR_NAME = "STATUS";
    private final ExprRootNode<?> selectExpr;
    private final String baseSelectVarName;

    public MsgSelectNode(int id, String commandText) throws SoySyntaxException {
        super(id, "select", commandText);
        this.selectExpr = ExprParseUtils.parseExprElseThrowSoySyntaxException(commandText, "Invalid data reference in 'select' command text \"" + commandText + "\".");
        this.baseSelectVarName = MsgSubstUnitBaseVarNameUtils.genNaiveBaseNameForExpr(this.selectExpr, FALLBACK_BASE_SELECT_VAR_NAME);
    }

    public MsgSelectNode(int id, ExprRootNode<?> selectExpr, @Nullable String baseSelectVarName) {
        super(id, "select", selectExpr.toSourceString() + (baseSelectVarName != null ? " phname=\"" + baseSelectVarName + "\"" : ""));
        this.selectExpr = selectExpr;
        this.baseSelectVarName = baseSelectVarName != null ? baseSelectVarName : MsgSubstUnitBaseVarNameUtils.genNaiveBaseNameForExpr(selectExpr, FALLBACK_BASE_SELECT_VAR_NAME);
    }

    protected MsgSelectNode(MsgSelectNode orig) {
        super(orig);
        this.selectExpr = orig.selectExpr.clone();
        this.baseSelectVarName = orig.baseSelectVarName;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.MSG_SELECT_NODE;
    }

    public String getExprText() {
        return this.selectExpr.toSourceString();
    }

    public ExprRootNode<?> getExpr() {
        return this.selectExpr;
    }

    @Override
    public String getBaseVarName() {
        return this.baseSelectVarName;
    }

    @Override
    public boolean shouldUseSameVarNameAs(SoyNode.MsgSubstUnitNode other) {
        return other instanceof MsgSelectNode && this.getCommandText().equals(((MsgSelectNode)other).getCommandText());
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return ImmutableList.of((Object)new ExprUnion(this.selectExpr));
    }

    @Override
    public SoyNode.MsgBlockNode getParent() {
        return (SoyNode.MsgBlockNode)super.getParent();
    }

    @Override
    public MsgSelectNode clone() {
        return new MsgSelectNode(this);
    }
}

