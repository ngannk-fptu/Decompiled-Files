/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.util.XMLChar
 *  org.apache.xerces.util.XMLStringBuffer
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.NamespaceContext
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XMLLocator
 *  org.apache.xerces.xni.XMLString
 *  org.apache.xerces.xni.XNIException
 *  org.apache.xerces.xni.parser.XMLComponentManager
 *  org.apache.xerces.xni.parser.XMLConfigurationException
 */
package org.cyberneko.html.filters;

import java.util.Locale;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.cyberneko.html.HTMLAugmentations;
import org.cyberneko.html.HTMLEventInfo;
import org.cyberneko.html.filters.DefaultFilter;
import org.cyberneko.html.filters.NamespaceBinder;
import org.cyberneko.html.xercesbridge.XercesBridge;

public class Purifier
extends DefaultFilter {
    public static final String SYNTHESIZED_NAMESPACE_PREFX = "http://cyberneko.org/html/ns/synthesized/";
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String AUGMENTATIONS = "http://cyberneko.org/html/features/augmentations";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/namespaces", "http://cyberneko.org/html/features/augmentations"};
    protected static final HTMLEventInfo SYNTHESIZED_ITEM = new HTMLEventInfo.SynthesizedItem();
    protected boolean fNamespaces;
    protected boolean fAugmentations;
    protected boolean fSeenDoctype;
    protected boolean fSeenRootElement;
    protected boolean fInCDATASection;
    protected String fPublicId;
    protected String fSystemId;
    protected NamespaceContext fNamespaceContext;
    protected int fSynthesizedNamespaceCount;
    private QName fQName = new QName();
    private final HTMLAugmentations fInfosetAugs = new HTMLAugmentations();
    private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();

    @Override
    public void reset(XMLComponentManager manager) throws XMLConfigurationException {
        this.fInCDATASection = false;
        this.fNamespaces = manager.getFeature(NAMESPACES);
        this.fAugmentations = manager.getFeature(AUGMENTATIONS);
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, Augmentations augs) throws XNIException {
        this.fNamespaceContext = this.fNamespaces ? new NamespaceBinder.NamespaceSupport() : null;
        this.fSynthesizedNamespaceCount = 0;
        this.handleStartDocument();
        super.startDocument(locator, encoding, augs);
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) throws XNIException {
        this.fNamespaceContext = nscontext;
        this.fSynthesizedNamespaceCount = 0;
        this.handleStartDocument();
        super.startDocument(locator, encoding, nscontext, augs);
    }

    @Override
    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
        if (version == null || !version.equals("1.0")) {
            version = "1.0";
        }
        if (encoding != null && encoding.length() == 0) {
            encoding = null;
        }
        if (standalone != null) {
            standalone = !standalone.equalsIgnoreCase("true") && !standalone.equalsIgnoreCase("false") ? null : standalone.toLowerCase();
        }
        super.xmlDecl(version, encoding, standalone, augs);
    }

    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
        StringBuffer str = new StringBuffer(this.purifyText(text).toString());
        int length = str.length();
        for (int i = length - 1; i >= 0; --i) {
            char c = str.charAt(i);
            if (c != '-') continue;
            str.insert(i + 1, ' ');
        }
        this.fStringBuffer.length = 0;
        this.fStringBuffer.append(str.toString());
        text = this.fStringBuffer;
        super.comment(text, augs);
    }

    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        target = this.purifyName(target, true);
        data = this.purifyText(data);
        super.processingInstruction(target, data, augs);
    }

    @Override
    public void doctypeDecl(String root, String pubid, String sysid, Augmentations augs) throws XNIException {
        this.fSeenDoctype = true;
        this.fPublicId = pubid;
        this.fSystemId = sysid;
        if (this.fPublicId != null && this.fSystemId == null) {
            this.fSystemId = "";
        }
    }

    @Override
    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        this.handleStartElement(element, attrs);
        super.startElement(element, attrs, augs);
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        this.handleStartElement(element, attrs);
        super.emptyElement(element, attrs, augs);
    }

    @Override
    public void startCDATA(Augmentations augs) throws XNIException {
        this.fInCDATASection = true;
        super.startCDATA(augs);
    }

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
        this.fInCDATASection = false;
        super.endCDATA(augs);
    }

    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        text = this.purifyText(text);
        if (this.fInCDATASection) {
            StringBuffer str = new StringBuffer(text.toString());
            int length = str.length();
            for (int i = length - 1; i >= 0; --i) {
                char c = str.charAt(i);
                if (c != ']') continue;
                str.insert(i + 1, ' ');
            }
            this.fStringBuffer.length = 0;
            this.fStringBuffer.append(str.toString());
            text = this.fStringBuffer;
        }
        super.characters(text, augs);
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        element = this.purifyQName(element);
        if (this.fNamespaces && element.prefix != null && element.uri == null) {
            element.uri = this.fNamespaceContext.getURI(element.prefix);
        }
        super.endElement(element, augs);
    }

    protected void handleStartDocument() {
        this.fSeenDoctype = false;
        this.fSeenRootElement = false;
    }

    protected void handleStartElement(QName element, XMLAttributes attrs) {
        element = this.purifyQName(element);
        int attrCount = attrs != null ? attrs.getLength() : 0;
        for (int i = attrCount - 1; i >= 0; --i) {
            attrs.getName(i, this.fQName);
            attrs.setName(i, this.purifyQName(this.fQName));
            if (!this.fNamespaces || this.fQName.rawname.equals("xmlns") || this.fQName.rawname.startsWith("xmlns:")) continue;
            attrs.getName(i, this.fQName);
            if (this.fQName.prefix == null || this.fQName.uri != null) continue;
            this.synthesizeBinding(attrs, this.fQName.prefix);
        }
        if (this.fNamespaces && element.prefix != null && element.uri == null) {
            this.synthesizeBinding(attrs, element.prefix);
        }
        if (!this.fSeenRootElement && this.fSeenDoctype) {
            Augmentations augs = this.synthesizedAugs();
            super.doctypeDecl(element.rawname, this.fPublicId, this.fSystemId, augs);
        }
        this.fSeenRootElement = true;
    }

    protected void synthesizeBinding(XMLAttributes attrs, String ns) {
        String prefix = "xmlns";
        String localpart = ns;
        String qname = prefix + ':' + localpart;
        String uri = "http://cyberneko.org/html/properties/namespaces-uri";
        String atype = "CDATA";
        String avalue = SYNTHESIZED_NAMESPACE_PREFX + this.fSynthesizedNamespaceCount++;
        this.fQName.setValues(prefix, localpart, qname, uri);
        attrs.addAttribute(this.fQName, atype, avalue);
        XercesBridge.getInstance().NamespaceContext_declarePrefix(this.fNamespaceContext, ns, avalue);
    }

    protected final Augmentations synthesizedAugs() {
        HTMLAugmentations augs = null;
        if (this.fAugmentations) {
            augs = this.fInfosetAugs;
            augs.removeAllItems();
            augs.putItem(AUGMENTATIONS, SYNTHESIZED_ITEM);
        }
        return augs;
    }

    protected QName purifyQName(QName qname) {
        qname.prefix = this.purifyName(qname.prefix, true);
        qname.localpart = this.purifyName(qname.localpart, true);
        qname.rawname = this.purifyName(qname.rawname, false);
        return qname;
    }

    protected String purifyName(String name, boolean localpart) {
        if (name == null) {
            return name;
        }
        StringBuffer str = new StringBuffer();
        int length = name.length();
        boolean seenColon = localpart;
        for (int i = 0; i < length; ++i) {
            char c = name.charAt(i);
            if (i == 0) {
                if (!XMLChar.isNameStart((int)c)) {
                    str.append("_u" + Purifier.toHexString(c, 4) + "_");
                    continue;
                }
                str.append(c);
                continue;
            }
            if (this.fNamespaces && c == ':' && seenColon || !XMLChar.isName((int)c)) {
                str.append("_u" + Purifier.toHexString(c, 4) + "_");
            } else {
                str.append(c);
            }
            seenColon = seenColon || c == ':';
        }
        return str.toString();
    }

    protected XMLString purifyText(XMLString text) {
        this.fStringBuffer.length = 0;
        for (int i = 0; i < text.length; ++i) {
            char c = text.ch[text.offset + i];
            if (XMLChar.isInvalid((int)c)) {
                this.fStringBuffer.append("\\u" + Purifier.toHexString(c, 4));
                continue;
            }
            this.fStringBuffer.append(c);
        }
        return this.fStringBuffer;
    }

    protected static String toHexString(int c, int padlen) {
        StringBuffer str = new StringBuffer(padlen);
        str.append(Integer.toHexString(c));
        int len = padlen - str.length();
        for (int i = 0; i < len; ++i) {
            str.insert(0, '0');
        }
        return str.toString().toUpperCase(Locale.ENGLISH);
    }
}

