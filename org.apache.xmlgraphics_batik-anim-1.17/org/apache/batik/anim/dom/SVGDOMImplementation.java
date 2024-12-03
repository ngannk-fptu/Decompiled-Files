/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.dom.CSSOMSVGViewCSS
 *  org.apache.batik.css.engine.CSSContext
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.css.engine.SVGCSSEngine
 *  org.apache.batik.css.engine.value.ShorthandManager
 *  org.apache.batik.css.engine.value.ValueManager
 *  org.apache.batik.css.parser.ExtendedParser
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.AbstractStylableDocument
 *  org.apache.batik.dom.ExtensibleDOMImplementation
 *  org.apache.batik.dom.ExtensibleDOMImplementation$ElementFactory
 *  org.apache.batik.dom.events.DOMTimeEvent
 *  org.apache.batik.dom.events.DocumentEventSupport
 *  org.apache.batik.dom.events.DocumentEventSupport$EventFactory
 *  org.apache.batik.dom.svg.SVGOMEvent
 *  org.apache.batik.dom.util.CSSStyleDeclarationFactory
 *  org.apache.batik.dom.util.DOMUtilities
 *  org.apache.batik.i18n.LocalizableSupport
 *  org.apache.batik.util.ParsedURL
 *  org.w3c.css.sac.InputSource
 */
package org.apache.batik.anim.dom;

import java.net.URL;
import java.util.HashMap;
import org.apache.batik.anim.dom.SVGOMAElement;
import org.apache.batik.anim.dom.SVGOMAltGlyphDefElement;
import org.apache.batik.anim.dom.SVGOMAltGlyphElement;
import org.apache.batik.anim.dom.SVGOMAltGlyphItemElement;
import org.apache.batik.anim.dom.SVGOMAnimateColorElement;
import org.apache.batik.anim.dom.SVGOMAnimateElement;
import org.apache.batik.anim.dom.SVGOMAnimateMotionElement;
import org.apache.batik.anim.dom.SVGOMAnimateTransformElement;
import org.apache.batik.anim.dom.SVGOMCircleElement;
import org.apache.batik.anim.dom.SVGOMClipPathElement;
import org.apache.batik.anim.dom.SVGOMColorProfileElement;
import org.apache.batik.anim.dom.SVGOMCursorElement;
import org.apache.batik.anim.dom.SVGOMDefinitionSrcElement;
import org.apache.batik.anim.dom.SVGOMDefsElement;
import org.apache.batik.anim.dom.SVGOMDescElement;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMEllipseElement;
import org.apache.batik.anim.dom.SVGOMFEBlendElement;
import org.apache.batik.anim.dom.SVGOMFEColorMatrixElement;
import org.apache.batik.anim.dom.SVGOMFEComponentTransferElement;
import org.apache.batik.anim.dom.SVGOMFECompositeElement;
import org.apache.batik.anim.dom.SVGOMFEConvolveMatrixElement;
import org.apache.batik.anim.dom.SVGOMFEDiffuseLightingElement;
import org.apache.batik.anim.dom.SVGOMFEDisplacementMapElement;
import org.apache.batik.anim.dom.SVGOMFEDistantLightElement;
import org.apache.batik.anim.dom.SVGOMFEFloodElement;
import org.apache.batik.anim.dom.SVGOMFEFuncAElement;
import org.apache.batik.anim.dom.SVGOMFEFuncBElement;
import org.apache.batik.anim.dom.SVGOMFEFuncGElement;
import org.apache.batik.anim.dom.SVGOMFEFuncRElement;
import org.apache.batik.anim.dom.SVGOMFEGaussianBlurElement;
import org.apache.batik.anim.dom.SVGOMFEImageElement;
import org.apache.batik.anim.dom.SVGOMFEMergeElement;
import org.apache.batik.anim.dom.SVGOMFEMergeNodeElement;
import org.apache.batik.anim.dom.SVGOMFEMorphologyElement;
import org.apache.batik.anim.dom.SVGOMFEOffsetElement;
import org.apache.batik.anim.dom.SVGOMFEPointLightElement;
import org.apache.batik.anim.dom.SVGOMFESpecularLightingElement;
import org.apache.batik.anim.dom.SVGOMFESpotLightElement;
import org.apache.batik.anim.dom.SVGOMFETileElement;
import org.apache.batik.anim.dom.SVGOMFETurbulenceElement;
import org.apache.batik.anim.dom.SVGOMFilterElement;
import org.apache.batik.anim.dom.SVGOMFontElement;
import org.apache.batik.anim.dom.SVGOMFontFaceElement;
import org.apache.batik.anim.dom.SVGOMFontFaceFormatElement;
import org.apache.batik.anim.dom.SVGOMFontFaceNameElement;
import org.apache.batik.anim.dom.SVGOMFontFaceSrcElement;
import org.apache.batik.anim.dom.SVGOMFontFaceUriElement;
import org.apache.batik.anim.dom.SVGOMForeignObjectElement;
import org.apache.batik.anim.dom.SVGOMGElement;
import org.apache.batik.anim.dom.SVGOMGlyphElement;
import org.apache.batik.anim.dom.SVGOMGlyphRefElement;
import org.apache.batik.anim.dom.SVGOMHKernElement;
import org.apache.batik.anim.dom.SVGOMImageElement;
import org.apache.batik.anim.dom.SVGOMLineElement;
import org.apache.batik.anim.dom.SVGOMLinearGradientElement;
import org.apache.batik.anim.dom.SVGOMMPathElement;
import org.apache.batik.anim.dom.SVGOMMarkerElement;
import org.apache.batik.anim.dom.SVGOMMaskElement;
import org.apache.batik.anim.dom.SVGOMMetadataElement;
import org.apache.batik.anim.dom.SVGOMMissingGlyphElement;
import org.apache.batik.anim.dom.SVGOMPathElement;
import org.apache.batik.anim.dom.SVGOMPatternElement;
import org.apache.batik.anim.dom.SVGOMPolygonElement;
import org.apache.batik.anim.dom.SVGOMPolylineElement;
import org.apache.batik.anim.dom.SVGOMRadialGradientElement;
import org.apache.batik.anim.dom.SVGOMRectElement;
import org.apache.batik.anim.dom.SVGOMSVGElement;
import org.apache.batik.anim.dom.SVGOMScriptElement;
import org.apache.batik.anim.dom.SVGOMSetElement;
import org.apache.batik.anim.dom.SVGOMStopElement;
import org.apache.batik.anim.dom.SVGOMStyleElement;
import org.apache.batik.anim.dom.SVGOMSwitchElement;
import org.apache.batik.anim.dom.SVGOMSymbolElement;
import org.apache.batik.anim.dom.SVGOMTRefElement;
import org.apache.batik.anim.dom.SVGOMTSpanElement;
import org.apache.batik.anim.dom.SVGOMTextElement;
import org.apache.batik.anim.dom.SVGOMTextPathElement;
import org.apache.batik.anim.dom.SVGOMTitleElement;
import org.apache.batik.anim.dom.SVGOMUseElement;
import org.apache.batik.anim.dom.SVGOMVKernElement;
import org.apache.batik.anim.dom.SVGOMViewElement;
import org.apache.batik.css.dom.CSSOMSVGViewCSS;
import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.parser.ExtendedParser;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractStylableDocument;
import org.apache.batik.dom.ExtensibleDOMImplementation;
import org.apache.batik.dom.events.DOMTimeEvent;
import org.apache.batik.dom.events.DocumentEventSupport;
import org.apache.batik.dom.svg.SVGOMEvent;
import org.apache.batik.dom.util.CSSStyleDeclarationFactory;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.i18n.LocalizableSupport;
import org.apache.batik.util.ParsedURL;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.ViewCSS;
import org.w3c.dom.events.Event;
import org.w3c.dom.stylesheets.StyleSheet;

public class SVGDOMImplementation
extends ExtensibleDOMImplementation
implements CSSStyleDeclarationFactory {
    public static final String SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg";
    protected static final String RESOURCES = "org.apache.batik.dom.svg.resources.Messages";
    protected HashMap<String, ExtensibleDOMImplementation.ElementFactory> factories = svg11Factories;
    protected static HashMap<String, ExtensibleDOMImplementation.ElementFactory> svg11Factories = new HashMap();
    protected static final DOMImplementation DOM_IMPLEMENTATION;

    public static DOMImplementation getDOMImplementation() {
        return DOM_IMPLEMENTATION;
    }

    public SVGDOMImplementation() {
        this.registerFeature("CSS", "2.0");
        this.registerFeature("StyleSheets", "2.0");
        this.registerFeature("SVG", new String[]{"1.0", "1.1"});
        this.registerFeature("SVGEvents", new String[]{"1.0", "1.1"});
    }

    protected void initLocalizable() {
        this.localizableSupport = new LocalizableSupport(RESOURCES, ((Object)((Object)this)).getClass().getClassLoader());
    }

    public CSSEngine createCSSEngine(AbstractStylableDocument doc, CSSContext ctx, ExtendedParser ep, ValueManager[] vms, ShorthandManager[] sms) {
        ParsedURL durl = ((SVGOMDocument)doc).getParsedURL();
        SVGCSSEngine result = new SVGCSSEngine((Document)doc, durl, ep, vms, sms, ctx);
        URL url = ((Object)((Object)this)).getClass().getResource("resources/UserAgentStyleSheet.css");
        if (url != null) {
            ParsedURL purl = new ParsedURL(url);
            InputSource is = new InputSource(purl.toString());
            result.setUserAgentStyleSheet(result.parseStyleSheet(is, purl, "all"));
        }
        return result;
    }

    public ViewCSS createViewCSS(AbstractStylableDocument doc) {
        return new CSSOMSVGViewCSS(doc.getCSSEngine());
    }

    public Document createDocument(String namespaceURI, String qualifiedName, DocumentType doctype) throws DOMException {
        SVGOMDocument result = new SVGOMDocument(doctype, (DOMImplementation)((Object)this));
        if (qualifiedName != null) {
            result.appendChild(result.createElementNS(namespaceURI, qualifiedName));
        }
        return result;
    }

    public CSSStyleSheet createCSSStyleSheet(String title, String media) {
        throw new UnsupportedOperationException("DOMImplementationCSS.createCSSStyleSheet is not implemented");
    }

    public CSSStyleDeclaration createCSSStyleDeclaration() {
        throw new UnsupportedOperationException("CSSStyleDeclarationFactory.createCSSStyleDeclaration is not implemented");
    }

    public StyleSheet createStyleSheet(Node n, HashMap<String, String> attrs) {
        throw new UnsupportedOperationException("StyleSheetFactory.createStyleSheet is not implemented");
    }

    public CSSStyleSheet getUserAgentStyleSheet() {
        throw new UnsupportedOperationException("StyleSheetFactory.getUserAgentStyleSheet is not implemented");
    }

    public Element createElementNS(AbstractDocument document, String namespaceURI, String qualifiedName) {
        if (SVG_NAMESPACE_URI.equals(namespaceURI)) {
            String name = DOMUtilities.getLocalName((String)qualifiedName);
            ExtensibleDOMImplementation.ElementFactory ef = this.factories.get(name);
            if (ef != null) {
                return ef.create(DOMUtilities.getPrefix((String)qualifiedName), (Document)document);
            }
            throw document.createDOMException((short)8, "invalid.element", new Object[]{namespaceURI, qualifiedName});
        }
        return super.createElementNS(document, namespaceURI, qualifiedName);
    }

    public DocumentEventSupport createDocumentEventSupport() {
        DocumentEventSupport result = new DocumentEventSupport();
        result.registerEventFactory("SVGEvents", new DocumentEventSupport.EventFactory(){

            public Event createEvent() {
                return new SVGOMEvent();
            }
        });
        result.registerEventFactory("TimeEvent", new DocumentEventSupport.EventFactory(){

            public Event createEvent() {
                return new DOMTimeEvent();
            }
        });
        return result;
    }

    static {
        svg11Factories.put("a", new AElementFactory());
        svg11Factories.put("altGlyph", new AltGlyphElementFactory());
        svg11Factories.put("altGlyphDef", new AltGlyphDefElementFactory());
        svg11Factories.put("altGlyphItem", new AltGlyphItemElementFactory());
        svg11Factories.put("animate", new AnimateElementFactory());
        svg11Factories.put("animateColor", new AnimateColorElementFactory());
        svg11Factories.put("animateMotion", new AnimateMotionElementFactory());
        svg11Factories.put("animateTransform", new AnimateTransformElementFactory());
        svg11Factories.put("circle", new CircleElementFactory());
        svg11Factories.put("clipPath", new ClipPathElementFactory());
        svg11Factories.put("color-profile", new ColorProfileElementFactory());
        svg11Factories.put("cursor", new CursorElementFactory());
        svg11Factories.put("definition-src", new DefinitionSrcElementFactory());
        svg11Factories.put("defs", new DefsElementFactory());
        svg11Factories.put("desc", new DescElementFactory());
        svg11Factories.put("ellipse", new EllipseElementFactory());
        svg11Factories.put("feBlend", new FeBlendElementFactory());
        svg11Factories.put("feColorMatrix", new FeColorMatrixElementFactory());
        svg11Factories.put("feComponentTransfer", new FeComponentTransferElementFactory());
        svg11Factories.put("feComposite", new FeCompositeElementFactory());
        svg11Factories.put("feConvolveMatrix", new FeConvolveMatrixElementFactory());
        svg11Factories.put("feDiffuseLighting", new FeDiffuseLightingElementFactory());
        svg11Factories.put("feDisplacementMap", new FeDisplacementMapElementFactory());
        svg11Factories.put("feDistantLight", new FeDistantLightElementFactory());
        svg11Factories.put("feFlood", new FeFloodElementFactory());
        svg11Factories.put("feFuncA", new FeFuncAElementFactory());
        svg11Factories.put("feFuncR", new FeFuncRElementFactory());
        svg11Factories.put("feFuncG", new FeFuncGElementFactory());
        svg11Factories.put("feFuncB", new FeFuncBElementFactory());
        svg11Factories.put("feGaussianBlur", new FeGaussianBlurElementFactory());
        svg11Factories.put("feImage", new FeImageElementFactory());
        svg11Factories.put("feMerge", new FeMergeElementFactory());
        svg11Factories.put("feMergeNode", new FeMergeNodeElementFactory());
        svg11Factories.put("feMorphology", new FeMorphologyElementFactory());
        svg11Factories.put("feOffset", new FeOffsetElementFactory());
        svg11Factories.put("fePointLight", new FePointLightElementFactory());
        svg11Factories.put("feSpecularLighting", new FeSpecularLightingElementFactory());
        svg11Factories.put("feSpotLight", new FeSpotLightElementFactory());
        svg11Factories.put("feTile", new FeTileElementFactory());
        svg11Factories.put("feTurbulence", new FeTurbulenceElementFactory());
        svg11Factories.put("filter", new FilterElementFactory());
        svg11Factories.put("font", new FontElementFactory());
        svg11Factories.put("font-face", new FontFaceElementFactory());
        svg11Factories.put("font-face-format", new FontFaceFormatElementFactory());
        svg11Factories.put("font-face-name", new FontFaceNameElementFactory());
        svg11Factories.put("font-face-src", new FontFaceSrcElementFactory());
        svg11Factories.put("font-face-uri", new FontFaceUriElementFactory());
        svg11Factories.put("foreignObject", new ForeignObjectElementFactory());
        svg11Factories.put("g", new GElementFactory());
        svg11Factories.put("glyph", new GlyphElementFactory());
        svg11Factories.put("glyphRef", new GlyphRefElementFactory());
        svg11Factories.put("hkern", new HkernElementFactory());
        svg11Factories.put("image", new ImageElementFactory());
        svg11Factories.put("line", new LineElementFactory());
        svg11Factories.put("linearGradient", new LinearGradientElementFactory());
        svg11Factories.put("marker", new MarkerElementFactory());
        svg11Factories.put("mask", new MaskElementFactory());
        svg11Factories.put("metadata", new MetadataElementFactory());
        svg11Factories.put("missing-glyph", new MissingGlyphElementFactory());
        svg11Factories.put("mpath", new MpathElementFactory());
        svg11Factories.put("path", new PathElementFactory());
        svg11Factories.put("pattern", new PatternElementFactory());
        svg11Factories.put("polygon", new PolygonElementFactory());
        svg11Factories.put("polyline", new PolylineElementFactory());
        svg11Factories.put("radialGradient", new RadialGradientElementFactory());
        svg11Factories.put("rect", new RectElementFactory());
        svg11Factories.put("set", new SetElementFactory());
        svg11Factories.put("script", new ScriptElementFactory());
        svg11Factories.put("stop", new StopElementFactory());
        svg11Factories.put("style", new StyleElementFactory());
        svg11Factories.put("svg", new SvgElementFactory());
        svg11Factories.put("switch", new SwitchElementFactory());
        svg11Factories.put("symbol", new SymbolElementFactory());
        svg11Factories.put("text", new TextElementFactory());
        svg11Factories.put("textPath", new TextPathElementFactory());
        svg11Factories.put("title", new TitleElementFactory());
        svg11Factories.put("tref", new TrefElementFactory());
        svg11Factories.put("tspan", new TspanElementFactory());
        svg11Factories.put("use", new UseElementFactory());
        svg11Factories.put("view", new ViewElementFactory());
        svg11Factories.put("vkern", new VkernElementFactory());
        DOM_IMPLEMENTATION = new SVGDOMImplementation();
    }

    protected static class VkernElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMVKernElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class ViewElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMViewElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class UseElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMUseElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class TspanElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMTSpanElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class TrefElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMTRefElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class TitleElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMTitleElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class TextPathElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMTextPathElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class TextElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMTextElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class SymbolElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMSymbolElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class SwitchElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMSwitchElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class SvgElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMSVGElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class StyleElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMStyleElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class StopElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMStopElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class SetElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMSetElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class ScriptElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMScriptElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class RectElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMRectElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class RadialGradientElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMRadialGradientElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class PolylineElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMPolylineElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class PolygonElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMPolygonElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class PatternElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMPatternElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class PathElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMPathElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class MpathElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMMPathElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class MissingGlyphElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMMissingGlyphElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class MetadataElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMMetadataElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class MaskElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMMaskElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class MarkerElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMMarkerElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class LinearGradientElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMLinearGradientElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class LineElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMLineElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class ImageElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMImageElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class HkernElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMHKernElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class GlyphRefElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMGlyphRefElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class GlyphElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMGlyphElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class GElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMGElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class ForeignObjectElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMForeignObjectElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FontFaceUriElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFontFaceUriElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FontFaceSrcElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFontFaceSrcElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FontFaceNameElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFontFaceNameElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FontFaceFormatElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFontFaceFormatElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FontFaceElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFontFaceElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FontElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFontElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FilterElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFilterElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeTurbulenceElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFETurbulenceElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeTileElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFETileElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeSpotLightElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFESpotLightElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeSpecularLightingElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFESpecularLightingElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FePointLightElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEPointLightElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeOffsetElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEOffsetElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeMorphologyElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEMorphologyElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeMergeNodeElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEMergeNodeElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeMergeElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEMergeElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeImageElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEImageElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeGaussianBlurElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEGaussianBlurElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeFuncBElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEFuncBElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeFuncGElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEFuncGElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeFuncRElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEFuncRElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeFuncAElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEFuncAElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeFloodElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEFloodElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeDistantLightElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEDistantLightElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeDisplacementMapElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEDisplacementMapElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeDiffuseLightingElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEDiffuseLightingElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeConvolveMatrixElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEConvolveMatrixElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeCompositeElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFECompositeElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeComponentTransferElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEComponentTransferElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeColorMatrixElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEColorMatrixElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class FeBlendElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMFEBlendElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class EllipseElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMEllipseElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class DescElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMDescElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class DefsElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMDefsElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class DefinitionSrcElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMDefinitionSrcElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class CursorElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMCursorElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class ColorProfileElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMColorProfileElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class ClipPathElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMClipPathElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class CircleElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMCircleElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class AnimateTransformElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMAnimateTransformElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class AnimateMotionElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMAnimateMotionElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class AnimateColorElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMAnimateColorElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class AnimateElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMAnimateElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class AltGlyphItemElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMAltGlyphItemElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class AltGlyphDefElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMAltGlyphDefElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class AltGlyphElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMAltGlyphElement(prefix, (AbstractDocument)doc);
        }
    }

    protected static class AElementFactory
    implements ExtensibleDOMImplementation.ElementFactory {
        public Element create(String prefix, Document doc) {
            return new SVGOMAElement(prefix, (AbstractDocument)doc);
        }
    }
}

