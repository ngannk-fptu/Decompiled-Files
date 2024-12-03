/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.HashSet;
import java.util.Set;

public class LaxDefaultNameClass
extends NameClass {
    private NameClass base;
    protected NameClass equivalentNameClass;
    private final Set names = new HashSet();
    private static final long serialVersionUID = 1L;

    public LaxDefaultNameClass(NameClass _base) {
        this.base = _base;
        this.names.add(new StringPair("*", "*"));
    }

    public Object visit(NameClassVisitor visitor) {
        if (this.equivalentNameClass == null) {
            NameClass nc = this.base;
            StringPair[] items = this.names.toArray(new StringPair[0]);
            for (int i = 0; i < items.length; ++i) {
                if (items[i].namespaceURI == "*" || items[i].localName == "*") continue;
                nc = new DifferenceNameClass(nc, new SimpleNameClass(items[i]));
            }
            this.equivalentNameClass = nc;
        }
        return this.equivalentNameClass.visit(visitor);
    }

    public boolean accepts(String namespaceURI, String localName) {
        return this.base.accepts(namespaceURI, localName) && !this.names.contains(new StringPair(namespaceURI, localName));
    }

    public void addName(String namespaceURI, String localName) {
        this.names.add(new StringPair(namespaceURI, localName));
        this.names.add(new StringPair(namespaceURI, "*"));
        this.names.add(new StringPair("*", localName));
    }
}

