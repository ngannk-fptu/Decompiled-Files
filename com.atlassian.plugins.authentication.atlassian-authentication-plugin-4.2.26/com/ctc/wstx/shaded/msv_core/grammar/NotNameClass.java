/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;

public final class NotNameClass
extends NameClass {
    public final NameClass child;
    private static final long serialVersionUID = 1L;

    public boolean accepts(String namespaceURI, String localName) {
        return !this.child.accepts(namespaceURI, localName);
    }

    public Object visit(NameClassVisitor visitor) {
        return visitor.onNot(this);
    }

    public NotNameClass(NameClass child) {
        this.child = child;
    }

    public String toString() {
        return "~" + this.child.toString();
    }
}

