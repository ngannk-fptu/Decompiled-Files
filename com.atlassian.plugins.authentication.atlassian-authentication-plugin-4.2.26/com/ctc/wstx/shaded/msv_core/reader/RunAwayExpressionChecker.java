/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.BinaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.UnaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.Locator;

public class RunAwayExpressionChecker
implements ExpressionVisitorVoid {
    protected static final RuntimeException eureka = new RuntimeException();
    private final Set testedExps = new HashSet();
    private Set contentModel = new HashSet();
    private Stack refStack = new Stack();
    private Stack unprocessedElementExps = new Stack();
    private final GrammarReader reader;

    protected RunAwayExpressionChecker(GrammarReader reader) {
        this.reader = reader;
    }

    private void check(Expression exp) {
        block3: {
            try {
                exp.visit(this);
                while (!this.unprocessedElementExps.isEmpty()) {
                    this.contentModel.clear();
                    this.refStack.clear();
                    ElementExp e = (ElementExp)this.unprocessedElementExps.pop();
                    e.contentModel.visit(this);
                }
            }
            catch (RuntimeException e) {
                if (e == eureka) break block3;
                throw e;
            }
        }
    }

    public static void check(GrammarReader reader, Expression exp) {
        new RunAwayExpressionChecker(reader).check(exp);
    }

    public void onAttribute(AttributeExp exp) {
        this.enter(exp);
        exp.exp.visit(this);
        this.leave();
    }

    public void onConcur(ConcurExp exp) {
        this.binaryVisit(exp);
    }

    public void onInterleave(InterleaveExp exp) {
        this.binaryVisit(exp);
    }

    public void onSequence(SequenceExp exp) {
        this.binaryVisit(exp);
    }

    public void onChoice(ChoiceExp exp) {
        this.binaryVisit(exp);
    }

    public void onOneOrMore(OneOrMoreExp exp) {
        this.unaryVisit(exp);
    }

    public void onMixed(MixedExp exp) {
        this.unaryVisit(exp);
    }

    public void onList(ListExp exp) {
        this.unaryVisit(exp);
    }

    public void onEpsilon() {
    }

    public void onNullSet() {
    }

    public void onAnyString() {
    }

    public void onData(DataExp exp) {
    }

    public void onValue(ValueExp exp) {
    }

    protected final void binaryVisit(BinaryExp exp) {
        int cnt = 0;
        while (true) {
            this.enter(exp);
            ++cnt;
            exp.exp2.visit(this);
            if (!(exp.exp1 instanceof BinaryExp)) break;
            exp = (BinaryExp)exp.exp1;
        }
        exp.exp1.visit(this);
        while (cnt > 0) {
            this.leave();
            --cnt;
        }
    }

    protected final void unaryVisit(UnaryExp exp) {
        this.enter(exp);
        exp.exp.visit(this);
        this.leave();
    }

    private void enter(Expression exp) {
        if (this.contentModel.contains(exp)) {
            String s = "";
            int sz = this.refStack.size();
            Vector<Locator> locs = new Vector<Locator>();
            for (int i = this.refStack.indexOf(exp); i < sz; ++i) {
                if (!(this.refStack.elementAt(i) instanceof ReferenceExp)) continue;
                ReferenceExp e = (ReferenceExp)this.refStack.elementAt(i);
                if (e.name == null) continue;
                if (s.length() != 0) {
                    s = s + " > ";
                }
                s = s + e.name;
                Locator loc = this.reader.getDeclaredLocationOf(e);
                if (loc == null) continue;
                locs.add(loc);
            }
            this.reader.reportError(locs.toArray(new Locator[0]), "GrammarReader.Abstract.RunAwayExpression", new Object[]{s});
            throw eureka;
        }
        this.contentModel.add(exp);
        this.refStack.push(exp);
    }

    private void leave() {
        this.contentModel.remove(this.refStack.pop());
    }

    public void onRef(ReferenceExp exp) {
        this.enter(exp);
        if (!this.testedExps.contains(exp)) {
            this.testedExps.add(exp);
            exp.exp.visit(this);
        }
        this.leave();
    }

    public void onOther(OtherExp exp) {
        this.enter(exp);
        exp.exp.visit(this);
        this.leave();
    }

    public void onElement(ElementExp exp) {
        if (!this.testedExps.add(exp)) {
            return;
        }
        this.unprocessedElementExps.push(exp);
    }
}

