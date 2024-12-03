/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.XSNotationDecl;
import org.apache.xerces.impl.xs.traversers.XSAttributeChecker;
import org.apache.xerces.impl.xs.traversers.XSDAbstractTraverser;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.impl.xs.traversers.XSDocumentInfo;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.util.DOMUtil;
import org.w3c.dom.Element;

class XSDNotationTraverser
extends XSDAbstractTraverser {
    XSDNotationTraverser(XSDHandler xSDHandler, XSAttributeChecker xSAttributeChecker) {
        super(xSDHandler, xSAttributeChecker);
    }

    XSNotationDecl traverse(Element element, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        XSNotationDecl xSNotationDecl;
        Object[] objectArray;
        Object object;
        Object[] objectArray2 = this.fAttrChecker.checkAttributes(element, true, xSDocumentInfo);
        String string = (String)objectArray2[XSAttributeChecker.ATTIDX_NAME];
        String string2 = (String)objectArray2[XSAttributeChecker.ATTIDX_PUBLIC];
        String string3 = (String)objectArray2[XSAttributeChecker.ATTIDX_SYSTEM];
        if (string == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_NOTATION, SchemaSymbols.ATT_NAME}, element);
            this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
            return null;
        }
        if (string3 == null && string2 == null) {
            this.reportSchemaError("PublicSystemOnNotation", null, element);
            string2 = "missing";
        }
        XSNotationDecl xSNotationDecl2 = new XSNotationDecl();
        xSNotationDecl2.fName = string;
        xSNotationDecl2.fTargetNamespace = xSDocumentInfo.fTargetNamespace;
        xSNotationDecl2.fPublicId = string2;
        xSNotationDecl2.fSystemId = string3;
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSAnnotationImpl xSAnnotationImpl = null;
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            xSAnnotationImpl = this.traverseAnnotationDecl(element2, objectArray2, false, xSDocumentInfo);
            element2 = DOMUtil.getNextSiblingElement(element2);
        } else {
            object = DOMUtil.getSyntheticAnnotation(element);
            if (object != null) {
                xSAnnotationImpl = this.traverseSyntheticAnnotation(element, (String)object, objectArray2, false, xSDocumentInfo);
            }
        }
        if (xSAnnotationImpl != null) {
            object = new XSObjectListImpl();
            ((XSObjectListImpl)object).addXSObject(xSAnnotationImpl);
        } else {
            object = XSObjectListImpl.EMPTY_LIST;
        }
        xSNotationDecl2.fAnnotations = object;
        if (element2 != null) {
            objectArray = new Object[]{SchemaSymbols.ELT_NOTATION, "(annotation?)", DOMUtil.getLocalName(element2)};
            this.reportSchemaError("s4s-elt-must-match.1", objectArray, element2);
        }
        if (schemaGrammar.getGlobalNotationDecl(xSNotationDecl2.fName) == null) {
            schemaGrammar.addGlobalNotationDecl(xSNotationDecl2);
        }
        if ((xSNotationDecl = schemaGrammar.getGlobalNotationDecl(xSNotationDecl2.fName, (String)(objectArray = this.fSchemaHandler.schemaDocument2SystemId(xSDocumentInfo)))) == null) {
            schemaGrammar.addGlobalNotationDecl(xSNotationDecl2, (String)objectArray);
        }
        if (this.fSchemaHandler.fTolerateDuplicates) {
            if (xSNotationDecl != null) {
                xSNotationDecl2 = xSNotationDecl;
            }
            this.fSchemaHandler.addGlobalNotationDecl(xSNotationDecl2);
        }
        this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
        return xSNotationDecl2;
    }
}

