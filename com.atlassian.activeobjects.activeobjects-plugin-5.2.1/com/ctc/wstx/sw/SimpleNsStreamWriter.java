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
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;

public class SimpleNsStreamWriter
extends BaseNsStreamWriter {
    public SimpleNsStreamWriter(XmlWriter xw, String enc, WriterConfig cfg) {
        super(xw, enc, cfg, false);
    }

    public void writeAttribute(String nsURI, String localName, String value) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            SimpleNsStreamWriter.throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        String prefix = this.mCurrElem.getExplicitPrefix(nsURI);
        if (!this.mReturnNullForDefaultNamespace && prefix == null) {
            SimpleNsStreamWriter.throwOutputError("Unbound namespace URI '" + nsURI + "'");
        }
        this.doWriteAttr(localName, nsURI, prefix, value);
    }

    public void writeAttribute(String prefix, String nsURI, String localName, String value) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            SimpleNsStreamWriter.throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        this.doWriteAttr(localName, nsURI, prefix, value);
    }

    public void writeDefaultNamespace(String nsURI) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            SimpleNsStreamWriter.throwOutputError("Trying to write a namespace declaration when there is no open start element.");
        }
        this.setDefaultNamespace(nsURI);
        this.doWriteDefaultNs(nsURI);
    }

    public void writeNamespace(String prefix, String nsURI) throws XMLStreamException {
        if (prefix == null || prefix.length() == 0 || prefix.equals("xmlns")) {
            this.writeDefaultNamespace(nsURI);
            return;
        }
        if (!this.mStartElementOpen) {
            SimpleNsStreamWriter.throwOutputError("Trying to write a namespace declaration when there is no open start element.");
        }
        if (!this.mXml11 && nsURI.length() == 0) {
            SimpleNsStreamWriter.throwOutputError(ErrorConsts.ERR_NS_EMPTY);
        }
        this.setPrefix(prefix, nsURI);
        this.doWriteNamespace(prefix, nsURI);
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.mCurrElem.setDefaultNsUri(uri);
    }

    public void doSetPrefix(String prefix, String uri) throws XMLStreamException {
        this.mCurrElem.addPrefix(prefix, uri);
    }

    public void writeStartElement(StartElement elem) throws XMLStreamException {
        String prefix;
        QName name = elem.getName();
        Iterator<Attribute> it = elem.getNamespaces();
        while (it.hasNext()) {
            Namespace ns = it.next();
            prefix = ns.getPrefix();
            if (prefix == null || prefix.length() == 0) {
                this.setDefaultNamespace(ns.getNamespaceURI());
                continue;
            }
            this.setPrefix(prefix, ns.getNamespaceURI());
        }
        String nsURI = name.getNamespaceURI();
        if (nsURI == null) {
            this.writeStartElement(name.getLocalPart());
        } else {
            prefix = name.getPrefix();
            this.writeStartElement(prefix, name.getLocalPart(), nsURI);
        }
        it = elem.getNamespaces();
        while (it.hasNext()) {
            Namespace ns = it.next();
            String prefix2 = ns.getPrefix();
            if (prefix2 == null || prefix2.length() == 0) {
                this.writeDefaultNamespace(ns.getNamespaceURI());
                continue;
            }
            this.writeNamespace(prefix2, ns.getNamespaceURI());
        }
        it = elem.getAttributes();
        while (it.hasNext()) {
            Attribute attr = it.next();
            name = attr.getName();
            nsURI = name.getNamespaceURI();
            if (nsURI != null && nsURI.length() > 0) {
                this.writeAttribute(name.getPrefix(), nsURI, name.getLocalPart(), attr.getValue());
                continue;
            }
            this.writeAttribute(name.getLocalPart(), attr.getValue());
        }
    }

    protected void writeStartOrEmpty(String localName, String nsURI) throws XMLStreamException {
        String prefix = this.mCurrElem.getPrefix(nsURI);
        if (prefix == null) {
            throw new XMLStreamException("Unbound namespace URI '" + nsURI + "'");
        }
        this.checkStartElement(localName, prefix);
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, nsURI, prefix);
        }
        if (this.mOutputElemPool != null) {
            SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, prefix, localName, nsURI);
            --this.mPoolSize;
            this.mCurrElem = newCurr;
        } else {
            this.mCurrElem = this.mCurrElem.createChild(prefix, localName, nsURI);
        }
        this.doWriteStartTag(prefix, localName);
    }

    protected void writeStartOrEmpty(String prefix, String localName, String nsURI) throws XMLStreamException {
        this.checkStartElement(localName, prefix);
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, nsURI, prefix);
        }
        if (this.mOutputElemPool != null) {
            SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, prefix, localName, nsURI);
            --this.mPoolSize;
            this.mCurrElem = newCurr;
        } else {
            this.mCurrElem = this.mCurrElem.createChild(prefix, localName, nsURI);
        }
        this.doWriteStartTag(prefix, localName);
    }

    public final void copyStartElement(InputElementStack elemStack, AttributeCollector attrCollector) throws IOException, XMLStreamException {
        int attrCount;
        String uri;
        String prefix;
        int i;
        int nsCount = elemStack.getCurrentNsCount();
        if (nsCount > 0) {
            for (i = 0; i < nsCount; ++i) {
                prefix = elemStack.getLocalNsPrefix(i);
                uri = elemStack.getLocalNsURI(i);
                if (prefix == null || prefix.length() == 0) {
                    this.setDefaultNamespace(uri);
                    continue;
                }
                this.setPrefix(prefix, uri);
            }
        }
        this.writeStartElement(elemStack.getPrefix(), elemStack.getLocalName(), elemStack.getNsURI());
        if (nsCount > 0) {
            for (i = 0; i < nsCount; ++i) {
                prefix = elemStack.getLocalNsPrefix(i);
                uri = elemStack.getLocalNsURI(i);
                if (prefix == null || prefix.length() == 0) {
                    this.writeDefaultNamespace(uri);
                    continue;
                }
                this.writeNamespace(prefix, uri);
            }
        }
        int n = attrCount = this.mConfig.willCopyDefaultAttrs() ? attrCollector.getCount() : attrCollector.getSpecifiedCount();
        if (attrCount > 0) {
            for (int i2 = 0; i2 < attrCount; ++i2) {
                attrCollector.writeAttribute(i2, this.mWriter);
            }
        }
    }

    public String validateQNamePrefix(QName name) {
        return name.getPrefix();
    }
}

