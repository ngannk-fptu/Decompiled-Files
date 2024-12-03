/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.relaxns.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.DeclImpl;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ComplexAcceptorBaseImpl;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;

public class RulesAcceptor
extends ComplexAcceptorBaseImpl {
    protected final DeclImpl[] owners;

    private static Expression createCombined(ExpressionPool pool, DeclImpl[] rules) {
        Expression exp = Expression.nullSet;
        for (int i = 0; i < rules.length; ++i) {
            exp = pool.createChoice(exp, rules[i].exp);
        }
        return exp;
    }

    private static Expression[] getContents(DeclImpl[] rules) {
        Expression[] r = new Expression[rules.length];
        for (int i = 0; i < rules.length; ++i) {
            r[i] = rules[i].exp;
        }
        return r;
    }

    public RulesAcceptor(REDocumentDeclaration docDecl, DeclImpl[] rules) {
        this(docDecl, RulesAcceptor.createCombined(docDecl.pool, rules), RulesAcceptor.getContents(rules), rules);
    }

    private RulesAcceptor(REDocumentDeclaration docDecl, Expression combined, Expression[] contentModels, DeclImpl[] owners) {
        super(docDecl, combined, contentModels, false);
        this.owners = owners;
    }

    public Acceptor createClone() {
        Expression[] models = new Expression[this.contents.length];
        System.arraycopy(this.contents, 0, models, 0, this.contents.length);
        return new RulesAcceptor(this.docDecl, this.getExpression(), models, this.owners);
    }

    ElementDecl[] getSatisfiedElementDecls() {
        int cnt = 0;
        for (int i = 0; i < this.owners.length; ++i) {
            if (!this.contents[i].isEpsilonReducible()) continue;
            ++cnt;
        }
        ElementDecl[] r = new DeclImpl[cnt];
        cnt = 0;
        for (int i = 0; i < this.owners.length; ++i) {
            if (!this.contents[i].isEpsilonReducible()) continue;
            r[cnt++] = this.owners[i];
        }
        return r;
    }
}

