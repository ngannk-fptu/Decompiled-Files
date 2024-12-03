/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ContentModelAcceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;

public class SimpleAcceptor
extends ContentModelAcceptor {
    public final Expression continuation;
    public final ElementExp owner;

    public final Object getOwnerType() {
        return this.owner;
    }

    public SimpleAcceptor(REDocumentDeclaration docDecl, Expression combined, ElementExp owner, Expression continuation) {
        super(docDecl, combined, owner == null ? true : owner.ignoreUndeclaredAttributes);
        this.continuation = continuation;
        this.owner = owner;
    }

    public Acceptor createClone() {
        return new SimpleAcceptor(this.docDecl, this.getExpression(), this.owner, this.continuation);
    }
}

