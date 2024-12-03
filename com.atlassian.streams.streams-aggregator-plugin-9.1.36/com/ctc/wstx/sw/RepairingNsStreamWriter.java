/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.sw.BaseNsStreamWriter;
import com.ctc.wstx.sw.SimpleOutputElement;
import com.ctc.wstx.sw.XmlWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;

public final class RepairingNsStreamWriter
extends BaseNsStreamWriter {
    protected final String mAutomaticNsPrefix;
    protected int[] mAutoNsSeq = null;
    protected String mSuggestedDefNs = null;
    protected HashMap mSuggestedPrefixes = null;

    public RepairingNsStreamWriter(XmlWriter xw, String enc, WriterConfig cfg) {
        super(xw, enc, cfg, true);
        this.mAutomaticNsPrefix = cfg.getAutomaticNsPrefix();
    }

    public void writeAttribute(String nsURI, String localName, String value) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            RepairingNsStreamWriter.throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        this.doWriteAttr(localName, nsURI, this.findOrCreateAttrPrefix(null, nsURI, this.mCurrElem), value);
    }

    public void writeAttribute(String prefix, String nsURI, String localName, String value) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            RepairingNsStreamWriter.throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        this.doWriteAttr(localName, nsURI, this.findOrCreateAttrPrefix(prefix, nsURI, this.mCurrElem), value);
    }

    public void writeDefaultNamespace(String nsURI) throws XMLStreamException {
        String prefix;
        if (!this.mStartElementOpen) {
            RepairingNsStreamWriter.throwOutputError("Trying to write a namespace declaration when there is no open start element.");
        }
        if ((prefix = this.mCurrElem.getPrefix()) != null && prefix.length() > 0) {
            this.mCurrElem.setDefaultNsUri(nsURI);
            this.doWriteDefaultNs(nsURI);
        }
    }

    public void writeNamespace(String prefix, String nsURI) throws XMLStreamException {
        int value;
        if (prefix == null || prefix.length() == 0) {
            this.writeDefaultNamespace(nsURI);
            return;
        }
        if (!this.mStartElementOpen) {
            RepairingNsStreamWriter.throwOutputError("Trying to write a namespace declaration when there is no open start element.");
        }
        if ((value = this.mCurrElem.isPrefixValid(prefix, nsURI, true)) == 0) {
            this.mCurrElem.addPrefix(prefix, nsURI);
            this.doWriteNamespace(prefix, nsURI);
        }
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.mSuggestedDefNs = uri == null || uri.length() == 0 ? null : uri;
    }

    public void doSetPrefix(String prefix, String uri) throws XMLStreamException {
        if (uri == null || uri.length() == 0) {
            if (this.mSuggestedPrefixes != null) {
                Iterator it = this.mSuggestedPrefixes.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry en = it.next();
                    String thisP = (String)en.getValue();
                    if (!thisP.equals(prefix)) continue;
                    it.remove();
                }
            }
        } else {
            if (this.mSuggestedPrefixes == null) {
                this.mSuggestedPrefixes = new HashMap(16);
            }
            this.mSuggestedPrefixes.put(uri, prefix);
        }
    }

    public void writeStartElement(StartElement elem) throws XMLStreamException {
        QName name = elem.getName();
        this.writeStartElement(name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());
        Iterator it = elem.getAttributes();
        while (it.hasNext()) {
            Attribute attr = (Attribute)it.next();
            name = attr.getName();
            this.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attr.getValue());
        }
    }

    protected void writeTypedAttribute(String prefix, String nsURI, String localName, AsciiValueEncoder enc) throws XMLStreamException {
        super.writeTypedAttribute(this.findOrCreateAttrPrefix(prefix, nsURI, this.mCurrElem), nsURI, localName, enc);
    }

    protected void writeStartOrEmpty(String localName, String nsURI) throws XMLStreamException {
        this.checkStartElement(localName, "");
        String prefix = this.findElemPrefix(nsURI, this.mCurrElem);
        if (this.mOutputElemPool != null) {
            SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, prefix, localName, nsURI);
            --this.mPoolSize;
            this.mCurrElem = newCurr;
        } else {
            this.mCurrElem = this.mCurrElem.createChild(prefix, localName, nsURI);
        }
        if (prefix != null) {
            if (this.mValidator != null) {
                this.mValidator.validateElementStart(localName, nsURI, prefix);
            }
            this.doWriteStartTag(prefix, localName);
        } else {
            prefix = this.generateElemPrefix(null, nsURI, this.mCurrElem);
            if (this.mValidator != null) {
                this.mValidator.validateElementStart(localName, nsURI, prefix);
            }
            this.mCurrElem.setPrefix(prefix);
            this.doWriteStartTag(prefix, localName);
            if (prefix == null || prefix.length() == 0) {
                this.mCurrElem.setDefaultNsUri(nsURI);
                this.doWriteDefaultNs(nsURI);
            } else {
                this.mCurrElem.addPrefix(prefix, nsURI);
                this.doWriteNamespace(prefix, nsURI);
            }
        }
    }

    protected void writeStartOrEmpty(String suggPrefix, String localName, String nsURI) throws XMLStreamException {
        this.checkStartElement(localName, suggPrefix);
        String actPrefix = this.validateElemPrefix(suggPrefix, nsURI, this.mCurrElem);
        if (actPrefix != null) {
            if (this.mValidator != null) {
                this.mValidator.validateElementStart(localName, nsURI, actPrefix);
            }
            if (this.mOutputElemPool != null) {
                SimpleOutputElement newCurr = this.mOutputElemPool;
                this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, actPrefix, localName, nsURI);
                --this.mPoolSize;
                this.mCurrElem = newCurr;
            } else {
                this.mCurrElem = this.mCurrElem.createChild(actPrefix, localName, nsURI);
            }
            this.doWriteStartTag(actPrefix, localName);
        } else {
            if (suggPrefix == null) {
                suggPrefix = "";
            }
            actPrefix = this.generateElemPrefix(suggPrefix, nsURI, this.mCurrElem);
            if (this.mValidator != null) {
                this.mValidator.validateElementStart(localName, nsURI, actPrefix);
            }
            if (this.mOutputElemPool != null) {
                SimpleOutputElement newCurr = this.mOutputElemPool;
                this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, actPrefix, localName, nsURI);
                --this.mPoolSize;
                this.mCurrElem = newCurr;
            } else {
                this.mCurrElem = this.mCurrElem.createChild(actPrefix, localName, nsURI);
            }
            this.mCurrElem.setPrefix(actPrefix);
            this.doWriteStartTag(actPrefix, localName);
            if (actPrefix == null || actPrefix.length() == 0) {
                this.mCurrElem.setDefaultNsUri(nsURI);
                this.doWriteDefaultNs(nsURI);
            } else {
                this.mCurrElem.addPrefix(actPrefix, nsURI);
                this.doWriteNamespace(actPrefix, nsURI);
            }
        }
    }

    public final void copyStartElement(InputElementStack elemStack, AttributeCollector ac) throws IOException, XMLStreamException {
        int attrCount;
        String prefix = elemStack.getPrefix();
        String uri = elemStack.getNsURI();
        this.writeStartElement(prefix, elemStack.getLocalName(), uri);
        int nsCount = elemStack.getCurrentNsCount();
        if (nsCount > 0) {
            for (int i = 0; i < nsCount; ++i) {
                this.writeNamespace(elemStack.getLocalNsPrefix(i), elemStack.getLocalNsURI(i));
            }
        }
        int n = attrCount = this.mCfgCopyDefaultAttrs ? ac.getCount() : ac.getSpecifiedCount();
        if (attrCount > 0) {
            for (int i = 0; i < attrCount; ++i) {
                uri = ac.getURI(i);
                prefix = ac.getPrefix(i);
                if (prefix != null && prefix.length() != 0) {
                    prefix = this.findOrCreateAttrPrefix(prefix, uri, this.mCurrElem);
                }
                if (prefix == null || prefix.length() == 0) {
                    this.mWriter.writeAttribute(ac.getLocalName(i), ac.getValue(i));
                    continue;
                }
                this.mWriter.writeAttribute(prefix, ac.getLocalName(i), ac.getValue(i));
            }
        }
    }

    public String validateQNamePrefix(QName name) throws XMLStreamException {
        String uri = name.getNamespaceURI();
        String suggPrefix = name.getPrefix();
        String actPrefix = this.validateElemPrefix(suggPrefix, uri, this.mCurrElem);
        if (actPrefix == null) {
            if (suggPrefix == null) {
                suggPrefix = "";
            }
            if ((actPrefix = this.generateElemPrefix(suggPrefix, uri, this.mCurrElem)) == null || actPrefix.length() == 0) {
                this.writeDefaultNamespace(uri);
            } else {
                this.writeNamespace(actPrefix, uri);
            }
        }
        return actPrefix;
    }

    protected final String findElemPrefix(String nsURI, SimpleOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            String currDefNsURI = elem.getDefaultNsUri();
            if (currDefNsURI != null && currDefNsURI.length() > 0) {
                return null;
            }
            return "";
        }
        return this.mCurrElem.getPrefix(nsURI);
    }

    protected final String generateElemPrefix(String suggPrefix, String nsURI, SimpleOutputElement elem) throws XMLStreamException {
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

    protected final String findOrCreateAttrPrefix(String suggPrefix, String nsURI, SimpleOutputElement elem) throws XMLStreamException {
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
                this.doWriteNamespace(suggPrefix, nsURI);
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
        this.doWriteNamespace(prefix, nsURI);
        return prefix;
    }

    private final String validateElemPrefix(String prefix, String nsURI, SimpleOutputElement elem) throws XMLStreamException {
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
}

