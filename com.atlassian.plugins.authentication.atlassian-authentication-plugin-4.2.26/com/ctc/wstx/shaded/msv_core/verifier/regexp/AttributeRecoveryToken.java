/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.AttributeToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;

final class AttributeRecoveryToken
extends AttributeToken {
    private Expression failedExp = Expression.nullSet;

    AttributeRecoveryToken(REDocumentDeclaration docDecl, String namespaceURI, String localName, String qName, StringToken value) {
        super(docDecl, namespaceURI, localName, qName, value);
    }

    public boolean match(AttributeExp exp) {
        if (!exp.nameClass.accepts(this.namespaceURI, this.localName)) {
            return false;
        }
        if (!this.docDecl.resCalc.calcResidual(exp.exp, this.value).isEpsilonReducible()) {
            this.failedExp = this.docDecl.pool.createChoice(this.failedExp, exp.exp);
        }
        return true;
    }

    Expression getFailedExp() {
        return this.failedExp;
    }
}

