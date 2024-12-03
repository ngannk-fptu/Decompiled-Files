/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp;

import java.util.HashMap;
import org.apache.xerces.impl.validation.EntityState;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDTDFilter;
import org.apache.xerces.xni.parser.XMLDTDSource;

final class UnparsedEntityHandler
implements XMLDTDFilter,
EntityState {
    private XMLDTDSource fDTDSource;
    private XMLDTDHandler fDTDHandler;
    private final ValidationManager fValidationManager;
    private HashMap fUnparsedEntities = null;

    UnparsedEntityHandler(ValidationManager validationManager) {
        this.fValidationManager = validationManager;
    }

    @Override
    public void startDTD(XMLLocator xMLLocator, Augmentations augmentations) throws XNIException {
        this.fValidationManager.setEntityState(this);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startDTD(xMLLocator, augmentations);
        }
    }

    @Override
    public void startParameterEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startParameterEntity(string, xMLResourceIdentifier, string2, augmentations);
        }
    }

    @Override
    public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.textDecl(string, string2, augmentations);
        }
    }

    @Override
    public void endParameterEntity(String string, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endParameterEntity(string, augmentations);
        }
    }

    @Override
    public void startExternalSubset(XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startExternalSubset(xMLResourceIdentifier, augmentations);
        }
    }

    @Override
    public void endExternalSubset(Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endExternalSubset(augmentations);
        }
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.comment(xMLString, augmentations);
        }
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.processingInstruction(string, xMLString, augmentations);
        }
    }

    @Override
    public void elementDecl(String string, String string2, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.elementDecl(string, string2, augmentations);
        }
    }

    @Override
    public void startAttlist(String string, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startAttlist(string, augmentations);
        }
    }

    @Override
    public void attributeDecl(String string, String string2, String string3, String[] stringArray, String string4, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.attributeDecl(string, string2, string3, stringArray, string4, xMLString, xMLString2, augmentations);
        }
    }

    @Override
    public void endAttlist(Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endAttlist(augmentations);
        }
    }

    @Override
    public void internalEntityDecl(String string, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.internalEntityDecl(string, xMLString, xMLString2, augmentations);
        }
    }

    @Override
    public void externalEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.externalEntityDecl(string, xMLResourceIdentifier, augmentations);
        }
    }

    @Override
    public void unparsedEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        if (this.fUnparsedEntities == null) {
            this.fUnparsedEntities = new HashMap();
        }
        this.fUnparsedEntities.put(string, string);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.unparsedEntityDecl(string, xMLResourceIdentifier, string2, augmentations);
        }
    }

    @Override
    public void notationDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.notationDecl(string, xMLResourceIdentifier, augmentations);
        }
    }

    @Override
    public void startConditional(short s, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startConditional(s, augmentations);
        }
    }

    @Override
    public void ignoredCharacters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.ignoredCharacters(xMLString, augmentations);
        }
    }

    @Override
    public void endConditional(Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endConditional(augmentations);
        }
    }

    @Override
    public void endDTD(Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endDTD(augmentations);
        }
    }

    @Override
    public void setDTDSource(XMLDTDSource xMLDTDSource) {
        this.fDTDSource = xMLDTDSource;
    }

    @Override
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }

    @Override
    public void setDTDHandler(XMLDTDHandler xMLDTDHandler) {
        this.fDTDHandler = xMLDTDHandler;
    }

    @Override
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }

    @Override
    public boolean isEntityDeclared(String string) {
        return false;
    }

    @Override
    public boolean isEntityUnparsed(String string) {
        if (this.fUnparsedEntities != null) {
            return this.fUnparsedEntities.containsKey(string);
        }
        return false;
    }

    public void reset() {
        if (this.fUnparsedEntities != null && !this.fUnparsedEntities.isEmpty()) {
            this.fUnparsedEntities.clear();
        }
    }
}

