/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.AnyNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassAndExpression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClassVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker;
import com.ctc.wstx.shaded.msv_core.grammar.util.NameClassCollisionChecker;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.RELAXNGReader;
import java.util.HashSet;
import java.util.Set;
import org.xml.sax.Locator;

public class RestrictionChecker {
    private final RELAXNGReader reader;
    private Expression errorContext;
    private final Set visitedExps = new HashSet();
    private DuplicateAttributesChecker attDupChecker;
    private DuplicateElementsChecker elemDupChecker;
    private final ExpressionWalker inExcept = new DefaultChecker(){

        public void onAttribute(AttributeExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_ATTRIBUTE_IN_EXCEPT);
        }

        public void onElement(ElementExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_ELEMENT_IN_EXCEPT);
        }

        public void onList(ListExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_LIST_IN_EXCEPT);
        }

        public void onAnyString() {
            RestrictionChecker.this.reportError(null, RestrictionChecker.ERR_TEXT_IN_EXCEPT);
        }

        public void onEpsilon() {
            RestrictionChecker.this.reportError(null, RestrictionChecker.ERR_EMPTY_IN_EXCEPT);
        }

        public void onSequence(SequenceExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_SEQUENCE_IN_EXCEPT);
        }

        public void onInterleave(InterleaveExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_INTERLEAVE_IN_EXCEPT);
        }

        public void onOneOrMore(OneOrMoreExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_ONEORMORE_IN_EXCEPT);
        }
    };
    private final ExpressionWalker inGroupInOneOrMoreInElement = new DefaultChecker(){

        public void onAttribute(AttributeExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_REPEATED_GROUPED_ATTRIBUTE);
        }
    };
    private final ExpressionWalker inOneOrMoreInElement = new DefaultChecker(){

        public void onSequence(SequenceExp exp) {
            exp.visit(RestrictionChecker.this.inGroupInOneOrMoreInElement);
        }

        public void onInterleave(InterleaveExp exp) {
            exp.visit(RestrictionChecker.this.inGroupInOneOrMoreInElement);
        }

        protected void checkAttributeInfiniteName(AttributeExp exp) {
        }
    };
    private final ExpressionWalker inElement = new DefaultChecker(){

        public void onOneOrMore(OneOrMoreExp exp) {
            exp.exp.visit(RestrictionChecker.this.inOneOrMoreInElement);
        }
    };
    private final ExpressionWalker inAttribute = new DefaultChecker(){

        public void onElement(ElementExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_ELEMENT_IN_ATTRIBUTE);
        }

        public void onAttribute(AttributeExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_ATTRIBUTE_IN_ATTRIBUTE);
        }
    };
    private final ExpressionWalker inInterleaveInList = new ListChecker(){

        public void onData(DataExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_DATA_IN_INTERLEAVE_IN_LIST);
        }

        public void onValue(ValueExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_VALUE_IN_INTERLEAVE_IN_LIST);
        }
    };
    private final ExpressionWalker inList = new ListChecker(){

        public void onInterleave(InterleaveExp exp) {
            RestrictionChecker.this.inInterleaveInList.onInterleave(exp);
        }
    };
    private final ExpressionWalker inStart = new DefaultChecker(){

        public void onAttribute(AttributeExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_ATTRIBUTE_IN_START);
        }

        public void onList(ListExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_LIST_IN_START);
        }

        public void onAnyString() {
            RestrictionChecker.this.reportError(null, RestrictionChecker.ERR_TEXT_IN_START);
        }

        public void onEpsilon() {
            RestrictionChecker.this.reportError(null, RestrictionChecker.ERR_EMPTY_IN_START);
        }

        public void onSequence(SequenceExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_SEQUENCE_IN_START);
        }

        public void onInterleave(InterleaveExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_INTERLEAVE_IN_START);
        }

        public void onData(DataExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_DATA_IN_START);
        }

        public void onValue(ValueExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_DATA_IN_START);
        }

        public void onOneOrMore(OneOrMoreExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_ONEORMORE_IN_START);
        }
    };
    private final NameClassWalker inNameClass = new NameClassWalker();
    private final NameClassVisitor inAnyNameClass = new NameClassWalker(){

        public Object onAnyName(AnyNameClass nc) {
            RestrictionChecker.this.reportError(null, RestrictionChecker.ERR_ANYNAME_IN_ANYNAME);
            return null;
        }
    };
    private final NameClassVisitor inNsNameClass = new NameClassWalker(){

        public Object onAnyName(AnyNameClass nc) {
            RestrictionChecker.this.reportError(null, RestrictionChecker.ERR_ANYNAME_IN_NSNAME);
            return null;
        }

        public Object onNsName(NamespaceNameClass nc) {
            RestrictionChecker.this.reportError(null, RestrictionChecker.ERR_NSNAME_IN_NSNAME);
            return null;
        }
    };
    private static final String ERR_ATTRIBUTE_IN_EXCEPT = "RELAXNGReader.AttributeInExcept";
    private static final String ERR_ELEMENT_IN_EXCEPT = "RELAXNGReader.ElementInExcept";
    private static final String ERR_LIST_IN_EXCEPT = "RELAXNGReader.ListInExcept";
    private static final String ERR_TEXT_IN_EXCEPT = "RELAXNGReader.TextInExcept";
    private static final String ERR_EMPTY_IN_EXCEPT = "RELAXNGReader.EmptyInExcept";
    private static final String ERR_SEQUENCE_IN_EXCEPT = "RELAXNGReader.SequenceInExcept";
    private static final String ERR_INTERLEAVE_IN_EXCEPT = "RELAXNGReader.InterleaveInExcept";
    private static final String ERR_ONEORMORE_IN_EXCEPT = "RELAXNGReader.OneOrMoreInExcept";
    private static final String ERR_REPEATED_GROUPED_ATTRIBUTE = "RELAXNGReader.RepeatedGroupedAttribute";
    private static final String ERR_ELEMENT_IN_ATTRIBUTE = "RELAXNGReader.ElementInAttribute";
    private static final String ERR_ATTRIBUTE_IN_ATTRIBUTE = "RELAXNGReader.AttributeInAttribute";
    private static final String ERR_ATTRIBUTE_IN_LIST = "RELAXNGReader.AttributeInList";
    private static final String ERR_ELEMENT_IN_LIST = "RELAXNGReader.ElementInList";
    private static final String ERR_LIST_IN_LIST = "RELAXNGReader.ListInList";
    private static final String ERR_TEXT_IN_LIST = "RELAXNGReader.TextInList";
    private static final String ERR_ATTRIBUTE_IN_START = "RELAXNGReader.AttributeInStart";
    private static final String ERR_LIST_IN_START = "RELAXNGReader.ListInStart";
    private static final String ERR_TEXT_IN_START = "RELAXNGReader.TextInStart";
    private static final String ERR_EMPTY_IN_START = "RELAXNGReader.EmptyInStart";
    private static final String ERR_SEQUENCE_IN_START = "RELAXNGReader.SequenceInStart";
    private static final String ERR_INTERLEAVE_IN_START = "RELAXNGReader.InterleaveInStart";
    private static final String ERR_DATA_IN_START = "RELAXNGReader.DataInStart";
    private static final String ERR_ONEORMORE_IN_START = "RELAXNGReader.OneOrMoreInStart";
    private static final String ERR_DATA_IN_INTERLEAVE_IN_LIST = "RELAXNGReader.DataInInterleaveInList";
    private static final String ERR_VALUE_IN_INTERLEAVE_IN_LIST = "RELAXNGReader.ValueInInterleaveInList";
    private static final String ERR_ANYNAME_IN_ANYNAME = "RELAXNGReader.AnyNameInAnyName";
    private static final String ERR_ANYNAME_IN_NSNAME = "RELAXNGReader.AnyNameInNsName";
    private static final String ERR_NSNAME_IN_NSNAME = "RELAXNGReader.NsNameInNsName";
    private static final String ERR_DUPLICATE_ATTRIBUTES = "RELAXNGReader.DuplicateAttributes";
    private static final String ERR_DUPLICATE_ELEMENTS = "RELAXNGReader.DuplicateElements";

    public RestrictionChecker(RELAXNGReader _reader) {
        this.reader = _reader;
    }

    public void check() {
        this.reader.getGrammar().visit(this.inStart);
    }

    private void reportError(Expression exp, String errorMsg) {
        this.reportError(exp, errorMsg, null);
    }

    private void reportError(Expression exp, String errorMsg, Object[] args) {
        this.reader.reportError(new Locator[]{this.reader.getDeclaredLocationOf(exp), this.reader.getDeclaredLocationOf(this.errorContext)}, errorMsg, args);
    }

    public void checkNameClass(NameClass nc) {
        nc.visit(this.inNameClass);
    }

    private class DuplicateAttributesChecker
    extends DuplicateNameChecker {
        private DuplicateAttributesChecker() {
        }

        protected void check(NameClassAndExpression exp) {
            int j = 0;
            for (int i = 0; i < this.areaLen; i += 2) {
                while (j < this.areas[i]) {
                    this.check(exp, this.exps[j++]);
                }
                j = this.areas[i + 1];
            }
            while (j < this.expsLen) {
                this.check(exp, this.exps[j++]);
            }
        }

        protected String getErrorMessage() {
            return RestrictionChecker.ERR_DUPLICATE_ATTRIBUTES;
        }
    }

    private class DuplicateElementsChecker
    extends DuplicateNameChecker {
        private DuplicateElementsChecker() {
        }

        protected void check(NameClassAndExpression exp) {
            for (int i = 0; i < this.areaLen; i += 2) {
                for (int j = this.areas[i]; j < this.areas[i + 1]; ++j) {
                    this.check(exp, this.exps[j]);
                }
            }
        }

        protected String getErrorMessage() {
            return RestrictionChecker.ERR_DUPLICATE_ELEMENTS;
        }
    }

    protected abstract class DuplicateNameChecker {
        protected NameClassAndExpression[] exps = new NameClassAndExpression[16];
        protected int expsLen = 0;
        protected int[] areas = new int[8];
        protected int areaLen = 0;
        private final NameClassCollisionChecker checker = new NameClassCollisionChecker();

        protected DuplicateNameChecker() {
        }

        public void add(NameClassAndExpression exp) {
            this.check(exp);
            if (this.exps.length == this.expsLen) {
                NameClassAndExpression[] n = new NameClassAndExpression[this.expsLen * 2];
                System.arraycopy(this.exps, 0, n, 0, this.expsLen);
                this.exps = n;
            }
            this.exps[this.expsLen++] = exp;
        }

        protected abstract void check(NameClassAndExpression var1);

        public int start() {
            return this.expsLen;
        }

        public void endLeftBranch(int start) {
            if (this.areas.length == this.areaLen) {
                int[] n = new int[this.areaLen * 2];
                System.arraycopy(this.areas, 0, n, 0, this.areaLen);
                this.areas = n;
            }
            this.areas[this.areaLen++] = start;
            this.areas[this.areaLen++] = this.expsLen;
        }

        public void endRightBranch() {
            this.areaLen -= 2;
        }

        protected void check(NameClassAndExpression newExp, NameClassAndExpression oldExp) {
            if (this.checker.check(newExp.getNameClass(), oldExp.getNameClass())) {
                NameClass intersection = NameClass.intersection(newExp.getNameClass(), oldExp.getNameClass());
                RestrictionChecker.this.reader.reportError(new Locator[]{RestrictionChecker.this.reader.getDeclaredLocationOf(RestrictionChecker.this.errorContext), RestrictionChecker.this.reader.getDeclaredLocationOf(newExp), RestrictionChecker.this.reader.getDeclaredLocationOf(oldExp)}, this.getErrorMessage(), new Object[]{intersection.toString()});
            }
        }

        protected abstract String getErrorMessage();
    }

    class NameClassWalker
    implements NameClassVisitor {
        NameClassWalker() {
        }

        public Object onAnyName(AnyNameClass nc) {
            return null;
        }

        public Object onSimple(SimpleNameClass nc) {
            return null;
        }

        public Object onNsName(NamespaceNameClass nc) {
            return null;
        }

        public Object onNot(NotNameClass nc) {
            throw new Error();
        }

        public Object onDifference(DifferenceNameClass nc) {
            nc.nc1.visit(this);
            if (nc.nc1 instanceof AnyNameClass) {
                nc.nc2.visit(RestrictionChecker.this.inAnyNameClass);
            } else if (nc.nc1 instanceof NamespaceNameClass) {
                nc.nc2.visit(RestrictionChecker.this.inNsNameClass);
            } else {
                throw new Error();
            }
            return null;
        }

        public Object onChoice(ChoiceNameClass nc) {
            nc.nc1.visit(this);
            nc.nc2.visit(this);
            return null;
        }
    }

    private class ListChecker
    extends DefaultChecker {
        private ListChecker() {
        }

        public void onAttribute(AttributeExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_ATTRIBUTE_IN_LIST);
        }

        public void onElement(ElementExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_ELEMENT_IN_LIST);
        }

        public void onList(ListExp exp) {
            RestrictionChecker.this.reportError(exp, RestrictionChecker.ERR_LIST_IN_LIST);
        }

        public void onAnyString() {
            RestrictionChecker.this.reportError(null, RestrictionChecker.ERR_TEXT_IN_LIST);
        }
    }

    private class DefaultChecker
    extends ExpressionWalker {
        private DefaultChecker() {
        }

        public void onElement(ElementExp exp) {
            if (!RestrictionChecker.this.visitedExps.add(exp)) {
                return;
            }
            if (RestrictionChecker.this.elemDupChecker != null) {
                RestrictionChecker.this.elemDupChecker.add(exp);
            }
            Expression oldContext = RestrictionChecker.this.errorContext;
            DuplicateAttributesChecker oldADC = RestrictionChecker.this.attDupChecker;
            DuplicateElementsChecker oldEDC = RestrictionChecker.this.elemDupChecker;
            RestrictionChecker.this.errorContext = exp;
            RestrictionChecker.this.attDupChecker = new DuplicateAttributesChecker();
            RestrictionChecker.this.elemDupChecker = new DuplicateElementsChecker();
            exp.contentModel.getExpandedExp(((RestrictionChecker)RestrictionChecker.this).reader.pool).visit(RestrictionChecker.this.inElement);
            RestrictionChecker.this.errorContext = oldContext;
            RestrictionChecker.this.attDupChecker = oldADC;
            RestrictionChecker.this.elemDupChecker = oldEDC;
        }

        public void onAttribute(AttributeExp exp) {
            if (!RestrictionChecker.this.visitedExps.add(exp)) {
                return;
            }
            RestrictionChecker.this.attDupChecker.add(exp);
            this.checkAttributeInfiniteName(exp);
            Expression oldContext = RestrictionChecker.this.errorContext;
            RestrictionChecker.this.errorContext = exp;
            exp.exp.getExpandedExp(((RestrictionChecker)RestrictionChecker.this).reader.pool).visit(RestrictionChecker.this.inAttribute);
            RestrictionChecker.this.errorContext = oldContext;
        }

        protected void checkAttributeInfiniteName(final AttributeExp exp) {
            exp.nameClass.visit(new NameClassVisitor(){

                public Object onAnyName(AnyNameClass nc) {
                    return this.error();
                }

                public Object onSimple(SimpleNameClass nc) {
                    return null;
                }

                public Object onNsName(NamespaceNameClass nc) {
                    return this.error();
                }

                public Object onNot(NotNameClass nc) {
                    throw new Error();
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

                private Object error() {
                    RestrictionChecker.this.reportError(exp, "RELAXNGReader.NakedInfiniteAttributeNameClass");
                    return null;
                }
            });
        }

        public void onList(ListExp exp) {
            exp.exp.visit(RestrictionChecker.this.inList);
        }

        public void onData(DataExp exp) {
            exp.except.visit(RestrictionChecker.this.inExcept);
        }

        public void onChoice(ChoiceExp exp) {
            if (RestrictionChecker.this.attDupChecker == null) {
                super.onChoice(exp);
            } else {
                int idx = RestrictionChecker.this.attDupChecker.start();
                exp.exp1.visit(this);
                RestrictionChecker.this.attDupChecker.endLeftBranch(idx);
                exp.exp2.visit(this);
                RestrictionChecker.this.attDupChecker.endRightBranch();
            }
        }

        public void onInterleave(InterleaveExp exp) {
            if (RestrictionChecker.this.elemDupChecker == null) {
                super.onInterleave(exp);
            } else {
                int idx = RestrictionChecker.this.elemDupChecker.start();
                exp.exp1.visit(this);
                RestrictionChecker.this.elemDupChecker.endLeftBranch(idx);
                exp.exp2.visit(this);
                RestrictionChecker.this.elemDupChecker.endRightBranch();
            }
        }

        public void onAnyString() {
            super.onAnyString();
        }
    }
}

