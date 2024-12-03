/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp;

public class GroupDeclExp
extends RedefinableExp {
    private static final long serialVersionUID = 1L;

    public GroupDeclExp(String typeLocalName) {
        super(typeLocalName);
    }

    public RedefinableExp getClone() {
        GroupDeclExp exp = new GroupDeclExp(this.name);
        exp.redefine(this);
        return exp;
    }
}

