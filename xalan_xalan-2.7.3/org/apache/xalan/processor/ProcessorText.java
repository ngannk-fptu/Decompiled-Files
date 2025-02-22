/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import org.apache.xalan.processor.ProcessorCharacters;
import org.apache.xalan.processor.ProcessorTemplateElem;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemText;
import org.xml.sax.SAXException;

public class ProcessorText
extends ProcessorTemplateElem {
    static final long serialVersionUID = 5170229307201307523L;

    @Override
    protected void appendAndPush(StylesheetHandler handler, ElemTemplateElement elem) throws SAXException {
        ProcessorCharacters charProcessor = (ProcessorCharacters)handler.getProcessorFor(null, "text()", "text");
        charProcessor.setXslTextElement((ElemText)elem);
        ElemTemplateElement parent = handler.getElemTemplateElement();
        parent.appendChild(elem);
        elem.setDOMBackPointer(handler.getOriginatingNode());
    }

    @Override
    public void endElement(StylesheetHandler handler, String uri, String localName, String rawName) throws SAXException {
        ProcessorCharacters charProcessor = (ProcessorCharacters)handler.getProcessorFor(null, "text()", "text");
        charProcessor.setXslTextElement(null);
    }
}

