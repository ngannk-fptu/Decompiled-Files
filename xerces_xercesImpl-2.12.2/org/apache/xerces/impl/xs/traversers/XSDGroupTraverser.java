/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.impl.xs.XSGroupDecl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.traversers.XSAttributeChecker;
import org.apache.xerces.impl.xs.traversers.XSDAbstractParticleTraverser;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.impl.xs.traversers.XSDocumentInfo;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.QName;
import org.w3c.dom.Element;

class XSDGroupTraverser
extends XSDAbstractParticleTraverser {
    XSDGroupTraverser(XSDHandler xSDHandler, XSAttributeChecker xSAttributeChecker) {
        super(xSDHandler, xSAttributeChecker);
    }

    XSParticleDecl traverseLocal(Element element, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        Object[] objectArray = this.fAttrChecker.checkAttributes(element, false, xSDocumentInfo);
        QName qName = (QName)objectArray[XSAttributeChecker.ATTIDX_REF];
        XInt xInt = (XInt)objectArray[XSAttributeChecker.ATTIDX_MINOCCURS];
        XInt xInt2 = (XInt)objectArray[XSAttributeChecker.ATTIDX_MAXOCCURS];
        XSGroupDecl xSGroupDecl = null;
        if (qName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[]{"group (local)", "ref"}, element);
        } else {
            xSGroupDecl = (XSGroupDecl)this.fSchemaHandler.getGlobalDecl(xSDocumentInfo, 4, qName, element);
        }
        XSAnnotationImpl xSAnnotationImpl = null;
        Element element2 = DOMUtil.getFirstChildElement(element);
        if (element2 != null && DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
            xSAnnotationImpl = this.traverseAnnotationDecl(element2, objectArray, false, xSDocumentInfo);
            element2 = DOMUtil.getNextSiblingElement(element2);
        } else {
            String string = DOMUtil.getSyntheticAnnotation(element);
            if (string != null) {
                xSAnnotationImpl = this.traverseSyntheticAnnotation(element, string, objectArray, false, xSDocumentInfo);
            }
        }
        if (element2 != null) {
            this.reportSchemaError("s4s-elt-must-match.1", new Object[]{"group (local)", "(annotation?)", DOMUtil.getLocalName(element)}, element);
        }
        int n = xInt.intValue();
        int n2 = xInt2.intValue();
        XSParticleDecl xSParticleDecl = null;
        if (xSGroupDecl != null && xSGroupDecl.fModelGroup != null && (n != 0 || n2 != 0)) {
            Object object;
            xSParticleDecl = this.fSchemaHandler.fDeclPool != null ? this.fSchemaHandler.fDeclPool.getParticleDecl() : new XSParticleDecl();
            xSParticleDecl.fType = (short)3;
            xSParticleDecl.fValue = xSGroupDecl.fModelGroup;
            xSParticleDecl.fMinOccurs = n;
            xSParticleDecl.fMaxOccurs = n2;
            if (xSGroupDecl.fModelGroup.fCompositor == 103) {
                object = (Long)objectArray[XSAttributeChecker.ATTIDX_FROMDEFAULT];
                xSParticleDecl = this.checkOccurrences(xSParticleDecl, SchemaSymbols.ELT_GROUP, (Element)element.getParentNode(), 2, (Long)object);
            }
            if (qName != null) {
                if (xSAnnotationImpl != null) {
                    object = new XSObjectListImpl();
                    ((XSObjectListImpl)object).addXSObject(xSAnnotationImpl);
                } else {
                    object = XSObjectListImpl.EMPTY_LIST;
                }
                xSParticleDecl.fAnnotations = object;
            } else {
                xSParticleDecl.fAnnotations = xSGroupDecl.fAnnotations;
            }
        }
        this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
        return xSParticleDecl;
    }

    XSGroupDecl traverseGlobal(Element element, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        String string;
        Object object;
        Object[] objectArray = this.fAttrChecker.checkAttributes(element, true, xSDocumentInfo);
        String string2 = (String)objectArray[XSAttributeChecker.ATTIDX_NAME];
        if (string2 == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[]{"group (global)", "name"}, element);
        }
        XSGroupDecl xSGroupDecl = new XSGroupDecl();
        XSParticleDecl xSParticleDecl = null;
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSAnnotationImpl xSAnnotationImpl = null;
        if (element2 == null) {
            this.reportSchemaError("s4s-elt-must-match.2", new Object[]{"group (global)", "(annotation?, (all | choice | sequence))"}, element);
        } else {
            object = element2.getLocalName();
            if (((String)object).equals(SchemaSymbols.ELT_ANNOTATION)) {
                xSAnnotationImpl = this.traverseAnnotationDecl(element2, objectArray, true, xSDocumentInfo);
                if ((element2 = DOMUtil.getNextSiblingElement(element2)) != null) {
                    object = element2.getLocalName();
                }
            } else {
                string = DOMUtil.getSyntheticAnnotation(element);
                if (string != null) {
                    xSAnnotationImpl = this.traverseSyntheticAnnotation(element, string, objectArray, false, xSDocumentInfo);
                }
            }
            if (element2 == null) {
                this.reportSchemaError("s4s-elt-must-match.2", new Object[]{"group (global)", "(annotation?, (all | choice | sequence))"}, element);
            } else if (((String)object).equals(SchemaSymbols.ELT_ALL)) {
                xSParticleDecl = this.traverseAll(element2, xSDocumentInfo, schemaGrammar, 4, xSGroupDecl);
            } else if (((String)object).equals(SchemaSymbols.ELT_CHOICE)) {
                xSParticleDecl = this.traverseChoice(element2, xSDocumentInfo, schemaGrammar, 4, xSGroupDecl);
            } else if (((String)object).equals(SchemaSymbols.ELT_SEQUENCE)) {
                xSParticleDecl = this.traverseSequence(element2, xSDocumentInfo, schemaGrammar, 4, xSGroupDecl);
            } else {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[]{"group (global)", "(annotation?, (all | choice | sequence))", DOMUtil.getLocalName(element2)}, element2);
            }
            if (element2 != null && DOMUtil.getNextSiblingElement(element2) != null) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[]{"group (global)", "(annotation?, (all | choice | sequence))", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(element2))}, DOMUtil.getNextSiblingElement(element2));
            }
        }
        if (string2 != null) {
            XSGroupDecl xSGroupDecl2;
            xSGroupDecl.fName = string2;
            xSGroupDecl.fTargetNamespace = xSDocumentInfo.fTargetNamespace;
            if (xSParticleDecl == null) {
                xSParticleDecl = XSConstraints.getEmptySequence();
            }
            xSGroupDecl.fModelGroup = (XSModelGroupImpl)xSParticleDecl.fValue;
            if (xSAnnotationImpl != null) {
                object = new XSObjectListImpl();
                ((XSObjectListImpl)object).addXSObject(xSAnnotationImpl);
            } else {
                object = XSObjectListImpl.EMPTY_LIST;
            }
            xSGroupDecl.fAnnotations = object;
            if (schemaGrammar.getGlobalGroupDecl(xSGroupDecl.fName) == null) {
                schemaGrammar.addGlobalGroupDecl(xSGroupDecl);
            }
            if ((xSGroupDecl2 = schemaGrammar.getGlobalGroupDecl(xSGroupDecl.fName, string = this.fSchemaHandler.schemaDocument2SystemId(xSDocumentInfo))) == null) {
                schemaGrammar.addGlobalGroupDecl(xSGroupDecl, string);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (xSGroupDecl2 != null) {
                    xSGroupDecl = xSGroupDecl2;
                }
                this.fSchemaHandler.addGlobalGroupDecl(xSGroupDecl);
            }
        } else {
            xSGroupDecl = null;
        }
        if (xSGroupDecl != null && (object = this.fSchemaHandler.getGrpOrAttrGrpRedefinedByRestriction(4, new QName(XMLSymbols.EMPTY_STRING, string2, string2, xSDocumentInfo.fTargetNamespace), xSDocumentInfo, element)) != null) {
            schemaGrammar.addRedefinedGroupDecl(xSGroupDecl, (XSGroupDecl)object, this.fSchemaHandler.element2Locator(element));
        }
        this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
        return xSGroupDecl;
    }
}

