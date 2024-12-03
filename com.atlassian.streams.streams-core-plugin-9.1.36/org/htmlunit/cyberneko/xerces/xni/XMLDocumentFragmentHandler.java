/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.xni;

import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.NamespaceContext;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;
import org.htmlunit.cyberneko.xerces.xni.XMLLocator;
import org.htmlunit.cyberneko.xerces.xni.XMLString;
import org.htmlunit.cyberneko.xerces.xni.XNIException;

public interface XMLDocumentFragmentHandler {
    public void startDocumentFragment(XMLLocator var1, NamespaceContext var2, Augmentations var3) throws XNIException;

    public void startGeneralEntity(String var1, String var2, Augmentations var3) throws XNIException;

    public void textDecl(String var1, String var2, Augmentations var3) throws XNIException;

    public void endGeneralEntity(String var1, Augmentations var2) throws XNIException;

    public void comment(XMLString var1, Augmentations var2) throws XNIException;

    public void processingInstruction(String var1, XMLString var2, Augmentations var3) throws XNIException;

    public void startElement(QName var1, XMLAttributes var2, Augmentations var3) throws XNIException;

    public void emptyElement(QName var1, XMLAttributes var2, Augmentations var3) throws XNIException;

    public void characters(XMLString var1, Augmentations var2) throws XNIException;

    public void ignorableWhitespace(XMLString var1, Augmentations var2) throws XNIException;

    public void endElement(QName var1, Augmentations var2) throws XNIException;

    public void startCDATA(Augmentations var1) throws XNIException;

    public void endCDATA(Augmentations var1) throws XNIException;

    public void endDocumentFragment(Augmentations var1) throws XNIException;
}

