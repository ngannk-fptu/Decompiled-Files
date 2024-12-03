/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.sw.TypedStreamWriter;
import com.ctc.wstx.sw.XmlWriter;
import com.ctc.wstx.util.EmptyNamespaceContext;
import com.ctc.wstx.util.StringVector;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;

public class NonNsStreamWriter
extends TypedStreamWriter {
    final StringVector mElements = new StringVector(32);
    TreeSet mAttrNames;

    public NonNsStreamWriter(XmlWriter xw, String enc, WriterConfig cfg) {
        super(xw, enc, cfg);
    }

    public NamespaceContext getNamespaceContext() {
        return EmptyNamespaceContext.getInstance();
    }

    public String getPrefix(String uri) {
        return null;
    }

    public void setDefaultNamespace(String uri) throws XMLStreamException {
        NonNsStreamWriter.reportIllegalArg("Can not set default namespace for non-namespace writer.");
    }

    public void setNamespaceContext(NamespaceContext context) {
        NonNsStreamWriter.reportIllegalArg("Can not set NamespaceContext for non-namespace writer.");
    }

    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        NonNsStreamWriter.reportIllegalArg("Can not set namespace prefix for non-namespace writer.");
    }

    public void writeAttribute(String localName, String value) throws XMLStreamException {
        if (!this.mStartElementOpen && this.mCheckStructure) {
            NonNsStreamWriter.reportNwfStructure(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        if (this.mCheckAttrs) {
            if (this.mAttrNames == null) {
                this.mAttrNames = new TreeSet();
            }
            if (!this.mAttrNames.add(localName)) {
                NonNsStreamWriter.reportNwfAttr("Trying to write attribute '" + localName + "' twice");
            }
        }
        if (this.mValidator != null) {
            this.mValidator.validateAttribute(localName, "", "", value);
        }
        try {
            this.mWriter.writeAttribute(localName, value);
        }
        catch (IOException ioe) {
            NonNsStreamWriter.throwFromIOE(ioe);
        }
    }

    public void writeAttribute(String nsURI, String localName, String value) throws XMLStreamException {
        this.writeAttribute(localName, value);
    }

    public void writeAttribute(String prefix, String nsURI, String localName, String value) throws XMLStreamException {
        this.writeAttribute(localName, value);
    }

    public void writeDefaultNamespace(String nsURI) throws XMLStreamException {
        NonNsStreamWriter.reportIllegalMethod("Can not call writeDefaultNamespace namespaces with non-namespace writer.");
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.doWriteStartElement(localName);
        this.mEmptyElement = true;
    }

    public void writeEmptyElement(String nsURI, String localName) throws XMLStreamException {
        this.writeEmptyElement(localName);
    }

    public void writeEmptyElement(String prefix, String localName, String nsURI) throws XMLStreamException {
        this.writeEmptyElement(localName);
    }

    public void writeEndElement() throws XMLStreamException {
        this.doWriteEndTag(null, this.mCfgAutomaticEmptyElems);
    }

    public void writeNamespace(String prefix, String nsURI) throws XMLStreamException {
        NonNsStreamWriter.reportIllegalMethod("Can not set write namespaces with non-namespace writer.");
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        this.doWriteStartElement(localName);
        this.mEmptyElement = false;
    }

    public void writeStartElement(String nsURI, String localName) throws XMLStreamException {
        this.writeStartElement(localName);
    }

    public void writeStartElement(String prefix, String localName, String nsURI) throws XMLStreamException {
        this.writeStartElement(localName);
    }

    public void writeFullEndElement() throws XMLStreamException {
        this.doWriteEndTag(null, false);
    }

    public QName getCurrentElementName() {
        if (this.mElements.isEmpty()) {
            return null;
        }
        return new QName(this.mElements.getLastString());
    }

    public String getNamespaceURI(String prefix) {
        return null;
    }

    public void writeStartElement(StartElement elem) throws XMLStreamException {
        QName name = elem.getName();
        this.writeStartElement(name.getLocalPart());
        Iterator<Attribute> it = elem.getAttributes();
        while (it.hasNext()) {
            Attribute attr = it.next();
            name = attr.getName();
            this.writeAttribute(name.getLocalPart(), attr.getValue());
        }
    }

    public void writeEndElement(QName name) throws XMLStreamException {
        this.doWriteEndTag(this.mCheckStructure ? name.getLocalPart() : null, this.mCfgAutomaticEmptyElems);
    }

    protected void writeTypedAttribute(String prefix, String nsURI, String localName, AsciiValueEncoder enc) throws XMLStreamException {
        if (!this.mStartElementOpen && this.mCheckStructure) {
            NonNsStreamWriter.reportNwfStructure(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        if (this.mCheckAttrs) {
            if (this.mAttrNames == null) {
                this.mAttrNames = new TreeSet();
            }
            if (!this.mAttrNames.add(localName)) {
                NonNsStreamWriter.reportNwfAttr("Trying to write attribute '" + localName + "' twice");
            }
        }
        try {
            if (this.mValidator == null) {
                this.mWriter.writeTypedAttribute(localName, enc);
            } else {
                this.mWriter.writeTypedAttribute(null, localName, null, enc, this.mValidator, this.getCopyBuffer());
            }
        }
        catch (IOException ioe) {
            NonNsStreamWriter.throwFromIOE(ioe);
        }
    }

    protected void closeStartElement(boolean emptyElem) throws XMLStreamException {
        this.mStartElementOpen = false;
        if (this.mAttrNames != null) {
            this.mAttrNames.clear();
        }
        try {
            if (emptyElem) {
                this.mWriter.writeStartTagEmptyEnd();
            } else {
                this.mWriter.writeStartTagEnd();
            }
        }
        catch (IOException ioe) {
            NonNsStreamWriter.throwFromIOE(ioe);
        }
        if (this.mValidator != null) {
            this.mVldContent = this.mValidator.validateElementAndAttributes();
        }
        if (emptyElem) {
            String localName = this.mElements.removeLast();
            if (this.mElements.isEmpty()) {
                this.mState = 3;
            }
            if (this.mValidator != null) {
                this.mVldContent = this.mValidator.validateElementEnd(localName, "", "");
            }
        }
    }

    public void copyStartElement(InputElementStack elemStack, AttributeCollector attrCollector) throws IOException, XMLStreamException {
        int attrCount;
        int i;
        int nsCount;
        String prefix;
        String ln = elemStack.getLocalName();
        boolean nsAware = elemStack.isNamespaceAware();
        if (nsAware && (prefix = elemStack.getPrefix()) != null && prefix.length() > 0) {
            ln = prefix + ":" + ln;
        }
        this.writeStartElement(ln);
        if (nsAware && (nsCount = elemStack.getCurrentNsCount()) > 0) {
            for (i = 0; i < nsCount; ++i) {
                String prefix2 = elemStack.getLocalNsPrefix(i);
                prefix2 = prefix2 == null || prefix2.length() == 0 ? "xml" : "xmlns:" + prefix2;
                this.writeAttribute(prefix2, elemStack.getLocalNsURI(i));
            }
        }
        int n = attrCount = this.mConfig.willCopyDefaultAttrs() ? attrCollector.getCount() : attrCollector.getSpecifiedCount();
        if (attrCount > 0) {
            for (i = 0; i < attrCount; ++i) {
                attrCollector.writeAttribute(i, this.mWriter);
            }
        }
    }

    protected String getTopElementDesc() {
        return this.mElements.isEmpty() ? "#root" : this.mElements.getLastString();
    }

    public String validateQNamePrefix(QName name) {
        return name.getPrefix();
    }

    private void doWriteStartElement(String localName) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        } else if (this.mState == 1) {
            this.verifyRootElement(localName, null);
        } else if (this.mState == 3) {
            if (this.mCheckStructure) {
                NonNsStreamWriter.reportNwfStructure(ErrorConsts.WERR_PROLOG_SECOND_ROOT, localName);
            }
            this.mState = 2;
        }
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, "", "");
        }
        this.mStartElementOpen = true;
        this.mElements.addString(localName);
        try {
            this.mWriter.writeStartTagStart(localName);
        }
        catch (IOException ioe) {
            NonNsStreamWriter.throwFromIOE(ioe);
        }
    }

    private void doWriteEndTag(String expName, boolean allowEmpty) throws XMLStreamException {
        if (this.mStartElementOpen && this.mEmptyElement) {
            this.mEmptyElement = false;
            this.closeStartElement(true);
        }
        if (this.mState != 2) {
            if (this.mCheckStructure) {
                NonNsStreamWriter.reportNwfStructure("No open start element, when trying to write end element");
            }
            return;
        }
        String localName = this.mElements.removeLast();
        if (this.mCheckStructure && expName != null && !localName.equals(expName)) {
            NonNsStreamWriter.reportNwfStructure("Mismatching close element name, '" + localName + "'; expected '" + expName + "'.");
        }
        if (this.mStartElementOpen) {
            if (this.mValidator != null) {
                this.mVldContent = this.mValidator.validateElementAndAttributes();
            }
            this.mStartElementOpen = false;
            if (this.mAttrNames != null) {
                this.mAttrNames.clear();
            }
            try {
                if (allowEmpty) {
                    this.mWriter.writeStartTagEmptyEnd();
                    if (this.mElements.isEmpty()) {
                        this.mState = 3;
                    }
                    if (this.mValidator != null) {
                        this.mVldContent = this.mValidator.validateElementEnd(localName, "", "");
                    }
                    return;
                }
                this.mWriter.writeStartTagEnd();
            }
            catch (IOException ioe) {
                NonNsStreamWriter.throwFromIOE(ioe);
            }
        }
        try {
            this.mWriter.writeEndTag(localName);
        }
        catch (IOException ioe) {
            NonNsStreamWriter.throwFromIOE(ioe);
        }
        if (this.mElements.isEmpty()) {
            this.mState = 3;
        }
        if (this.mValidator != null) {
            this.mVldContent = this.mValidator.validateElementEnd(localName, "", "");
        }
    }
}

