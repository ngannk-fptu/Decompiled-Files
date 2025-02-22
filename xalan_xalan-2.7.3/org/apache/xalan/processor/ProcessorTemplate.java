/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import org.apache.xalan.processor.ProcessorTemplateElem;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.xml.sax.SAXException;

class ProcessorTemplate
extends ProcessorTemplateElem {
    static final long serialVersionUID = -8457812845473603860L;

    ProcessorTemplate() {
    }

    @Override
    protected void appendAndPush(StylesheetHandler handler, ElemTemplateElement elem) throws SAXException {
        super.appendAndPush(handler, elem);
        elem.setDOMBackPointer(handler.getOriginatingNode());
        handler.getStylesheet().setTemplate((ElemTemplate)elem);
    }
}

