/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import java.util.ArrayList;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.processor.XSLTAttributeDef;
import org.apache.xalan.processor.XSLTElementDef;
import org.apache.xalan.processor.XSLTElementProcessor;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.KeyDeclaration;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class ProcessorKey
extends XSLTElementProcessor {
    static final long serialVersionUID = 4285205417566822979L;

    ProcessorKey() {
    }

    @Override
    public void startElement(StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes) throws SAXException {
        KeyDeclaration kd = new KeyDeclaration(handler.getStylesheet(), handler.nextUid());
        kd.setDOMBackPointer(handler.getOriginatingNode());
        kd.setLocaterInfo(handler.getLocator());
        this.setPropertiesFromAttributes(handler, rawName, attributes, kd);
        handler.getStylesheet().setKey(kd);
    }

    @Override
    void setPropertiesFromAttributes(StylesheetHandler handler, String rawName, Attributes attributes, ElemTemplateElement target) throws SAXException {
        XSLTElementDef def = this.getElemDef();
        ArrayList<XSLTAttributeDef> processedDefs = new ArrayList<XSLTAttributeDef>();
        int nAttrs = attributes.getLength();
        for (int i = 0; i < nAttrs; ++i) {
            String attrLocalName;
            String attrUri = attributes.getURI(i);
            XSLTAttributeDef attrDef = def.getAttributeDef(attrUri, attrLocalName = attributes.getLocalName(i));
            if (null == attrDef) {
                handler.error(attributes.getQName(i) + "attribute is not allowed on the " + rawName + " element!", null);
                continue;
            }
            String valueString = attributes.getValue(i);
            if (valueString.indexOf("key(") >= 0) {
                handler.error(XSLMessages.createMessage("ER_INVALID_KEY_CALL", null), null);
            }
            processedDefs.add(attrDef);
            attrDef.setAttrValue(handler, attrUri, attrLocalName, attributes.getQName(i), attributes.getValue(i), target);
        }
        for (XSLTAttributeDef attrDef : def.getAttributes()) {
            String defVal = attrDef.getDefault();
            if (null != defVal && !processedDefs.contains(attrDef)) {
                attrDef.setDefAttrValue(handler, target);
            }
            if (!attrDef.getRequired() || processedDefs.contains(attrDef)) continue;
            handler.error(XSLMessages.createMessage("ER_REQUIRES_ATTRIB", new Object[]{rawName, attrDef.getName()}), null);
        }
    }
}

