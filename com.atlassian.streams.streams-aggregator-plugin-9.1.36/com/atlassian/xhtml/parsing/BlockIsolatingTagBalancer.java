/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.xhtml.parsing;

import com.atlassian.xhtml.parsing.IsolatedBodyListener;
import java.util.HashSet;
import java.util.Set;
import org.htmlunit.cyberneko.CustomizableHTMLTagBalancer;
import org.htmlunit.cyberneko.HTMLConfiguration;
import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;
import org.htmlunit.cyberneko.xerces.xni.XMLString;
import org.htmlunit.cyberneko.xerces.xni.XNIException;

public class BlockIsolatingTagBalancer
extends CustomizableHTMLTagBalancer
implements IsolatedBodyListener {
    private Set<String> elementsToIsolate = new HashSet<String>(2);
    private BlockIsolatingTagBalancer delegateBalancer;
    private String delegatingForRawName;
    private IsolatedBodyListener listener;
    private final HTMLConfiguration htmlConfiguration;

    public BlockIsolatingTagBalancer(Set<String> elementRawNamesToIsolate, HTMLConfiguration htmlConfiguratioo) {
        super(htmlConfiguratioo);
        this.htmlConfiguration = htmlConfiguratioo;
        this.elementsToIsolate = elementRawNamesToIsolate;
    }

    @Override
    public void startElement(QName elem, XMLAttributes attrs, Augmentations augs) throws XNIException {
        if (this.delegateBalancer != null) {
            this.delegateBalancer.startElement(elem, attrs, augs);
        } else if (this.elementsToIsolate.contains(elem.rawname)) {
            this.delegateBalancer = this.constructNew();
            this.delegateBalancer.setDelegatingForRawName(elem.rawname);
            super.startElement(elem, attrs, augs);
        } else {
            super.startElement(elem, attrs, augs);
        }
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        if (this.delegateBalancer != null) {
            this.delegateBalancer.endElement(element, augs);
        } else if (this.delegatingForRawName != null && element.rawname.equals(this.delegatingForRawName)) {
            this.listener.completeForEndElement(element, augs);
            this.listener = null;
        } else {
            super.endElement(element, augs);
        }
    }

    @Override
    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
        if (this.delegateBalancer != null) {
            this.delegateBalancer.xmlDecl(version, encoding, standalone, augs);
        } else {
            super.xmlDecl(version, encoding, standalone, augs);
        }
    }

    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
        if (this.delegateBalancer != null) {
            this.delegateBalancer.comment(text, augs);
        } else {
            super.comment(text, augs);
        }
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        if (this.delegateBalancer != null) {
            this.delegateBalancer.emptyElement(element, attrs, augs);
        } else {
            super.emptyElement(element, attrs, augs);
        }
    }

    @Override
    public void startGeneralEntity(String name, String encoding, Augmentations augs) throws XNIException {
        if (this.delegateBalancer != null) {
            this.delegateBalancer.startGeneralEntity(name, encoding, augs);
        } else {
            super.startGeneralEntity(name, encoding, augs);
        }
    }

    @Override
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
        if (this.delegateBalancer != null) {
            this.delegateBalancer.textDecl(version, encoding, augs);
        } else {
            super.textDecl(version, encoding, augs);
        }
    }

    @Override
    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        if (this.delegateBalancer != null) {
            this.delegateBalancer.endGeneralEntity(name, augs);
        } else {
            super.endGeneralEntity(name, augs);
        }
    }

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
        if (this.delegateBalancer != null) {
            this.delegateBalancer.endCDATA(augs);
        } else {
            super.endCDATA(augs);
        }
    }

    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (this.delegateBalancer != null) {
            this.delegateBalancer.characters(text, augs);
        } else {
            super.characters(text, augs);
        }
    }

    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        if (this.delegateBalancer != null) {
            this.delegateBalancer.processingInstruction(target, data, augs);
        } else {
            super.processingInstruction(target, data, augs);
        }
    }

    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        if (this.delegateBalancer != null) {
            this.delegateBalancer.ignorableWhitespace(text, augs);
        } else {
            super.ignorableWhitespace(text, augs);
        }
    }

    public void setDelegatingForRawName(String delegatingForRawName) {
        this.delegatingForRawName = delegatingForRawName;
    }

    public void setListener(IsolatedBodyListener listener) {
        this.listener = listener;
    }

    @Override
    public void completeForEndElement(QName element, Augmentations augs) throws XNIException {
        this.delegateBalancer = null;
        super.endElement(element, augs);
    }

    protected BlockIsolatingTagBalancer constructNew() {
        BlockIsolatingTagBalancer isolatedTagBalancer = new BlockIsolatingTagBalancer(this.elementsToIsolate, this.htmlConfiguration);
        isolatedTagBalancer.setDocumentHandler(this.getDocumentHandler());
        isolatedTagBalancer.setDocumentSource(this.getDocumentSource());
        isolatedTagBalancer.fErrorReporter = this.fErrorReporter;
        isolatedTagBalancer.tagBalancingListener = this.tagBalancingListener;
        isolatedTagBalancer.fNamespaces = this.fNamespaces;
        isolatedTagBalancer.fAugmentations = this.fAugmentations;
        isolatedTagBalancer.fReportErrors = this.fReportErrors;
        isolatedTagBalancer.fDocumentFragment = this.fDocumentFragment;
        isolatedTagBalancer.fIgnoreOutsideContent = this.fIgnoreOutsideContent;
        isolatedTagBalancer.fAllowSelfclosingTags = this.fAllowSelfclosingTags;
        isolatedTagBalancer.fAllowSelfclosingIframe = this.fAllowSelfclosingIframe;
        isolatedTagBalancer.fNamesElems = this.fNamesElems;
        isolatedTagBalancer.setListener(this);
        return isolatedTagBalancer;
    }
}

