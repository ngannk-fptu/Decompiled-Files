/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.soytree;

import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.soytree.CaseOrDefaultNode;
import com.google.template.soy.soytree.SoyNode;

public class MsgPluralCaseNode
extends CaseOrDefaultNode
implements SoyNode.MsgBlockNode {
    private final int caseNumber;

    public MsgPluralCaseNode(int id, String commandText) throws SoySyntaxException {
        super(id, "case", commandText);
        try {
            this.caseNumber = Integer.parseInt(commandText);
            if (this.caseNumber < 0) {
                throw SoySyntaxException.createWithoutMetaInfo("Plural cases must be nonnegative integers.");
            }
        }
        catch (NumberFormatException nfe) {
            throw SoySyntaxException.createCausedWithoutMetaInfo("Invalid number in 'plural case' command text \"" + this.getCommandText() + "\".", nfe);
        }
    }

    protected MsgPluralCaseNode(MsgPluralCaseNode orig) {
        super(orig);
        this.caseNumber = orig.caseNumber;
    }

    @Override
    public SoyNode.Kind getKind() {
        return SoyNode.Kind.MSG_PLURAL_CASE_NODE;
    }

    public int getCaseNumber() {
        return this.caseNumber;
    }

    @Override
    public MsgPluralCaseNode clone() {
        return new MsgPluralCaseNode(this);
    }
}

