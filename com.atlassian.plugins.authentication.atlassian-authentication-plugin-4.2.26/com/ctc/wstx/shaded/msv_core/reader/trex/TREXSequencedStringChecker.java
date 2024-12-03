/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TREXSequencedStringChecker
implements ExpressionVisitor {
    private final boolean rejectTextInInterleave;
    private static final Integer[] intPool = new Integer[]{new Integer(0), new Integer(1), new Integer(2), new Integer(3), new Integer(4), new Integer(5), new Integer(6), new Integer(7)};
    private static final int HAS_ELEMENT = 4;
    private static final int HAS_ANYSTRING = 2;
    private static final int HAS_DATA = 1;
    private final TREXBaseReader reader;
    private final Set checkedExps = new HashSet();
    private final Map checkedRefExps = new HashMap();

    public TREXSequencedStringChecker(TREXBaseReader reader, boolean _rejectTextInInterleave) {
        this.reader = reader;
        this.rejectTextInInterleave = _rejectTextInInterleave;
    }

    public Object onRef(ReferenceExp exp) {
        Object r = this.checkedRefExps.get(exp);
        if (r != null) {
            return r;
        }
        r = exp.exp.visit(this);
        this.checkedRefExps.put(exp, r);
        return r;
    }

    public Object onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    public Object onInterleave(InterleaveExp exp) {
        Object r;
        Object l = exp.exp1.visit(this);
        if (TREXSequencedStringChecker.isError(l, r = exp.exp2.visit(this))) {
            this.reader.reportError("TREXGrammarReader.InterleavedString");
            return intPool[0];
        }
        if (this.rejectTextInInterleave && (TREXSequencedStringChecker.toInt(l) & 2) != 0 && (TREXSequencedStringChecker.toInt(r) & 2) != 0) {
            this.reader.reportError("TREXGrammarReader.InterleavedAnyString");
            return intPool[0];
        }
        return TREXSequencedStringChecker.merge(l, r);
    }

    public Object onSequence(SequenceExp exp) {
        Object r;
        Object l = exp.exp1.visit(this);
        if (TREXSequencedStringChecker.isError(l, r = exp.exp2.visit(this))) {
            this.reader.reportError("TREXGrammarReader.SequencedString");
            return intPool[0];
        }
        return TREXSequencedStringChecker.merge(l, r);
    }

    public Object onEpsilon() {
        return intPool[0];
    }

    public Object onNullSet() {
        return intPool[0];
    }

    public Object onData(DataExp exp) {
        return intPool[1];
    }

    public Object onValue(ValueExp exp) {
        return intPool[1];
    }

    public Object onList(ListExp exp) {
        return intPool[1];
    }

    public Object onAnyString() {
        return intPool[2];
    }

    public Object onAttribute(AttributeExp exp) {
        if (this.checkedExps.add(exp)) {
            exp.exp.visit(this);
        }
        return intPool[0];
    }

    public Object onElement(ElementExp exp) {
        if (this.checkedExps.add(exp)) {
            exp.contentModel.visit(this);
        }
        return intPool[4];
    }

    private static final int toInt(Object o) {
        return (Integer)o;
    }

    private static Object merge(Object o1, Object o2) {
        return intPool[TREXSequencedStringChecker.toInt(o1) | TREXSequencedStringChecker.toInt(o2)];
    }

    private static boolean isError(Object o1, Object o2) {
        return (TREXSequencedStringChecker.toInt(o1) & 1) != 0 && TREXSequencedStringChecker.toInt(o2) != 0 || (TREXSequencedStringChecker.toInt(o2) & 1) != 0 && TREXSequencedStringChecker.toInt(o1) != 0;
    }

    public Object onChoice(ChoiceExp exp) {
        return TREXSequencedStringChecker.merge(exp.exp1.visit(this), exp.exp2.visit(this));
    }

    public Object onConcur(ConcurExp exp) {
        return TREXSequencedStringChecker.merge(exp.exp1.visit(this), exp.exp2.visit(this));
    }

    public Object onOneOrMore(OneOrMoreExp exp) {
        Object o = exp.exp.visit(this);
        if ((TREXSequencedStringChecker.toInt(o) & 1) != 0) {
            this.reader.reportError("TREXGrammarReader.RepeatedString");
            return intPool[0];
        }
        return o;
    }

    public Object onMixed(MixedExp exp) {
        Object o = exp.exp.visit(this);
        if (this.rejectTextInInterleave && (TREXSequencedStringChecker.toInt(o) & 2) != 0) {
            this.reader.reportError("TREXGrammarReader.InterleavedAnyString");
            return intPool[0];
        }
        return TREXSequencedStringChecker.merge(o, intPool[2]);
    }
}

