/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.identity.KeyRef;
import org.apache.xerces.impl.xs.identity.UniqueOrKey;
import org.apache.xerces.impl.xs.traversers.XSAttributeChecker;
import org.apache.xerces.impl.xs.traversers.XSDAbstractIDConstraintTraverser;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.impl.xs.traversers.XSDocumentInfo;
import org.apache.xerces.xni.QName;
import org.w3c.dom.Element;

class XSDKeyrefTraverser
extends XSDAbstractIDConstraintTraverser {
    public XSDKeyrefTraverser(XSDHandler xSDHandler, XSAttributeChecker xSAttributeChecker) {
        super(xSDHandler, xSAttributeChecker);
    }

    void traverse(Element element, XSElementDecl xSElementDecl, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        Object[] objectArray = this.fAttrChecker.checkAttributes(element, false, xSDocumentInfo);
        String string = (String)objectArray[XSAttributeChecker.ATTIDX_NAME];
        if (string == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_KEYREF, SchemaSymbols.ATT_NAME}, element);
            this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
            return;
        }
        QName qName = (QName)objectArray[XSAttributeChecker.ATTIDX_REFER];
        if (qName == null) {
            this.reportSchemaError("s4s-att-must-appear", new Object[]{SchemaSymbols.ELT_KEYREF, SchemaSymbols.ATT_REFER}, element);
            this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
            return;
        }
        UniqueOrKey uniqueOrKey = null;
        IdentityConstraint identityConstraint = (IdentityConstraint)this.fSchemaHandler.getGlobalDecl(xSDocumentInfo, 5, qName, element);
        if (identityConstraint != null) {
            if (identityConstraint.getCategory() == 1 || identityConstraint.getCategory() == 3) {
                uniqueOrKey = (UniqueOrKey)identityConstraint;
            } else {
                this.reportSchemaError("src-resolve", new Object[]{qName.rawname, "identity constraint key/unique"}, element);
            }
        }
        if (uniqueOrKey == null) {
            this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
            return;
        }
        KeyRef keyRef = new KeyRef(xSDocumentInfo.fTargetNamespace, string, xSElementDecl.fName, uniqueOrKey);
        if (this.traverseIdentityConstraint(keyRef, element, xSDocumentInfo, objectArray)) {
            if (uniqueOrKey.getFieldCount() != keyRef.getFieldCount()) {
                this.reportSchemaError("c-props-correct.2", new Object[]{string, uniqueOrKey.getIdentityConstraintName()}, element);
            } else {
                if (schemaGrammar.getIDConstraintDecl(keyRef.getIdentityConstraintName()) == null) {
                    schemaGrammar.addIDConstraintDecl(xSElementDecl, keyRef);
                }
                String string2 = this.fSchemaHandler.schemaDocument2SystemId(xSDocumentInfo);
                IdentityConstraint identityConstraint2 = schemaGrammar.getIDConstraintDecl(keyRef.getIdentityConstraintName(), string2);
                if (identityConstraint2 == null) {
                    schemaGrammar.addIDConstraintDecl(xSElementDecl, keyRef, string2);
                }
                if (this.fSchemaHandler.fTolerateDuplicates) {
                    if (identityConstraint2 != null && identityConstraint2 instanceof KeyRef) {
                        keyRef = (KeyRef)identityConstraint2;
                    }
                    this.fSchemaHandler.addIDConstraintDecl(keyRef);
                }
            }
        }
        this.fAttrChecker.returnAttrArray(objectArray, xSDocumentInfo);
    }
}

