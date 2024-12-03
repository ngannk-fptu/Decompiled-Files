/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import org.apache.xalan.processor.ProcessorTemplateElem;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.templates.ElemExsltFuncResult;
import org.apache.xalan.templates.ElemExsltFunction;
import org.apache.xalan.templates.ElemParam;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemVariable;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessorExsltFuncResult
extends ProcessorTemplateElem {
    static final long serialVersionUID = 6451230911473482423L;

    @Override
    public void startElement(StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes) throws SAXException {
        ElemTemplateElement ancestor;
        String msg = "";
        super.startElement(handler, uri, localName, rawName, attributes);
        for (ancestor = handler.getElemTemplateElement().getParentElem(); ancestor != null && !(ancestor instanceof ElemExsltFunction); ancestor = ancestor.getParentElem()) {
            if (!(ancestor instanceof ElemVariable) && !(ancestor instanceof ElemParam) && !(ancestor instanceof ElemExsltFuncResult)) continue;
            msg = "func:result cannot appear within a variable, parameter, or another func:result.";
            handler.error(msg, new SAXException(msg));
        }
        if (ancestor == null) {
            msg = "func:result must appear in a func:function element";
            handler.error(msg, new SAXException(msg));
        }
    }
}

