/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import java.util.Stack;
import java.util.Vector;
import org.apache.xerces.impl.validation.ValidationState;
import org.apache.xerces.impl.xs.SchemaNamespaceSupport;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.impl.xs.opti.SchemaDOM;
import org.apache.xerces.impl.xs.traversers.XSAnnotationInfo;
import org.apache.xerces.impl.xs.traversers.XSAttributeChecker;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.util.SymbolTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class XSDocumentInfo {
    protected SchemaNamespaceSupport fNamespaceSupport;
    protected SchemaNamespaceSupport fNamespaceSupportRoot;
    protected Stack SchemaNamespaceSupportStack = new Stack();
    protected boolean fAreLocalAttributesQualified;
    protected boolean fAreLocalElementsQualified;
    protected short fBlockDefault;
    protected short fFinalDefault;
    String fTargetNamespace;
    protected boolean fIsChameleonSchema;
    protected Element fSchemaElement;
    Vector fImportedNS = new Vector();
    protected ValidationState fValidationContext = new ValidationState();
    SymbolTable fSymbolTable = null;
    protected XSAttributeChecker fAttrChecker;
    protected Object[] fSchemaAttrs;
    protected XSAnnotationInfo fAnnotations = null;
    private Vector fReportedTNS = null;

    XSDocumentInfo(Element element, XSAttributeChecker xSAttributeChecker, SymbolTable symbolTable) throws XMLSchemaException {
        this.fSchemaElement = element;
        this.fNamespaceSupport = new SchemaNamespaceSupport(element, symbolTable);
        this.fNamespaceSupport.reset();
        this.fIsChameleonSchema = false;
        this.fSymbolTable = symbolTable;
        this.fAttrChecker = xSAttributeChecker;
        if (element != null) {
            Element element2 = element;
            this.fSchemaAttrs = xSAttributeChecker.checkAttributes(element2, true, this);
            if (this.fSchemaAttrs == null) {
                throw new XMLSchemaException(null, null);
            }
            this.fAreLocalAttributesQualified = ((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_AFORMDEFAULT]).intValue() == 1;
            this.fAreLocalElementsQualified = ((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_EFORMDEFAULT]).intValue() == 1;
            this.fBlockDefault = ((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_BLOCKDEFAULT]).shortValue();
            this.fFinalDefault = ((XInt)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_FINALDEFAULT]).shortValue();
            this.fTargetNamespace = (String)this.fSchemaAttrs[XSAttributeChecker.ATTIDX_TARGETNAMESPACE];
            if (this.fTargetNamespace != null) {
                this.fTargetNamespace = symbolTable.addSymbol(this.fTargetNamespace);
            }
            this.fNamespaceSupportRoot = new SchemaNamespaceSupport(this.fNamespaceSupport);
            this.fValidationContext.setNamespaceSupport(this.fNamespaceSupport);
            this.fValidationContext.setSymbolTable(symbolTable);
        }
    }

    void backupNSSupport(SchemaNamespaceSupport schemaNamespaceSupport) {
        this.SchemaNamespaceSupportStack.push(this.fNamespaceSupport);
        if (schemaNamespaceSupport == null) {
            schemaNamespaceSupport = this.fNamespaceSupportRoot;
        }
        this.fNamespaceSupport = new SchemaNamespaceSupport(schemaNamespaceSupport);
        this.fValidationContext.setNamespaceSupport(this.fNamespaceSupport);
    }

    void restoreNSSupport() {
        this.fNamespaceSupport = (SchemaNamespaceSupport)this.SchemaNamespaceSupportStack.pop();
        this.fValidationContext.setNamespaceSupport(this.fNamespaceSupport);
    }

    public String toString() {
        String string;
        Document document;
        StringBuffer stringBuffer = new StringBuffer();
        if (this.fTargetNamespace == null) {
            stringBuffer.append("no targetNamspace");
        } else {
            stringBuffer.append("targetNamespace is ");
            stringBuffer.append(this.fTargetNamespace);
        }
        Document document2 = document = this.fSchemaElement != null ? this.fSchemaElement.getOwnerDocument() : null;
        if (document instanceof SchemaDOM && (string = document.getDocumentURI()) != null && string.length() > 0) {
            stringBuffer.append(" :: schemaLocation is ");
            stringBuffer.append(string);
        }
        return stringBuffer.toString();
    }

    public void addAllowedNS(String string) {
        this.fImportedNS.addElement(string == null ? "" : string);
    }

    public boolean isAllowedNS(String string) {
        return this.fImportedNS.contains(string == null ? "" : string);
    }

    final boolean needReportTNSError(String string) {
        if (this.fReportedTNS == null) {
            this.fReportedTNS = new Vector();
        } else if (this.fReportedTNS.contains(string)) {
            return false;
        }
        this.fReportedTNS.addElement(string);
        return true;
    }

    Object[] getSchemaAttrs() {
        return this.fSchemaAttrs;
    }

    void returnSchemaAttrs() {
        this.fAttrChecker.returnAttrArray(this.fSchemaAttrs, null);
        this.fSchemaAttrs = null;
    }

    void addAnnotation(XSAnnotationInfo xSAnnotationInfo) {
        xSAnnotationInfo.next = this.fAnnotations;
        this.fAnnotations = xSAnnotationInfo;
    }

    XSAnnotationInfo getAnnotations() {
        return this.fAnnotations;
    }

    void removeAnnotations() {
        this.fAnnotations = null;
    }
}

