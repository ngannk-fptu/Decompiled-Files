/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeGroupExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyAttributeOwner;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.RedefinableDeclState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.xml.sax.Locator;

public class AttributeGroupState
extends RedefinableDeclState
implements AnyAttributeOwner {
    private AttributeWildcard wildcard;

    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        return reader.createAttributeState(this, tag);
    }

    protected ReferenceContainer getContainer() {
        return ((XMLSchemaReader)this.reader).currentSchema.attributeGroups;
    }

    protected Expression initialExpression() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        String refQName = this.startTag.getAttribute("ref");
        if (refQName == null) {
            return Expression.epsilon;
        }
        Expression exp = reader.resolveQNameRef(this.startTag, "ref", new XMLSchemaReader.RefResolver(){

            public ReferenceContainer get(XMLSchemaSchema g) {
                return g.attributeGroups;
            }
        });
        if (exp == null) {
            return Expression.epsilon;
        }
        return exp;
    }

    public void setAttributeWildcard(AttributeWildcard local) {
        this.wildcard = local;
    }

    protected Expression castExpression(Expression halfCastedExpression, Expression newChildExpression) {
        if (this.startTag.containsAttribute("ref")) {
            this.reader.reportError("GrammarReader.Abstract.MoreThanOneChildExpression");
        }
        if (halfCastedExpression == null) {
            return newChildExpression;
        }
        return this.reader.pool.createSequence(newChildExpression, halfCastedExpression);
    }

    protected Expression annealExpression(Expression contentType) {
        AttributeGroupExp exp;
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        if (!this.isGlobal()) {
            return contentType;
        }
        String name = this.startTag.getAttribute("name");
        if (name == null) {
            reader.reportError("GrammarReader.MissingAttribute", (Object)"attributeGroup", (Object)"name");
            return Expression.epsilon;
        }
        if (this.isRedefine()) {
            exp = (AttributeGroupExp)this.oldDecl;
        } else {
            exp = reader.currentSchema.attributeGroups.getOrCreate(name);
            if (exp.exp != null) {
                reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(exp)}, "XMLSchemaReader.DuplicateAttributeGroupDefinition", new Object[]{name});
            }
        }
        reader.setDeclaredLocationOf(exp);
        exp.exp = contentType;
        exp.wildcard = this.wildcard;
        return exp;
    }
}

