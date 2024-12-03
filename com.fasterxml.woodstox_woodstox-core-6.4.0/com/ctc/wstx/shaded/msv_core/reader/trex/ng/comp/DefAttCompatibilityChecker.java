/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker;
import com.ctc.wstx.shaded.msv_core.grammar.util.RefExpRemover;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.CompatibilityChecker;
import com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.RELAXNGCompReader;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ResidualCalculator;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.xml.sax.Locator;

class DefAttCompatibilityChecker
extends CompatibilityChecker {
    private final Map defaultedAttributes;
    private final RefExpRemover refRemover;
    private ExpressionWalker contextDependentTypeChecker;
    public static final String CERR_DEFVALUE_NAME_IS_NOT_SIMPLE = "RELAXNGReader.Compatibility.DefaultValue.NameIsNotSimple";
    public static final String CERR_DEFVALUE_INVALID = "RELAXNGReader.Compatibility.DefaultValue.Invalid";
    public static final String CERR_DEFVALUE_NOT_OPTIONAL = "RELAXNGReader.Compatibility.DefaultValue.NotOptional";
    public static final String CERR_DEFVALUE_REPEATABLE = "RELAXNGReader.Compatibility.DefaultValue.Repeatable";
    public static final String CERR_DEFVALUE_COMPLEX_ELEMENTNAME = "RELAXNGReader.Compatibility.DefaultValue.ComplexElementName";
    public static final String CERR_DEFVALUE_DIFFERENT_VALUES = "RELAXNGReader.Compatibility.DefaultValue.DifferentValues";
    public static final String CERR_DEFVALUE_CONTEXT_DEPENDENT_TYPE = "RELAXNGReader.Compatibility.DefaultValue.ContextDependentType";
    public static final String CERR_DEFVALUE_COMPETING_ELEMENTS = "RELAXNGReader.Compatibility.DefaultValue.CompetingElements";

    DefAttCompatibilityChecker(RELAXNGCompReader _reader, Map _defaultedAttributes) {
        super(_reader);
        this.refRemover = new RefExpRemover(this.reader.pool, false);
        this.contextDependentTypeChecker = new ExpressionWalker(){

            public void onData(DataExp exp) {
                this.check(exp.dt, exp.name);
            }

            public void onValue(ValueExp exp) {
                this.check(exp.dt, exp.name);
            }

            private void check(Datatype dt, StringPair name) {
                if (dt.isContextDependent()) {
                    DefAttCompatibilityChecker.this.reportCompError(null, DefAttCompatibilityChecker.CERR_DEFVALUE_CONTEXT_DEPENDENT_TYPE, new Object[]{name.localName});
                    throw new Abort();
                }
            }
        };
        this.defaultedAttributes = _defaultedAttributes;
    }

    protected void setCompatibility(boolean val) {
        this.grammar.isDefaultAttributeValueCompatible = val;
    }

    private boolean isEpsilon(Expression exp) {
        if (exp == Expression.epsilon) {
            return true;
        }
        return exp.visit(this.refRemover) == Expression.epsilon;
    }

    public void test() {
        this.grammar.isDefaultAttributeValueCompatible = true;
        if (this.defaultedAttributes.size() == 0) {
            return;
        }
        Iterator itr = this.defaultedAttributes.entrySet().iterator();
        ResidualCalculator resCalc = new ResidualCalculator(this.reader.pool);
        while (itr.hasNext()) {
            Map.Entry item = itr.next();
            AttributeExp exp = (AttributeExp)item.getKey();
            String value = (String)item.getValue();
            if (!(exp.nameClass instanceof SimpleNameClass)) {
                this.reportCompError(new Locator[]{this.reader.getDeclaredLocationOf(exp)}, CERR_DEFVALUE_NAME_IS_NOT_SIMPLE);
            }
            try {
                exp.exp.visit(this.contextDependentTypeChecker);
            }
            catch (Abort a) {
                continue;
            }
            StringToken token = new StringToken(resCalc, value, null, null);
            if (resCalc.calcResidual(exp.exp, token).isEpsilonReducible()) continue;
            this.reportCompError(new Locator[]{this.reader.getDeclaredLocationOf(exp)}, CERR_DEFVALUE_INVALID, new Object[]{value});
        }
        if (!this.grammar.isDefaultAttributeValueCompatible) {
            return;
        }
        final HashMap name2value = new HashMap();
        final HashSet elements = new HashSet();
        this.grammar.visit(new ExpressionWalker(){
            private boolean inOneOrMore = false;
            private boolean inChoice = false;
            private boolean inOptionalChoice = false;
            private boolean inSimpleElement = false;
            private Map currentAttributes = null;
            private SimpleNameClass currentElementName = null;

            public void onElement(ElementExp exp) {
                if (!elements.add(exp)) {
                    return;
                }
                boolean oldSE = this.inSimpleElement;
                boolean oldOC = this.inOptionalChoice;
                boolean oldC = this.inChoice;
                boolean oldOOM = this.inOneOrMore;
                SimpleNameClass prevElemName = this.currentElementName;
                Map oldCA = this.currentAttributes;
                this.inSimpleElement = exp.getNameClass() instanceof SimpleNameClass;
                this.inOptionalChoice = true;
                this.inChoice = false;
                this.inOneOrMore = false;
                StringPair en = null;
                if (this.inSimpleElement) {
                    this.currentElementName = (SimpleNameClass)exp.getNameClass();
                    en = new StringPair(this.currentElementName);
                    this.currentAttributes = new HashMap();
                } else {
                    this.currentElementName = null;
                }
                exp.contentModel.visit(this);
                if (en != null) {
                    DefAttMap m = (DefAttMap)name2value.get(en);
                    if (m == null) {
                        name2value.put(en, new DefAttMap(exp, this.currentAttributes));
                    } else if (!((Object)m.defaultAttributes).equals(this.currentAttributes)) {
                        DefAttCompatibilityChecker.this.reportCompError(new Locator[]{DefAttCompatibilityChecker.this.reader.getDeclaredLocationOf(m.sampleDecl), DefAttCompatibilityChecker.this.reader.getDeclaredLocationOf(exp)}, DefAttCompatibilityChecker.CERR_DEFVALUE_COMPETING_ELEMENTS, new Object[]{((SimpleNameClass)m.sampleDecl.getNameClass()).localName});
                        name2value.remove(en);
                    }
                }
                this.inSimpleElement = oldSE;
                this.inOptionalChoice = oldOC;
                this.inChoice = oldC;
                this.inOneOrMore = oldOOM;
                this.currentElementName = prevElemName;
                this.currentAttributes = oldCA;
            }

            public void onOneOrMore(OneOrMoreExp exp) {
                boolean oldOOM = this.inOneOrMore;
                this.inOneOrMore = true;
                exp.exp.visit(this);
                this.inOneOrMore = oldOOM;
            }

            public void onChoice(ChoiceExp exp) {
                boolean oldOC = this.inOptionalChoice;
                boolean oldC = this.inChoice;
                this.inChoice = true;
                if (!DefAttCompatibilityChecker.this.isEpsilon(exp.exp1) && !DefAttCompatibilityChecker.this.isEpsilon(exp.exp2)) {
                    this.inOptionalChoice = false;
                }
                super.onChoice(exp);
                this.inOptionalChoice = oldOC;
                this.inChoice = oldC;
            }

            public void onAttribute(AttributeExp exp) {
                if (DefAttCompatibilityChecker.this.defaultedAttributes.containsKey(exp)) {
                    if (!this.inOptionalChoice || !this.inChoice) {
                        DefAttCompatibilityChecker.this.reportCompError(new Locator[]{DefAttCompatibilityChecker.this.reader.getDeclaredLocationOf(exp)}, DefAttCompatibilityChecker.CERR_DEFVALUE_NOT_OPTIONAL);
                        return;
                    }
                    if (this.inOneOrMore) {
                        DefAttCompatibilityChecker.this.reportCompError(new Locator[]{DefAttCompatibilityChecker.this.reader.getDeclaredLocationOf(exp)}, DefAttCompatibilityChecker.CERR_DEFVALUE_REPEATABLE);
                        return;
                    }
                    if (!this.inSimpleElement) {
                        DefAttCompatibilityChecker.this.reportCompError(new Locator[]{DefAttCompatibilityChecker.this.reader.getDeclaredLocationOf(exp)}, DefAttCompatibilityChecker.CERR_DEFVALUE_COMPLEX_ELEMENTNAME);
                        return;
                    }
                    String value = (String)DefAttCompatibilityChecker.this.defaultedAttributes.get(exp);
                    String v = this.currentAttributes.put(new StringPair((SimpleNameClass)exp.nameClass), value);
                    if (v != null && !v.equals(value)) {
                        DefAttCompatibilityChecker.this.reportCompError(new Locator[]{DefAttCompatibilityChecker.this.reader.getDeclaredLocationOf(exp)}, DefAttCompatibilityChecker.CERR_DEFVALUE_DIFFERENT_VALUES, new Object[]{v, value, this.currentElementName.localName, ((SimpleNameClass)exp.nameClass).localName});
                    }
                }
            }

            public void onList(ListExp exp) {
            }
        });
        for (ElementExp eexp : elements) {
            NameClass nc = eexp.getNameClass();
            if (nc instanceof SimpleNameClass) continue;
            for (Map.Entry e : name2value.entrySet()) {
                if (!nc.accepts((StringPair)e.getKey())) continue;
                DefAttMap defAtts = (DefAttMap)e.getValue();
                if (defAtts.defaultAttributes.size() <= 0) continue;
                this.reportCompError(new Locator[]{this.reader.getDeclaredLocationOf(defAtts.sampleDecl), this.reader.getDeclaredLocationOf(eexp)}, CERR_DEFVALUE_COMPETING_ELEMENTS, new Object[]{((SimpleNameClass)defAtts.sampleDecl.getNameClass()).localName});
                return;
            }
        }
    }

    private static final class DefAttMap {
        final Map defaultAttributes;
        final ElementExp sampleDecl;

        DefAttMap(ElementExp sample, Map atts) {
            this.sampleDecl = sample;
            this.defaultAttributes = atts;
        }
    }

    private static final class Abort
    extends RuntimeException {
        private Abort() {
        }
    }
}

