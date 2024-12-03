/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.CSSContext
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.css.engine.SVG12CSSEngine
 *  org.apache.batik.css.engine.value.ShorthandManager
 *  org.apache.batik.css.engine.value.ValueManager
 *  org.apache.batik.css.parser.ExtendedParser
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.dom.AbstractStylableDocument
 *  org.apache.batik.dom.ExtensibleDOMImplementation$ElementFactory
 *  org.apache.batik.dom.GenericElement
 *  org.apache.batik.dom.events.DocumentEventSupport
 *  org.apache.batik.dom.events.DocumentEventSupport$EventFactory
 *  org.apache.batik.dom.events.EventSupport
 *  org.apache.batik.dom.svg12.SVGOMWheelEvent
 *  org.apache.batik.dom.svg12.XBLOMShadowTreeEvent
 *  org.apache.batik.dom.util.DOMUtilities
 *  org.apache.batik.util.ParsedURL
 *  org.w3c.css.sac.InputSource
 */
package org.apache.batik.anim.dom;

import java.net.URL;
import java.util.HashMap;
import org.apache.batik.anim.dom.BindableElement;
import org.apache.batik.anim.dom.SVG12OMDocument;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMFlowDivElement;
import org.apache.batik.anim.dom.SVGOMFlowLineElement;
import org.apache.batik.anim.dom.SVGOMFlowParaElement;
import org.apache.batik.anim.dom.SVGOMFlowRegionBreakElement;
import org.apache.batik.anim.dom.SVGOMFlowRegionElement;
import org.apache.batik.anim.dom.SVGOMFlowRegionExcludeElement;
import org.apache.batik.anim.dom.SVGOMFlowRootElement;
import org.apache.batik.anim.dom.SVGOMFlowSpanElement;
import org.apache.batik.anim.dom.SVGOMHandlerElement;
import org.apache.batik.anim.dom.SVGOMMultiImageElement;
import org.apache.batik.anim.dom.SVGOMSolidColorElement;
import org.apache.batik.anim.dom.SVGOMSubImageElement;
import org.apache.batik.anim.dom.SVGOMSubImageRefElement;
import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.anim.dom.XBLOMContentElement;
import org.apache.batik.anim.dom.XBLOMDefinitionElement;
import org.apache.batik.anim.dom.XBLOMHandlerGroupElement;
import org.apache.batik.anim.dom.XBLOMImportElement;
import org.apache.batik.anim.dom.XBLOMShadowTreeElement;
import org.apache.batik.anim.dom.XBLOMTemplateElement;
import org.apache.batik.anim.dom.XBLOMXBLElement;
import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.SVG12CSSEngine;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.parser.ExtendedParser;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.AbstractStylableDocument;
import org.apache.batik.dom.ExtensibleDOMImplementation;
import org.apache.batik.dom.GenericElement;
import org.apache.batik.dom.events.DocumentEventSupport;
import org.apache.batik.dom.events.EventSupport;
import org.apache.batik.dom.svg12.SVGOMWheelEvent;
import org.apache.batik.dom.svg12.XBLOMShadowTreeEvent;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.util.ParsedURL;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

public class SVG12DOMImplementation
extends SVGDOMImplementation {
    protected static HashMap<String, ExtensibleDOMImplementation.ElementFactory> svg12Factories = new HashMap(svg11Factories);
    protected static HashMap<String, ExtensibleDOMImplementation.ElementFactory> xblFactories;
    protected static final DOMImplementation DOM_IMPLEMENTATION;

    public SVG12DOMImplementation() {
        this.factories = svg12Factories;
        this.registerFeature("CSS", "2.0");
        this.registerFeature("StyleSheets", "2.0");
        this.registerFeature("SVG", new String[]{"1.0", "1.1", "1.2"});
        this.registerFeature("SVGEvents", new String[]{"1.0", "1.1", "1.2"});
    }

    @Override
    public CSSEngine createCSSEngine(AbstractStylableDocument doc, CSSContext ctx, ExtendedParser ep, ValueManager[] vms, ShorthandManager[] sms) {
        ParsedURL durl = ((SVGOMDocument)doc).getParsedURL();
        SVG12CSSEngine result = new SVG12CSSEngine((Document)doc, durl, ep, vms, sms, ctx);
        URL url = ((Object)((Object)this)).getClass().getResource("resources/UserAgentStyleSheet.css");
        if (url != null) {
            ParsedURL purl = new ParsedURL(url);
            InputSource is = new InputSource(purl.toString());
            result.setUserAgentStyleSheet(result.parseStyleSheet(is, purl, "all"));
        }
        return result;
    }

    @Override
    public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) throws DOMException {
        SVG12OMDocument result = new SVG12OMDocument(doctype, (DOMImplementation)((Object)this));
        result.setIsSVG12(true);
        if (qualifiedName != null) {
            result.appendChild(result.createElementNS(namespaceURI, qualifiedName));
        }
        return result;
    }

    @Override
    public Element createElementNS(AbstractDocument document, String namespaceURI, String qualifiedName) {
        ExtensibleDOMImplementation.ElementFactory cef;
        ExtensibleDOMImplementation.ElementFactory ef;
        if (namespaceURI == null) {
            return new GenericElement(qualifiedName.intern(), document);
        }
        String name = DOMUtilities.getLocalName((String)qualifiedName);
        String prefix = DOMUtilities.getPrefix((String)qualifiedName);
        if ("http://www.w3.org/2000/svg".equals(namespaceURI) ? (ef = (ExtensibleDOMImplementation.ElementFactory)this.factories.get(name)) != null : "http://www.w3.org/2004/xbl".equals(namespaceURI) && (ef = xblFactories.get(name)) != null) {
            return ef.create(prefix, (Document)document);
        }
        if (this.customFactories != null && (cef = (ExtensibleDOMImplementation.ElementFactory)this.customFactories.get((Object)namespaceURI, (Object)name)) != null) {
            return cef.create(prefix, (Document)document);
        }
        return new BindableElement(prefix, document, namespaceURI, name);
    }

    @Override
    public DocumentEventSupport createDocumentEventSupport() {
        DocumentEventSupport result = super.createDocumentEventSupport();
        result.registerEventFactory("WheelEvent", new DocumentEventSupport.EventFactory(){

            public Event createEvent() {
                return new SVGOMWheelEvent();
            }
        });
        result.registerEventFactory("ShadowTreeEvent", new DocumentEventSupport.EventFactory(){

            public Event createEvent() {
                return new XBLOMShadowTreeEvent();
            }
        });
        return result;
    }

    public EventSupport createEventSupport(AbstractNode n) {
        return new XBLEventSupport(n);
    }

    public static DOMImplementation getDOMImplementation() {
        return DOM_IMPLEMENTATION;
    }

    static {
        svg12Factories.put("flowDiv", new FlowDivElementFactory());
        svg12Factories.put("flowLine", new FlowLineElementFactory());
        svg12Factories.put("flowPara", new FlowParaElementFactory());
        svg12Factories.put("flowRegionBreak", new FlowRegionBreakElementFactory());
        svg12Factories.put("flowRegion", new FlowRegionElementFactory());
        svg12Factories.put("flowRegionExclude", new FlowRegionExcludeElementFactory());
        svg12Factories.put("flowRoot", new FlowRootElementFactory());
        svg12Factories.put("flowSpan", new FlowSpanElementFactory());
        svg12Factories.put("handler", new HandlerElementFactory());
        svg12Factories.put("multiImage", new MultiImageElementFactory());
        svg12Factories.put("solidColor", new SolidColorElementFactory());
        svg12Factories.put("subImage", new SubImageElementFactory());
        svg12Factories.put("subImageRef", new SubImageRefElementFactory());
        xblFactories = new HashMap();
        xblFactories.put("xbl", new XBLXBLElementFactory());
        xblFactories.put("definition", new XBLDefinitionElementFactory());
        xblFactories.put("template", new XBLTemplateElementFactory());
        xblFactories.put("content", new XBLContentElementFactory());
        xblFactories.put("handlerGroup", new XBLHandlerGroupElementFactory());
        xblFactories.put("import", new XBLImportElementFactory());
        xblFactories.put("shadowTree", new XBLShadowTreeElementFactory());
        DOM_IMPLEMENTATION = new SVG12DOMImplementation();
    }

    protected static class XBLShadowTreeElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new XBLOMShadowTreeElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class XBLImportElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new XBLOMImportElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class XBLHandlerGroupElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new XBLOMHandlerGroupElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class XBLContentElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new XBLOMContentElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class XBLTemplateElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new XBLOMTemplateElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class XBLDefinitionElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new XBLOMDefinitionElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class XBLXBLElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new XBLOMXBLElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class SubImageRefElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMSubImageRefElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class SubImageElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMSubImageElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class SolidColorElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMSolidColorElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class MultiImageElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMMultiImageElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class HandlerElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMHandlerElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FlowSpanElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFlowSpanElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FlowRootElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFlowRootElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FlowRegionExcludeElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFlowRegionExcludeElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FlowRegionElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFlowRegionElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FlowRegionBreakElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFlowRegionBreakElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FlowParaElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFlowParaElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FlowLineElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFlowLineElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FlowDivElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFlowDivElement(prefix, (AbstractDocument)doc);
        }
    }
}

