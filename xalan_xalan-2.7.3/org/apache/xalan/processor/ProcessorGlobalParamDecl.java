/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import org.apache.xalan.processor.ProcessorTemplateElem;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.templates.ElemParam;
import org.apache.xalan.templates.ElemTemplateElement;
import org.xml.sax.SAXException;

class ProcessorGlobalParamDecl
extends ProcessorTemplateElem {
    static final long serialVersionUID = 1900450872353587350L;

    ProcessorGlobalParamDecl() {
    }

    @Override
    protected void appendAndPush(StylesheetHandler handler, ElemTemplateElement elem) throws SAXException {
        handler.pushElemTemplateElement(elem);
    }

    @Override
    public void endElement(StylesheetHandler handler, String uri, String localName, String rawName) throws SAXException {
        ElemParam v = (ElemParam)handler.getElemTemplateElement();
        handler.getStylesheet().appendChild(v);
        handler.getStylesheet().setParam(v);
        super.endElement(handler, uri, localName, rawName);
    }
}

