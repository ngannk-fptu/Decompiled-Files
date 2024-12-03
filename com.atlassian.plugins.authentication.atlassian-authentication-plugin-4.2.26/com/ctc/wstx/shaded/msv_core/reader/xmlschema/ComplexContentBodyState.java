/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyAttributeOwner;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public class ComplexContentBodyState
extends SequenceState
implements AnyAttributeOwner {
    protected ComplexTypeExp parentDecl;
    protected boolean extension;

    protected ComplexContentBodyState(ComplexTypeExp parentDecl, boolean extension) {
        this.parentDecl = parentDecl;
        this.extension = extension;
    }

    protected State createChildState(StartTagInfo tag) {
        State s;
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        if (this.exp == Expression.epsilon && (s = reader.createModelGroupState(this, tag)) != null) {
            return s;
        }
        return reader.createAttributeState(this, tag);
    }

    public void setAttributeWildcard(AttributeWildcard local) {
        this.parentDecl.wildcard = local;
    }

    protected Expression initialExpression() {
        return Expression.epsilon;
    }

    protected Expression annealExpression(Expression exp) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        exp = super.annealExpression(exp);
        String refQName = this.startTag.getAttribute("base");
        if (refQName == null) {
            reader.reportError("GrammarReader.MissingAttribute", (Object)this.startTag.qName, (Object)"base");
            return exp;
        }
        String[] r = reader.splitQName(refQName);
        if (r == null) {
            reader.reportError("XMLSchemaReader.UndeclaredPrefix", (Object)refQName);
            return exp;
        }
        if (reader.isSchemaNamespace(r[0]) && r[1].equals("anyType")) {
            return exp;
        }
        ComplexTypeExp baseType = reader.getOrCreateSchema((String)r[0]).complexTypes.getOrCreate(r[1]);
        if (baseType == null) {
            return exp;
        }
        reader.backwardReference.memorizeLink(baseType);
        this.parentDecl.derivationMethod = this.extension ? 2 : 1;
        this.parentDecl.complexBaseType = baseType;
        return this.combineToBaseType(baseType, exp);
    }

    protected Expression combineToBaseType(ComplexTypeExp baseType, Expression addedExp) {
        if (this.extension) {
            return this.reader.pool.createSequence(baseType.body, addedExp);
        }
        return addedExp;
    }
}

