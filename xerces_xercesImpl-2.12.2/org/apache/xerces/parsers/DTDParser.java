/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.parsers.XMLGrammarParser;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDTDScanner;

public abstract class DTDParser
extends XMLGrammarParser
implements XMLDTDHandler,
XMLDTDContentModelHandler {
    protected XMLDTDScanner fDTDScanner;

    public DTDParser(SymbolTable symbolTable) {
        super(symbolTable);
    }

    public DTDGrammar getDTDGrammar() {
        return null;
    }

    public void startEntity(String string, String string2, String string3, String string4) throws XNIException {
    }

    public void textDecl(String string, String string2) throws XNIException {
    }

    @Override
    public void startDTD(XMLLocator xMLLocator, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startExternalSubset(XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endExternalSubset(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void elementDecl(String string, String string2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startAttlist(String string, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void attributeDecl(String string, String string2, String string3, String[] stringArray, String string4, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endAttlist(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void internalEntityDecl(String string, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void externalEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void unparsedEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void notationDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startConditional(short s, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endConditional(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endDTD(Augmentations augmentations) throws XNIException {
    }

    public void endEntity(String string, Augmentations augmentations) throws XNIException {
    }

    public void startContentModel(String string, short s) throws XNIException {
    }

    public void mixedElement(String string) throws XNIException {
    }

    public void childrenStartGroup() throws XNIException {
    }

    public void childrenElement(String string) throws XNIException {
    }

    public void childrenSeparator(short s) throws XNIException {
    }

    public void childrenOccurrence(short s) throws XNIException {
    }

    public void childrenEndGroup() throws XNIException {
    }

    public void endContentModel() throws XNIException {
    }
}

