/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import javax.xml.transform.TransformerException;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.processor.XSLTElementDef;
import org.apache.xalan.processor.XSLTElementProcessor;
import org.apache.xalan.templates.ElemTemplateElement;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessorTemplateElem
extends XSLTElementProcessor {
    static final long serialVersionUID = 8344994001943407235L;

    @Override
    public void startElement(StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes) throws SAXException {
        super.startElement(handler, uri, localName, rawName, attributes);
        try {
            XSLTElementDef def = this.getElemDef();
            Class classObject = def.getClassObject();
            ElemTemplateElement elem = null;
            try {
                elem = (ElemTemplateElement)classObject.newInstance();
                elem.setDOMBackPointer(handler.getOriginatingNode());
                elem.setLocaterInfo(handler.getLocator());
                elem.setPrefixes(handler.getNamespaceSupport());
            }
            catch (InstantiationException ie) {
                handler.error("ER_FAILED_CREATING_ELEMTMPL", null, ie);
            }
            catch (IllegalAccessException iae) {
                handler.error("ER_FAILED_CREATING_ELEMTMPL", null, iae);
            }
            this.setPropertiesFromAttributes(handler, rawName, attributes, elem);
            this.appendAndPush(handler, elem);
        }
        catch (TransformerException te) {
            throw new SAXException(te);
        }
    }

    protected void appendAndPush(StylesheetHandler handler, ElemTemplateElement elem) throws SAXException {
        ElemTemplateElement parent = handler.getElemTemplateElement();
        if (null != parent) {
            parent.appendChild(elem);
            handler.pushElemTemplateElement(elem);
        }
    }

    @Override
    public void endElement(StylesheetHandler handler, String uri, String localName, String rawName) throws SAXException {
        super.endElement(handler, uri, localName, rawName);
        handler.popElemTemplateElement().setEndLocaterInfo(handler.getLocator());
    }
}

