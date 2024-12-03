/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;

public class NamespaceNameClass
extends NameClass {
    public final String namespaceURI;
    private static final long serialVersionUID = 1L;

    public boolean accepts(String namespaceURI, String localName) {
        if ("*" == namespaceURI) {
            return true;
        }
        return this.namespaceURI.equals(namespaceURI);
    }

    public Object visit(NameClassVisitor visitor) {
        return visitor.onNsName(this);
    }

    public NamespaceNameClass(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public String toString() {
        return this.namespaceURI + ":*";
    }
}

