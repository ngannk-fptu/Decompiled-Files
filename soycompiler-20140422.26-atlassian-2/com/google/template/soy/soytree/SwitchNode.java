/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.google.template.soy.soytree;

import com.google.common.collect.ImmutableList;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.AbstractParentCommandNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;

public class SwitchNode
extends AbstractParentCommandNode<SoyNode>
implements SoyNode.StandaloneNode,
SoyNode.SplitLevelTopNode<SoyNode>,
SoyNode.StatementNode,
SoyNode.ExprHolderNode {
    private final ExprRootNode<?> expr;

    public SwitchNode(int id, String commandText) throws SoySyntaxException {
        super(id, "switch", commandText);
        this.expr = ExprParseUtils.parseExprElseThrowSoySyntaxException(commandText, "Invalid expression in 'switch' command text \"" + commandText + "\".");
    }

    protected SwitchNode(SwitchNode orig) {
        super(orig);
        this.expr = orig.expr.clone();
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.SWITCH_NODE;
    }

    public String getExprText() {
        return this.expr.toSourceString();
    }

    public ExprRootNode<?> getExpr() {
        return this.expr;
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return ImmutableList.of((Object)new ExprUnion(this.expr));
    }

    @Override
    public String getCommandText() {
        return this.expr.toSourceString();
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public SwitchNode clone() {
        return new SwitchNode(this);
    }
}

