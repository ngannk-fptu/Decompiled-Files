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
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.AbstractParentCommandNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.MsgSubstUnitBaseVarNameUtils;
import com.google.template.soy.soytree.PrintDirectiveNode;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;
import javax.annotation.Nullable;

public class PrintNode
extends AbstractParentCommandNode<PrintDirectiveNode>
implements SoyNode.StandaloneNode,
SoyNode.SplitLevelTopNode<PrintDirectiveNode>,
SoyNode.StatementNode,
SoyNode.ExprHolderNode,
SoyNode.MsgPlaceholderInitialNode {
    public static final String FALLBACK_BASE_PLACEHOLDER_NAME = "XXX";
    private final boolean isImplicit;
    private final ExprUnion exprUnion;
    @Nullable
    private final String userSuppliedPlaceholderName;

    public PrintNode(int id, boolean isImplicit, String exprText, @Nullable String userSuppliedPlaceholderName) throws SoySyntaxException {
        super(id, "print", "");
        this.isImplicit = isImplicit;
        ExprRootNode<?> expr = ExprParseUtils.parseExprElseNull(exprText);
        this.exprUnion = expr != null ? new ExprUnion(expr) : new ExprUnion(exprText);
        this.userSuppliedPlaceholderName = userSuppliedPlaceholderName;
    }

    public PrintNode(int id, boolean isImplicit, ExprUnion exprUnion, @Nullable String userSuppliedPlaceholderName) {
        super(id, "print", "");
        this.isImplicit = isImplicit;
        this.exprUnion = exprUnion;
        this.userSuppliedPlaceholderName = userSuppliedPlaceholderName;
    }

    protected PrintNode(PrintNode orig) {
        super(orig);
        this.isImplicit = orig.isImplicit;
        this.exprUnion = orig.exprUnion != null ? orig.exprUnion.clone() : null;
        this.userSuppliedPlaceholderName = orig.userSuppliedPlaceholderName;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.PRINT_NODE;
    }

    public boolean isImplicit() {
        return this.isImplicit;
    }

    public String getExprText() {
        return this.exprUnion.getExprText();
    }

    public ExprUnion getExprUnion() {
        return this.exprUnion;
    }

    @Override
    public String getUserSuppliedPhName() {
        return this.userSuppliedPlaceholderName;
    }

    @Override
    public String genBasePhName() {
        if (this.userSuppliedPlaceholderName != null) {
            return BaseUtils.convertToUpperUnderscore(this.userSuppliedPlaceholderName);
        }
        ExprRootNode<?> exprRoot = this.exprUnion.getExpr();
        if (exprRoot == null) {
            return FALLBACK_BASE_PLACEHOLDER_NAME;
        }
        return MsgSubstUnitBaseVarNameUtils.genNaiveBaseNameForExpr(exprRoot, FALLBACK_BASE_PLACEHOLDER_NAME);
    }

    @Override
    public Object genSamenessKey() {
        return this.getCommandText();
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return ImmutableList.of((Object)this.exprUnion);
    }

    @Override
    public String getCommandText() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.exprUnion.getExprText());
        for (PrintDirectiveNode child : this.getChildren()) {
            sb.append(' ').append(child.toSourceString());
        }
        if (this.userSuppliedPlaceholderName != null) {
            sb.append(" phname=\"").append(this.userSuppliedPlaceholderName).append('\"');
        }
        return sb.toString();
    }

    @Override
    public String getTagString() {
        return this.buildTagStringHelper(false, this.isImplicit);
    }

    @Override
    public String toSourceString() {
        return this.getTagString();
    }

    @Override
    public SoyNode.BlockNode getParent() {
        return (SoyNode.BlockNode)super.getParent();
    }

    @Override
    public PrintNode clone() {
        return new PrintNode(this);
    }
}

