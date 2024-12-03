/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class ReferenceContainer
implements Serializable {
    protected final Map impl = new HashMap();
    private static final long serialVersionUID = 1L;

    public final ReferenceExp _getOrCreate(String name) {
        Object o = this.impl.get(name);
        if (o != null) {
            return (ReferenceExp)o;
        }
        ReferenceExp exp = this.createReference(name);
        this.impl.put(name, exp);
        return exp;
    }

    protected abstract ReferenceExp createReference(String var1);

    public void redefine(String name, ReferenceExp newExp) {
        if (this.impl.put(name, newExp) == null) {
            throw new IllegalArgumentException();
        }
    }

    public final ReferenceExp _get(String name) {
        Object o = this.impl.get(name);
        if (o != null) {
            return (ReferenceExp)o;
        }
        return null;
    }

    public final Iterator iterator() {
        return this.impl.values().iterator();
    }

    public final ReferenceExp[] getAll() {
        ReferenceExp[] r = new ReferenceExp[this.size()];
        this.impl.values().toArray(r);
        return r;
    }

    public final ReferenceExp remove(String name) {
        return (ReferenceExp)this.impl.remove(name);
    }

    public final int size() {
        return this.impl.size();
    }
}

