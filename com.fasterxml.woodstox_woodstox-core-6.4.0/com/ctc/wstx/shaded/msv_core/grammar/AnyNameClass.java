/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;

public final class AnyNameClass
extends NameClass {
    public static final NameClass theInstance = new AnyNameClass();
    private static final long serialVersionUID = 1L;

    public boolean accepts(String namespaceURI, String localName) {
        return true;
    }

    public Object visit(NameClassVisitor visitor) {
        return visitor.onAnyName(this);
    }

    protected AnyNameClass() {
    }

    public String toString() {
        return "*:*";
    }

    private Object readResolve() {
        return theInstance;
    }
}

