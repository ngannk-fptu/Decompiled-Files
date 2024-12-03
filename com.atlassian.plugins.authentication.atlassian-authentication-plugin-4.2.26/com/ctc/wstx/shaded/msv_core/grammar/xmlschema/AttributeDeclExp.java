/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;

public class AttributeDeclExp
extends ReferenceExp {
    public AttributeExp self;
    private static final long serialVersionUID = 1L;

    public AttributeDeclExp(String typeLocalName) {
        super(typeLocalName);
    }

    public void set(AttributeExp exp) {
        this.self = exp;
        this.exp = this.self;
    }
}

