/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.relax.core.checker;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Locator;

public class DblAttrConstraintChecker
implements RELAXExpressionVisitorVoid {
    private final Map atts = new HashMap();
    private ReferenceExp current;

    public void check(TagClause clause, RELAXCoreReader reader) {
        this.atts.clear();
        this.current = clause;
        try {
            clause.visit(this);
        }
        catch (Eureka e) {
            reader.reportError(new Locator[]{reader.getDeclaredLocationOf(this.current), reader.getDeclaredLocationOf((ReferenceExp)this.atts.get(e.name))}, "RELAXReader.MultipleAttributeConstraint", new Object[]{e.name.localName});
        }
    }

    public void onAttribute(AttributeExp exp) {
        if (exp.nameClass instanceof SimpleNameClass) {
            SimpleNameClass nc = (SimpleNameClass)exp.nameClass;
            StringPair p = new StringPair(nc.namespaceURI, nc.localName);
            if (this.atts.containsKey(p)) {
                throw new Eureka(p);
            }
            this.atts.put(p, this.current);
        }
    }

    public void onAttPool(AttPoolClause exp) {
        ReferenceExp old = this.current;
        this.current = exp;
        exp.exp.visit(this);
        this.current = old;
    }

    public void onSequence(SequenceExp exp) {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
    }

    public void onChoice(ChoiceExp exp) {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
    }

    public void onEpsilon() {
    }

    public void onRef(ReferenceExp exp) {
    }

    public void onOther(OtherExp exp) {
        exp.exp.visit(this);
    }

    public void onElement(ElementExp exp) {
    }

    public void onOneOrMore(OneOrMoreExp exp) {
        exp.exp.visit(this);
    }

    public void onMixed(MixedExp exp) {
        exp.exp.visit(this);
    }

    public void onNullSet() {
    }

    public void onAnyString() {
    }

    public void onData(DataExp exp) {
    }

    public void onValue(ValueExp exp) {
    }

    public void onTag(TagClause exp) {
        exp.exp.visit(this);
    }

    public void onElementRules(ElementRules exp) {
        exp.exp.visit(this);
    }

    public void onHedgeRules(HedgeRules exp) {
        exp.exp.visit(this);
    }

    public void onConcur(ConcurExp exp) {
    }

    public void onInterleave(InterleaveExp exp) {
    }

    public void onList(ListExp exp) {
    }

    private static final class Eureka
    extends RuntimeException {
        final StringPair name;

        Eureka(StringPair an) {
            this.name = an;
        }
    }
}

