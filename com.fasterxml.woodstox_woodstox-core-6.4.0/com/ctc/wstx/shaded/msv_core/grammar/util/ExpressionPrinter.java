/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar.util;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.BinaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import java.util.Iterator;

public class ExpressionPrinter
implements ExpressionVisitor {
    public static final int FRAGMENT = 1;
    public static final int CONTENTMODEL = 2;
    public static final ExpressionPrinter fragmentInstance = new ExpressionPrinter(1);
    public static final ExpressionPrinter contentModelInstance = new ExpressionPrinter(2);
    public static final ExpressionPrinter smallestInstance = new ExpressionPrinter(3);
    protected final int mode;

    public static String printFragment(Expression exp) {
        return (String)exp.visit(fragmentInstance);
    }

    public static String printContentModel(Expression exp) {
        return (String)exp.visit(contentModelInstance);
    }

    public static String printSmallest(Expression exp) {
        return (String)exp.visit(smallestInstance);
    }

    protected ExpressionPrinter(int mode) {
        this.mode = mode;
    }

    public String printRefContainer(ReferenceContainer cont) {
        String r = "";
        Iterator itr = cont.iterator();
        while (itr.hasNext()) {
            ReferenceExp exp = (ReferenceExp)itr.next();
            r = r + exp.name + "  : " + exp.exp.visit(this) + "\n";
        }
        return r;
    }

    protected static boolean isComplex(Expression exp) {
        return exp instanceof BinaryExp;
    }

    protected String printBinary(BinaryExp exp, String op) {
        String r = exp.exp1.getClass() == exp.getClass() || !ExpressionPrinter.isComplex(exp.exp1) ? (String)exp.exp1.visit(this) : "(" + exp.exp1.visit(this) + ")";
        r = r + op;
        r = !ExpressionPrinter.isComplex(exp.exp2) ? r + exp.exp2.visit(this) : r + "(" + exp.exp2.visit(this) + ")";
        return r;
    }

    public Object onAttribute(AttributeExp exp) {
        return "@" + exp.nameClass.toString() + "<" + exp.exp.visit(this) + ">";
    }

    private Object optional(Expression exp) {
        if (exp instanceof OneOrMoreExp) {
            OneOrMoreExp ome = (OneOrMoreExp)exp;
            if (ExpressionPrinter.isComplex(ome.exp)) {
                return "(" + ome.exp.visit(this) + ")*";
            }
            return ome.exp.visit(this) + "*";
        }
        if (ExpressionPrinter.isComplex(exp)) {
            return "(" + exp.visit(this) + ")?";
        }
        return exp.visit(this) + "?";
    }

    public Object onChoice(ChoiceExp exp) {
        if (exp.exp1 == Expression.epsilon) {
            return this.optional(exp.exp2);
        }
        if (exp.exp2 == Expression.epsilon) {
            return this.optional(exp.exp1);
        }
        return this.printBinary(exp, "|");
    }

    public Object onConcur(ConcurExp exp) {
        return this.printBinary(exp, "&");
    }

    public Object onInterleave(InterleaveExp exp) {
        return this.printBinary(exp, "^");
    }

    public Object onElement(ElementExp exp) {
        if ((this.mode & 2) != 0) {
            return exp.getNameClass().toString();
        }
        return exp.getNameClass().toString() + "<" + exp.contentModel.visit(this) + ">";
    }

    public Object onOneOrMore(OneOrMoreExp exp) {
        if (ExpressionPrinter.isComplex(exp.exp)) {
            return "(" + exp.exp.visit(this) + ")+";
        }
        return exp.exp.visit(this) + "+";
    }

    public Object onMixed(MixedExp exp) {
        return "mixed[" + exp.exp.visit(this) + "]";
    }

    public Object onList(ListExp exp) {
        return "list[" + exp.exp.visit(this) + "]";
    }

    public Object onEpsilon() {
        return "#epsilon";
    }

    public Object onNullSet() {
        return "#nullSet";
    }

    public Object onAnyString() {
        return "<anyString>";
    }

    public Object onSequence(SequenceExp exp) {
        return this.printBinary(exp, ",");
    }

    public Object onData(DataExp exp) {
        return "$" + exp.name.localName;
    }

    public Object onValue(ValueExp exp) {
        return "$$" + exp.value;
    }

    public Object onOther(OtherExp exp) {
        return exp.printName() + "[" + exp.exp.visit(this) + "]";
    }

    public Object onRef(ReferenceExp exp) {
        if ((this.mode & 1) != 0) {
            return "{%" + exp.name + "}";
        }
        return "(" + exp.exp.visit(this) + ")";
    }
}

