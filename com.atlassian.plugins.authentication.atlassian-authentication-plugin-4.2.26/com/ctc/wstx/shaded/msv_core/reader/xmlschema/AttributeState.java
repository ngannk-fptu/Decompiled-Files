/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.relax.NoneType;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeIncubator;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.GlobalDeclState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import org.xml.sax.Locator;

public class AttributeState
extends ExpressionWithChildState
implements XSTypeOwner {
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("simpleType")) {
            return ((XMLSchemaReader)this.reader).sfactory.simpleType(this, tag);
        }
        return super.createChildState(tag);
    }

    protected Expression initialExpression() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        if (this.startTag.containsAttribute("ref")) {
            if (this.isGlobal()) {
                reader.reportError("GrammarReader.DisallowedAttribute", (Object)this.startTag.qName, (Object)"ref");
                return Expression.epsilon;
            }
            Expression exp = reader.resolveQNameRef(this.startTag, "ref", new XMLSchemaReader.RefResolver(){

                public ReferenceContainer get(XMLSchemaSchema g) {
                    return g.attributeDecls;
                }
            });
            if (exp == null) {
                return Expression.epsilon;
            }
            return exp;
        }
        String typeAttr = this.startTag.getAttribute("type");
        if (typeAttr == null) {
            return null;
        }
        return reader.resolveXSDatatype(typeAttr);
    }

    protected Expression defaultExpression() {
        return Expression.anyString;
    }

    protected Expression castExpression(Expression halfCastedExpression, Expression newChildExpression) {
        if (halfCastedExpression != null) {
            this.reader.reportError("GrammarReader.Abstract.MoreThanOneChildExpression");
        }
        return newChildExpression;
    }

    protected Expression annealExpression(Expression contentType) {
        Expression exp;
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        String fixed = this.startTag.getAttribute("fixed");
        String name = this.startTag.getAttribute("name");
        String use = this.startTag.getAttribute("use");
        if (this.startTag.containsAttribute("ref")) {
            if (fixed != null) {
                reader.reportWarning("XMLSchemaReader.UnimplementedFeature", "<attribute> element with both 'ref' and 'fixed' attributes");
            }
            exp = contentType;
        } else {
            if (name == null) {
                reader.reportError("GrammarReader.MissingAttribute", (Object)"attribute", (Object)"name");
                return Expression.nullSet;
            }
            String targetNamespace = this.isGlobal() ? reader.currentSchema.targetNamespace : reader.resolveNamespaceOfAttributeDecl(this.startTag.getAttribute("form"));
            if (fixed != null) {
                if (contentType instanceof XSDatatypeExp) {
                    XSDatatypeExp baseType = (XSDatatypeExp)contentType;
                    try {
                        XSTypeIncubator inc = baseType.createIncubator();
                        inc.addFacet("enumeration", fixed, false, reader);
                        contentType = inc.derive(null, null);
                    }
                    catch (DatatypeException e) {
                        reader.reportError(e, "GrammarReader.BadType", (Object)e.getMessage());
                        return Expression.nullSet;
                    }
                } else {
                    contentType = reader.pool.createValue(TokenType.theInstance, new StringPair("", "token"), fixed);
                }
            }
            if ("prohibited".equals(use)) {
                contentType = reader.pool.createData(NoneType.theInstance);
            }
            exp = this.createAttribute(new SimpleNameClass(targetNamespace, name), contentType);
        }
        if (this.isGlobal()) {
            AttributeDeclExp decl = reader.currentSchema.attributeDecls.getOrCreate(name);
            if (decl.exp != null) {
                reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(decl)}, "XMLSchemaReader.DuplicateAttributeDefinition", new Object[]{name});
            }
            reader.setDeclaredLocationOf(decl);
            if (exp instanceof AttributeExp) {
                decl.set((AttributeExp)exp);
            } else if (!reader.controller.hadError()) {
                throw new Error();
            }
        } else if ("optional".equals(use) || use == null || "prohibited".equals(use)) {
            exp = reader.pool.createOptional(exp);
        } else if (!"required".equals(use)) {
            reader.reportError("GrammarReader.BadAttributeValue", (Object)"use", (Object)use);
        }
        return exp;
    }

    protected Expression createAttribute(NameClass nc, Expression exp) {
        return this.reader.pool.createAttribute(nc, exp);
    }

    public String getTargetNamespaceUri() {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        return reader.currentSchema.targetNamespace;
    }

    public void onEndChild(XSDatatypeExp type) {
        super.onEndChild(type);
    }

    protected boolean isGlobal() {
        return this.parentState instanceof GlobalDeclState;
    }
}

