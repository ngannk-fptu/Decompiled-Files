/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.processor.XSLTElementProcessor;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.StylesheetComposed;
import org.apache.xalan.templates.StylesheetRoot;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessorStylesheetElement
extends XSLTElementProcessor {
    static final long serialVersionUID = -877798927447840792L;

    @Override
    public void startElement(StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes) throws SAXException {
        super.startElement(handler, uri, localName, rawName, attributes);
        try {
            Stylesheet stylesheet;
            int stylesheetType = handler.getStylesheetType();
            if (stylesheetType == 1) {
                try {
                    stylesheet = this.getStylesheetRoot(handler);
                }
                catch (TransformerConfigurationException tfe) {
                    throw new TransformerException(tfe);
                }
            } else {
                Stylesheet parent = handler.getStylesheet();
                if (stylesheetType == 3) {
                    StylesheetComposed sc = new StylesheetComposed(parent);
                    parent.setImport(sc);
                    stylesheet = sc;
                } else {
                    stylesheet = new Stylesheet(parent);
                    parent.setInclude(stylesheet);
                }
            }
            stylesheet.setDOMBackPointer(handler.getOriginatingNode());
            stylesheet.setLocaterInfo(handler.getLocator());
            stylesheet.setPrefixes(handler.getNamespaceSupport());
            handler.pushStylesheet(stylesheet);
            this.setPropertiesFromAttributes(handler, rawName, attributes, handler.getStylesheet());
            handler.pushElemTemplateElement(handler.getStylesheet());
        }
        catch (TransformerException te) {
            throw new SAXException(te);
        }
    }

    protected Stylesheet getStylesheetRoot(StylesheetHandler handler) throws TransformerConfigurationException {
        StylesheetRoot stylesheet = new StylesheetRoot(handler.getSchema(), handler.getStylesheetProcessor().getErrorListener());
        if (handler.getStylesheetProcessor().isSecureProcessing()) {
            stylesheet.setSecureProcessing(true);
        }
        return stylesheet;
    }

    @Override
    public void endElement(StylesheetHandler handler, String uri, String localName, String rawName) throws SAXException {
        super.endElement(handler, uri, localName, rawName);
        handler.popElemTemplateElement();
        handler.popStylesheet();
    }
}

