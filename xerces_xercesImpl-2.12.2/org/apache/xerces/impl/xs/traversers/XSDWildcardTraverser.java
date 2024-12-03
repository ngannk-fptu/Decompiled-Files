/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.traversers.XSAttributeChecker;
import org.apache.xerces.impl.xs.traversers.XSDAbstractTraverser;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.impl.xs.traversers.XSDocumentInfo;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.util.XSObjectListImpl;
import org.apache.xerces.util.DOMUtil;
import org.w3c.dom.Element;

class XSDWildcardTraverser
extends XSDAbstractTraverser {
    XSDWildcardTraverser(XSDHandler xSDHandler, XSAttributeChecker xSAttributeChecker) {
        super(xSDHandler, xSAttributeChecker);
    }

    XSParticleDecl traverseAny(Element element, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        Object[] objectArray = this.fAttrChecker.checkAttributes(element, false, xSDocumentInfo);
        XSWildcardDecl xSWildcardDecl = this.traverseWildcardDecl(element, objectArray, xSDocumentInfo, schemaGrammar);
        XSParticleDecl xSParticleDecl = null;
        if (xSWildcardDecl != null) {
            int n = ((XInt)objectArray[XSAttributeChecker.ATTIDX_MINOCCURS]).intValue();
            int n2 = ((XInt)objectArray[XSAttributeChecker.ATTIDX_MAXOCCURS]).intValue();
            if (n2 != 0) {
                xSParticleDecl = this.fSchemaHandler.fDeclPool != null ? this.fSchemaHandler.fDeclPool.getParticleDecl() : new XSParticleDecl();
                xSParticleDecl.fType = (short)2;
                xSParticleDecl.fValue = xSWildcardDecl;
                xSParticleDecl.fMinOccurs = n;
                xSParticleDecl.fMaxOccurs = n2;
                xSParticleDecl.fAnnotations = xSWildcardDecl.fAnnotations;
            }
        }
        this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
        return xSParticleDecl;
    }

    XSWildcardDecl traverseAnyAttribute(Element element, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        Object[] objectArray = this.fAttrChecker.checkAttributes(element, false, xSDocumentInfo);
        XSWildcardDecl xSWildcardDecl = this.traverseWildcardDecl(element, objectArray, xSDocumentInfo, schemaGrammar);
        this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
        return xSWildcardDecl;
    }

    XSWildcardDecl traverseWildcardDecl(Element element, Object[] objectArray, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        Object object;
        XSWildcardDecl xSWildcardDecl = new XSWildcardDecl();
        XInt xInt = (XInt)objectArray[XSAttributeChecker.ATTIDX_NAMESPACE];
        xSWildcardDecl.fType = xInt.shortValue();
        xSWildcardDecl.fNamespaceList = (String[])objectArray[XSAttributeChecker.ATTIDX_NAMESPACE_LIST];
        XInt xInt2 = (XInt)objectArray[XSAttributeChecker.ATTIDX_PROCESSCONTENTS];
        xSWildcardDecl.fProcessContents = xInt2.shortValue();
        Element element2 = DOMUtil.getFirstChildElement(element);
        XSAnnotationImpl xSAnnotationImpl = null;
        if (element2 != null) {
            if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                xSAnnotationImpl = this.traverseAnnotationDecl(element2, objectArray, false, xSDocumentInfo);
                element2 = DOMUtil.getNextSiblingElement(element2);
            } else {
                object = DOMUtil.getSyntheticAnnotation(element);
                if (object != null) {
                    xSAnnotationImpl = this.traverseSyntheticAnnotation(element, (String)object, objectArray, false, xSDocumentInfo);
                }
            }
            if (element2 != null) {
                this.reportSchemaError("s4s-elt-must-match.1", new Object[]{"wildcard", "(annotation?)", DOMUtil.getLocalName(element2)}, element);
            }
        } else {
            object = DOMUtil.getSyntheticAnnotation(element);
            if (object != null) {
                xSAnnotationImpl = this.traverseSyntheticAnnotation(element, (String)object, objectArray, false, xSDocumentInfo);
            }
        }
        if (xSAnnotationImpl != null) {
            object = new XSObjectListImpl();
            ((XSObjectListImpl)object).addXSObject(xSAnnotationImpl);
        } else {
            object = XSObjectListImpl.EMPTY_LIST;
        }
        xSWildcardDecl.fAnnotations = object;
        return xSWildcardDecl;
    }
}

