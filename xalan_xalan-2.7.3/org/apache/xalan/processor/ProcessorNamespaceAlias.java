/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.processor.XSLTElementProcessor;
import org.apache.xalan.templates.NamespaceAlias;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

class ProcessorNamespaceAlias
extends XSLTElementProcessor {
    static final long serialVersionUID = -6309867839007018964L;

    ProcessorNamespaceAlias() {
    }

    @Override
    public void startElement(StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes) throws SAXException {
        String resultNS;
        NamespaceAlias na = new NamespaceAlias(handler.nextUid());
        this.setPropertiesFromAttributes(handler, rawName, attributes, na);
        String prefix = na.getStylesheetPrefix();
        if (prefix.equals("#default")) {
            prefix = "";
            na.setStylesheetPrefix(prefix);
        }
        String stylesheetNS = handler.getNamespaceForPrefix(prefix);
        na.setStylesheetNamespace(stylesheetNS);
        prefix = na.getResultPrefix();
        if (prefix.equals("#default")) {
            prefix = "";
            na.setResultPrefix(prefix);
            resultNS = handler.getNamespaceForPrefix(prefix);
            if (null == resultNS) {
                handler.error("ER_INVALID_NAMESPACE_URI_VALUE_FOR_RESULT_PREFIX_FOR_DEFAULT", null, null);
            }
        } else {
            resultNS = handler.getNamespaceForPrefix(prefix);
            if (null == resultNS) {
                handler.error("ER_INVALID_SET_NAMESPACE_URI_VALUE_FOR_RESULT_PREFIX", new Object[]{prefix}, null);
            }
        }
        na.setResultNamespace(resultNS);
        handler.getStylesheet().setNamespaceAlias(na);
        handler.getStylesheet().appendChild(na);
    }
}

