/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.regexp.xmlschema;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.QnameType;
import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ElementDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaTypeExp;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringRef;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.AttributeToken;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.SimpleAcceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.xmlschema.XSREDocDecl;

public class XSAcceptor
extends SimpleAcceptor {
    public static final String XSINamespace = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String XMLSchemaNamespace = "http://www.w3.org/2001/XMLSchema";
    private final XSREDocDecl _docDecl;

    public XSAcceptor(XSREDocDecl docDecl, Expression combined, ElementExp owner, Expression continuation) {
        super(docDecl, combined, owner, continuation);
        this._docDecl = docDecl;
    }

    public Acceptor createClone() {
        return new XSAcceptor(this._docDecl, this.getExpression(), this.owner, this.continuation);
    }

    protected Acceptor createAcceptor(Expression combined, Expression continuation, ElementExp[] primitives, int numPrimitives) {
        if (primitives == null || numPrimitives <= 1) {
            return new XSAcceptor((XSREDocDecl)this.docDecl, combined, primitives == null ? null : primitives[0], continuation);
        }
        return new XSAcceptor((XSREDocDecl)this.docDecl, primitives[0].contentModel.getExpandedExp(this.docDecl.pool), primitives[0], null);
    }

    protected boolean onAttribute(AttributeToken token, StringRef refErr) {
        if (token.namespaceURI.equals(XSINamespace)) {
            token.match(this._docDecl.xsiAttExp);
            return true;
        }
        return super.onAttribute(token, refErr);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Acceptor createChildAcceptor(StartTagInfo sti, StringRef refErr) {
        Expression contentModel;
        String type = sti.getAttribute(XSINamespace, "type");
        String nil = sti.getAttribute(XSINamespace, "nil");
        if (type == null && nil == null) {
            return super.createChildAcceptor(sti, refErr);
        }
        this._docDecl.getCCCEC().get(this.getExpression(), sti, true);
        switch (this._docDecl.getCCCEC().numMatchedElements()) {
            case 0: {
                return super.createChildAcceptor(sti, refErr);
            }
            case 1: {
                break;
            }
            default: {
                return super.createChildAcceptor(sti, refErr);
            }
        }
        ElementExp element = this._docDecl.getCCCEC().getMatchedElements()[0];
        if (!(element instanceof ElementDeclExp.XSElementExp)) {
            return super.createChildAcceptor(sti, refErr);
        }
        ElementDeclExp.XSElementExp xe = (ElementDeclExp.XSElementExp)element;
        if (nil != null) {
            if (!xe.parent.isNillable) {
                if (refErr == null) {
                    return null;
                }
                refErr.str = this._docDecl.localizeMessage("XMLSchemaVerifier.NonNillableElement", sti.qName);
                return new XSAcceptor(this._docDecl, Expression.epsilon, (ElementExp)xe, null);
            }
            if (nil.trim().equals("true")) {
                if (!Debug.debug) return new XSAcceptor(this._docDecl, Expression.epsilon, (ElementExp)xe, null);
                System.out.println("xsi:nil is found");
                return new XSAcceptor(this._docDecl, Expression.epsilon, (ElementExp)xe, null);
            }
        }
        if (type == null) {
            return super.createChildAcceptor(sti, refErr);
        }
        String[] typeName = (String[])QnameType.theInstance.createJavaObject(type, sti.context);
        if (typeName == null) {
            return this.onTypeResolutionFailure(sti, type, refErr);
        }
        if (typeName[0].equals(XMLSchemaNamespace)) {
            try {
                contentModel = this._docDecl.grammar.getPool().createData(DatatypeFactory.getTypeByName(typeName[1]));
                return new XSAcceptor(this._docDecl, contentModel, (ElementExp)xe, null);
            }
            catch (DatatypeException e) {
                return this.onTypeResolutionFailure(sti, type, refErr);
            }
        } else {
            XMLSchemaSchema schema = this._docDecl.grammar.getByNamespace(typeName[0]);
            if (schema == null) {
                return this.onTypeResolutionFailure(sti, type, refErr);
            }
            XMLSchemaTypeExp currentType = xe.parent.getTypeDefinition();
            ComplexTypeExp cexp = schema.complexTypes.get(typeName[1]);
            if (cexp != null) {
                if (!cexp.isDerivedTypeOf(currentType, xe.parent.block | currentType.getBlock())) return this.onNotSubstitutableType(sti, type, refErr);
                contentModel = cexp;
                return new XSAcceptor(this._docDecl, contentModel, (ElementExp)xe, null);
            } else {
                SimpleTypeExp sexp = schema.simpleTypes.get(typeName[1]);
                if (sexp == null) {
                    return this.onTypeResolutionFailure(sti, type, refErr);
                }
                if (!(currentType instanceof SimpleTypeExp)) {
                    return this.onNotSubstitutableType(sti, type, refErr);
                }
                SimpleTypeExp curT = (SimpleTypeExp)currentType;
                if (!sexp.getDatatype().isDerivedTypeOf(curT.getDatatype(), !xe.parent.isRestrictionBlocked())) return this.onNotSubstitutableType(sti, type, refErr);
                contentModel = sexp;
            }
        }
        return new XSAcceptor(this._docDecl, contentModel, (ElementExp)xe, null);
    }

    private Acceptor onNotSubstitutableType(StartTagInfo sti, String type, StringRef refErr) {
        if (refErr == null) {
            return null;
        }
        refErr.str = this._docDecl.localizeMessage("XMLSchemaVerifier.NotSubstitutableType", type);
        return super.createChildAcceptor(sti, refErr);
    }

    private Acceptor onTypeResolutionFailure(StartTagInfo sti, String type, StringRef refErr) {
        if (refErr == null) {
            return null;
        }
        refErr.str = this._docDecl.localizeMessage("XMLSchemaVerifier.UndefinedType", type);
        return super.createChildAcceptor(sti, refErr);
    }
}

