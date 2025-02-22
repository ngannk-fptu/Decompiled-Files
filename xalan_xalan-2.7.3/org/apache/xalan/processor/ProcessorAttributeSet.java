/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import javax.xml.transform.TransformerException;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.processor.XSLTElementProcessor;
import org.apache.xalan.templates.ElemAttributeSet;
import org.apache.xalan.templates.ElemTemplateElement;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class ProcessorAttributeSet
extends XSLTElementProcessor {
    static final long serialVersionUID = -6473739251316787552L;

    ProcessorAttributeSet() {
    }

    @Override
    public void startElement(StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes) throws SAXException {
        ElemAttributeSet eat = new ElemAttributeSet();
        eat.setLocaterInfo(handler.getLocator());
        try {
            eat.setPrefixes(handler.getNamespaceSupport());
        }
        catch (TransformerException te) {
            throw new SAXException(te);
        }
        eat.setDOMBackPointer(handler.getOriginatingNode());
        this.setPropertiesFromAttributes(handler, rawName, attributes, eat);
        handler.getStylesheet().setAttributeSet(eat);
        ElemTemplateElement parent = handler.getElemTemplateElement();
        parent.appendChild(eat);
        handler.pushElemTemplateElement(eat);
    }

    @Override
    public void endElement(StylesheetHandler handler, String uri, String localName, String rawName) throws SAXException {
        handler.popElemTemplateElement();
    }
}

