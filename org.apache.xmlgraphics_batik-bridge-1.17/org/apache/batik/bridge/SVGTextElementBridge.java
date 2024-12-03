/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.AbstractSVGAnimatedLength
 *  org.apache.batik.anim.dom.AnimatedLiveAttributeValue
 *  org.apache.batik.anim.dom.SVGOMAnimatedEnumeration
 *  org.apache.batik.anim.dom.SVGOMAnimatedLengthList
 *  org.apache.batik.anim.dom.SVGOMAnimatedNumberList
 *  org.apache.batik.anim.dom.SVGOMElement
 *  org.apache.batik.anim.dom.SVGOMTextPositioningElement
 *  org.apache.batik.css.engine.CSSEngineEvent
 *  org.apache.batik.css.engine.CSSStylableElement
 *  org.apache.batik.css.engine.StyleMap
 *  org.apache.batik.css.engine.value.ListValue
 *  org.apache.batik.css.engine.value.Value
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.svg.SVGContext
 *  org.apache.batik.dom.svg.SVGTextContent
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.dom.util.XMLSupport
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.font.GVTFont
 *  org.apache.batik.gvt.font.GVTFontFamily
 *  org.apache.batik.gvt.font.GVTGlyphMetrics
 *  org.apache.batik.gvt.font.GVTGlyphVector
 *  org.apache.batik.gvt.font.UnresolvedFontFamily
 *  org.apache.batik.gvt.text.GVTAttributedCharacterIterator$TextAttribute
 *  org.apache.batik.gvt.text.TextPaintInfo
 *  org.apache.batik.gvt.text.TextPath
 *  org.w3c.dom.svg.SVGLengthList
 *  org.w3c.dom.svg.SVGNumberList
 *  org.w3c.dom.svg.SVGTextContentElement
 *  org.w3c.dom.svg.SVGTextPositioningElement
 */
package org.apache.batik.bridge;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.AnimatedLiveAttributeValue;
import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMAnimatedLengthList;
import org.apache.batik.anim.dom.SVGOMAnimatedNumberList;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMTextPositioningElement;
import org.apache.batik.bridge.AbstractGraphicsNodeBridge;
import org.apache.batik.bridge.AnimatableSVGBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.BridgeUpdateHandler;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.CursorManager;
import org.apache.batik.bridge.Mark;
import org.apache.batik.bridge.PaintServer;
import org.apache.batik.bridge.SVGAElementBridge;
import org.apache.batik.bridge.SVGAltGlyphHandler;
import org.apache.batik.bridge.SVGFontUtilities;
import org.apache.batik.bridge.SVGTextPathElementBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.StrokingTextPainter;
import org.apache.batik.bridge.TextHit;
import org.apache.batik.bridge.TextNode;
import org.apache.batik.bridge.TextSpanLayout;
import org.apache.batik.bridge.TextUtilities;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.css.engine.CSSEngineEvent;
import org.apache.batik.css.engine.CSSStylableElement;
import org.apache.batik.css.engine.StyleMap;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.svg.SVGTextContent;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.GVTGlyphMetrics;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.UnresolvedFontFamily;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.apache.batik.gvt.text.TextPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MutationEvent;
import org.w3c.dom.svg.SVGLengthList;
import org.w3c.dom.svg.SVGNumberList;
import org.w3c.dom.svg.SVGTextContentElement;
import org.w3c.dom.svg.SVGTextPositioningElement;

public class SVGTextElementBridge
extends AbstractGraphicsNodeBridge
implements SVGTextContent {
    protected static final Integer ZERO = 0;
    public static final AttributedCharacterIterator.Attribute TEXT_COMPOUND_DELIMITER = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER;
    public static final AttributedCharacterIterator.Attribute TEXT_COMPOUND_ID = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_ID;
    public static final AttributedCharacterIterator.Attribute PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
    public static final AttributedCharacterIterator.Attribute ALT_GLYPH_HANDLER = GVTAttributedCharacterIterator.TextAttribute.ALT_GLYPH_HANDLER;
    public static final AttributedCharacterIterator.Attribute TEXTPATH = GVTAttributedCharacterIterator.TextAttribute.TEXTPATH;
    public static final AttributedCharacterIterator.Attribute ANCHOR_TYPE = GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE;
    public static final AttributedCharacterIterator.Attribute GVT_FONT_FAMILIES = GVTAttributedCharacterIterator.TextAttribute.GVT_FONT_FAMILIES;
    public static final AttributedCharacterIterator.Attribute GVT_FONTS = GVTAttributedCharacterIterator.TextAttribute.GVT_FONTS;
    public static final AttributedCharacterIterator.Attribute BASELINE_SHIFT = GVTAttributedCharacterIterator.TextAttribute.BASELINE_SHIFT;
    protected AttributedString laidoutText;
    protected WeakHashMap elemTPI = new WeakHashMap();
    protected boolean usingComplexSVGFont = false;
    protected DOMChildNodeRemovedEventListener childNodeRemovedEventListener;
    protected DOMSubtreeModifiedEventListener subtreeModifiedEventListener;
    private boolean hasNewACI;
    private Element cssProceedElement;
    protected int endLimit;

    @Override
    public String getLocalName() {
        return "text";
    }

    @Override
    public Bridge getInstance() {
        return new SVGTextElementBridge();
    }

    protected TextNode getTextNode() {
        return (TextNode)this.node;
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        TextNode node = (TextNode)super.createGraphicsNode(ctx, e);
        if (node == null) {
            return null;
        }
        this.associateSVGContext(ctx, e, (GraphicsNode)node);
        Node child = this.getFirstChild(e);
        while (child != null) {
            if (child.getNodeType() == 1) {
                this.addContextToChild(ctx, (Element)child);
            }
            child = this.getNextSibling(child);
        }
        if (ctx.getTextPainter() != null) {
            node.setTextPainter(ctx.getTextPainter());
        }
        RenderingHints hints = null;
        hints = CSSUtilities.convertColorRendering(e, hints);
        if ((hints = CSSUtilities.convertTextRendering(e, hints)) != null) {
            node.setRenderingHints(hints);
        }
        node.setLocation(this.getLocation(ctx, e));
        return node;
    }

    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return new TextNode();
    }

    protected Point2D getLocation(BridgeContext ctx, Element e) {
        try {
            SVGOMTextPositioningElement te = (SVGOMTextPositioningElement)e;
            SVGOMAnimatedLengthList _x = (SVGOMAnimatedLengthList)te.getX();
            _x.check();
            SVGLengthList xs = _x.getAnimVal();
            float x = 0.0f;
            if (xs.getNumberOfItems() > 0) {
                x = xs.getItem(0).getValue();
            }
            SVGOMAnimatedLengthList _y = (SVGOMAnimatedLengthList)te.getY();
            _y.check();
            SVGLengthList ys = _y.getAnimVal();
            float y = 0.0f;
            if (ys.getNumberOfItems() > 0) {
                y = ys.getItem(0).getValue();
            }
            return new Point2D.Float(x, y);
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }

    protected boolean isTextElement(Element e) {
        if (!"http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            return false;
        }
        String nodeName = e.getLocalName();
        return nodeName.equals("text") || nodeName.equals("tspan") || nodeName.equals("altGlyph") || nodeName.equals("a") || nodeName.equals("textPath") || nodeName.equals("tref");
    }

    protected boolean isTextChild(Element e) {
        if (!"http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            return false;
        }
        String nodeName = e.getLocalName();
        return nodeName.equals("tspan") || nodeName.equals("altGlyph") || nodeName.equals("a") || nodeName.equals("textPath") || nodeName.equals("tref");
    }

    @Override
    public void buildGraphicsNode(BridgeContext ctx, Element e, GraphicsNode node) {
        e.normalize();
        this.computeLaidoutText(ctx, e, node);
        node.setComposite(CSSUtilities.convertOpacity(e));
        node.setFilter(CSSUtilities.convertFilter(e, node, ctx));
        node.setMask(CSSUtilities.convertMask(e, node, ctx));
        node.setClip(CSSUtilities.convertClipPath(e, node, ctx));
        node.setPointerEventType(CSSUtilities.convertPointerEvents(e));
        this.initializeDynamicSupport(ctx, e, node);
        if (!ctx.isDynamic()) {
            this.elemTPI.clear();
        }
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    protected Node getFirstChild(Node n) {
        return n.getFirstChild();
    }

    protected Node getNextSibling(Node n) {
        return n.getNextSibling();
    }

    protected Node getParentNode(Node n) {
        return n.getParentNode();
    }

    @Override
    protected void initializeDynamicSupport(BridgeContext ctx, Element e, GraphicsNode node) {
        super.initializeDynamicSupport(ctx, e, node);
        if (ctx.isDynamic()) {
            this.addTextEventListeners(ctx, (NodeEventTarget)e);
        }
    }

    protected void addTextEventListeners(BridgeContext ctx, NodeEventTarget e) {
        if (this.childNodeRemovedEventListener == null) {
            this.childNodeRemovedEventListener = new DOMChildNodeRemovedEventListener();
        }
        if (this.subtreeModifiedEventListener == null) {
            this.subtreeModifiedEventListener = new DOMSubtreeModifiedEventListener();
        }
        e.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)this.childNodeRemovedEventListener, true, null);
        ctx.storeEventListenerNS((EventTarget)e, "http://www.w3.org/2001/xml-events", "DOMNodeRemoved", this.childNodeRemovedEventListener, true);
        e.addEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", (EventListener)this.subtreeModifiedEventListener, false, null);
        ctx.storeEventListenerNS((EventTarget)e, "http://www.w3.org/2001/xml-events", "DOMSubtreeModified", this.subtreeModifiedEventListener, false);
    }

    protected void removeTextEventListeners(BridgeContext ctx, NodeEventTarget e) {
        e.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", (EventListener)this.childNodeRemovedEventListener, true);
        e.removeEventListenerNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", (EventListener)this.subtreeModifiedEventListener, false);
    }

    @Override
    public void dispose() {
        this.removeTextEventListeners(this.ctx, (NodeEventTarget)this.e);
        super.dispose();
    }

    protected void addContextToChild(BridgeContext ctx, Element e) {
        if ("http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            if (e.getLocalName().equals("tspan")) {
                ((SVGOMElement)e).setSVGContext((SVGContext)new TspanBridge(ctx, this, e));
            } else if (e.getLocalName().equals("textPath")) {
                ((SVGOMElement)e).setSVGContext((SVGContext)new TextPathBridge(ctx, this, e));
            } else if (e.getLocalName().equals("tref")) {
                ((SVGOMElement)e).setSVGContext((SVGContext)new TRefBridge(ctx, this, e));
            }
        }
        Node child = this.getFirstChild(e);
        while (child != null) {
            if (child.getNodeType() == 1) {
                this.addContextToChild(ctx, (Element)child);
            }
            child = this.getNextSibling(child);
        }
    }

    protected void removeContextFromChild(BridgeContext ctx, Element e) {
        if ("http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            if (e.getLocalName().equals("tspan")) {
                ((AbstractTextChildBridgeUpdateHandler)((SVGOMElement)e).getSVGContext()).dispose();
            } else if (e.getLocalName().equals("textPath")) {
                ((AbstractTextChildBridgeUpdateHandler)((SVGOMElement)e).getSVGContext()).dispose();
            } else if (e.getLocalName().equals("tref")) {
                ((AbstractTextChildBridgeUpdateHandler)((SVGOMElement)e).getSVGContext()).dispose();
            }
        }
        Node child = this.getFirstChild(e);
        while (child != null) {
            if (child.getNodeType() == 1) {
                this.removeContextFromChild(ctx, (Element)child);
            }
            child = this.getNextSibling(child);
        }
    }

    @Override
    public void handleDOMNodeInsertedEvent(MutationEvent evt) {
        Node childNode = (Node)((Object)evt.getTarget());
        switch (childNode.getNodeType()) {
            case 3: 
            case 4: {
                this.laidoutText = null;
                break;
            }
            case 1: {
                Element childElement = (Element)childNode;
                if (!this.isTextChild(childElement)) break;
                this.addContextToChild(this.ctx, childElement);
                this.laidoutText = null;
                break;
            }
        }
        if (this.laidoutText == null) {
            this.computeLaidoutText(this.ctx, this.e, (GraphicsNode)this.getTextNode());
        }
    }

    public void handleDOMChildNodeRemovedEvent(MutationEvent evt) {
        Node childNode = (Node)((Object)evt.getTarget());
        switch (childNode.getNodeType()) {
            case 3: 
            case 4: {
                if (!this.isParentDisplayed(childNode)) break;
                this.laidoutText = null;
                break;
            }
            case 1: {
                Element childElt = (Element)childNode;
                if (!this.isTextChild(childElt)) break;
                this.laidoutText = null;
                this.removeContextFromChild(this.ctx, childElt);
                break;
            }
        }
    }

    public void handleDOMSubtreeModifiedEvent(MutationEvent evt) {
        if (this.laidoutText == null) {
            this.computeLaidoutText(this.ctx, this.e, (GraphicsNode)this.getTextNode());
        }
    }

    @Override
    public void handleDOMCharacterDataModified(MutationEvent evt) {
        Node childNode = (Node)((Object)evt.getTarget());
        if (this.isParentDisplayed(childNode)) {
            this.laidoutText = null;
        }
    }

    protected boolean isParentDisplayed(Node childNode) {
        Node parentNode = this.getParentNode(childNode);
        return this.isTextElement((Element)parentNode);
    }

    protected void computeLaidoutText(BridgeContext ctx, Element e, GraphicsNode node) {
        TextNode tn = (TextNode)node;
        this.elemTPI.clear();
        AttributedString as = this.buildAttributedString(ctx, e);
        if (as == null) {
            tn.setAttributedCharacterIterator(null);
            return;
        }
        this.addGlyphPositionAttributes(as, e, ctx);
        if (ctx.isDynamic()) {
            this.laidoutText = new AttributedString(as.getIterator());
        }
        tn.setAttributedCharacterIterator(as.getIterator());
        TextPaintInfo pi = new TextPaintInfo();
        this.setBaseTextPaintInfo(pi, e, node, ctx);
        this.setDecorationTextPaintInfo(pi, e);
        this.addPaintAttributes(as, e, tn, pi, ctx);
        if (this.usingComplexSVGFont) {
            tn.setAttributedCharacterIterator(as.getIterator());
        }
        if (ctx.isDynamic()) {
            this.checkBBoxChange();
        }
    }

    @Override
    public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
        String ln;
        if (alav.getNamespaceURI() == null && ((ln = alav.getLocalName()).equals("x") || ln.equals("y") || ln.equals("dx") || ln.equals("dy") || ln.equals("rotate") || ln.equals("textLength") || ln.equals("lengthAdjust"))) {
            char c = ln.charAt(0);
            if (c == 'x' || c == 'y') {
                this.getTextNode().setLocation(this.getLocation(this.ctx, this.e));
            }
            this.computeLaidoutText(this.ctx, this.e, (GraphicsNode)this.getTextNode());
            return;
        }
        super.handleAnimatedAttributeChanged(alav);
    }

    @Override
    public void handleCSSEngineEvent(CSSEngineEvent evt) {
        int[] properties;
        this.hasNewACI = false;
        block3: for (int property : properties = evt.getProperties()) {
            switch (property) {
                case 1: 
                case 11: 
                case 12: 
                case 21: 
                case 22: 
                case 24: 
                case 25: 
                case 27: 
                case 28: 
                case 29: 
                case 31: 
                case 32: 
                case 53: 
                case 56: 
                case 58: 
                case 59: {
                    if (this.hasNewACI) continue block3;
                    this.hasNewACI = true;
                    this.computeLaidoutText(this.ctx, this.e, (GraphicsNode)this.getTextNode());
                }
            }
        }
        this.cssProceedElement = evt.getElement();
        super.handleCSSEngineEvent(evt);
        this.cssProceedElement = null;
    }

    @Override
    protected void handleCSSPropertyChanged(int property) {
        switch (property) {
            case 15: 
            case 16: 
            case 45: 
            case 46: 
            case 47: 
            case 48: 
            case 49: 
            case 50: 
            case 51: 
            case 52: 
            case 54: {
                this.rebuildACI();
                break;
            }
            case 57: {
                this.rebuildACI();
                super.handleCSSPropertyChanged(property);
                break;
            }
            case 55: {
                RenderingHints hints = this.node.getRenderingHints();
                hints = CSSUtilities.convertTextRendering(this.e, hints);
                if (hints == null) break;
                this.node.setRenderingHints(hints);
                break;
            }
            case 9: {
                RenderingHints hints = this.node.getRenderingHints();
                hints = CSSUtilities.convertColorRendering(this.e, hints);
                if (hints == null) break;
                this.node.setRenderingHints(hints);
                break;
            }
            default: {
                super.handleCSSPropertyChanged(property);
            }
        }
    }

    protected void rebuildACI() {
        TextPaintInfo oldPI;
        TextPaintInfo pi;
        if (this.hasNewACI) {
            return;
        }
        TextNode textNode = this.getTextNode();
        if (textNode.getAttributedCharacterIterator() == null) {
            return;
        }
        if (this.cssProceedElement == this.e) {
            pi = new TextPaintInfo();
            this.setBaseTextPaintInfo(pi, this.e, this.node, this.ctx);
            this.setDecorationTextPaintInfo(pi, this.e);
            oldPI = (TextPaintInfo)this.elemTPI.get(this.e);
        } else {
            TextPaintInfo parentPI = this.getParentTextPaintInfo(this.cssProceedElement);
            pi = this.getTextPaintInfo(this.cssProceedElement, (GraphicsNode)textNode, parentPI, this.ctx);
            oldPI = (TextPaintInfo)this.elemTPI.get(this.cssProceedElement);
        }
        if (oldPI == null) {
            return;
        }
        textNode.swapTextPaintInfo(pi, oldPI);
        if (this.usingComplexSVGFont) {
            textNode.setAttributedCharacterIterator(textNode.getAttributedCharacterIterator());
        }
    }

    int getElementStartIndex(Element element) {
        TextPaintInfo tpi = (TextPaintInfo)this.elemTPI.get(element);
        if (tpi == null) {
            return -1;
        }
        return tpi.startChar;
    }

    int getElementEndIndex(Element element) {
        TextPaintInfo tpi = (TextPaintInfo)this.elemTPI.get(element);
        if (tpi == null) {
            return -1;
        }
        return tpi.endChar;
    }

    protected AttributedString buildAttributedString(BridgeContext ctx, Element element) {
        AttributedStringBuffer asb = new AttributedStringBuffer();
        this.fillAttributedStringBuffer(ctx, element, true, null, null, null, asb);
        return asb.toAttributedString();
    }

    protected void fillAttributedStringBuffer(BridgeContext ctx, Element element, boolean top, TextPath textPath, Integer bidiLevel, Map initialAttributes, AttributedStringBuffer asb) {
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent()) || !CSSUtilities.convertDisplay(element)) {
            return;
        }
        String s = XMLSupport.getXMLSpace((Element)element);
        boolean preserve = s.equals("preserve");
        Element nodeElement = element;
        int elementStartChar = asb.length();
        if (top) {
            this.endLimit = 0;
        }
        if (preserve) {
            this.endLimit = asb.length();
        }
        HashMap map = initialAttributes == null ? new HashMap() : new HashMap(initialAttributes);
        initialAttributes = this.getAttributeMap(ctx, element, textPath, bidiLevel, map);
        Object o = map.get(TextAttribute.BIDI_EMBEDDING);
        Integer subBidiLevel = bidiLevel;
        if (o != null) {
            subBidiLevel = (Integer)o;
        }
        Node n = this.getFirstChild(element);
        while (n != null) {
            boolean prevEndsWithSpace = preserve ? false : (asb.length() == 0 ? true : asb.getLastChar() == 32);
            switch (n.getNodeType()) {
                case 1: {
                    if (!"http://www.w3.org/2000/svg".equals(n.getNamespaceURI())) break;
                    nodeElement = (Element)n;
                    String ln = n.getLocalName();
                    if (ln.equals("tspan") || ln.equals("altGlyph")) {
                        int before = asb.count;
                        this.fillAttributedStringBuffer(ctx, nodeElement, false, textPath, subBidiLevel, initialAttributes, asb);
                        if (asb.count == before) break;
                        initialAttributes = null;
                        break;
                    }
                    if (ln.equals("textPath")) {
                        SVGTextPathElementBridge textPathBridge = (SVGTextPathElementBridge)ctx.getBridge(nodeElement);
                        TextPath newTextPath = textPathBridge.createTextPath(ctx, nodeElement);
                        if (newTextPath == null) break;
                        int before = asb.count;
                        this.fillAttributedStringBuffer(ctx, nodeElement, false, newTextPath, subBidiLevel, initialAttributes, asb);
                        if (asb.count == before) break;
                        initialAttributes = null;
                        break;
                    }
                    if (ln.equals("tref")) {
                        String uriStr = XLinkSupport.getXLinkHref((Element)((Element)n));
                        Element ref = ctx.getReferencedElement((Element)n, uriStr);
                        s = TextUtilities.getElementContent(ref);
                        if ((s = this.normalizeString(s, preserve, prevEndsWithSpace)).length() == 0) break;
                        int trefStart = asb.length();
                        HashMap m = initialAttributes == null ? new HashMap() : new HashMap(initialAttributes);
                        this.getAttributeMap(ctx, nodeElement, textPath, bidiLevel, m);
                        asb.append(s, m);
                        int trefEnd = asb.length() - 1;
                        TextPaintInfo tpi = (TextPaintInfo)this.elemTPI.get(nodeElement);
                        tpi.startChar = trefStart;
                        tpi.endChar = trefEnd;
                        initialAttributes = null;
                        break;
                    }
                    if (!ln.equals("a")) break;
                    NodeEventTarget target = (NodeEventTarget)nodeElement;
                    UserAgent ua = ctx.getUserAgent();
                    SVGAElementBridge.CursorHolder ch = new SVGAElementBridge.CursorHolder(CursorManager.DEFAULT_CURSOR);
                    SVGAElementBridge.AnchorListener l = new SVGAElementBridge.AnchorListener(ua, ch);
                    target.addEventListenerNS("http://www.w3.org/2001/xml-events", "click", (EventListener)l, false, null);
                    ctx.storeEventListenerNS((EventTarget)target, "http://www.w3.org/2001/xml-events", "click", l, false);
                    int before = asb.count;
                    this.fillAttributedStringBuffer(ctx, nodeElement, false, textPath, subBidiLevel, initialAttributes, asb);
                    if (asb.count == before) break;
                    initialAttributes = null;
                    break;
                }
                case 3: 
                case 4: {
                    s = n.getNodeValue();
                    s = this.normalizeString(s, preserve, prevEndsWithSpace);
                    if (s.length() == 0) break;
                    asb.append(s, map);
                    if (preserve) {
                        this.endLimit = asb.length();
                    }
                    initialAttributes = null;
                }
            }
            n = this.getNextSibling(n);
        }
        if (top) {
            boolean strippedSome = false;
            while (this.endLimit < asb.length() && asb.getLastChar() == 32) {
                asb.stripLast();
                strippedSome = true;
            }
            if (strippedSome) {
                for (Object o1 : this.elemTPI.values()) {
                    TextPaintInfo tpi = (TextPaintInfo)o1;
                    if (tpi.endChar < asb.length()) continue;
                    tpi.endChar = asb.length() - 1;
                    if (tpi.startChar <= tpi.endChar) continue;
                    tpi.startChar = tpi.endChar;
                }
            }
        }
        int elementEndChar = asb.length() - 1;
        TextPaintInfo tpi = (TextPaintInfo)this.elemTPI.get(element);
        tpi.startChar = elementStartChar;
        tpi.endChar = elementEndChar;
    }

    protected String normalizeString(String s, boolean preserve, boolean stripfirst) {
        StringBuffer sb = new StringBuffer(s.length());
        if (preserve) {
            block10: for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                switch (c) {
                    case '\t': 
                    case '\n': 
                    case '\r': {
                        sb.append(' ');
                        continue block10;
                    }
                    default: {
                        sb.append(c);
                    }
                }
            }
            return sb.toString();
        }
        if (stripfirst) {
            block11: for (int idx = 0; idx < s.length(); ++idx) {
                switch (s.charAt(idx)) {
                    default: {
                        break block11;
                    }
                    case '\t': 
                    case '\n': 
                    case '\r': 
                    case ' ': {
                        continue block11;
                    }
                }
            }
        }
        boolean space = false;
        block12: for (int i = idx; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '\n': 
                case '\r': {
                    continue block12;
                }
                case '\t': 
                case ' ': {
                    if (space) continue block12;
                    sb.append(' ');
                    space = true;
                    continue block12;
                }
                default: {
                    sb.append(c);
                    space = false;
                }
            }
        }
        return sb.toString();
    }

    protected boolean nodeAncestorOf(Node node1, Node node2) {
        if (node2 == null || node1 == null) {
            return false;
        }
        Node parent = this.getParentNode(node2);
        while (parent != null && parent != node1) {
            parent = this.getParentNode(parent);
        }
        return parent == node1;
    }

    protected void addGlyphPositionAttributes(AttributedString as, Element element, BridgeContext ctx) {
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent()) || !CSSUtilities.convertDisplay(element)) {
            return;
        }
        if (element.getLocalName().equals("textPath")) {
            this.addChildGlyphPositionAttributes(as, element, ctx);
            return;
        }
        int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return;
        }
        int lastChar = this.getElementEndIndex(element);
        if (!(element instanceof SVGTextPositioningElement)) {
            this.addChildGlyphPositionAttributes(as, element, ctx);
            return;
        }
        SVGTextPositioningElement te = (SVGTextPositioningElement)element;
        try {
            int i;
            SVGOMAnimatedLengthList _x = (SVGOMAnimatedLengthList)te.getX();
            _x.check();
            SVGOMAnimatedLengthList _y = (SVGOMAnimatedLengthList)te.getY();
            _y.check();
            SVGOMAnimatedLengthList _dx = (SVGOMAnimatedLengthList)te.getDx();
            _dx.check();
            SVGOMAnimatedLengthList _dy = (SVGOMAnimatedLengthList)te.getDy();
            _dy.check();
            SVGOMAnimatedNumberList _rotate = (SVGOMAnimatedNumberList)te.getRotate();
            _rotate.check();
            SVGLengthList xs = _x.getAnimVal();
            SVGLengthList ys = _y.getAnimVal();
            SVGLengthList dxs = _dx.getAnimVal();
            SVGLengthList dys = _dy.getAnimVal();
            SVGNumberList rs = _rotate.getAnimVal();
            int len = xs.getNumberOfItems();
            for (i = 0; i < len && firstChar + i <= lastChar; ++i) {
                as.addAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.X, Float.valueOf(xs.getItem(i).getValue()), firstChar + i, firstChar + i + 1);
            }
            len = ys.getNumberOfItems();
            for (i = 0; i < len && firstChar + i <= lastChar; ++i) {
                as.addAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.Y, Float.valueOf(ys.getItem(i).getValue()), firstChar + i, firstChar + i + 1);
            }
            len = dxs.getNumberOfItems();
            for (i = 0; i < len && firstChar + i <= lastChar; ++i) {
                as.addAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.DX, Float.valueOf(dxs.getItem(i).getValue()), firstChar + i, firstChar + i + 1);
            }
            len = dys.getNumberOfItems();
            for (i = 0; i < len && firstChar + i <= lastChar; ++i) {
                as.addAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.DY, Float.valueOf(dys.getItem(i).getValue()), firstChar + i, firstChar + i + 1);
            }
            len = rs.getNumberOfItems();
            if (len == 1) {
                Float rad = Float.valueOf((float)Math.toRadians(rs.getItem(0).getValue()));
                as.addAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.ROTATION, rad, firstChar, lastChar + 1);
            } else if (len > 1) {
                for (i = 0; i < len && firstChar + i <= lastChar; ++i) {
                    Float rad = Float.valueOf((float)Math.toRadians(rs.getItem(i).getValue()));
                    as.addAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.ROTATION, rad, firstChar + i, firstChar + i + 1);
                }
            }
            this.addChildGlyphPositionAttributes(as, element, ctx);
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }

    protected void addChildGlyphPositionAttributes(AttributedString as, Element element, BridgeContext ctx) {
        Node child = this.getFirstChild(element);
        while (child != null) {
            Element childElement;
            if (child.getNodeType() == 1 && this.isTextChild(childElement = (Element)child)) {
                this.addGlyphPositionAttributes(as, childElement, ctx);
            }
            child = this.getNextSibling(child);
        }
    }

    protected void addPaintAttributes(AttributedString as, Element element, TextNode node, TextPaintInfo pi, BridgeContext ctx) {
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent()) || !CSSUtilities.convertDisplay(element)) {
            return;
        }
        Object o = this.elemTPI.get(element);
        if (o != null) {
            node.swapTextPaintInfo(pi, (TextPaintInfo)o);
        }
        this.addChildPaintAttributes(as, element, node, pi, ctx);
    }

    protected void addChildPaintAttributes(AttributedString as, Element element, TextNode node, TextPaintInfo parentPI, BridgeContext ctx) {
        Node child = this.getFirstChild(element);
        while (child != null) {
            Element childElement;
            if (child.getNodeType() == 1 && this.isTextChild(childElement = (Element)child)) {
                TextPaintInfo pi = this.getTextPaintInfo(childElement, (GraphicsNode)node, parentPI, ctx);
                this.addPaintAttributes(as, childElement, node, pi, ctx);
            }
            child = this.getNextSibling(child);
        }
    }

    protected List getFontList(BridgeContext ctx, Element element, Map result) {
        result.put(TEXT_COMPOUND_ID, new SoftReference<Element>(element));
        Float fsFloat = TextUtilities.convertFontSize(element);
        float fontSize = fsFloat.floatValue();
        result.put(TextAttribute.SIZE, fsFloat);
        result.put(TextAttribute.WIDTH, TextUtilities.convertFontStretch(element));
        result.put(TextAttribute.POSTURE, TextUtilities.convertFontStyle(element));
        result.put(TextAttribute.WEIGHT, TextUtilities.convertFontWeight(element));
        Value v = CSSUtilities.getComputedStyle(element, 27);
        String fontWeightString = v.getCssText();
        String fontStyleString = CSSUtilities.getComputedStyle(element, 25).getStringValue();
        result.put(TEXT_COMPOUND_DELIMITER, element);
        Value val = CSSUtilities.getComputedStyle(element, 21);
        ArrayList<GVTFontFamily> fontFamilyList = new ArrayList<GVTFontFamily>();
        ArrayList<GVTFont> fontList = new ArrayList<GVTFont>();
        int len = val.getLength();
        for (int i = 0; i < len; ++i) {
            Value it = val.item(i);
            String fontFamilyName = it.getStringValue();
            GVTFontFamily fontFamily = SVGFontUtilities.getFontFamily(element, ctx, fontFamilyName, fontWeightString, fontStyleString);
            if (fontFamily != null && fontFamily instanceof UnresolvedFontFamily) {
                fontFamily = ctx.getFontFamilyResolver().resolve(fontFamily.getFamilyName());
            }
            if (fontFamily == null) continue;
            fontFamilyList.add(fontFamily);
            if (fontFamily.isComplex()) {
                this.usingComplexSVGFont = true;
            }
            GVTFont ft = fontFamily.deriveFont(fontSize, result);
            fontList.add(ft);
        }
        result.put(GVT_FONT_FAMILIES, fontFamilyList);
        if (!ctx.isDynamic()) {
            result.remove(TEXT_COMPOUND_DELIMITER);
        }
        return fontList;
    }

    protected Map getAttributeMap(BridgeContext ctx, Element element, TextPath textPath, Integer bidiLevel, Map result) {
        Value val;
        String s;
        SVGTextContentElement tce = null;
        if (element instanceof SVGTextContentElement) {
            tce = (SVGTextContentElement)element;
        }
        HashMap<GVTAttributedCharacterIterator.TextAttribute, Serializable> inheritMap = null;
        if ("http://www.w3.org/2000/svg".equals(element.getNamespaceURI()) && element.getLocalName().equals("altGlyph")) {
            result.put(ALT_GLYPH_HANDLER, new SVGAltGlyphHandler(ctx, element));
        }
        TextPaintInfo pi = new TextPaintInfo();
        pi.visible = true;
        pi.fillPaint = Color.black;
        result.put(PAINT_INFO, pi);
        this.elemTPI.put(element, pi);
        if (textPath != null) {
            result.put(TEXTPATH, textPath);
        }
        TextNode.Anchor a = TextUtilities.convertTextAnchor(element);
        result.put(ANCHOR_TYPE, a);
        List fontList = this.getFontList(ctx, element, result);
        result.put(GVT_FONTS, fontList);
        Object bs = TextUtilities.convertBaselineShift(element);
        if (bs != null) {
            result.put(BASELINE_SHIFT, bs);
        }
        if ((s = (val = CSSUtilities.getComputedStyle(element, 56)).getStringValue()).charAt(0) == 'n') {
            if (bidiLevel != null) {
                result.put(TextAttribute.BIDI_EMBEDDING, bidiLevel);
            }
        } else {
            val = CSSUtilities.getComputedStyle(element, 11);
            String rs = val.getStringValue();
            int cbidi = 0;
            if (bidiLevel != null) {
                cbidi = bidiLevel;
            }
            if (cbidi < 0) {
                cbidi = -cbidi;
            }
            switch (rs.charAt(0)) {
                case 'l': {
                    result.put(TextAttribute.RUN_DIRECTION, TextAttribute.RUN_DIRECTION_LTR);
                    if ((cbidi & 1) == 1) {
                        ++cbidi;
                        break;
                    }
                    cbidi += 2;
                    break;
                }
                case 'r': {
                    result.put(TextAttribute.RUN_DIRECTION, TextAttribute.RUN_DIRECTION_RTL);
                    if ((cbidi & 1) == 1) {
                        cbidi += 2;
                        break;
                    }
                    ++cbidi;
                }
            }
            switch (s.charAt(0)) {
                case 'b': {
                    cbidi = -cbidi;
                }
            }
            result.put(TextAttribute.BIDI_EMBEDDING, cbidi);
        }
        val = CSSUtilities.getComputedStyle(element, 59);
        s = val.getStringValue();
        switch (s.charAt(0)) {
            case 'l': {
                result.put(GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE, GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_LTR);
                break;
            }
            case 'r': {
                result.put(GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE, GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_RTL);
                break;
            }
            case 't': {
                result.put(GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE, GVTAttributedCharacterIterator.TextAttribute.WRITING_MODE_TTB);
            }
        }
        val = CSSUtilities.getComputedStyle(element, 29);
        short primitiveType = val.getPrimitiveType();
        switch (primitiveType) {
            case 21: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION, GVTAttributedCharacterIterator.TextAttribute.ORIENTATION_AUTO);
                break;
            }
            case 11: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION, GVTAttributedCharacterIterator.TextAttribute.ORIENTATION_ANGLE);
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION_ANGLE, Float.valueOf(val.getFloatValue()));
                break;
            }
            case 12: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION, GVTAttributedCharacterIterator.TextAttribute.ORIENTATION_ANGLE);
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION_ANGLE, Float.valueOf((float)Math.toDegrees(val.getFloatValue())));
                break;
            }
            case 13: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION, GVTAttributedCharacterIterator.TextAttribute.ORIENTATION_ANGLE);
                result.put(GVTAttributedCharacterIterator.TextAttribute.VERTICAL_ORIENTATION_ANGLE, Float.valueOf(val.getFloatValue() * 9.0f / 5.0f));
                break;
            }
            default: {
                throw new IllegalStateException("unexpected primitiveType (V):" + primitiveType);
            }
        }
        val = CSSUtilities.getComputedStyle(element, 28);
        primitiveType = val.getPrimitiveType();
        switch (primitiveType) {
            case 11: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.HORIZONTAL_ORIENTATION_ANGLE, Float.valueOf(val.getFloatValue()));
                break;
            }
            case 12: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.HORIZONTAL_ORIENTATION_ANGLE, Float.valueOf((float)Math.toDegrees(val.getFloatValue())));
                break;
            }
            case 13: {
                result.put(GVTAttributedCharacterIterator.TextAttribute.HORIZONTAL_ORIENTATION_ANGLE, Float.valueOf(val.getFloatValue() * 9.0f / 5.0f));
                break;
            }
            default: {
                throw new IllegalStateException("unexpected primitiveType (H):" + primitiveType);
            }
        }
        Float sp = TextUtilities.convertLetterSpacing(element);
        if (sp != null) {
            result.put(GVTAttributedCharacterIterator.TextAttribute.LETTER_SPACING, sp);
            result.put(GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING, Boolean.TRUE);
        }
        if ((sp = TextUtilities.convertWordSpacing(element)) != null) {
            result.put(GVTAttributedCharacterIterator.TextAttribute.WORD_SPACING, sp);
            result.put(GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING, Boolean.TRUE);
        }
        if ((sp = TextUtilities.convertKerning(element)) != null) {
            result.put(GVTAttributedCharacterIterator.TextAttribute.KERNING, sp);
            result.put(GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING, Boolean.TRUE);
        }
        if (tce == null) {
            return inheritMap;
        }
        try {
            AbstractSVGAnimatedLength textLength = (AbstractSVGAnimatedLength)tce.getTextLength();
            if (textLength.isSpecified()) {
                if (inheritMap == null) {
                    inheritMap = new HashMap<GVTAttributedCharacterIterator.TextAttribute, Serializable>();
                }
                Float value = Float.valueOf(textLength.getCheckedValue());
                result.put(GVTAttributedCharacterIterator.TextAttribute.BBOX_WIDTH, value);
                inheritMap.put(GVTAttributedCharacterIterator.TextAttribute.BBOX_WIDTH, value);
                SVGOMAnimatedEnumeration _lengthAdjust = (SVGOMAnimatedEnumeration)tce.getLengthAdjust();
                if (_lengthAdjust.getCheckedVal() == 2) {
                    result.put(GVTAttributedCharacterIterator.TextAttribute.LENGTH_ADJUST, GVTAttributedCharacterIterator.TextAttribute.ADJUST_ALL);
                    inheritMap.put(GVTAttributedCharacterIterator.TextAttribute.LENGTH_ADJUST, GVTAttributedCharacterIterator.TextAttribute.ADJUST_ALL);
                } else {
                    result.put(GVTAttributedCharacterIterator.TextAttribute.LENGTH_ADJUST, GVTAttributedCharacterIterator.TextAttribute.ADJUST_SPACING);
                    inheritMap.put(GVTAttributedCharacterIterator.TextAttribute.LENGTH_ADJUST, GVTAttributedCharacterIterator.TextAttribute.ADJUST_SPACING);
                    result.put(GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING, Boolean.TRUE);
                    inheritMap.put(GVTAttributedCharacterIterator.TextAttribute.CUSTOM_SPACING, Boolean.TRUE);
                }
            }
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
        return inheritMap;
    }

    protected TextPaintInfo getParentTextPaintInfo(Element child) {
        Node parent = this.getParentNode(child);
        while (parent != null) {
            TextPaintInfo tpi = (TextPaintInfo)this.elemTPI.get(parent);
            if (tpi != null) {
                return tpi;
            }
            parent = this.getParentNode(parent);
        }
        return null;
    }

    protected TextPaintInfo getTextPaintInfo(Element element, GraphicsNode node, TextPaintInfo parentTPI, BridgeContext ctx) {
        CSSUtilities.getComputedStyle(element, 54);
        TextPaintInfo pi = new TextPaintInfo(parentTPI);
        StyleMap sm = ((CSSStylableElement)element).getComputedStyleMap(null);
        if (sm.isNullCascaded(54) && sm.isNullCascaded(15) && sm.isNullCascaded(45) && sm.isNullCascaded(52) && sm.isNullCascaded(38)) {
            return pi;
        }
        this.setBaseTextPaintInfo(pi, element, node, ctx);
        if (!sm.isNullCascaded(54)) {
            this.setDecorationTextPaintInfo(pi, element);
        }
        return pi;
    }

    public void setBaseTextPaintInfo(TextPaintInfo pi, Element element, GraphicsNode node, BridgeContext ctx) {
        pi.composite = !element.getLocalName().equals("text") ? CSSUtilities.convertOpacity(element) : AlphaComposite.SrcOver;
        pi.visible = CSSUtilities.convertVisibility(element);
        pi.fillPaint = PaintServer.convertFillPaint(element, node, ctx);
        pi.strokePaint = PaintServer.convertStrokePaint(element, node, ctx);
        pi.strokeStroke = PaintServer.convertStroke(element);
    }

    public void setDecorationTextPaintInfo(TextPaintInfo pi, Element element) {
        Value val = CSSUtilities.getComputedStyle(element, 54);
        switch (val.getCssValueType()) {
            case 2: {
                ListValue lst = (ListValue)val;
                int len = lst.getLength();
                block8: for (int i = 0; i < len; ++i) {
                    Value v = lst.item(i);
                    String s = v.getStringValue();
                    switch (s.charAt(0)) {
                        case 'u': {
                            if (pi.fillPaint != null) {
                                pi.underlinePaint = pi.fillPaint;
                            }
                            if (pi.strokePaint != null) {
                                pi.underlineStrokePaint = pi.strokePaint;
                            }
                            if (pi.strokeStroke == null) continue block8;
                            pi.underlineStroke = pi.strokeStroke;
                            continue block8;
                        }
                        case 'o': {
                            if (pi.fillPaint != null) {
                                pi.overlinePaint = pi.fillPaint;
                            }
                            if (pi.strokePaint != null) {
                                pi.overlineStrokePaint = pi.strokePaint;
                            }
                            if (pi.strokeStroke == null) continue block8;
                            pi.overlineStroke = pi.strokeStroke;
                            continue block8;
                        }
                        case 'l': {
                            if (pi.fillPaint != null) {
                                pi.strikethroughPaint = pi.fillPaint;
                            }
                            if (pi.strokePaint != null) {
                                pi.strikethroughStrokePaint = pi.strokePaint;
                            }
                            if (pi.strokeStroke == null) continue block8;
                            pi.strikethroughStroke = pi.strokeStroke;
                        }
                    }
                }
                break;
            }
            default: {
                pi.underlinePaint = null;
                pi.underlineStrokePaint = null;
                pi.underlineStroke = null;
                pi.overlinePaint = null;
                pi.overlineStrokePaint = null;
                pi.overlineStroke = null;
                pi.strikethroughPaint = null;
                pi.strikethroughStrokePaint = null;
                pi.strikethroughStroke = null;
            }
        }
    }

    public int getNumberOfChars() {
        return this.getNumberOfChars(this.e);
    }

    public Rectangle2D getExtentOfChar(int charnum) {
        return this.getExtentOfChar(this.e, charnum);
    }

    public Point2D getStartPositionOfChar(int charnum) {
        return this.getStartPositionOfChar(this.e, charnum);
    }

    public Point2D getEndPositionOfChar(int charnum) {
        return this.getEndPositionOfChar(this.e, charnum);
    }

    public void selectSubString(int charnum, int nchars) {
        this.selectSubString(this.e, charnum, nchars);
    }

    public float getRotationOfChar(int charnum) {
        return this.getRotationOfChar(this.e, charnum);
    }

    public float getComputedTextLength() {
        return this.getComputedTextLength(this.e);
    }

    public float getSubStringLength(int charnum, int nchars) {
        return this.getSubStringLength(this.e, charnum, nchars);
    }

    public int getCharNumAtPosition(float x, float y) {
        return this.getCharNumAtPosition(this.e, x, y);
    }

    protected int getNumberOfChars(Element element) {
        AttributedCharacterIterator aci = this.getTextNode().getAttributedCharacterIterator();
        if (aci == null) {
            return 0;
        }
        int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return 0;
        }
        int lastChar = this.getElementEndIndex(element);
        return lastChar - firstChar + 1;
    }

    protected Rectangle2D getExtentOfChar(Element element, int charnum) {
        TextNode textNode = this.getTextNode();
        AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return null;
        }
        List list = this.getTextRuns(textNode);
        CharacterInformation info = this.getCharacterInformation(list, firstChar, charnum, aci);
        if (info == null) {
            return null;
        }
        GVTGlyphVector it = info.layout.getGlyphVector();
        Shape b = null;
        if (info.glyphIndexStart == info.glyphIndexEnd) {
            if (it.isGlyphVisible(info.glyphIndexStart)) {
                b = it.getGlyphCellBounds(info.glyphIndexStart);
            }
        } else {
            Path2D path = null;
            for (int k = info.glyphIndexStart; k <= info.glyphIndexEnd; ++k) {
                if (!it.isGlyphVisible(k)) continue;
                Rectangle2D gb = it.getGlyphCellBounds(k);
                if (path == null) {
                    path = new GeneralPath(gb);
                    continue;
                }
                path.append(gb, false);
            }
            b = path;
        }
        if (b == null) {
            return null;
        }
        return b.getBounds2D();
    }

    protected Point2D getStartPositionOfChar(Element element, int charnum) {
        TextNode textNode = this.getTextNode();
        AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return null;
        }
        List list = this.getTextRuns(textNode);
        CharacterInformation info = this.getCharacterInformation(list, firstChar, charnum, aci);
        if (info == null) {
            return null;
        }
        return this.getStartPoint(info);
    }

    protected Point2D getStartPoint(CharacterInformation info) {
        GVTGlyphVector it = info.layout.getGlyphVector();
        if (!it.isGlyphVisible(info.glyphIndexStart)) {
            return null;
        }
        Point2D b = it.getGlyphPosition(info.glyphIndexStart);
        AffineTransform glyphTransform = it.getGlyphTransform(info.glyphIndexStart);
        Point2D.Float result = new Point2D.Float(0.0f, 0.0f);
        if (glyphTransform != null) {
            glyphTransform.transform(result, result);
        }
        result.x = (float)((double)result.x + b.getX());
        result.y = (float)((double)result.y + b.getY());
        return result;
    }

    protected Point2D getEndPositionOfChar(Element element, int charnum) {
        TextNode textNode = this.getTextNode();
        AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return null;
        }
        int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return null;
        }
        List list = this.getTextRuns(textNode);
        CharacterInformation info = this.getCharacterInformation(list, firstChar, charnum, aci);
        if (info == null) {
            return null;
        }
        return this.getEndPoint(info);
    }

    protected Point2D getEndPoint(CharacterInformation info) {
        GVTGlyphVector it = info.layout.getGlyphVector();
        if (!it.isGlyphVisible(info.glyphIndexEnd)) {
            return null;
        }
        Point2D b = it.getGlyphPosition(info.glyphIndexEnd);
        AffineTransform glyphTransform = it.getGlyphTransform(info.glyphIndexEnd);
        GVTGlyphMetrics metrics = it.getGlyphMetrics(info.glyphIndexEnd);
        Point2D.Float result = new Point2D.Float(metrics.getHorizontalAdvance(), 0.0f);
        if (glyphTransform != null) {
            glyphTransform.transform(result, result);
        }
        result.x = (float)((double)result.x + b.getX());
        result.y = (float)((double)result.y + b.getY());
        return result;
    }

    protected float getRotationOfChar(Element element, int charnum) {
        TextNode textNode = this.getTextNode();
        AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return 0.0f;
        }
        int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return 0.0f;
        }
        List list = this.getTextRuns(textNode);
        CharacterInformation info = this.getCharacterInformation(list, firstChar, charnum, aci);
        double angle = 0.0;
        int nbGlyphs = 0;
        if (info != null) {
            GVTGlyphVector it = info.layout.getGlyphVector();
            for (int k = info.glyphIndexStart; k <= info.glyphIndexEnd; ++k) {
                if (!it.isGlyphVisible(k)) continue;
                ++nbGlyphs;
                AffineTransform glyphTransform = it.getGlyphTransform(k);
                if (glyphTransform == null) continue;
                double glyphAngle = 0.0;
                double cosTheta = glyphTransform.getScaleX();
                double sinTheta = glyphTransform.getShearX();
                if (cosTheta == 0.0) {
                    glyphAngle = sinTheta > 0.0 ? Math.PI : -Math.PI;
                } else {
                    glyphAngle = Math.atan(sinTheta / cosTheta);
                    if (cosTheta < 0.0) {
                        glyphAngle += Math.PI;
                    }
                }
                glyphAngle = Math.toDegrees(-glyphAngle) % 360.0;
                angle += glyphAngle - info.getComputedOrientationAngle();
            }
        }
        if (nbGlyphs == 0) {
            return 0.0f;
        }
        return (float)(angle / (double)nbGlyphs);
    }

    protected float getComputedTextLength(Element e) {
        return this.getSubStringLength(e, 0, this.getNumberOfChars(e));
    }

    protected float getSubStringLength(Element element, int charnum, int nchars) {
        if (nchars == 0) {
            return 0.0f;
        }
        float length = 0.0f;
        TextNode textNode = this.getTextNode();
        AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return -1.0f;
        }
        int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return -1.0f;
        }
        List list = this.getTextRuns(textNode);
        CharacterInformation currentInfo = this.getCharacterInformation(list, firstChar, charnum, aci);
        CharacterInformation lastCharacterInRunInfo = null;
        int chIndex = currentInfo.characterIndex + 1;
        GVTGlyphVector vector = currentInfo.layout.getGlyphVector();
        float[] advs = currentInfo.layout.getGlyphAdvances();
        boolean[] glyphTrack = new boolean[advs.length];
        for (int k = charnum + 1; k < charnum + nchars; ++k) {
            if (currentInfo.layout.isOnATextPath()) {
                for (int gi = currentInfo.glyphIndexStart; gi <= currentInfo.glyphIndexEnd; ++gi) {
                    if (vector.isGlyphVisible(gi) && !glyphTrack[gi]) {
                        length += advs[gi + 1] - advs[gi];
                    }
                    glyphTrack[gi] = true;
                }
                CharacterInformation newInfo = this.getCharacterInformation(list, firstChar, k, aci);
                if (newInfo.layout != currentInfo.layout) {
                    vector = newInfo.layout.getGlyphVector();
                    advs = newInfo.layout.getGlyphAdvances();
                    glyphTrack = new boolean[advs.length];
                    chIndex = currentInfo.characterIndex + 1;
                }
                currentInfo = newInfo;
                continue;
            }
            if (currentInfo.layout.hasCharacterIndex(chIndex)) {
                ++chIndex;
                continue;
            }
            lastCharacterInRunInfo = this.getCharacterInformation(list, firstChar, k - 1, aci);
            length += this.distanceFirstLastCharacterInRun(currentInfo, lastCharacterInRunInfo);
            currentInfo = this.getCharacterInformation(list, firstChar, k, aci);
            chIndex = currentInfo.characterIndex + 1;
            vector = currentInfo.layout.getGlyphVector();
            advs = currentInfo.layout.getGlyphAdvances();
            glyphTrack = new boolean[advs.length];
            lastCharacterInRunInfo = null;
        }
        if (currentInfo.layout.isOnATextPath()) {
            for (int gi = currentInfo.glyphIndexStart; gi <= currentInfo.glyphIndexEnd; ++gi) {
                if (vector.isGlyphVisible(gi) && !glyphTrack[gi]) {
                    length += advs[gi + 1] - advs[gi];
                }
                glyphTrack[gi] = true;
            }
        } else {
            if (lastCharacterInRunInfo == null) {
                lastCharacterInRunInfo = this.getCharacterInformation(list, firstChar, charnum + nchars - 1, aci);
            }
            length += this.distanceFirstLastCharacterInRun(currentInfo, lastCharacterInRunInfo);
        }
        return length;
    }

    protected float distanceFirstLastCharacterInRun(CharacterInformation first, CharacterInformation last) {
        float[] advs = first.layout.getGlyphAdvances();
        int firstStart = first.glyphIndexStart;
        int firstEnd = first.glyphIndexEnd;
        int lastStart = last.glyphIndexStart;
        int lastEnd = last.glyphIndexEnd;
        int start = firstStart < lastStart ? firstStart : lastStart;
        int end = firstEnd < lastEnd ? lastEnd : firstEnd;
        return advs[end + 1] - advs[start];
    }

    protected float distanceBetweenRun(CharacterInformation last, CharacterInformation first) {
        CharacterInformation info = new CharacterInformation();
        info.layout = last.layout;
        info.glyphIndexEnd = last.layout.getGlyphCount() - 1;
        Point2D startPoint = this.getEndPoint(info);
        info.layout = first.layout;
        info.glyphIndexStart = 0;
        Point2D endPoint = this.getStartPoint(info);
        float distance = first.isVertical() ? (float)(endPoint.getY() - startPoint.getY()) : (float)(endPoint.getX() - startPoint.getX());
        return distance;
    }

    protected void selectSubString(Element element, int charnum, int nchars) {
        TextNode textNode = this.getTextNode();
        AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return;
        }
        int firstChar = this.getElementStartIndex(element);
        if (firstChar == -1) {
            return;
        }
        List list = this.getTextRuns(textNode);
        int lastChar = this.getElementEndIndex(element);
        CharacterInformation firstInfo = this.getCharacterInformation(list, firstChar, charnum, aci);
        CharacterInformation lastInfo = this.getCharacterInformation(list, firstChar, charnum + nchars - 1, aci);
        Mark firstMark = textNode.getMarkerForChar(firstInfo.characterIndex, true);
        Mark lastMark = lastInfo != null && lastInfo.characterIndex <= lastChar ? textNode.getMarkerForChar(lastInfo.characterIndex, false) : textNode.getMarkerForChar(lastChar, false);
        this.ctx.getUserAgent().setTextSelection(firstMark, lastMark);
    }

    protected int getCharNumAtPosition(Element e, float x, float y) {
        TextNode textNode = this.getTextNode();
        AttributedCharacterIterator aci = textNode.getAttributedCharacterIterator();
        if (aci == null) {
            return -1;
        }
        List list = this.getTextRuns(textNode);
        TextHit hit = null;
        for (int i = list.size() - 1; i >= 0 && hit == null; --i) {
            StrokingTextPainter.TextRun textRun = (StrokingTextPainter.TextRun)list.get(i);
            hit = textRun.getLayout().hitTestChar(x, y);
        }
        if (hit == null) {
            return -1;
        }
        int first = this.getElementStartIndex(e);
        int last = this.getElementEndIndex(e);
        int hitIndex = hit.getCharIndex();
        if (hitIndex >= first && hitIndex <= last) {
            return hitIndex - first;
        }
        return -1;
    }

    protected List getTextRuns(TextNode node) {
        if (node.getTextRuns() == null) {
            node.getPrimitiveBounds();
        }
        return node.getTextRuns();
    }

    protected CharacterInformation getCharacterInformation(List list, int startIndex, int charnum, AttributedCharacterIterator aci) {
        CharacterInformation info = new CharacterInformation();
        info.characterIndex = startIndex + charnum;
        for (Object aList : list) {
            StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
            if (!run.getLayout().hasCharacterIndex(info.characterIndex)) continue;
            info.layout = run.getLayout();
            aci.setIndex(info.characterIndex);
            if (aci.getAttribute(ALT_GLYPH_HANDLER) != null) {
                info.glyphIndexStart = 0;
                info.glyphIndexEnd = info.layout.getGlyphCount() - 1;
            } else {
                info.glyphIndexStart = info.layout.getGlyphIndex(info.characterIndex);
                if (info.glyphIndexStart == -1) {
                    info.glyphIndexStart = 0;
                    info.glyphIndexEnd = info.layout.getGlyphCount() - 1;
                } else {
                    info.glyphIndexEnd = info.glyphIndexStart;
                }
            }
            return info;
        }
        return null;
    }

    public Set getTextIntersectionSet(AffineTransform at, Rectangle2D rect) {
        HashSet<Element> elems = new HashSet<Element>();
        TextNode tn = this.getTextNode();
        List list = tn.getTextRuns();
        if (list == null) {
            return elems;
        }
        block0: for (Object aList : list) {
            Rectangle2D glBounds;
            StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
            TextSpanLayout layout = run.getLayout();
            AttributedCharacterIterator aci = run.getACI();
            aci.first();
            SoftReference sr = (SoftReference)aci.getAttribute(TEXT_COMPOUND_ID);
            Element elem = (Element)sr.get();
            if (elem == null || elems.contains(elem) || !SVGTextElementBridge.isTextSensitive(elem) || (glBounds = layout.getBounds2D()) != null && !rect.intersects(glBounds = at.createTransformedShape(glBounds).getBounds2D())) continue;
            GVTGlyphVector gv = layout.getGlyphVector();
            for (int g = 0; g < gv.getNumGlyphs(); ++g) {
                Shape gBounds = gv.getGlyphLogicalBounds(g);
                if (gBounds == null || !(gBounds = at.createTransformedShape(gBounds).getBounds2D()).intersects(rect)) continue;
                elems.add(elem);
                continue block0;
            }
        }
        return elems;
    }

    public Set getTextEnclosureSet(AffineTransform at, Rectangle2D rect) {
        TextNode tn = this.getTextNode();
        HashSet<Element> elems = new HashSet<Element>();
        List list = tn.getTextRuns();
        if (list == null) {
            return elems;
        }
        HashSet<Element> reject = new HashSet<Element>();
        for (Object aList : list) {
            StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
            TextSpanLayout layout = run.getLayout();
            AttributedCharacterIterator aci = run.getACI();
            aci.first();
            SoftReference sr = (SoftReference)aci.getAttribute(TEXT_COMPOUND_ID);
            Element elem = (Element)sr.get();
            if (elem == null || reject.contains(elem)) continue;
            if (!SVGTextElementBridge.isTextSensitive(elem)) {
                reject.add(elem);
                continue;
            }
            Rectangle2D glBounds = layout.getBounds2D();
            if (glBounds == null) continue;
            if (rect.contains(glBounds = at.createTransformedShape(glBounds).getBounds2D())) {
                elems.add(elem);
                continue;
            }
            reject.add(elem);
            elems.remove(elem);
        }
        return elems;
    }

    public static boolean getTextIntersection(BridgeContext ctx, Element elem, AffineTransform ati, Rectangle2D rect, boolean checkSensitivity) {
        SVGContext svgCtx = null;
        if (elem instanceof SVGOMElement) {
            svgCtx = ((SVGOMElement)elem).getSVGContext();
        }
        if (svgCtx == null) {
            return false;
        }
        SVGTextElementBridge txtBridge = null;
        if (svgCtx instanceof SVGTextElementBridge) {
            txtBridge = (SVGTextElementBridge)svgCtx;
        } else if (svgCtx instanceof AbstractTextChildSVGContext) {
            AbstractTextChildSVGContext childCtx = (AbstractTextChildSVGContext)svgCtx;
            txtBridge = childCtx.getTextBridge();
        }
        if (txtBridge == null) {
            return false;
        }
        TextNode tn = txtBridge.getTextNode();
        List list = tn.getTextRuns();
        if (list == null) {
            return false;
        }
        Element txtElem = txtBridge.e;
        AffineTransform at = tn.getGlobalTransform();
        at.preConcatenate(ati);
        Rectangle2D tnRect = tn.getBounds();
        tnRect = at.createTransformedShape(tnRect).getBounds2D();
        if (!rect.intersects(tnRect)) {
            return false;
        }
        for (Object aList : list) {
            Rectangle2D glBounds;
            StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
            TextSpanLayout layout = run.getLayout();
            AttributedCharacterIterator aci = run.getACI();
            aci.first();
            SoftReference sr = (SoftReference)aci.getAttribute(TEXT_COMPOUND_ID);
            Element runElem = (Element)sr.get();
            if (runElem == null || checkSensitivity && !SVGTextElementBridge.isTextSensitive(runElem)) continue;
            Element p = runElem;
            while (p != null && p != txtElem && p != elem) {
                p = (Element)txtBridge.getParentNode(p);
            }
            if (p != elem || (glBounds = layout.getBounds2D()) == null || !rect.intersects(glBounds = at.createTransformedShape(glBounds).getBounds2D())) continue;
            GVTGlyphVector gv = layout.getGlyphVector();
            for (int g = 0; g < gv.getNumGlyphs(); ++g) {
                Shape gBounds = gv.getGlyphLogicalBounds(g);
                if (gBounds == null || !(gBounds = at.createTransformedShape(gBounds).getBounds2D()).intersects(rect)) continue;
                return true;
            }
        }
        return false;
    }

    public static Rectangle2D getTextBounds(BridgeContext ctx, Element elem, boolean checkSensitivity) {
        SVGContext svgCtx = null;
        if (elem instanceof SVGOMElement) {
            svgCtx = ((SVGOMElement)elem).getSVGContext();
        }
        if (svgCtx == null) {
            return null;
        }
        SVGTextElementBridge txtBridge = null;
        if (svgCtx instanceof SVGTextElementBridge) {
            txtBridge = (SVGTextElementBridge)svgCtx;
        } else if (svgCtx instanceof AbstractTextChildSVGContext) {
            AbstractTextChildSVGContext childCtx = (AbstractTextChildSVGContext)svgCtx;
            txtBridge = childCtx.getTextBridge();
        }
        if (txtBridge == null) {
            return null;
        }
        TextNode tn = txtBridge.getTextNode();
        List list = tn.getTextRuns();
        if (list == null) {
            return null;
        }
        Element txtElem = txtBridge.e;
        Rectangle2D ret = null;
        for (Object aList : list) {
            Rectangle2D glBounds;
            StrokingTextPainter.TextRun run = (StrokingTextPainter.TextRun)aList;
            TextSpanLayout layout = run.getLayout();
            AttributedCharacterIterator aci = run.getACI();
            aci.first();
            SoftReference sr = (SoftReference)aci.getAttribute(TEXT_COMPOUND_ID);
            Element runElem = (Element)sr.get();
            if (runElem == null || checkSensitivity && !SVGTextElementBridge.isTextSensitive(runElem)) continue;
            Element p = runElem;
            while (p != null && p != txtElem && p != elem) {
                p = (Element)txtBridge.getParentNode(p);
            }
            if (p != elem || (glBounds = layout.getBounds2D()) == null) continue;
            if (ret == null) {
                ret = (Rectangle2D)glBounds.clone();
                continue;
            }
            ret.add(glBounds);
        }
        return ret;
    }

    public static boolean isTextSensitive(Element e) {
        int ptrEvts = CSSUtilities.convertPointerEvents(e);
        switch (ptrEvts) {
            case 0: 
            case 1: 
            case 2: 
            case 3: {
                return CSSUtilities.convertVisibility(e);
            }
            case 4: 
            case 5: 
            case 6: 
            case 7: {
                return true;
            }
        }
        return false;
    }

    protected static class CharacterInformation {
        TextSpanLayout layout;
        int glyphIndexStart;
        int glyphIndexEnd;
        int characterIndex;

        protected CharacterInformation() {
        }

        public boolean isVertical() {
            return this.layout.isVertical();
        }

        public double getComputedOrientationAngle() {
            return this.layout.getComputedOrientationAngle(this.characterIndex);
        }
    }

    protected class TspanBridge
    extends AbstractTextChildTextContent {
        protected TspanBridge(BridgeContext ctx, SVGTextElementBridge parent, Element e) {
            super(ctx, parent, e);
        }

        @Override
        public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
            String ln;
            if (alav.getNamespaceURI() == null && ((ln = alav.getLocalName()).equals("x") || ln.equals("y") || ln.equals("dx") || ln.equals("dy") || ln.equals("rotate") || ln.equals("textLength") || ln.equals("lengthAdjust"))) {
                this.textBridge.computeLaidoutText(this.ctx, this.textBridge.e, (GraphicsNode)this.textBridge.getTextNode());
                return;
            }
            super.handleAnimatedAttributeChanged(alav);
        }
    }

    protected class TextPathBridge
    extends AbstractTextChildTextContent {
        protected TextPathBridge(BridgeContext ctx, SVGTextElementBridge parent, Element e) {
            super(ctx, parent, e);
        }
    }

    protected class TRefBridge
    extends AbstractTextChildTextContent {
        protected TRefBridge(BridgeContext ctx, SVGTextElementBridge parent, Element e) {
            super(ctx, parent, e);
        }

        @Override
        public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
            String ln;
            if (alav.getNamespaceURI() == null && ((ln = alav.getLocalName()).equals("x") || ln.equals("y") || ln.equals("dx") || ln.equals("dy") || ln.equals("rotate") || ln.equals("textLength") || ln.equals("lengthAdjust"))) {
                this.textBridge.computeLaidoutText(this.ctx, this.textBridge.e, (GraphicsNode)this.textBridge.getTextNode());
                return;
            }
            super.handleAnimatedAttributeChanged(alav);
        }
    }

    protected class AbstractTextChildTextContent
    extends AbstractTextChildBridgeUpdateHandler
    implements SVGTextContent {
        protected AbstractTextChildTextContent(BridgeContext ctx, SVGTextElementBridge parent, Element e) {
            super(ctx, parent, e);
        }

        public int getNumberOfChars() {
            return this.textBridge.getNumberOfChars(this.e);
        }

        public Rectangle2D getExtentOfChar(int charnum) {
            return this.textBridge.getExtentOfChar(this.e, charnum);
        }

        public Point2D getStartPositionOfChar(int charnum) {
            return this.textBridge.getStartPositionOfChar(this.e, charnum);
        }

        public Point2D getEndPositionOfChar(int charnum) {
            return this.textBridge.getEndPositionOfChar(this.e, charnum);
        }

        public void selectSubString(int charnum, int nchars) {
            this.textBridge.selectSubString(this.e, charnum, nchars);
        }

        public float getRotationOfChar(int charnum) {
            return this.textBridge.getRotationOfChar(this.e, charnum);
        }

        public float getComputedTextLength() {
            return this.textBridge.getComputedTextLength(this.e);
        }

        public float getSubStringLength(int charnum, int nchars) {
            return this.textBridge.getSubStringLength(this.e, charnum, nchars);
        }

        public int getCharNumAtPosition(float x, float y) {
            return this.textBridge.getCharNumAtPosition(this.e, x, y);
        }
    }

    protected abstract class AbstractTextChildBridgeUpdateHandler
    extends AbstractTextChildSVGContext
    implements BridgeUpdateHandler {
        protected AbstractTextChildBridgeUpdateHandler(BridgeContext ctx, SVGTextElementBridge parent, Element e) {
            super(ctx, parent, e);
        }

        @Override
        public void handleDOMAttrModifiedEvent(MutationEvent evt) {
        }

        @Override
        public void handleDOMNodeInsertedEvent(MutationEvent evt) {
            this.textBridge.handleDOMNodeInsertedEvent(evt);
        }

        @Override
        public void handleDOMNodeRemovedEvent(MutationEvent evt) {
        }

        @Override
        public void handleDOMCharacterDataModified(MutationEvent evt) {
            this.textBridge.handleDOMCharacterDataModified(evt);
        }

        @Override
        public void handleCSSEngineEvent(CSSEngineEvent evt) {
            this.textBridge.handleCSSEngineEvent(evt);
        }

        @Override
        public void handleAnimatedAttributeChanged(AnimatedLiveAttributeValue alav) {
        }

        @Override
        public void handleOtherAnimationChanged(String type) {
        }

        @Override
        public void dispose() {
            ((SVGOMElement)this.e).setSVGContext(null);
            SVGTextElementBridge.this.elemTPI.remove(this.e);
        }
    }

    public static abstract class AbstractTextChildSVGContext
    extends AnimatableSVGBridge {
        protected SVGTextElementBridge textBridge;

        public AbstractTextChildSVGContext(BridgeContext ctx, SVGTextElementBridge parent, Element e) {
            this.ctx = ctx;
            this.textBridge = parent;
            this.e = e;
        }

        @Override
        public String getNamespaceURI() {
            return null;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public Bridge getInstance() {
            return null;
        }

        public SVGTextElementBridge getTextBridge() {
            return this.textBridge;
        }

        public float getPixelUnitToMillimeter() {
            return this.ctx.getUserAgent().getPixelUnitToMillimeter();
        }

        public float getPixelToMM() {
            return this.getPixelUnitToMillimeter();
        }

        public Rectangle2D getBBox() {
            return null;
        }

        public AffineTransform getCTM() {
            return null;
        }

        public AffineTransform getGlobalTransform() {
            return null;
        }

        public AffineTransform getScreenTransform() {
            return null;
        }

        public void setScreenTransform(AffineTransform at) {
        }

        public float getViewportWidth() {
            return this.ctx.getBlockWidth(this.e);
        }

        public float getViewportHeight() {
            return this.ctx.getBlockHeight(this.e);
        }

        public float getFontSize() {
            return CSSUtilities.getComputedStyle(this.e, 22).getFloatValue();
        }
    }

    protected static class AttributedStringBuffer {
        protected List strings = new ArrayList();
        protected List attributes = new ArrayList();
        protected int count = 0;
        protected int length = 0;

        public boolean isEmpty() {
            return this.count == 0;
        }

        public int length() {
            return this.length;
        }

        public void append(String s, Map m) {
            if (s.length() == 0) {
                return;
            }
            this.strings.add(s);
            this.attributes.add(m);
            ++this.count;
            this.length += s.length();
        }

        public int getLastChar() {
            if (this.count == 0) {
                return -1;
            }
            String s = (String)this.strings.get(this.count - 1);
            return s.charAt(s.length() - 1);
        }

        public void stripFirst() {
            String s = (String)this.strings.get(0);
            if (s.charAt(s.length() - 1) != ' ') {
                return;
            }
            --this.length;
            if (s.length() == 1) {
                this.attributes.remove(0);
                this.strings.remove(0);
                --this.count;
                return;
            }
            this.strings.set(0, s.substring(1));
        }

        public void stripLast() {
            String s = (String)this.strings.get(this.count - 1);
            if (s.charAt(s.length() - 1) != ' ') {
                return;
            }
            --this.length;
            if (s.length() == 1) {
                this.attributes.remove(--this.count);
                this.strings.remove(this.count);
                return;
            }
            this.strings.set(this.count - 1, s.substring(0, s.length() - 1));
        }

        public AttributedString toAttributedString() {
            switch (this.count) {
                case 0: {
                    return null;
                }
                case 1: {
                    return new AttributedString((String)this.strings.get(0), (Map)this.attributes.get(0));
                }
            }
            StringBuffer sb = new StringBuffer(this.strings.size() * 5);
            for (Object string : this.strings) {
                sb.append((String)string);
            }
            AttributedString result = new AttributedString(sb.toString());
            Iterator sit = this.strings.iterator();
            Iterator ait = this.attributes.iterator();
            int idx = 0;
            while (sit.hasNext()) {
                String s = (String)sit.next();
                int nidx = idx + s.length();
                Map m = (Map)ait.next();
                Iterator kit = m.keySet().iterator();
                Iterator vit = m.values().iterator();
                while (kit.hasNext()) {
                    AttributedCharacterIterator.Attribute attr = (AttributedCharacterIterator.Attribute)kit.next();
                    Object val = vit.next();
                    result.addAttribute(attr, val, idx, nidx);
                }
                idx = nidx;
            }
            return result;
        }

        public String toString() {
            switch (this.count) {
                case 0: {
                    return "";
                }
                case 1: {
                    return (String)this.strings.get(0);
                }
            }
            StringBuffer sb = new StringBuffer(this.strings.size() * 5);
            for (Object string : this.strings) {
                sb.append((String)string);
            }
            return sb.toString();
        }
    }

    protected class DOMSubtreeModifiedEventListener
    implements EventListener {
        protected DOMSubtreeModifiedEventListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            SVGTextElementBridge.this.handleDOMSubtreeModifiedEvent((MutationEvent)evt);
        }
    }

    protected class DOMChildNodeRemovedEventListener
    implements EventListener {
        protected DOMChildNodeRemovedEventListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            SVGTextElementBridge.this.handleDOMChildNodeRemovedEvent((MutationEvent)evt);
        }
    }
}

