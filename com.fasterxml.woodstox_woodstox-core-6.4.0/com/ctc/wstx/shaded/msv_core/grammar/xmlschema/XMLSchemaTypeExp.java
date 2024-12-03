/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp;

public abstract class XMLSchemaTypeExp
extends RedefinableExp {
    public static final int RESTRICTION = 1;
    public static final int EXTENSION = 2;
    private static final long serialVersionUID = 1L;

    XMLSchemaTypeExp(String typeLocalName) {
        super(typeLocalName);
    }

    public abstract int getBlock();
}

