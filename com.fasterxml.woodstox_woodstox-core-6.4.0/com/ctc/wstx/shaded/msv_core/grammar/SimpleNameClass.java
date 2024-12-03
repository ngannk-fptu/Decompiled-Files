/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

public final class SimpleNameClass
extends NameClass {
    public final String namespaceURI;
    public final String localName;
    private static final long serialVersionUID = 1L;

    public boolean accepts(String namespaceURI, String localName) {
        return !(!this.namespaceURI.equals(namespaceURI) && "*" != namespaceURI || !this.localName.equals(localName) && "*" != localName);
    }

    public Object visit(NameClassVisitor visitor) {
        return visitor.onSimple(this);
    }

    public SimpleNameClass(StringPair name) {
        this(name.namespaceURI, name.localName);
    }

    public SimpleNameClass(String namespaceURI, String localName) {
        this.namespaceURI = namespaceURI;
        this.localName = localName;
    }

    public StringPair toStringPair() {
        return new StringPair(this.namespaceURI, this.localName);
    }

    public String toString() {
        if (this.namespaceURI.length() == 0) {
            return this.localName;
        }
        return this.localName;
    }
}

