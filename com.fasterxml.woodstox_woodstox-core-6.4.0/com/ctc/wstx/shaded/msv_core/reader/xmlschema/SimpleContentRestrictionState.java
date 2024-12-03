/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetStateParent;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyAttributeOwner;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class SimpleContentRestrictionState
extends SequenceState
implements FacetStateParent,
XSTypeOwner,
AnyAttributeOwner {
    protected ComplexTypeExp parentDecl;
    protected XSTypeIncubator incubator;
    private String base;
    private String[] baseTypeName;
    private XMLSchemaSchema baseSchema;

    protected SimpleContentRestrictionState(ComplexTypeExp parentDecl) {
        this.parentDecl = parentDecl;
    }

    public void setAttributeWildcard(AttributeWildcard local) {
        this.parentDecl.wildcard = local;
    }

    public XSTypeIncubator getIncubator() {
        if (this.incubator == null) {
            XMLSchemaReader reader = (XMLSchemaReader)this.reader;
            if (this.baseTypeName == null) {
                this.incubator = new XSDatatypeExp(StringType.theInstance, reader.pool).createIncubator();
            } else {
                XSDatatype dt;
                if (reader.isSchemaNamespace(this.baseTypeName[0]) && (dt = reader.resolveBuiltinDataType(this.baseTypeName[1])) != null) {
                    this.incubator = new XSDatatypeExp(dt, reader.pool).createIncubator();
                }
                if (this.incubator == null) {
                    this.incubator = new XSDatatypeExp(this.baseTypeName[0], this.baseTypeName[1], reader, new BaseContentTypeRenderer()).createIncubator();
                }
            }
        }
        return this.incubator;
    }

    public String getTargetNamespaceUri() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        return reader.currentSchema.targetNamespace;
    }

    public void onEndChild(XSDatatypeExp child) {
        if (this.incubator != null) {
            throw new Error();
        }
        this.incubator = child.createIncubator();
    }

    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        if (this.incubator == null && tag.localName.equals("simpleType")) {
            return reader.sfactory.simpleType(this, tag);
        }
        State s = reader.createAttributeState(this, tag);
        if (s != null) {
            return s;
        }
        return reader.createFacetState(this, tag);
    }

    protected Expression initialExpression() {
        return Expression.epsilon;
    }

    protected void startSelf() {
        super.startSelf();
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        this.base = this.startTag.getAttribute("base");
        if (this.base == null) {
            reader.reportError("GrammarReader.MissingAttribute", (Object)this.startTag.localName, (Object)"base");
            return;
        }
        this.baseTypeName = reader.splitQName(this.base);
        if (this.baseTypeName == null) {
            reader.reportError("XMLSchemaReader.UndeclaredPrefix", (Object)this.base);
            return;
        }
        this.baseSchema = reader.grammar.getByNamespace(this.baseTypeName[0]);
    }

    protected Expression annealExpression(Expression exp) {
        XSDatatype dt;
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        this.parentDecl.derivationMethod = 1;
        try {
            XSDatatypeExp p = this.getIncubator().derive(null, null);
            exp = reader.pool.createSequence(super.annealExpression(exp), p);
        }
        catch (DatatypeException e) {
            reader.reportError(e, "GrammarReader.BadType", (Object)e.getMessage());
            return Expression.nullSet;
        }
        if (reader.isSchemaNamespace(this.baseTypeName[0]) && (dt = reader.resolveBuiltinDataType(this.baseTypeName[1])) != null) {
            this.parentDecl.simpleBaseType = new XSDatatypeExp(dt, reader.pool);
            return exp;
        }
        reader.addBackPatchJob(new GrammarReader.BackPatch(){

            public State getOwnerState() {
                return SimpleContentRestrictionState.this;
            }

            public void patch() {
                SimpleTypeExp sexp = ((SimpleContentRestrictionState)SimpleContentRestrictionState.this).baseSchema.simpleTypes.get(SimpleContentRestrictionState.this.baseTypeName[1]);
                if (sexp != null) {
                    SimpleContentRestrictionState.this.parentDecl.simpleBaseType = sexp.getType();
                    State._assert(SimpleContentRestrictionState.this.parentDecl.simpleBaseType != null);
                    return;
                }
                ComplexTypeExp cexp = ((SimpleContentRestrictionState)SimpleContentRestrictionState.this).baseSchema.complexTypes.get(SimpleContentRestrictionState.this.baseTypeName[1]);
                if (cexp != null) {
                    SimpleContentRestrictionState.this.parentDecl.complexBaseType = cexp;
                    return;
                }
                reader.reportError("XMLSchemaReader.UndefinedComplexOrSimpleType", (Object)SimpleContentRestrictionState.this.base);
            }
        });
        return exp;
    }

    private class BaseContentTypeRenderer
    implements XSDatatypeExp.Renderer {
        private BaseContentTypeRenderer() {
        }

        public XSDatatype render(XSDatatypeExp.RenderingContext context) {
            XMLSchemaReader reader = (XMLSchemaReader)SimpleContentRestrictionState.this.reader;
            SimpleTypeExp sexp = ((SimpleContentRestrictionState)SimpleContentRestrictionState.this).baseSchema.simpleTypes.get(SimpleContentRestrictionState.this.baseTypeName[1]);
            if (sexp != null) {
                return sexp.getType().getType(context);
            }
            ComplexTypeExp cexp = ((SimpleContentRestrictionState)SimpleContentRestrictionState.this).baseSchema.complexTypes.get(SimpleContentRestrictionState.this.baseTypeName[1]);
            if (cexp != null) {
                final XSDatatypeExp[] dexp = new XSDatatypeExp[1];
                cexp.body.visit(new ExpressionWalker(){

                    public void onAttribute(AttributeExp exp) {
                    }

                    public void onRef(ReferenceExp exp) {
                        if (exp instanceof XSDatatypeExp) {
                            dexp[0] = (XSDatatypeExp)exp;
                            return;
                        }
                        super.onRef(exp);
                    }
                });
                if (dexp[0] == null) {
                    reader.reportError("XMLSchemaReader.InvalidBasetypeForSimpleContent", (Object)SimpleContentRestrictionState.this.base);
                    return StringType.theInstance;
                }
                return dexp[0].getType(context);
            }
            return StringType.theInstance;
        }
    }
}

