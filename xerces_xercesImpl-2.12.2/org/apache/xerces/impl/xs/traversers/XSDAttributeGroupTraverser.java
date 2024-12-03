/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.XSAttributeGroupDecl;
import org.apache.xerces.impl.xs.traversers.XSAttributeChecker;
import org.apache.xerces.impl.xs.traversers.XSDAbstractTraverser;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.impl.xs.traversers.XSDocumentInfo;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.QName;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class XSDAttributeGroupTraverser
extends XSDAbstractTraverser {
    XSDAttributeGroupTraverser(XSDHandler xSDHandler, XSAttributeChecker xSAttributeChecker) {
        super(xSDHandler, xSAttributeChecker);
    }

    XSAttributeGroupDecl traverseLocal(Element element, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        Object[] objectArray = this.fAttrChecker.checkAttributes(element, false, xSDocumentInfo);
        QName qName = (QName)objectArray[XSAttributeChecker.ATTIDX_REF];
        XSAttributeGroupDecl xSAttributeGroupDecl = null;
        if (qName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[]{"attributeGroup (local)", "ref"}, element);
            this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
            return null;
        }
        xSAttributeGroupDecl = (XSAttributeGroupDecl)this.fSchemaHandler.getGlobalDecl(xSDocumentInfo, 2, qName, element);
        Element element2 = DOMUtil.getFirstChildElement(element);
        if (element2 != null) {
            Object[] objectArray2;
            String string = DOMUtil.getLocalName(element2);
            if (string.equals(SchemaSymbols.ELT_ANNOTATION)) {
                this.traverseAnnotationDecl(element2, objectArray, false, xSDocumentInfo);
                element2 = DOMUtil.getNextSiblingElement(element2);
            } else {
                objectArray2 = DOMUtil.getSyntheticAnnotation(element2);
                if (objectArray2 != null) {
                    this.traverseSyntheticAnnotation(element2, (String)objectArray2, objectArray, false, xSDocumentInfo);
                }
            }
            if (element2 != null) {
                objectArray2 = new Object[]{qName.rawname, "(annotation?)", DOMUtil.getLocalName(element2)};
                this.reportSchemaError("s4s-elt-must-match.1", objectArray2, element2);
            }
        }
        this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
        return xSAttributeGroupDecl;
    }

    XSAttributeGroupDecl traverseGlobal(Element element, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        String string;
        XSAttributeGroupDecl xSAttributeGroupDecl;
        Object[] objectArray;
        Object object;
        Object object2;
        XSAttributeGroupDecl xSAttributeGroupDecl2 = new XSAttributeGroupDecl();
        Object[] objectArray2 = this.fAttrChecker.checkAttributes(element, true, xSDocumentInfo);
        String string2 = (String)objectArray2[XSAttributeChecker.ATTIDX_NAME];
        if (string2 == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[]{"attributeGroup (global)", "name"}, element);
            string2 = "(no name)";
        }
        xSAttributeGroupDecl2.fName = string2;
        xSAttributeGroupDecl2.fTargetNamespace = xSDocumentInfo.fTargetNamespace;
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSAnnotationImpl xSAnnotationImpl = null;
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            xSAnnotationImpl = this.traverseAnnotationDecl(element2, objectArray2, false, xSDocumentInfo);
            element2 = DOMUtil.getNextSiblingElement(element2);
        } else {
            object2 = DOMUtil.getSyntheticAnnotation(element);
            if (object2 != null) {
                xSAnnotationImpl = this.traverseSyntheticAnnotation(element, (String)object2, objectArray2, false, xSDocumentInfo);
            }
        }
        object2 = this.traverseAttrsAndAttrGrps(element2, xSAttributeGroupDecl2, xSDocumentInfo, schemaGrammar, null);
        if (object2 != null) {
            object = new Object[]{string2, "(annotation?, ((attribute | attributeGroup)*, anyAttribute?))", DOMUtil.getLocalName((Node)object2)};
            this.reportSchemaError("s4s-elt-must-match.1", (Object[])object, (Element)object2);
        }
        if (string2.equals("(no name)")) {
            this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
            return null;
        }
        xSAttributeGroupDecl2.removeProhibitedAttrs();
        object = (XSAttributeGroupDecl)this.fSchemaHandler.getGrpOrAttrGrpRedefinedByRestriction(2, new QName(XMLSymbols.EMPTY_STRING, string2, string2, xSDocumentInfo.fTargetNamespace), xSDocumentInfo, element);
        if (object != null && (objectArray = xSAttributeGroupDecl2.validRestrictionOf(string2, (XSAttributeGroupDecl)object)) != null) {
            this.reportSchemaError((String)objectArray[objectArray.length - 1], objectArray, element2);
            this.reportSchemaError("src-redefine.7.2.2", new Object[]{string2, objectArray[objectArray.length - 1]}, element2);
        }
        if (xSAnnotationImpl != null) {
            objectArray = new XSObjectListImpl();
            ((XSObjectListImpl)objectArray).addXSObject(xSAnnotationImpl);
        } else {
            objectArray = XSObjectListImpl.EMPTY_LIST;
        }
        xSAttributeGroupDecl2.fAnnotations = objectArray;
        if (schemaGrammar.getGlobalAttributeGroupDecl(xSAttributeGroupDecl2.fName) == null) {
            schemaGrammar.addGlobalAttributeGroupDecl(xSAttributeGroupDecl2);
        }
        if ((xSAttributeGroupDecl = schemaGrammar.getGlobalAttributeGroupDecl(xSAttributeGroupDecl2.fName, string = this.fSchemaHandler.schemaDocument2SystemId(xSDocumentInfo))) == null) {
            schemaGrammar.addGlobalAttributeGroupDecl(xSAttributeGroupDecl2, string);
        }
        if (this.fSchemaHandler.fTolerateDuplicates) {
            if (xSAttributeGroupDecl != null) {
                xSAttributeGroupDecl2 = xSAttributeGroupDecl;
            }
            this.fSchemaHandler.addGlobalAttributeGroupDecl(xSAttributeGroupDecl2);
        }
        this.fAttrChecker.returnAttrArray(objectArray2, xSDocumentInfo);
        return xSAttributeGroupDecl2;
    }
}

