/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv_core.grammar.AnyNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.util.NameClassCollisionChecker;
import com.ctc.wstx.shaded.msv_core.grammar.util.NameClassComparator;
import com.ctc.wstx.shaded.msv_core.grammar.util.NameClassSimplifier;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.io.Serializable;

public abstract class NameClass
implements Serializable {
    public static final String NAMESPACE_WILDCARD = "*";
    public static final String LOCALNAME_WILDCARD = "*";
    public static final NameClass ALL = new AnyNameClass();
    public static final NameClass NONE = new NotNameClass(ALL);
    private static final long serialVersionUID = 1L;

    public abstract boolean accepts(String var1, String var2);

    public final boolean accepts(StringPair name) {
        return this.accepts(name.namespaceURI, name.localName);
    }

    public final boolean includes(NameClass rhs) {
        boolean r = new NameClassComparator(){

            protected void probe(String uri, String local) {
                if (!this.nc1.accepts(uri, local) && this.nc2.accepts(uri, local)) {
                    throw this.eureka;
                }
            }
        }.check(this, rhs);
        return !r;
    }

    public boolean isNull() {
        return !new NameClassCollisionChecker().check(this, ALL);
    }

    public final boolean isEqualTo(NameClass rhs) {
        boolean r = new NameClassComparator(){

            protected void probe(String uri, String local) {
                boolean a = this.nc1.accepts(uri, local);
                boolean b = this.nc2.accepts(uri, local);
                if (a && !b || !a && b) {
                    throw this.eureka;
                }
            }
        }.check(this, rhs);
        return !r;
    }

    public NameClass simplify() {
        return NameClassSimplifier.simplify(this);
    }

    public abstract Object visit(NameClassVisitor var1);

    public static NameClass intersection(NameClass lhs, NameClass rhs) {
        return NameClassSimplifier.simplify(new DifferenceNameClass(lhs, new NotNameClass(rhs)));
    }

    public static NameClass union(NameClass lhs, NameClass rhs) {
        return NameClassSimplifier.simplify(new ChoiceNameClass(lhs, rhs));
    }
}

