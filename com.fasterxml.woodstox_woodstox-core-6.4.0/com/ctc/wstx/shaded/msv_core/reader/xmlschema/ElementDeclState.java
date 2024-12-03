/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.BooleanType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ElementDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.IdentityConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.GlobalDeclState;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.Vector;
import org.xml.sax.Locator;

public class ElementDeclState
extends ExpressionWithChildState {
    protected final Vector idcs = new Vector();

    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        if (this.exp == null) {
            if (tag.localName.equals("simpleType")) {
                return reader.sfactory.simpleType(this, tag);
            }
            if (tag.localName.equals("complexType")) {
                return reader.sfactory.complexTypeDecl(this, tag);
            }
        }
        if (tag.localName.equals("unique")) {
            return reader.sfactory.unique(this, tag);
        }
        if (tag.localName.equals("key")) {
            return reader.sfactory.key(this, tag);
        }
        if (tag.localName.equals("keyref")) {
            return reader.sfactory.keyref(this, tag);
        }
        return null;
    }

    protected Expression initialExpression() {
        String typeQName = this.startTag.getAttribute("type");
        if (typeQName == null) {
            return null;
        }
        return this.resolveTypeRef(typeQName);
    }

    protected Expression resolveTypeRef(final String typeQName) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        final ReferenceExp ref = new ReferenceExp("elementType(" + typeQName + ")");
        final String[] s = reader.splitQName(typeQName);
        if (s == null) {
            reader.reportError("XMLSchemaReader.UndeclaredPrefix", (Object)typeQName);
            ref.exp = Expression.nullSet;
            return ref;
        }
        reader.addBackPatchJob(new GrammarReader.BackPatch(){

            public State getOwnerState() {
                return ElementDeclState.this;
            }

            public void patch() {
                Expression e = null;
                if (reader.isSchemaNamespace(s[0])) {
                    e = reader.resolveBuiltinSimpleType(s[1]);
                }
                if (e == null) {
                    XMLSchemaSchema g = reader.getOrCreateSchema(s[0]);
                    e = g.simpleTypes.get(s[1]);
                    if (e == null) {
                        e = g.complexTypes.get(s[1]);
                    }
                    if (e == null) {
                        reader.reportError("XMLSchemaReader.UndefinedElementType", (Object)typeQName);
                        e = Expression.nullSet;
                    }
                }
                ref.exp = e;
            }
        });
        return ref;
    }

    protected Expression castExpression(Expression halfCastedExpression, Expression newChildExpression) {
        if (halfCastedExpression != null) {
            throw new Error();
        }
        return newChildExpression;
    }

    protected Expression defaultExpression() {
        if (this.startTag.containsAttribute("substitutionGroup")) {
            this.reader.reportError("XMLSchemaReader.UnimplementedFeature", (Object)"omitting type attribute in <element> element with substitutionGroup attribute");
        }
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        reader.reportWarning("XMLSchemaReader.Warning.ImplicitUrTypeForElement", null);
        return reader.complexUrType;
    }

    protected Expression annealExpression(Expression contentType) {
        String finalValue;
        String block;
        String substitutionGroupQName;
        String abstract_;
        ElementDeclExp decl;
        XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        String name = this.startTag.getAttribute("name");
        if (name == null) {
            reader.reportError("GrammarReader.MissingAttribute", (Object)"element", (Object)"name");
            return Expression.nullSet;
        }
        String targetNamespace = this.isGlobal() ? reader.currentSchema.targetNamespace : reader.resolveNamespaceOfElementDecl(this.startTag.getAttribute("form"));
        String fixed = this.startTag.getAttribute("fixed");
        if (fixed != null) {
            contentType = reader.pool.createValue(TokenType.theInstance, new StringPair("", "token"), fixed);
        }
        if (this.isGlobal()) {
            decl = reader.currentSchema.elementDecls.getOrCreate(name);
            if (decl.getElementExp() != null) {
                reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(decl)}, "XMLSchemaReader.DuplicateElementDefinition", new Object[]{name});
            }
        } else {
            decl = new ElementDeclExp(reader.currentSchema, null);
        }
        reader.setDeclaredLocationOf(decl);
        ElementDeclExp elementDeclExp = decl;
        elementDeclExp.getClass();
        ElementDeclExp.XSElementExp exp = new ElementDeclExp.XSElementExp(elementDeclExp, new SimpleNameClass(targetNamespace, name), contentType);
        decl.setElementExp(exp);
        exp.identityConstraints.addAll(this.idcs);
        String nillable = this.startTag.getAttribute("nillable");
        if (nillable != null) {
            decl.isNillable = nillable.equals("true") || nillable.equals("1");
        }
        decl.setAbstract("true".equals(abstract_ = this.startTag.getAttribute("abstract")) || "1".equals(abstract_));
        if (abstract_ != null && !BooleanType.theInstance.isValid(abstract_, null)) {
            reader.reportError("GrammarReader.BadAttributeValue", (Object)"abstract", (Object)abstract_);
        }
        if ((substitutionGroupQName = this.startTag.getAttribute("substitutionGroup")) != null) {
            String[] r = reader.splitQName(substitutionGroupQName);
            if (r == null) {
                reader.reportError("XMLSchemaReader.UndeclaredPrefix", (Object)substitutionGroupQName);
            } else {
                ElementDeclExp head;
                decl.substitutionAffiliation = head = reader.getOrCreateSchema((String)r[0]).elementDecls.getOrCreate(r[1]);
            }
        }
        if ((block = this.startTag.getAttribute("block")) == null) {
            block = reader.blockDefault;
        }
        if (block != null) {
            if (block.indexOf("#all") >= 0) {
                decl.block |= 7;
            }
            if (block.indexOf("extension") >= 0) {
                decl.block |= 2;
            }
            if (block.indexOf("restriction") >= 0) {
                decl.block |= 1;
            }
            if (block.indexOf("substitution") >= 0) {
                decl.block |= 4;
            }
        }
        if ((finalValue = this.startTag.getAttribute("final")) == null) {
            finalValue = reader.finalDefault;
        }
        if (finalValue != null) {
            if (finalValue.indexOf("#all") >= 0) {
                decl.finalValue |= 7;
            }
            if (finalValue.indexOf("extension") >= 0) {
                decl.finalValue |= 2;
            }
            if (finalValue.indexOf("restriction") >= 0) {
                decl.finalValue |= 1;
            }
        }
        return this.annealDeclaration(decl);
    }

    protected Expression annealDeclaration(ElementDeclExp exp) {
        return exp;
    }

    public boolean isGlobal() {
        return this.parentState instanceof GlobalDeclState;
    }

    protected void onIdentityConstraint(IdentityConstraint idc) {
        this.idcs.add(idc);
    }
}

