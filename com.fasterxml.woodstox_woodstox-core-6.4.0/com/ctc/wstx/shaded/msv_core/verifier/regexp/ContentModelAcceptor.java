/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.util.StringRef;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ComplexAcceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ElementToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ExpressionAcceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.SimpleAcceptor;

public abstract class ContentModelAcceptor
extends ExpressionAcceptor {
    protected ContentModelAcceptor(REDocumentDeclaration docDecl, Expression exp, boolean ignoreUndeclaredAttributes) {
        super(docDecl, exp, ignoreUndeclaredAttributes);
    }

    public boolean stepForward(Acceptor child, StringRef errRef) {
        if (child instanceof SimpleAcceptor) {
            SimpleAcceptor sa = (SimpleAcceptor)child;
            if (sa.continuation != null) {
                return this.stepForwardByContinuation(sa.continuation, errRef);
            }
            return this.stepForward(new ElementToken(new ElementExp[]{sa.owner}), errRef);
        }
        if (child instanceof ComplexAcceptor) {
            ComplexAcceptor ca = (ComplexAcceptor)child;
            return this.stepForward(new ElementToken(errRef != null ? ca.owners : ca.getSatisfiedOwners()), errRef);
        }
        throw new Error();
    }

    protected Acceptor createAcceptor(Expression combined, Expression continuation, ElementExp[] primitives, int numPrimitives) {
        if (primitives == null || numPrimitives <= 1) {
            return new SimpleAcceptor(this.docDecl, combined, primitives == null ? null : primitives[0], continuation);
        }
        if (Debug.debug) {
            System.out.println("ComplexAcceptor is used");
        }
        ElementExp[] owners = new ElementExp[numPrimitives];
        System.arraycopy(primitives, 0, owners, 0, numPrimitives);
        return new ComplexAcceptor(this.docDecl, combined, owners);
    }

    public Object getOwnerType() {
        return null;
    }
}

