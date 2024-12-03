/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.google.template.soy.soytree;

import com.google.common.collect.Lists;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.soytree.CaseOrDefaultNode;
import com.google.template.soy.soytree.ExprUnion;
import com.google.template.soy.soytree.SoyNode;
import java.util.List;

public class SwitchCaseNode
extends CaseOrDefaultNode
implements SoyNode.ConditionalBlockNode,
SoyNode.ExprHolderNode {
    private final String exprListText;
    private final List<ExprRootNode<?>> exprList;

    public SwitchCaseNode(int id, String commandText) throws SoySyntaxException {
        super(id, "case", commandText);
        this.exprListText = commandText;
        this.exprList = ExprParseUtils.parseExprListElseThrowSoySyntaxException(this.exprListText, "Invalid expression list in 'case' command text \"" + commandText + "\".");
    }

    protected SwitchCaseNode(SwitchCaseNode orig) {
        super(orig);
        this.exprListText = orig.exprListText;
        this.exprList = Lists.newArrayListWithCapacity((int)orig.exprList.size());
        for (ExprRootNode<?> origExpr : orig.exprList) {
            this.exprList.add((ExprRootNode<?>)origExpr.clone());
        }
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.SWITCH_CASE_NODE;
    }

    public String getExprListText() {
        return this.exprListText;
    }

    public List<ExprRootNode<?>> getExprList() {
        return this.exprList;
    }

    @Override
    public List<ExprUnion> getAllExprUnions() {
        return ExprUnion.createList(this.exprList);
    }

    @Override
    public SwitchCaseNode clone() {
        return new SwitchCaseNode(this);
    }
}

