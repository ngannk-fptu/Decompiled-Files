/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;

public abstract class RedefinableExp
extends ReferenceExp {
    private static final long serialVersionUID = 1L;

    public RedefinableExp(String typeLocalName) {
        super(typeLocalName);
    }

    public abstract RedefinableExp getClone();

    public void redefine(RedefinableExp rhs) {
        if (this.getClass() != rhs.getClass() || !this.name.equals(rhs.name)) {
            throw new IllegalArgumentException();
        }
        this.exp = rhs.exp;
    }
}

