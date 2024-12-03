/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.dtd;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;

public final class LocalNameClass
extends NameClass {
    public final String localName;
    private static final long serialVersionUID = 1L;

    public boolean accepts(String namespaceURI, String localName) {
        return this.localName.equals(localName) || "*".equals(localName);
    }

    public Object visit(NameClassVisitor visitor) {
        return new SimpleNameClass("", this.localName).visit(visitor);
    }

    public LocalNameClass(String localName) {
        this.localName = localName;
    }

    public String toString() {
        return this.localName;
    }
}

