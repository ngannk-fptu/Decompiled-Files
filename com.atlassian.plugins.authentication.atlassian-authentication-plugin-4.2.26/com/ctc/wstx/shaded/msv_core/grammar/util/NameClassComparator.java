/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.util;

import com.ctc.wstx.shaded.msv_core.grammar.AnyNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;

public abstract class NameClassComparator
implements NameClassVisitor {
    protected NameClass nc1;
    protected NameClass nc2;
    protected final RuntimeException eureka = new RuntimeException();
    private final String MAGIC = "\u0000";

    public boolean check(NameClass _new, NameClass _old) {
        try {
            this.nc1 = _new;
            this.nc2 = _old;
            _old.visit(this);
            _new.visit(this);
            return false;
        }
        catch (RuntimeException e) {
            if (e == this.eureka) {
                return true;
            }
            throw e;
        }
    }

    protected abstract void probe(String var1, String var2);

    public Object onAnyName(AnyNameClass nc) {
        this.probe("\u0000", "\u0000");
        return null;
    }

    public Object onNsName(NamespaceNameClass nc) {
        this.probe(nc.namespaceURI, "\u0000");
        return null;
    }

    public Object onSimple(SimpleNameClass nc) {
        this.probe(nc.namespaceURI, nc.localName);
        return null;
    }

    public Object onNot(NotNameClass nc) {
        nc.child.visit(this);
        return null;
    }

    public Object onDifference(DifferenceNameClass nc) {
        nc.nc1.visit(this);
        nc.nc2.visit(this);
        return null;
    }

    public Object onChoice(ChoiceNameClass nc) {
        nc.nc1.visit(this);
        nc.nc2.visit(this);
        return null;
    }
}

