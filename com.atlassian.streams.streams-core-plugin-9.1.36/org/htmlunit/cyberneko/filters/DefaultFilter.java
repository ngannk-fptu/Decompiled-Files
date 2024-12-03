/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.filters;

import org.htmlunit.cyberneko.HTMLComponent;
import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.NamespaceContext;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;
import org.htmlunit.cyberneko.xerces.xni.XMLDocumentHandler;
import org.htmlunit.cyberneko.xerces.xni.XMLLocator;
import org.htmlunit.cyberneko.xerces.xni.XMLString;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLComponentManager;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLConfigurationException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentFilter;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentSource;

public class DefaultFilter
implements XMLDocumentFilter,
HTMLComponent {
    private XMLDocumentHandler fDocumentHandler_;
    private XMLDocumentSource fDocumentSource;

    @Override
    public void setDocumentHandler(XMLDocumentHandler handler) {
        this.fDocumentHandler_ = handler;
    }

    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler_;
    }

    @Override
    public void setDocumentSource(XMLDocumentSource source) {
        this.fDocumentSource = source;
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.startDocument(locator, encoding, nscontext, augs);
        }
    }

    @Override
    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.xmlDecl(version, encoding, standalone, augs);
        }
    }

    @Override
    public void doctypeDecl(String root, String publicId, String systemId, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.doctypeDecl(root, publicId, systemId, augs);
        }
    }

    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.comment(text, augs);
        }
    }

    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.processingInstruction(target, data, augs);
        }
    }

    @Override
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.startElement(element, attributes, augs);
        }
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.emptyElement(element, attributes, augs);
        }
    }

    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.characters(text, augs);
        }
    }

    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.ignorableWhitespace(text, augs);
        }
    }

    @Override
    public void startGeneralEntity(String name, String encoding, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.startGeneralEntity(name, encoding, augs);
        }
    }

    @Override
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.textDecl(version, encoding, augs);
        }
    }

    @Override
    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.endGeneralEntity(name, augs);
        }
    }

    @Override
    public void startCDATA(Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.startCDATA(augs);
        }
    }

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.endCDATA(augs);
        }
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.endElement(element, augs);
        }
    }

    @Override
    public void endDocument(Augmentations augs) throws XNIException {
        if (this.fDocumentHandler_ != null) {
            this.fDocumentHandler_.endDocument(augs);
        }
    }

    @Override
    public String[] getRecognizedFeatures() {
        return null;
    }

    @Override
    public Boolean getFeatureDefault(String featureId) {
        return null;
    }

    @Override
    public String[] getRecognizedProperties() {
        return null;
    }

    @Override
    public Object getPropertyDefault(String propertyId) {
        return null;
    }

    @Override
    public void reset(XMLComponentManager componentManager) throws XMLConfigurationException {
    }

    @Override
    public void setFeature(String featureId, boolean state) throws XMLConfigurationException {
    }

    @Override
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

