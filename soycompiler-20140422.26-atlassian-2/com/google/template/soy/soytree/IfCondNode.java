/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 */
package com.google.template.soy.soytree;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.AbstractBlockCommandNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;

public class IfCondNode
extends AbstractBlockCommandNode
implements SoyNode.ConditionalBlockNode,
SoyNode.ExprHolderNode {
    private final ExprUnion exprUnion;

    public IfCondNode(int id, String commandName, String commandText) {
        this(id, commandName, IfCondNode.buildExprUnion(commandText));
    }

    private static ExprUnion buildExprUnion(String commandText) {
        ExprRootNode<?> expr = ExprParseUtils.parseExprElseNull(commandText);
        return expr != null ? new ExprUnion(expr) : new ExprUnion(commandText);
    }

    public IfCondNode(int id, String commandName, ExprUnion condition) {
        super(id, commandName, condition.getExprText());
        Preconditions.checkArgument((commandName.equals("if") || commandName.equals("elseif") ? 1 : 0) != 0);
        this.exprUnion = condition;
    }

    protected IfCondNode(IfCondNode orig) {
        super(orig);
        this.exprUnion = orig.exprUnion != null ? orig.exprUnion.clone() : null;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.IF_COND_NODE;
    }

    public String getExprText() {
        return this.exprUnion.getExprText();
    }

    public ExprUnion getExprUnion() {
        return this.exprUnion;
    }

    @Override
    public String getCommandName() {
        return this.getParent().getChild(0) == this ? "if" : "elseif";
    }

    @Override
    public String getCommandText() {
        return this.exprUnion.getExprText();
    }

    @Override
    public String toSourceString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getTagString());
        this.appendSourceStringForChildren(sb);
        return sb.toString();
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return ImmutableList.of((Object)this.exprUnion);
    }

    @Override
    public IfCondNode clone() {
        return new IfCondNode(this);
    }
}

