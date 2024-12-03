/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dom;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.dom.DOMOutputElement;
import java.util.HashMap;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMResult;
import org.codehaus.stax2.ri.EmptyNamespaceContext;
import org.codehaus.stax2.ri.dom.DOMWrappingWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class WstxDOMWrappingWriter
extends DOMWrappingWriter {
    protected static final String ERR_NSDECL_WRONG_STATE = "Trying to write a namespace declaration when there is no open start element.";
    protected final WriterConfig mConfig;
    protected DOMOutputElement mCurrElem;
    protected DOMOutputElement mOpenElement;
    protected int[] mAutoNsSeq;
    protected String mSuggestedDefNs = null;
    protected String mAutomaticNsPrefix;
    HashMap mSuggestedPrefixes = null;

    private WstxDOMWrappingWriter(WriterConfig cfg, Node treeRoot) throws XMLStreamException {
        super(treeRoot, cfg.willSupportNamespaces(), cfg.automaticNamespacesEnabled());
        this.mConfig = cfg;
        this.mAutoNsSeq = null;
        this.mAutomaticNsPrefix = this.mNsRepairing ? this.mConfig.getAutomaticNsPrefix() : null;
        switch (treeRoot.getNodeType()) {
            case 9: 
            case 11: {
                this.mCurrElem = DOMOutputElement.createRoot(treeRoot);
                this.mOpenElement = null;
                break;
            }
            case 1: {
                DOMOutputElement root = DOMOutputElement.createRoot(treeRoot);
                Element elem = (Element)treeRoot;
                this.mOpenElement = this.mCurrElem = root.createChild(elem);
                break;
            }
            default: {
                throw new XMLStreamException("Can not create an XMLStreamWriter for a DOM node of type " + treeRoot.getClass());
            }
        }
    }

    public static WstxDOMWrappingWriter createFrom(WriterConfig cfg, DOMResult dst) throws XMLStreamException {
        Node rootNode = dst.getNode();
        return new WstxDOMWrappingWriter(cfg, rootNode);
    }

    public NamespaceContext getNamespaceContext() {
        if (!this.mNsAware) {
            return EmptyNamespaceContext.getInstance();
        }
        return this.mCurrElem;
    }

    public String getPrefix(String uri) {
        String prefix;
        if (!this.mNsAware) {
            return null;
        }
        if (this.mNsContext != null && (prefix = this.mNsContext.getPrefix(uri)) != null) {
            return prefix;
        }
        return this.mCurrElem.getPrefix(uri);
    }

    public Object getProperty(String name) {
        return this.mConfig.getProperty(name);
    }

    public void setDefaultNamespace(String uri) {
        this.mSuggestedDefNs = uri == null || uri.length() == 0 ? null : uri;
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        if (prefix == null) {
            throw new NullPointerException("Can not pass null 'prefix' value");
        }
        if (prefix.length() == 0) {
            this.setDefaultNamespace(uri);
            return;
        }
        if (uri == null) {
            throw new NullPointerException("Can not pass null 'uri' value");
        }
        if (prefix.equals("xml")) {
            if (!uri.equals("http://www.w3.org/XML/1998/namespace")) {
                WstxDOMWrappingWriter.throwOutputError(ErrorConsts.ERR_NS_REDECL_XML, uri);
            }
        } else {
            if (prefix.equals("xmlns")) {
                if (!uri.equals("http://www.w3.org/2000/xmlns/")) {
                    WstxDOMWrappingWriter.throwOutputError(ErrorConsts.ERR_NS_REDECL_XMLNS, uri);
                }
                return;
            }
            if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
                WstxDOMWrappingWriter.throwOutputError(ErrorConsts.ERR_NS_REDECL_XML_URI, prefix);
            } else if (uri.equals("http://www.w3.org/2000/xmlns/")) {
                WstxDOMWrappingWriter.throwOutputError(ErrorConsts.ERR_NS_REDECL_XMLNS_URI, prefix);
            }
        }
        if (this.mSuggestedPrefixes == null) {
            this.mSuggestedPrefixes = new HashMap(16);
        }
        this.mSuggestedPrefixes.put(uri, prefix);
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.outputAttribute(null, null, localName, value);
    }

    public void writeAttribute(String nsURI, String localName, String value) throws XMLStreamException {
        this.outputAttribute(nsURI, null, localName, value);
    }

    public void writeAttribute(String prefix, String nsURI, String localName, String value) throws XMLStreamException {
        this.outputAttribute(nsURI, prefix, localName, value);
    }

    public void writeDefaultNamespace(String nsURI) {
        if (this.mOpenElement == null) {
            throw new IllegalStateException("No currently open START_ELEMENT, cannot write attribute");
        }
        this.setDefaultNamespace(nsURI);
        this.mOpenElement.addAttribute("http://www.w3.org/2000/xmlns/", "xmlns", nsURI);
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.writeEmptyElement(null, localName);
    }

    public void writeEmptyElement(String nsURI, String localName) throws XMLStreamException {
        this.createStartElem(nsURI, null, localName, true);
    }

    public void writeEmptyElement(String prefix, String localName, String nsURI) throws XMLStreamException {
        if (prefix == null) {
            prefix = "";
        }
        this.createStartElem(nsURI, prefix, localName, true);
    }

    public void writeEndDocument() {
        this.mOpenElement = null;
        this.mCurrElem = null;
    }

    public void writeEndElement() {
        if (this.mCurrElem == null || this.mCurrElem.isRoot()) {
            throw new IllegalStateException("No open start element to close");
        }
        this.mOpenElement = null;
        this.mCurrElem = this.mCurrElem.getParent();
    }

    public void writeNamespace(String prefix, String nsURI) throws XMLStreamException {
        if (prefix == null || prefix.length() == 0) {
            this.writeDefaultNamespace(nsURI);
            return;
        }
        if (!this.mNsAware) {
            WstxDOMWrappingWriter.throwOutputError("Can not write namespaces with non-namespace writer.");
        }
        this.outputAttribute("http://www.w3.org/2000/xmlns/", "xmlns", prefix, nsURI);
        this.mCurrElem.addPrefix(prefix, nsURI);
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        this.writeStartElement(null, localName);
    }

    public void writeStartElement(String nsURI, String localName) throws XMLStreamException {
        this.createStartElem(nsURI, null, localName, false);
    }

    public void writeStartElement(String prefix, String localName, String nsURI) throws XMLStreamException {
        this.createStartElem(nsURI, prefix, localName, false);
    }

    public boolean isPropertySupported(String name) {
        return this.mConfig.isPropertySupported(name);
    }

    public boolean setProperty(String name, Object value) {
        return this.mConfig.setProperty(name, value);
    }

    public void writeDTD(String rootName, String systemId, String publicId, String internalSubset) throws XMLStreamException {
        if (this.mCurrElem != null) {
            throw new IllegalStateException("Operation only allowed to the document before adding root element");
        }
        this.reportUnsupported("writeDTD()");
    }

    protected void appendLeaf(Node n) throws IllegalStateException {
        this.mCurrElem.appendNode(n);
        this.mOpenElement = null;
    }

    protected void createStartElem(String nsURI, String prefix, String localName, boolean isEmpty) throws XMLStreamException {
        DOMOutputElement elem;
        if (!this.mNsAware) {
            if (nsURI != null && nsURI.length() > 0) {
                WstxDOMWrappingWriter.throwOutputError("Can not specify non-empty uri/prefix in non-namespace mode");
            }
            elem = this.mCurrElem.createAndAttachChild(this.mDocument.createElement(localName));
        } else if (this.mNsRepairing) {
            String actPrefix = this.validateElemPrefix(prefix, nsURI, this.mCurrElem);
            if (actPrefix != null) {
                elem = actPrefix.length() != 0 ? this.mCurrElem.createAndAttachChild(this.mDocument.createElementNS(nsURI, actPrefix + ":" + localName)) : this.mCurrElem.createAndAttachChild(this.mDocument.createElementNS(nsURI, localName));
            } else {
                boolean hasPrefix;
                if (prefix == null) {
                    prefix = "";
                }
                boolean bl = hasPrefix = (actPrefix = this.generateElemPrefix(prefix, nsURI, this.mCurrElem)).length() != 0;
                if (hasPrefix) {
                    localName = actPrefix + ":" + localName;
                }
                this.mOpenElement = elem = this.mCurrElem.createAndAttachChild(this.mDocument.createElementNS(nsURI, localName));
                if (hasPrefix) {
                    this.writeNamespace(actPrefix, nsURI);
                    elem.addPrefix(actPrefix, nsURI);
                } else {
                    this.writeDefaultNamespace(nsURI);
                    elem.setDefaultNsUri(nsURI);
                }
            }
        } else {
            if (prefix == null && nsURI != null && nsURI.length() > 0) {
                if (nsURI == null) {
                    nsURI = "";
                }
                String string = prefix = this.mSuggestedPrefixes == null ? null : (String)this.mSuggestedPrefixes.get(nsURI);
                if (prefix == null) {
                    WstxDOMWrappingWriter.throwOutputError("Can not find prefix for namespace \"" + nsURI + "\"");
                }
            }
            if (prefix != null && prefix.length() != 0) {
                localName = prefix + ":" + localName;
            }
            elem = this.mCurrElem.createAndAttachChild(this.mDocument.createElementNS(nsURI, localName));
        }
        this.mOpenElement = elem;
        if (!isEmpty) {
            this.mCurrElem = elem;
        }
    }

    protected void outputAttribute(String nsURI, String prefix, String localName, String value) throws XMLStreamException {
        if (this.mOpenElement == null) {
            throw new IllegalStateException("No currently open START_ELEMENT, cannot write attribute");
        }
        if (this.mNsAware) {
            if (this.mNsRepairing) {
                prefix = this.findOrCreateAttrPrefix(prefix, nsURI, this.mOpenElement);
            }
            if (prefix != null && prefix.length() > 0) {
                localName = prefix + ":" + localName;
            }
            this.mOpenElement.addAttribute(nsURI, localName, value);
        } else {
            if (prefix != null && prefix.length() > 0) {
                localName = prefix + ":" + localName;
            }
            this.mOpenElement.addAttribute(localName, value);
        }
    }

    private final String validateElemPrefix(String prefix, String nsURI, DOMOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            String currURL = elem.getDefaultNsUri();
            if (currURL == null || currURL.length() == 0) {
                return "";
            }
            return null;
        }
        int status = elem.isPrefixValid(prefix, nsURI, true);
        if (status == 1) {
            return prefix;
        }
        return null;
    }

    protected final String findElemPrefix(String nsURI, DOMOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            String currDefNsURI = elem.getDefaultNsUri();
            if (currDefNsURI != null && currDefNsURI.length() > 0) {
                return null;
            }
            return "";
        }
        return this.mCurrElem.getPrefix(nsURI);
    }

    protected final String generateElemPrefix(String suggPrefix, String nsURI, DOMOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            return "";
        }
        if (suggPrefix == null) {
            if (this.mSuggestedDefNs != null && this.mSuggestedDefNs.equals(nsURI)) {
                suggPrefix = "";
            } else {
                String string = suggPrefix = this.mSuggestedPrefixes == null ? null : (String)this.mSuggestedPrefixes.get(nsURI);
                if (suggPrefix == null) {
                    if (this.mAutoNsSeq == null) {
                        this.mAutoNsSeq = new int[1];
                        this.mAutoNsSeq[0] = 1;
                    }
                    suggPrefix = elem.generateMapping(this.mAutomaticNsPrefix, nsURI, this.mAutoNsSeq);
                }
            }
        }
        return suggPrefix;
    }

    protected final String findOrCreateAttrPrefix(String suggPrefix, String nsURI, DOMOutputElement elem) throws XMLStreamException {
        String prefix;
        if (nsURI == null || nsURI.length() == 0) {
            return null;
        }
        if (suggPrefix != null) {
            int status = elem.isPrefixValid(suggPrefix, nsURI, false);
            if (status == 1) {
                return suggPrefix;
            }
            if (status == 0) {
                elem.addPrefix(suggPrefix, nsURI);
                this.writeNamespace(suggPrefix, nsURI);
                return suggPrefix;
            }
        }
        if ((prefix = elem.getExplicitPrefix(nsURI)) != null) {
            return prefix;
        }
        if (suggPrefix != null) {
            prefix = suggPrefix;
        } else if (this.mSuggestedPrefixes != null) {
            prefix = (String)this.mSuggestedPrefixes.get(nsURI);
        }
        if (prefix != null && (prefix.length() == 0 || elem.getNamespaceURI(prefix) != null)) {
            prefix = null;
        }
        if (prefix == null) {
            if (this.mAutoNsSeq == null) {
                this.mAutoNsSeq = new int[1];
                this.mAutoNsSeq[0] = 1;
            }
            prefix = this.mCurrElem.generateMapping(this.mAutomaticNsPrefix, nsURI, this.mAutoNsSeq);
        }
        elem.addPrefix(prefix, nsURI);
        this.writeNamespace(prefix, nsURI);
        return prefix;
    }
}

