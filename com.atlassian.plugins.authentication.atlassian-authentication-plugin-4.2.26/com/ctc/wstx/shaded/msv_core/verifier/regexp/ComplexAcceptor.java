/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ComplexAcceptorBaseImpl;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;

public final class ComplexAcceptor
extends ComplexAcceptorBaseImpl {
    public final ElementExp[] owners;

    private static Expression[] createDefaultContentModels(ElementExp[] owners, ExpressionPool pool) {
        Expression[] r = new Expression[owners.length];
        for (int i = 0; i < owners.length; ++i) {
            r[i] = owners[i].contentModel.getExpandedExp(pool);
        }
        return r;
    }

    public ComplexAcceptor(REDocumentDeclaration docDecl, Expression combined, ElementExp[] primitives) {
        this(docDecl, combined, ComplexAcceptor.createDefaultContentModels(primitives, docDecl.pool), primitives);
    }

    public ComplexAcceptor(REDocumentDeclaration docDecl, Expression combined, Expression[] contentModels, ElementExp[] owners) {
        super(docDecl, combined, contentModels, owners[0].ignoreUndeclaredAttributes);
        this.owners = owners;
    }

    public Acceptor createClone() {
        Expression[] models = new Expression[this.contents.length];
        System.arraycopy(this.contents, 0, models, 0, this.contents.length);
        return new ComplexAcceptor(this.docDecl, this.getExpression(), models, this.owners);
    }

    public final ElementExp[] getSatisfiedOwners() {
        int i;
        int cnt = 0;
        for (i = 0; i < this.contents.length; ++i) {
            if (!this.contents[i].isEpsilonReducible()) continue;
            ++cnt;
        }
        if (cnt == 0) {
            return new ElementExp[0];
        }
        ElementExp[] satisfied = new ElementExp[cnt];
        cnt = 0;
        for (i = 0; i < this.contents.length; ++i) {
            if (!this.contents[i].isEpsilonReducible()) continue;
            satisfied[cnt++] = this.owners[i];
        }
        return satisfied;
    }
}

