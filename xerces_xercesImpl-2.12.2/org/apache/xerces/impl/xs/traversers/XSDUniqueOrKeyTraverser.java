/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.identity.UniqueOrKey;
import org.apache.xerces.impl.xs.traversers.XSAttributeChecker;
import org.apache.xerces.impl.xs.traversers.XSDAbstractIDConstraintTraverser;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.impl.xs.traversers.XSDocumentInfo;
import org.apache.xerces.util.DOMUtil;
import org.w3c.dom.Element;

class XSDUniqueOrKeyTraverser
extends XSDAbstractIDConstraintTraverser {
    public XSDUniqueOrKeyTraverser(XSDHandler xSDHandler, XSAttributeChecker xSAttributeChecker) {
        super(xSDHandler, xSAttributeChecker);
    }

    void traverse(Element element, XSElementDecl xSElementDecl, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        Object[] objectArray = this.fAttrChecker.checkAttributes(element, false, xSDocumentInfo);
        String string = (String)objectArray[XSAttributeChecker.ATTIDX_NAME];
        if (string == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[]{DOMUtil.getLocalName(element), SchemaSymbols.ATT_NAME}, element);
            this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
            return;
        }
        UniqueOrKey uniqueOrKey = null;
        uniqueOrKey = DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_UNIQUE) ? new UniqueOrKey(xSDocumentInfo.fTargetNamespace, string, xSElementDecl.fName, 3) : new UniqueOrKey(xSDocumentInfo.fTargetNamespace, string, xSElementDecl.fName, 1);
        if (this.traverseIdentityConstraint(uniqueOrKey, element, xSDocumentInfo, objectArray)) {
            if (schemaGrammar.getIDConstraintDecl(uniqueOrKey.getIdentityConstraintName()) == null) {
                schemaGrammar.addIDConstraintDecl(xSElementDecl, uniqueOrKey);
            }
            String string2 = this.fSchemaHandler.schemaDocument2SystemId(xSDocumentInfo);
            IdentityConstraint identityConstraint = schemaGrammar.getIDConstraintDecl(uniqueOrKey.getIdentityConstraintName(), string2);
            if (identityConstraint == null) {
                schemaGrammar.addIDConstraintDecl(xSElementDecl, uniqueOrKey, string2);
            }
            if (this.fSchemaHandler.fTolerateDuplicates) {
                if (identityConstraint == null || identityConstraint instanceof UniqueOrKey) {
                    // empty if block
                }
                this.fSchemaHandler.addIDConstraintDecl(uniqueOrKey);
            }
        }
        this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
    }
}

