/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringRef;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.AnyElementToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.AttributeToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ComplexAcceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ContentModelAcceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ElementToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ResidualCalculator;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.SimpleAcceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.Token;

public abstract class ComplexAcceptorBaseImpl
extends ContentModelAcceptor {
    protected final Expression[] contents;

    public ComplexAcceptorBaseImpl(REDocumentDeclaration docDecl, Expression combined, Expression[] contents, boolean ignoreUndeclaredAttributes) {
        super(docDecl, combined, ignoreUndeclaredAttributes);
        this.contents = contents;
    }

    public final boolean onText2(String literal, IDContextProvider2 context, StringRef refErr, DatatypeRef refType) {
        if (!super.onText2(literal, context, refErr, refType)) {
            return false;
        }
        StringToken token = new StringToken(this.docDecl, literal, context);
        ResidualCalculator res = this.docDecl.resCalc;
        for (int i = 0; i < this.contents.length; ++i) {
            this.contents[i] = res.calcResidual(this.contents[i], token);
        }
        return true;
    }

    public final boolean stepForward(Acceptor child, StringRef errRef) {
        ElementExp cowner;
        if (!super.stepForward(child, errRef)) {
            return false;
        }
        ResidualCalculator res = this.docDecl.resCalc;
        Token token = child instanceof SimpleAcceptor ? ((cowner = ((SimpleAcceptor)child).owner) == null ? AnyElementToken.theInstance : new ElementToken(new ElementExp[]{cowner})) : (errRef != null ? new ElementToken(((ComplexAcceptor)child).owners) : new ElementToken(((ComplexAcceptor)child).getSatisfiedOwners()));
        for (int i = 0; i < this.contents.length; ++i) {
            this.contents[i] = res.calcResidual(this.contents[i], token);
        }
        return true;
    }

    protected boolean onAttribute(AttributeToken token, StringRef refErr) {
        if (!super.onAttribute(token, refErr)) {
            return false;
        }
        for (int i = 0; i < this.contents.length; ++i) {
            this.contents[i] = this.docDecl.attFeeder.feed(this.contents[i], token, this.ignoreUndeclaredAttributes);
        }
        return true;
    }

    public boolean onEndAttributes(StartTagInfo sti, StringRef refErr) {
        if (!super.onEndAttributes(sti, refErr)) {
            return false;
        }
        for (int i = 0; i < this.contents.length; ++i) {
            this.contents[i] = this.docDecl.attPruner.prune(this.contents[i]);
        }
        return true;
    }
}

