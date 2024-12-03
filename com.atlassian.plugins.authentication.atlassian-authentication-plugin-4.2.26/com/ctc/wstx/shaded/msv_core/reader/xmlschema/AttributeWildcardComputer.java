/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttWildcardExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeGroupExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class AttributeWildcardComputer
extends ExpressionWalker {
    private final XMLSchemaReader reader;
    private final Set visitedExps = new HashSet();
    private final Stack unprocessedElementExps = new Stack();
    private Set wildcards = null;

    public static void compute(XMLSchemaReader reader, Expression topLevel) {
        new AttributeWildcardComputer(reader).compute(topLevel);
    }

    private void compute(Expression topLevel) {
        topLevel.visit(this);
        while (!this.unprocessedElementExps.isEmpty()) {
            ((ElementExp)this.unprocessedElementExps.pop()).contentModel.visit(this);
        }
    }

    protected AttributeWildcardComputer(XMLSchemaReader _reader) {
        this.reader = _reader;
    }

    public void onElement(ElementExp exp) {
        if (!this.visitedExps.add(exp)) {
            return;
        }
        this.unprocessedElementExps.add(exp);
    }

    public void onRef(ReferenceExp exp) {
        AttributeWildcard w;
        if (this.visitedExps.add(exp)) {
            if (exp instanceof AttributeGroupExp) {
                AttributeGroupExp aexp = (AttributeGroupExp)exp;
                Set o = this.wildcards;
                this.wildcards = new HashSet();
                exp.exp.visit(this);
                aexp.wildcard = this.calcCompleteWildcard(aexp.wildcard, this.wildcards);
                this.wildcards = o;
            } else if (exp instanceof ComplexTypeExp) {
                ComplexTypeExp cexp = (ComplexTypeExp)exp;
                Set o = this.wildcards;
                this.wildcards = new HashSet();
                exp.exp.visit(this);
                cexp.wildcard = this.calcCompleteWildcard(cexp.wildcard, this.wildcards);
                if (cexp.complexBaseType != null) {
                    cexp.complexBaseType.visit(this);
                    if (cexp.derivationMethod == 2) {
                        cexp.wildcard = this.calcComplexTypeWildcard(cexp.wildcard, cexp.complexBaseType.wildcard);
                    }
                    this.propagateAttributes(cexp);
                }
                if (cexp.wildcard != null) {
                    cexp.attWildcard.exp = cexp.wildcard.createExpression(this.reader.grammar);
                }
                this.wildcards = o;
            } else {
                super.onRef(exp);
            }
        }
        if (this.wildcards != null && exp instanceof AttWildcardExp && (w = ((AttWildcardExp)((Object)exp)).getAttributeWildcard()) != null) {
            this.wildcards.add(w);
        }
    }

    private AttributeWildcard calcCompleteWildcard(AttributeWildcard local, Set s) {
        AttributeWildcard[] children = s.toArray(new AttributeWildcard[s.size()]);
        if (children.length == 0) {
            return local;
        }
        NameClass target = children[0].getName();
        for (int i = 1; i < children.length; ++i) {
            target = NameClass.intersection(target, children[i].getName());
        }
        if (local != null) {
            return new AttributeWildcard(NameClass.intersection(local.getName(), target), local.getProcessMode());
        }
        return new AttributeWildcard(target, children[0].getProcessMode());
    }

    private AttributeWildcard calcComplexTypeWildcard(AttributeWildcard complete, AttributeWildcard base) {
        if (base != null) {
            if (complete == null) {
                return base;
            }
            return new AttributeWildcard(NameClass.union(complete.getName(), base.getName()), complete.getProcessMode());
        }
        return complete;
    }

    private void propagateAttributes(final ComplexTypeExp cexp) {
        if (cexp.derivationMethod != 1 || cexp.complexBaseType == null) {
            return;
        }
        if (cexp.complexBaseType == this.reader.complexUrType) {
            return;
        }
        final HashSet explicitAtts = new HashSet();
        cexp.body.visit(new ExpressionWalker(){

            public void onElement(ElementExp exp) {
            }

            public void onAttribute(AttributeExp exp) {
                if (!(exp.nameClass instanceof SimpleNameClass)) {
                    throw new Error(exp.nameClass.toString());
                }
                explicitAtts.add(((SimpleNameClass)exp.nameClass).toStringPair());
            }
        });
        cexp.complexBaseType.body.visit(new ExpressionWalker(){
            private boolean isOptional = false;

            public void onChoice(ChoiceExp exp) {
                boolean b = this.isOptional;
                this.isOptional = true;
                super.onChoice(exp);
                this.isOptional = b;
            }

            public void onElement(ElementExp exp) {
            }

            public void onAttribute(AttributeExp exp) {
                if (!(exp.nameClass instanceof SimpleNameClass)) {
                    throw new Error();
                }
                SimpleNameClass snc = (SimpleNameClass)exp.nameClass;
                if (!explicitAtts.contains(snc.toStringPair())) {
                    cexp.body.exp = ((AttributeWildcardComputer)AttributeWildcardComputer.this).reader.pool.createSequence(cexp.body.exp, this.isOptional ? ((AttributeWildcardComputer)AttributeWildcardComputer.this).reader.pool.createOptional(exp) : exp);
                }
            }
        });
    }
}

