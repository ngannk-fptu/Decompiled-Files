/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyAttributeOwner;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class SimpleContentExtensionState
extends SequenceState
implements AnyAttributeOwner {
    protected ComplexTypeExp parentDecl;

    protected SimpleContentExtensionState(ComplexTypeExp parentDecl) {
        this.parentDecl = parentDecl;
    }

    public void setAttributeWildcard(AttributeWildcard local) {
        this.parentDecl.wildcard = local;
    }

    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        return reader.createAttributeState(this, tag);
    }

    protected Expression initialExpression() {
        return Expression.epsilon;
    }

    protected Expression annealExpression(Expression exp) {
        this.parentDecl.derivationMethod = 2;
        return this.reader.pool.createSequence(super.annealExpression(exp), this.getBody());
    }

    private Expression getBody() {
        XSDatatype dt;
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        final String base = this.startTag.getAttribute("base");
        if (base == null) {
            reader.reportError("GrammarReader.MissingAttribute", (Object)this.startTag.localName, (Object)"base");
            return Expression.nullSet;
        }
        final String[] baseTypeName = reader.splitQName(base);
        if (baseTypeName == null) {
            reader.reportError("XMLSchemaReader.UndeclaredPrefix", (Object)base);
            return Expression.nullSet;
        }
        if (reader.isSchemaNamespace(baseTypeName[0]) && (dt = reader.resolveBuiltinDataType(baseTypeName[1])) != null) {
            XSDatatypeExp dtexp;
            this.parentDecl.simpleBaseType = dtexp = new XSDatatypeExp(dt, reader.pool);
            return dtexp;
        }
        final XMLSchemaSchema schema = reader.grammar.getByNamespace(baseTypeName[0]);
        final ReferenceExp ref = new ReferenceExp(null);
        reader.addBackPatchJob(new GrammarReader.BackPatch(){

            public State getOwnerState() {
                return SimpleContentExtensionState.this;
            }

            public void patch() {
                SimpleTypeExp sexp = schema.simpleTypes.get(baseTypeName[1]);
                if (sexp != null) {
                    ref.exp = sexp;
                    SimpleContentExtensionState.this.parentDecl.simpleBaseType = sexp.getType();
                    State._assert(SimpleContentExtensionState.this.parentDecl.simpleBaseType != null);
                    return;
                }
                ComplexTypeExp cexp = schema.complexTypes.get(baseTypeName[1]);
                if (cexp != null) {
                    ref.exp = cexp.body;
                    SimpleContentExtensionState.this.parentDecl.complexBaseType = cexp;
                    return;
                }
                reader.reportError("XMLSchemaReader.UndefinedComplexOrSimpleType", (Object)base);
            }
        });
        return ref;
    }
}

