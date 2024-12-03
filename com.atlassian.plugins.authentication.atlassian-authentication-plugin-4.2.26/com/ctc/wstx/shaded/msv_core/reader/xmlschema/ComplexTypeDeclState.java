/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyAttributeOwner;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.RedefinableDeclState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.xml.sax.Locator;

public class ComplexTypeDeclState
extends RedefinableDeclState
implements AnyAttributeOwner {
    protected ComplexTypeExp decl;

    protected ReferenceContainer getContainer() {
        return ((XMLSchemaReader)this.reader).currentSchema.complexTypes;
    }

    protected void startSelf() {
        super.startSelf();
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        String name = this.startTag.getAttribute("name");
        if (name == null) {
            if (this.isGlobal()) {
                reader.reportError("GrammarReader.MissingAttribute", (Object)"complexType", (Object)"name");
            }
            this.decl = new ComplexTypeExp(reader.currentSchema, null);
        } else if (this.isRedefine()) {
            this.decl = new ComplexTypeExp(reader.currentSchema, name);
        } else {
            this.decl = reader.currentSchema.complexTypes.getOrCreate(name);
            if (this.decl.body.exp != null && reader.currentSchema != reader.xsdSchema) {
                reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(this.decl)}, "XMLSchemaReader.DuplicateComplexTypeDefinition", new Object[]{name});
            }
        }
        this.decl.finalValue = this.parseFinalValue("final", reader.finalDefault);
        this.decl.block = this.parseFinalValue("block", reader.blockDefault);
    }

    private int parseFinalValue(String attName, String defaultValue) {
        int r = 0;
        String value = this.startTag.getAttribute(attName);
        if (value == null) {
            value = defaultValue;
        }
        if (value != null) {
            if (value.indexOf("#all") >= 0) {
                r |= 3;
            }
            if (value.indexOf("extension") >= 0) {
                r |= 2;
            }
            if (value.indexOf("restriction") >= 0) {
                r |= 1;
            }
        }
        return r;
    }

    public void setAttributeWildcard(AttributeWildcard local) {
        this.decl.wildcard = local;
    }

    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        if (tag.localName.equals("simpleContent")) {
            return reader.sfactory.simpleContent(this, tag, this.decl);
        }
        if (tag.localName.equals("complexContent")) {
            return reader.sfactory.complexContent(this, tag, this.decl);
        }
        State s = reader.createModelGroupState(this, tag);
        if (s != null) {
            return s;
        }
        if (this.exp == null) {
            this.exp = Expression.epsilon;
        }
        return reader.createAttributeState(this, tag);
    }

    protected Expression castExpression(Expression halfCastedExpression, Expression newChildExpression) {
        if (halfCastedExpression == null) {
            return newChildExpression;
        }
        return this.reader.pool.createSequence(newChildExpression, halfCastedExpression);
    }

    protected Expression defaultExpression() {
        return Expression.epsilon;
    }

    protected Expression annealExpression(Expression contentType) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        String abstract_ = this.startTag.getAttribute("abstract");
        if ("false".equals(abstract_) || abstract_ == null) {
            this.decl.setAbstract(false);
        } else {
            this.decl.setAbstract(true);
            if (!"true".equals(abstract_)) {
                reader.reportError("GrammarReader.BadAttributeValue", (Object)"abstract", (Object)abstract_);
            }
        }
        String mixed = this.startTag.getAttribute("mixed");
        if ("true".equals(mixed)) {
            contentType = reader.pool.createMixed(contentType);
        } else if (mixed != null && !"false".equals(mixed)) {
            reader.reportError("GrammarReader.BadAttributeValue", (Object)"mixed", (Object)mixed);
        }
        this.decl.body.exp = contentType;
        if (this.isRedefine()) {
            this.oldDecl.redefine(this.decl);
            this.decl = (ComplexTypeExp)this.oldDecl;
        }
        reader.setDeclaredLocationOf(this.decl);
        reader.setDeclaredLocationOf(this.decl.body);
        return this.decl;
    }
}

