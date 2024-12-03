/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.NamespaceContext
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XMLDocumentHandler
 *  org.apache.xerces.xni.XMLLocator
 *  org.apache.xerces.xni.XMLResourceIdentifier
 *  org.apache.xerces.xni.XMLString
 *  org.apache.xerces.xni.XNIException
 *  org.apache.xerces.xni.parser.XMLComponentManager
 *  org.apache.xerces.xni.parser.XMLConfigurationException
 *  org.apache.xerces.xni.parser.XMLDocumentFilter
 *  org.apache.xerces.xni.parser.XMLDocumentSource
 */
package org.cyberneko.html.filters;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.cyberneko.html.HTMLComponent;
import org.cyberneko.html.xercesbridge.XercesBridge;

public class DefaultFilter
implements XMLDocumentFilter,
HTMLComponent {
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;

    public void setDocumentHandler(XMLDocumentHandler handler) {
        this.fDocumentHandler = handler;
    }

    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }

    public void setDocumentSource(XMLDocumentSource source) {
        this.fDocumentSource = source;
    }

    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }

    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            XercesBridge.getInstance().XMLDocumentHandler_startDocument(this.fDocumentHandler, locator, encoding, nscontext, augs);
        }
    }

    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(version, encoding, standalone, augs);
        }
    }

    public void doctypeDecl(String root, String publicId, String systemId, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.doctypeDecl(root, publicId, systemId, augs);
        }
    }

    public void comment(XMLString text, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.comment(text, augs);
        }
    }

    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(target, data, augs);
        }
    }

    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startElement(element, attributes, augs);
        }
    }

    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.emptyElement(element, attributes, augs);
        }
    }

    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.characters(text, augs);
        }
    }

    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.ignorableWhitespace(text, augs);
        }
    }

    public void startGeneralEntity(String name, XMLResourceIdentifier id, String encoding, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startGeneralEntity(name, id, encoding, augs);
        }
    }

    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.textDecl(version, encoding, augs);
        }
    }

    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endGeneralEntity(name, augs);
        }
    }

    public void startCDATA(Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startCDATA(augs);
        }
    }

    public void endCDATA(Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endCDATA(augs);
        }
    }

    public void endElement(QName element, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endElement(element, augs);
        }
    }

    public void endDocument(Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endDocument(augs);
        }
    }

    public void startDocument(XMLLocator locator, String encoding, Augmentations augs) throws XNIException {
        this.startDocument(locator, encoding, null, augs);
    }

    public void startPrefixMapping(String prefix, String uri, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            XercesBridge.getInstance().XMLDocumentHandler_startPrefixMapping(this.fDocumentHandler, prefix, uri, augs);
        }
    }

    public void endPrefixMapping(String prefix, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler != null) {
            XercesBridge.getInstance().XMLDocumentHandler_endPrefixMapping(this.fDocumentHandler, prefix, augs);
        }
    }

    public String[] getRecognizedFeatures() {
        return null;
    }

    @Override
    public Boolean getFeatureDefault(String featureId) {
        return null;
    }

    public String[] getRecognizedProperties() {
        return null;
    }

    @Override
    public Object getPropertyDefault(String propertyId) {
        return null;
    }

    public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
    }

    public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
    }

    public void setProperty(String propertyId, Object value) throws XMLConfigurationException {
    }

    protected static String[] merge(String[] array1, String[] array2) {
        if (array1 == array2) {
            return array1;
        }
        if (array1 == null) {
            return array2;
        }
        if (array2 == null) {
            return array1;
        }
        String[] array3 = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, array3, 0, array1.length);
        System.arraycopy(array2, 0, array3, array1.length, array2.length);
        return array3;
    }
}

