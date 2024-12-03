/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import java.util.List;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xalan.processor.ProcessorTemplateElem;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.processor.XSLTElementDef;
import org.apache.xalan.processor.XSLTElementProcessor;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.ElemExtensionCall;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.xml.utils.SAXSourceLocator;
import org.apache.xpath.XPath;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class ProcessorLRE
extends ProcessorTemplateElem {
    static final long serialVersionUID = -1490218021772101404L;

    @Override
    public void startElement(StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes) throws SAXException {
        try {
            ElemTemplateElement p = handler.getElemTemplateElement();
            boolean excludeXSLDecl = false;
            boolean isLREAsStyleSheet = false;
            if (null == p) {
                Stylesheet stylesheet;
                XSLTElementProcessor lreProcessor = handler.popProcessor();
                XSLTElementProcessor stylesheetProcessor = handler.getProcessorFor("http://www.w3.org/1999/XSL/Transform", "stylesheet", "xsl:stylesheet");
                handler.pushProcessor(lreProcessor);
                try {
                    stylesheet = this.getStylesheetRoot(handler);
                }
                catch (TransformerConfigurationException tfe) {
                    throw new TransformerException(tfe);
                }
                SAXSourceLocator slocator = new SAXSourceLocator();
                SAXSourceLocator locator = handler.getLocator();
                if (null != locator) {
                    slocator.setLineNumber(locator.getLineNumber());
                    slocator.setColumnNumber(locator.getColumnNumber());
                    slocator.setPublicId(locator.getPublicId());
                    slocator.setSystemId(locator.getSystemId());
                }
                stylesheet.setLocaterInfo(slocator);
                stylesheet.setPrefixes(handler.getNamespaceSupport());
                handler.pushStylesheet(stylesheet);
                isLREAsStyleSheet = true;
                AttributesImpl stylesheetAttrs = new AttributesImpl();
                AttributesImpl lreAttrs = new AttributesImpl();
                int n = attributes.getLength();
                for (int i = 0; i < n; ++i) {
                    String attrLocalName = attributes.getLocalName(i);
                    String attrUri = attributes.getURI(i);
                    String value = attributes.getValue(i);
                    if (null != attrUri && attrUri.equals("http://www.w3.org/1999/XSL/Transform")) {
                        stylesheetAttrs.addAttribute(null, attrLocalName, attrLocalName, attributes.getType(i), attributes.getValue(i));
                        continue;
                    }
                    if ((attrLocalName.startsWith("xmlns:") || attrLocalName.equals("xmlns")) && value.equals("http://www.w3.org/1999/XSL/Transform")) continue;
                    lreAttrs.addAttribute(attrUri, attrLocalName, attributes.getQName(i), attributes.getType(i), attributes.getValue(i));
                }
                attributes = lreAttrs;
                try {
                    stylesheetProcessor.setPropertiesFromAttributes(handler, "stylesheet", stylesheetAttrs, stylesheet);
                }
                catch (Exception e) {
                    if (stylesheet.getDeclaredPrefixes() == null || !this.declaredXSLNS(stylesheet)) {
                        throw new SAXException(XSLMessages.createWarning("WG_OLD_XSLT_NS", null));
                    }
                    throw new SAXException(e);
                }
                handler.pushElemTemplateElement(stylesheet);
                ElemTemplate template = new ElemTemplate();
                if (slocator != null) {
                    template.setLocaterInfo(slocator);
                }
                this.appendAndPush(handler, template);
                XPath rootMatch = new XPath("/", stylesheet, stylesheet, 1, handler.getStylesheetProcessor().getErrorListener());
                template.setMatch(rootMatch);
                stylesheet.setTemplate(template);
                p = handler.getElemTemplateElement();
                excludeXSLDecl = true;
            }
            XSLTElementDef def = this.getElemDef();
            Class classObject = def.getClassObject();
            boolean isExtension = false;
            boolean isComponentDecl = false;
            boolean isUnknownTopLevel = false;
            while (null != p) {
                ElemTemplateElement parentElem;
                if (p instanceof ElemLiteralResult) {
                    parentElem = (ElemLiteralResult)p;
                    isExtension = ((ElemLiteralResult)parentElem).containsExtensionElementURI(uri);
                } else if (p instanceof Stylesheet) {
                    parentElem = (Stylesheet)p;
                    isExtension = ((Stylesheet)parentElem).containsExtensionElementURI(uri);
                    if (!isExtension && null != uri && (uri.equals("http://xml.apache.org/xalan") || uri.equals("http://xml.apache.org/xslt"))) {
                        isComponentDecl = true;
                    } else {
                        isUnknownTopLevel = true;
                    }
                }
                if (isExtension) break;
                p = p.getParentElem();
            }
            ElemTemplateElement elem = null;
            try {
                elem = isExtension ? new ElemExtensionCall() : (isComponentDecl ? (ElemTemplateElement)classObject.newInstance() : (isUnknownTopLevel ? (ElemTemplateElement)classObject.newInstance() : (ElemTemplateElement)classObject.newInstance()));
                elem.setDOMBackPointer(handler.getOriginatingNode());
                elem.setLocaterInfo(handler.getLocator());
                elem.setPrefixes(handler.getNamespaceSupport(), excludeXSLDecl);
                if (elem instanceof ElemLiteralResult) {
                    ((ElemLiteralResult)elem).setNamespace(uri);
                    ((ElemLiteralResult)elem).setLocalName(localName);
                    ((ElemLiteralResult)elem).setRawName(rawName);
                    ((ElemLiteralResult)elem).setIsLiteralResultAsStylesheet(isLREAsStyleSheet);
                }
            }
            catch (InstantiationException ie) {
                handler.error("ER_FAILED_CREATING_ELEMLITRSLT", null, ie);
            }
            catch (IllegalAccessException iae) {
                handler.error("ER_FAILED_CREATING_ELEMLITRSLT", null, iae);
            }
            this.setPropertiesFromAttributes(handler, rawName, attributes, elem);
            if (!isExtension && elem instanceof ElemLiteralResult && (isExtension = ((ElemLiteralResult)elem).containsExtensionElementURI(uri))) {
                elem = new ElemExtensionCall();
                elem.setLocaterInfo(handler.getLocator());
                elem.setPrefixes(handler.getNamespaceSupport());
                ((ElemLiteralResult)elem).setNamespace(uri);
                ((ElemLiteralResult)elem).setLocalName(localName);
                ((ElemLiteralResult)elem).setRawName(rawName);
                this.setPropertiesFromAttributes(handler, rawName, attributes, elem);
            }
            this.appendAndPush(handler, elem);
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
        ElemTemplateElement elem = handler.getElemTemplateElement();
        if (elem instanceof ElemLiteralResult && ((ElemLiteralResult)elem).getIsLiteralResultAsStylesheet()) {
            handler.popStylesheet();
        }
        super.endElement(handler, uri, localName, rawName);
    }

    private boolean declaredXSLNS(Stylesheet stylesheet) {
        List declaredPrefixes = stylesheet.getDeclaredPrefixes();
        int n = declaredPrefixes.size();
        for (int i = 0; i < n; ++i) {
            XMLNSDecl decl = (XMLNSDecl)declaredPrefixes.get(i);
            if (!decl.getURI().equals("http://www.w3.org/1999/XSL/Transform")) continue;
            return true;
        }
        return false;
    }
}

