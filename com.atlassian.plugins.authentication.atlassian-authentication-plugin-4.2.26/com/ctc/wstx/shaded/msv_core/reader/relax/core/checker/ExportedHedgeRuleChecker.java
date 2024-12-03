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
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import java.util.Stack;

public final class ExportedHedgeRuleChecker
implements RELAXExpressionVisitorBoolean {
    private final RELAXModule module;
    private final Stack traversalStack = new Stack();
    public ReferenceExp[] errorSnapshot = null;

    public ExportedHedgeRuleChecker(RELAXModule module) {
        this.module = module;
    }

    public boolean onAttribute(AttributeExp exp) {
        return true;
    }

    public boolean onChoice(ChoiceExp exp) {
        return exp.exp1.visit(this) && exp.exp2.visit(this);
    }

    public boolean onSequence(SequenceExp exp) {
        return exp.exp1.visit(this) && exp.exp2.visit(this);
    }

    public boolean onElement(ElementExp exp) {
        return true;
    }

    public boolean onOneOrMore(OneOrMoreExp exp) {
        return exp.exp.visit(this);
    }

    public boolean onMixed(MixedExp exp) {
        return exp.exp.visit(this);
    }

    public boolean onRef(ReferenceExp exp) {
        throw new Error();
    }

    public boolean onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    public boolean onEpsilon() {
        return true;
    }

    public boolean onNullSet() {
        return true;
    }

    public boolean onAnyString() {
        return true;
    }

    public boolean onData(DataExp exp) {
        return true;
    }

    public boolean onValue(ValueExp exp) {
        return true;
    }

    public boolean onAttPool(AttPoolClause exp) {
        throw new Error();
    }

    public boolean onTag(TagClause exp) {
        throw new Error();
    }

    public boolean onInterleave(InterleaveExp exp) {
        throw new Error();
    }

    public boolean onConcur(ConcurExp exp) {
        throw new Error();
    }

    public boolean onList(ListExp exp) {
        throw new Error();
    }

    public boolean onElementRules(ElementRules exp) {
        if (exp.ownerModule == this.module) {
            return true;
        }
        this.takeSnapshot(exp);
        return false;
    }

    public boolean onHedgeRules(HedgeRules exp) {
        if (exp.ownerModule != this.module) {
            this.takeSnapshot(exp);
            return false;
        }
        this.traversalStack.push(exp);
        boolean r = exp.exp.visit(this);
        this.traversalStack.pop();
        return r;
    }

    private void takeSnapshot(ReferenceExp lastExp) {
        this.errorSnapshot = new ReferenceExp[this.traversalStack.size() + 1];
        this.traversalStack.toArray(this.errorSnapshot);
        this.errorSnapshot[this.errorSnapshot.length - 1] = lastExp;
    }
}

