/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xpath.XPathException;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.identity.Field;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.identity.Selector;
import org.apache.xerces.impl.xs.traversers.XSAttributeChecker;
import org.apache.xerces.impl.xs.traversers.XSDAbstractTraverser;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.impl.xs.traversers.XSDocumentInfo;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.util.XMLChar;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class XSDAbstractIDConstraintTraverser
extends XSDAbstractTraverser {
    public XSDAbstractIDConstraintTraverser(XSDHandler xSDHandler, XSAttributeChecker xSAttributeChecker) {
        super(xSDHandler, xSAttributeChecker);
    }

    boolean traverseIdentityConstraint(IdentityConstraint identityConstraint, Element element, XSDocumentInfo xSDocumentInfo, Object[] objectArray) {
        Object object;
        String string;
        Object[] objectArray2;
        Element element2 = DOMUtil.getFirstChildElement(element);
        if (element2 == null) {
            this.reportSchemaError("s4s-elt-must-match.2", new Object[]{"identity constraint", "(annotation?, selector, field+)"}, element);
            return false;
        }
        if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            identityConstraint.addAnnotation(this.traverseAnnotationDecl(element2, objectArray, false, xSDocumentInfo));
            element2 = DOMUtil.getNextSiblingElement(element2);
            if (element2 == null) {
                this.reportSchemaError("s4s-elt-must-match.2", new Object[]{"identity constraint", "(annotation?, selector, field+)"}, element);
                return false;
            }
        } else {
            objectArray2 = DOMUtil.getSyntheticAnnotation(element);
            if (objectArray2 != null) {
                identityConstraint.addAnnotation(this.traverseSyntheticAnnotation(element, (String)objectArray2, objectArray, false, xSDocumentInfo));
            }
        }
        if (!DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_SELECTOR)) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[]{"identity constraint", "(annotation?, selector, field+)", SchemaSymbols.ELT_SELECTOR}, element2);
            return false;
        }
        objectArray2 = this.fAttrChecker.checkAttributes(element2, false, xSDocumentInfo);
        Element element3 = DOMUtil.getFirstChildElement(element2);
        if (element3 != null) {
            if (DOMUtil.getLocalName(element3).equals(SchemaSymbols.ELT_ANNOTATION)) {
                identityConstraint.addAnnotation(this.traverseAnnotationDecl(element3, objectArray2, false, xSDocumentInfo));
                element3 = DOMUtil.getNextSiblingElement(element3);
            } else {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[]{SchemaSymbols.ELT_SELECTOR, "(annotation?)", DOMUtil.getLocalName(element3)}, element3);
            }
            if (element3 != null) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[]{SchemaSymbols.ELT_SELECTOR, "(annotation?)", DOMUtil.getLocalName(element3)}, element3);
            }
        } else {
            string = DOMUtil.getSyntheticAnnotation(element2);
            if (string != null) {
                identityConstraint.addAnnotation(this.traverseSyntheticAnnotation(element, string, objectArray2, false, xSDocumentInfo));
            }
        }
        if ((string = (String)objectArray2[XSAttributeChecker.ATTIDX_XPATH]) == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_SELECTOR, SchemaSymbols.ATT_XPATH}, element2);
            return false;
        }
        string = XMLChar.trim(string);
        Selector.XPath xPath = null;
        try {
            xPath = new Selector.XPath(string, this.fSymbolTable, xSDocumentInfo.fNamespaceSupport);
            object = new Selector(xPath, identityConstraint);
            identityConstraint.setSelector((Selector)object);
        }
        catch (XPathException xPathException) {
            this.reportSchemaError(xPathException.getKey(), new Object[]{string}, element2);
            this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
            return false;
        }
        this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
        object = DOMUtil.getNextSiblingElement(element2);
        if (object == null) {
            this.reportSchemaError("s4s-elt-must-match.2", new Object[]{"identity constraint", "(annotation?, selector, field+)"}, element2);
            return false;
        }
        while (object != null) {
            String string2;
            if (!DOMUtil.getLocalName((Node)object).equals(SchemaSymbols.ELT_FIELD)) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[]{"identity constraint", "(annotation?, selector, field+)", SchemaSymbols.ELT_FIELD}, (Element)object);
                object = DOMUtil.getNextSiblingElement((Node)object);
                continue;
            }
            objectArray2 = this.fAttrChecker.checkAttributes((Element)object, false, xSDocumentInfo);
            Element element4 = DOMUtil.getFirstChildElement((Node)object);
            if (element4 != null && DOMUtil.getLocalName(element4).equals(SchemaSymbols.ELT_ANNOTATION)) {
                identityConstraint.addAnnotation(this.traverseAnnotationDecl(element4, objectArray2, false, xSDocumentInfo));
                element4 = DOMUtil.getNextSiblingElement(element4);
            }
            if (element4 != null) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[]{SchemaSymbols.ELT_FIELD, "(annotation?)", DOMUtil.getLocalName(element4)}, element4);
            } else {
                string2 = DOMUtil.getSyntheticAnnotation((Node)object);
                if (string2 != null) {
                    identityConstraint.addAnnotation(this.traverseSyntheticAnnotation(element, string2, objectArray2, false, xSDocumentInfo));
                }
            }
            string2 = (String)objectArray2[XSAttributeChecker.ATTIDX_XPATH];
            if (string2 == null) {
                this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_FIELD, SchemaSymbols.ATT_XPATH}, (Element)object);
                this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
                return false;
            }
            string2 = XMLChar.trim(string2);
            try {
                Field.XPath xPath2 = new Field.XPath(string2, this.fSymbolTable, xSDocumentInfo.fNamespaceSupport);
                Field field = new Field(xPath2, identityConstraint);
                identityConstraint.addField(field);
            }
            catch (XPathException xPathException) {
                this.reportSchemaError(xPathException.getKey(), new Object[]{string2}, (Element)object);
                this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
                return false;
            }
            object = DOMUtil.getNextSiblingElement((Node)object);
            this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
        }
        return identityConstraint.getFieldCount() > 0;
    }
}

