/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.writer.relaxng;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.EnumerationFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FinalComponent;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FractionDigitsFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.LengthFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ListType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.MaxLengthFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.MinLengthFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.PatternFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.RangeFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TotalDigitsFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnionType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;
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
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.EmptyStringType;
import com.ctc.wstx.shaded.msv_core.grammar.relax.NoneType;
import com.ctc.wstx.shaded.msv_core.writer.XMLWriter;
import com.ctc.wstx.shaded.msv_core.writer.relaxng.Context;
import java.util.HashSet;
import java.util.Vector;

public abstract class PatternWriter
implements ExpressionVisitorVoid {
    protected final XMLWriter writer;
    protected final Context context;

    public PatternWriter(Context ctxt) {
        this.writer = ctxt.getWriter();
        this.context = ctxt;
    }

    public abstract void onOther(OtherExp var1);

    public abstract void onRef(ReferenceExp var1);

    public void onElement(ElementExp exp) {
        this.writer.start("element");
        this.context.writeNameClass(exp.getNameClass());
        this.visitUnary(exp.contentModel);
        this.writer.end("element");
    }

    public void onEpsilon() {
        this.writer.element("empty");
    }

    public void onNullSet() {
        this.writer.element("notAllowed");
    }

    public void onAnyString() {
        this.writer.element("text");
    }

    public void onInterleave(InterleaveExp exp) {
        this.visitBinExp("interleave", exp, InterleaveExp.class);
    }

    public void onConcur(ConcurExp exp) {
        throw new IllegalArgumentException("the grammar includes concur, which is not supported");
    }

    public void onList(ListExp exp) {
        this.writer.start("list");
        this.visitUnary(exp.exp);
        this.writer.end("list");
    }

    protected void onOptional(Expression exp) {
        if (exp instanceof OneOrMoreExp) {
            this.onZeroOrMore((OneOrMoreExp)exp);
            return;
        }
        this.writer.start("optional");
        this.visitUnary(exp);
        this.writer.end("optional");
    }

    public void onChoice(ChoiceExp exp) {
        if (exp.exp1 == Expression.epsilon) {
            this.onOptional(exp.exp2);
            return;
        }
        if (exp.exp2 == Expression.epsilon) {
            this.onOptional(exp.exp1);
            return;
        }
        this.visitBinExp("choice", exp, ChoiceExp.class);
    }

    public void onSequence(SequenceExp exp) {
        this.visitBinExp("group", exp, SequenceExp.class);
    }

    public void visitBinExp(String elementName, BinaryExp exp, Class type) {
        this.writer.start(elementName);
        Expression[] children = exp.getChildren();
        for (int i = 0; i < children.length; ++i) {
            children[i].visit(this);
        }
        this.writer.end(elementName);
    }

    public void onMixed(MixedExp exp) {
        this.writer.start("mixed");
        this.visitUnary(exp.exp);
        this.writer.end("mixed");
    }

    public void onOneOrMore(OneOrMoreExp exp) {
        this.writer.start("oneOrMore");
        this.visitUnary(exp.exp);
        this.writer.end("oneOrMore");
    }

    protected void onZeroOrMore(OneOrMoreExp exp) {
        this.writer.start("zeroOrMore");
        this.visitUnary(exp.exp);
        this.writer.end("zeroOrMore");
    }

    public void onAttribute(AttributeExp exp) {
        this.writer.start("attribute");
        this.context.writeNameClass(exp.nameClass);
        this.visitUnary(exp.exp);
        this.writer.end("attribute");
    }

    public void visitUnary(Expression exp) {
        if (exp instanceof SequenceExp) {
            SequenceExp seq = (SequenceExp)exp;
            this.visitUnary(seq.exp1);
            seq.exp2.visit(this);
        } else {
            exp.visit(this);
        }
    }

    public void onValue(ValueExp exp) {
        if (exp.dt instanceof XSDatatypeImpl) {
            XSDatatypeImpl base = (XSDatatypeImpl)exp.dt;
            final Vector<String> ns = new Vector<String>();
            String lex = base.convertToLexicalValue(exp.value, new SerializationContext(){

                public String getNamespacePrefix(String namespaceURI) {
                    int cnt = ns.size() / 2;
                    ns.add("xmlns:ns" + cnt);
                    ns.add(namespaceURI);
                    return "ns" + cnt;
                }
            });
            if (base != TokenType.theInstance) {
                ns.add("type");
                ns.add(base.getName());
            }
            this.writer.start("value", ns.toArray(new String[0]));
            this.writer.characters(lex);
            this.writer.end("value");
            return;
        }
        throw new UnsupportedOperationException(exp.dt.getClass().getName());
    }

    public void onData(DataExp exp) {
        Datatype dt = exp.dt;
        if (dt instanceof XSDatatypeImpl) {
            XSDatatypeImpl dti = (XSDatatypeImpl)dt;
            if (this.isPredefinedType(dt)) {
                this.writer.element("data", new String[]{"type", dti.getName()});
            } else {
                this.serializeDataType(dti);
            }
            return;
        }
        this.writer.element("data-unknown", new String[]{"class", dt.getClass().getName()});
    }

    protected void serializeDataType(XSDatatype dt) {
        if (dt instanceof UnionType) {
            this.serializeUnionType((UnionType)dt);
            return;
        }
        HashSet<String> appliedFacets = new HashSet<String>();
        Vector<XSDatatype> effectiveFacets = new Vector<XSDatatype>();
        XSDatatype x = dt;
        while (x instanceof DataTypeWithFacet || x instanceof FinalComponent) {
            if (x instanceof FinalComponent) {
                x = x.getBaseType();
                continue;
            }
            String facetName = ((DataTypeWithFacet)x).facetName;
            if (facetName.equals("enumeration")) {
                this.serializeEnumeration((XSDatatypeImpl)dt, (EnumerationFacet)x);
                return;
            }
            if (facetName.equals("whiteSpace")) {
                System.err.println("warning: unsupported whiteSpace facet is ignored");
                x = x.getBaseType();
                continue;
            }
            if (!appliedFacets.contains(facetName)) {
                appliedFacets.add(facetName);
                effectiveFacets.add(x);
            }
            x = ((DataTypeWithFacet)x).baseType;
        }
        if (x instanceof ListType) {
            this.serializeListType((XSDatatypeImpl)dt);
            return;
        }
        if (!(x instanceof ConcreteType)) {
            throw new Error(x.getClass().getName());
        }
        if (x instanceof EmptyStringType) {
            this.writer.element("value");
            return;
        }
        if (x instanceof NoneType) {
            this.writer.element("notAllowed");
            return;
        }
        this.writer.start("data", new String[]{"type", x.getName()});
        for (int i = effectiveFacets.size() - 1; i >= 0; --i) {
            DataTypeWithFacet dtf = (DataTypeWithFacet)effectiveFacets.get(i);
            if (dtf instanceof LengthFacet) {
                this.param("length", Long.toString(((LengthFacet)dtf).length));
                continue;
            }
            if (dtf instanceof MinLengthFacet) {
                this.param("minLength", Long.toString(((MinLengthFacet)dtf).minLength));
                continue;
            }
            if (dtf instanceof MaxLengthFacet) {
                this.param("maxLength", Long.toString(((MaxLengthFacet)dtf).maxLength));
                continue;
            }
            if (dtf instanceof PatternFacet) {
                String pattern = "";
                PatternFacet pf = (PatternFacet)dtf;
                for (int j = 0; j < pf.getRegExps().length; ++j) {
                    if (pattern.length() != 0) {
                        pattern = pattern + "|";
                    }
                    pattern = pattern + pf.patterns[j];
                }
                this.param("pattern", pattern);
                continue;
            }
            if (dtf instanceof TotalDigitsFacet) {
                this.param("totalDigits", Long.toString(((TotalDigitsFacet)dtf).precision));
                continue;
            }
            if (dtf instanceof FractionDigitsFacet) {
                this.param("fractionDigits", Long.toString(((FractionDigitsFacet)dtf).scale));
                continue;
            }
            if (dtf instanceof RangeFacet) {
                this.param(dtf.facetName, dtf.convertToLexicalValue(((RangeFacet)dtf).limitValue, null));
                continue;
            }
            if (dtf instanceof WhiteSpaceFacet) continue;
            throw new Error();
        }
        this.writer.end("data");
    }

    protected void param(String name, String value) {
        this.writer.start("param", new String[]{"name", name});
        this.writer.characters(value);
        this.writer.end("param");
    }

    protected boolean isPredefinedType(Datatype x) {
        return !(x instanceof DataTypeWithFacet) && !(x instanceof UnionType) && !(x instanceof ListType) && !(x instanceof FinalComponent) && !(x instanceof EmptyStringType) && !(x instanceof NoneType);
    }

    protected void serializeUnionType(UnionType dt) {
        this.writer.start("choice");
        for (int i = 0; i < dt.memberTypes.length; ++i) {
            this.serializeDataType(dt.memberTypes[i]);
        }
        this.writer.end("choice");
    }

    protected void serializeListType(XSDatatypeImpl dt) {
        ListType base = (ListType)dt.getConcreteType();
        if (dt.getFacetObject("length") != null) {
            int len = ((LengthFacet)dt.getFacetObject((String)"length")).length;
            this.writer.start("list");
            for (int i = 0; i < len; ++i) {
                this.serializeDataType(base.itemType);
            }
            this.writer.end("list");
            return;
        }
        if (dt.getFacetObject("maxLength") != null) {
            throw new UnsupportedOperationException("warning: maxLength facet to list type is not properly converted.");
        }
        MinLengthFacet minLength = (MinLengthFacet)dt.getFacetObject("minLength");
        this.writer.start("list");
        if (minLength != null) {
            for (int i = 0; i < minLength.minLength; ++i) {
                this.serializeDataType(base.itemType);
            }
        }
        this.writer.start("zeroOrMore");
        this.serializeDataType(base.itemType);
        this.writer.end("zeroOrMore");
        this.writer.end("list");
    }

    protected void serializeEnumeration(XSDatatypeImpl dt, EnumerationFacet enums) {
        Object[] values = enums.values.toArray();
        if (values.length > 1) {
            this.writer.start("choice");
        }
        for (int i = 0; i < values.length; ++i) {
            final Vector<String> ns = new Vector<String>();
            String lex = dt.convertToLexicalValue(values[i], new SerializationContext(){

                public String getNamespacePrefix(String namespaceURI) {
                    int cnt = ns.size() / 2;
                    ns.add("xmlns:ns" + cnt);
                    ns.add(namespaceURI);
                    return "ns" + cnt;
                }
            });
            boolean allowed = dt.isValid(lex, new ValidationContext(){

                public String resolveNamespacePrefix(String prefix) {
                    if (!prefix.startsWith("ns")) {
                        return null;
                    }
                    int i = Integer.parseInt(prefix.substring(2));
                    return (String)ns.get(i * 2 + 1);
                }

                public boolean isUnparsedEntity(String name) {
                    return true;
                }

                public boolean isNotation(String name) {
                    return true;
                }

                public String getBaseUri() {
                    return null;
                }
            });
            ns.add("type");
            ns.add(dt.getConcreteType().getName());
            if (!allowed) continue;
            this.writer.start("value", ns.toArray(new String[0]));
            this.writer.characters(lex);
            this.writer.end("value");
        }
        if (values.length > 1) {
            this.writer.end("choice");
        }
    }
}

