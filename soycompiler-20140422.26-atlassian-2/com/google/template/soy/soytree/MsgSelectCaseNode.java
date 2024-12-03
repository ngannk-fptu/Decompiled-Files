/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.exprparse.ExprParseUtils;
import com.google.template.soy.exprtree.ExprRootNode;
import com.google.template.soy.exprtree.StringNode;
import com.google.template.soy.soytree.CaseOrDefaultNode;
import com.google.template.soy.soytree.SoyNode;

public class MsgSelectCaseNode
extends CaseOrDefaultNode
implements SoyNode.MsgBlockNode {
    private final String caseValue;

    public MsgSelectCaseNode(int id, String commandText) throws SoySyntaxException {
        super(id, "case", commandText);
        ExprRootNode<?> strLit = ExprParseUtils.parseExprElseThrowSoySyntaxException(commandText, "Invalid expression in 'case' command text \"" + commandText + "\".");
        if (strLit.numChildren() != 1 || !(strLit.getChild(0) instanceof StringNode)) {
            throw SoySyntaxException.createWithoutMetaInfo("Invalid string for select 'case'.");
        }
        this.caseValue = ((StringNode)strLit.getChild(0)).getValue();
    }

    protected MsgSelectCaseNode(MsgSelectCaseNode orig) {
        super(orig);
        this.caseValue = orig.caseValue;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.MSG_SELECT_CASE_NODE;
    }

    public String getCaseValue() {
        return this.caseValue;
    }

    @Override
    public MsgSelectCaseNode clone() {
        return new MsgSelectCaseNode(this);
    }
}

