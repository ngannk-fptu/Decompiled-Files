/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xni;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;

public interface XMLDocumentFragmentHandler {
    public void startDocumentFragment(XMLLocator var1, NamespaceContext var2, Augmentations var3) throws XNIException;

    public void startGeneralEntity(String var1, XMLResourceIdentifier var2, String var3, Augmentations var4) throws XNIException;

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

