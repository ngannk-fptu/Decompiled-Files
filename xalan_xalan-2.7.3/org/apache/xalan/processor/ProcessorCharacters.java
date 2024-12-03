/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import javax.xml.transform.TransformerException;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.processor.XSLTElementProcessor;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemText;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xml.utils.XMLCharacterRecognizer;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ProcessorCharacters
extends XSLTElementProcessor {
    static final long serialVersionUID = 8632900007814162650L;
    protected Node m_firstBackPointer = null;
    private StringBuffer m_accumulator = new StringBuffer();
    private ElemText m_xslTextElement;

    @Override
    public void startNonText(StylesheetHandler handler) throws SAXException {
        int nChars;
        if (this == handler.getCurrentProcessor()) {
            handler.popProcessor();
        }
        if ((nChars = this.m_accumulator.length()) > 0 && (null != this.m_xslTextElement || !XMLCharacterRecognizer.isWhiteSpace(this.m_accumulator)) || handler.isSpacePreserve()) {
            ElemTextLiteral elem = new ElemTextLiteral();
            elem.setDOMBackPointer(this.m_firstBackPointer);
            elem.setLocaterInfo(handler.getLocator());
            try {
                elem.setPrefixes(handler.getNamespaceSupport());
            }
            catch (TransformerException te) {
                throw new SAXException(te);
            }
            boolean doe = null != this.m_xslTextElement ? this.m_xslTextElement.getDisableOutputEscaping() : false;
            elem.setDisableOutputEscaping(doe);
            elem.setPreserveSpace(true);
            char[] chars = new char[nChars];
            this.m_accumulator.getChars(0, nChars, chars, 0);
            elem.setChars(chars);
            ElemTemplateElement parent = handler.getElemTemplateElement();
            parent.appendChild(elem);
        }
        this.m_accumulator.setLength(0);
        this.m_firstBackPointer = null;
    }

    @Override
    public void characters(StylesheetHandler handler, char[] ch, int start, int length) throws SAXException {
        this.m_accumulator.append(ch, start, length);
        if (null == this.m_firstBackPointer) {
            this.m_firstBackPointer = handler.getOriginatingNode();
        }
        if (this != handler.getCurrentProcessor()) {
            handler.pushProcessor(this);
        }
    }

    @Override
    public void endElement(StylesheetHandler handler, String uri, String localName, String rawName) throws SAXException {
        this.startNonText(handler);
        handler.getCurrentProcessor().endElement(handler, uri, localName, rawName);
        handler.popProcessor();
    }

    void setXslTextElement(ElemText xslTextElement) {
        this.m_xslTextElement = xslTextElement;
    }
}

