/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.NamespaceContext
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XMLLocator
 *  org.apache.xerces.xni.XMLResourceIdentifier
 *  org.apache.xerces.xni.XMLString
 *  org.apache.xerces.xni.XNIException
 */
package org.cyberneko.html.filters;

import java.util.Hashtable;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.filters.DefaultFilter;

public class ElementRemover
extends DefaultFilter {
    protected static final Object NULL = new Object();
    protected Hashtable fAcceptedElements = new Hashtable();
    protected Hashtable fRemovedElements = new Hashtable();
    protected int fElementDepth;
    protected int fRemovalElementDepth;

    public void acceptElement(String element, String[] attributes) {
        String key = element.toLowerCase();
        String[] value = NULL;
        if (attributes != null) {
            String[] newarray = new String[attributes.length];
            for (int i = 0; i < attributes.length; ++i) {
                newarray[i] = attributes[i].toLowerCase();
            }
            value = attributes;
        }
        this.fAcceptedElements.put(key, value);
    }

    public void removeElement(String element) {
        String key = element.toLowerCase();
        Object value = NULL;
        this.fRemovedElements.put(key, value);
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) throws XNIException {
        this.fElementDepth = 0;
        this.fRemovalElementDepth = Integer.MAX_VALUE;
        super.startDocument(locator, encoding, nscontext, augs);
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, Augmentations augs) throws XNIException {
        this.startDocument(locator, encoding, null, augs);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri, Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.startPrefixMapping(prefix, uri, augs);
        }
    }

    @Override
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth && this.handleOpenTag(element, attributes)) {
            super.startElement(element, attributes, augs);
        }
        ++this.fElementDepth;
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth && this.handleOpenTag(element, attributes)) {
            super.emptyElement(element, attributes, augs);
        }
    }

    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.comment(text, augs);
        }
    }

    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.processingInstruction(target, data, augs);
        }
    }

    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.characters(text, augs);
        }
    }

    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.ignorableWhitespace(text, augs);
        }
    }

    @Override
    public void startGeneralEntity(String name, XMLResourceIdentifier id, String encoding, Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.startGeneralEntity(name, id, encoding, augs);
        }
    }

    @Override
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.textDecl(version, encoding, augs);
        }
    }

    @Override
    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.endGeneralEntity(name, augs);
        }
    }

    @Override
    public void startCDATA(Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.startCDATA(augs);
        }
    }

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.endCDATA(augs);
        }
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth && this.elementAccepted(element.rawname)) {
            super.endElement(element, augs);
        }
        --this.fElementDepth;
        if (this.fElementDepth == this.fRemovalElementDepth) {
            this.fRemovalElementDepth = Integer.MAX_VALUE;
        }
    }

    @Override
    public void endPrefixMapping(String prefix, Augmentations augs) throws XNIException {
        if (this.fElementDepth <= this.fRemovalElementDepth) {
            super.endPrefixMapping(prefix, augs);
        }
    }

    protected boolean elementAccepted(String element) {
        String key = element.toLowerCase();
        return this.fAcceptedElements.containsKey(key);
    }

    protected boolean elementRemoved(String element) {
        String key = element.toLowerCase();
        return this.fRemovedElements.containsKey(key);
    }

    protected boolean handleOpenTag(QName element, XMLAttributes attributes) {
        if (this.elementAccepted(element.rawname)) {
            String key = element.rawname.toLowerCase();
            Object value = this.fAcceptedElements.get(key);
            if (value != NULL) {
                String[] anames = (String[])value;
                int attributeCount = attributes.getLength();
                block0: for (int i = 0; i < attributeCount; ++i) {
                    String aname = attributes.getQName(i).toLowerCase();
                    for (int j = 0; j < anames.length; ++j) {
                        if (anames[j].equals(aname)) continue block0;
                    }
                    attributes.removeAttributeAt(i--);
                    --attributeCount;
                }
            } else {
                attributes.removeAllAttributes();
            }
            return true;
        }
        if (this.elementRemoved(element.rawname)) {
            this.fRemovalElementDepth = this.fElementDepth;
        }
        return false;
    }
}

