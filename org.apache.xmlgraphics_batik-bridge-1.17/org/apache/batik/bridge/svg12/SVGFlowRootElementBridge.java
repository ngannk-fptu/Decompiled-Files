/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMElement
 *  org.apache.batik.anim.dom.SVGOMFlowRegionElement
 *  org.apache.batik.anim.dom.XBLEventSupport
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.css.engine.value.ComputedValue
 *  org.apache.batik.css.engine.value.Value
 *  org.apache.batik.css.engine.value.ValueConstants
 *  org.apache.batik.css.engine.value.svg12.LineHeightValue
 *  org.apache.batik.css.engine.value.svg12.SVG12ValueConstants
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.dom.events.NodeEventTarget
 *  org.apache.batik.dom.svg.SVGContext
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.dom.util.XMLSupport
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.flow.BlockInfo
 *  org.apache.batik.gvt.flow.RegionInfo
 *  org.apache.batik.gvt.flow.TextLineBreaks
 *  org.apache.batik.gvt.text.GVTAttributedCharacterIterator$TextAttribute
 *  org.apache.batik.gvt.text.TextPaintInfo
 *  org.apache.batik.gvt.text.TextPath
 */
package org.apache.batik.bridge.svg12;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.SVGOMFlowRegionElement;
import org.apache.batik.anim.dom.XBLEventSupport;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.CursorManager;
import org.apache.batik.bridge.FlowTextNode;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.SVGAElementBridge;
import org.apache.batik.bridge.SVGTextElementBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.TextNode;
import org.apache.batik.bridge.TextUtilities;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.svg12.SVG12TextElementBridge;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.ComputedValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.ValueConstants;
import org.apache.batik.css.engine.value.svg12.LineHeightValue;
import org.apache.batik.css.engine.value.svg12.SVG12ValueConstants;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.svg.SVGContext;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.flow.BlockInfo;
import org.apache.batik.gvt.flow.RegionInfo;
import org.apache.batik.gvt.flow.TextLineBreaks;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.apache.batik.gvt.text.TextPath;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

public class SVGFlowRootElementBridge
extends SVG12TextElementBridge {
    public static final AttributedCharacterIterator.Attribute FLOW_PARAGRAPH = GVTAttributedCharacterIterator.TextAttribute.FLOW_PARAGRAPH;
    public static final AttributedCharacterIterator.Attribute FLOW_EMPTY_PARAGRAPH = GVTAttributedCharacterIterator.TextAttribute.FLOW_EMPTY_PARAGRAPH;
    public static final AttributedCharacterIterator.Attribute FLOW_LINE_BREAK = GVTAttributedCharacterIterator.TextAttribute.FLOW_LINE_BREAK;
    public static final AttributedCharacterIterator.Attribute FLOW_REGIONS = GVTAttributedCharacterIterator.TextAttribute.FLOW_REGIONS;
    public static final AttributedCharacterIterator.Attribute LINE_HEIGHT = GVTAttributedCharacterIterator.TextAttribute.LINE_HEIGHT;
    public static final GVTAttributedCharacterIterator.TextAttribute TEXTPATH = GVTAttributedCharacterIterator.TextAttribute.TEXTPATH;
    public static final GVTAttributedCharacterIterator.TextAttribute ANCHOR_TYPE = GVTAttributedCharacterIterator.TextAttribute.ANCHOR_TYPE;
    public static final GVTAttributedCharacterIterator.TextAttribute LETTER_SPACING = GVTAttributedCharacterIterator.TextAttribute.LETTER_SPACING;
    public static final GVTAttributedCharacterIterator.TextAttribute WORD_SPACING = GVTAttributedCharacterIterator.TextAttribute.WORD_SPACING;
    public static final GVTAttributedCharacterIterator.TextAttribute KERNING = GVTAttributedCharacterIterator.TextAttribute.KERNING;
    protected Map flowRegionNodes;
    protected TextNode textNode;
    protected RegionChangeListener regionChangeListener;
    protected int startLen;
    int marginTopIndex = -1;
    int marginRightIndex = -1;
    int marginBottomIndex = -1;
    int marginLeftIndex = -1;
    int indentIndex = -1;
    int textAlignIndex = -1;
    int lineHeightIndex = -1;

    @Override
    protected TextNode getTextNode() {
        return this.textNode;
    }

    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2000/svg";
    }

    @Override
    public String getLocalName() {
        return "flowRoot";
    }

    @Override
    public Bridge getInstance() {
        return new SVGFlowRootElementBridge();
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        CompositeGraphicsNode cgn = new CompositeGraphicsNode();
        String s = e.getAttributeNS(null, "transform");
        if (s.length() != 0) {
            cgn.setTransform(SVGUtilities.convertTransform(e, "transform", s, ctx));
        }
        cgn.setVisible(CSSUtilities.convertVisibility(e));
        RenderingHints hints = null;
        hints = CSSUtilities.convertColorRendering(e, hints);
        hints = CSSUtilities.convertTextRendering(e, hints);
        if (hints != null) {
            cgn.setRenderingHints(hints);
        }
        CompositeGraphicsNode cgn2 = new CompositeGraphicsNode();
        cgn.add((Object)cgn2);
        FlowTextNode tn = (FlowTextNode)this.instantiateGraphicsNode();
        tn.setLocation(this.getLocation(ctx, e));
        if (ctx.getTextPainter() != null) {
            tn.setTextPainter(ctx.getTextPainter());
        }
        this.textNode = tn;
        cgn.add((Object)tn);
        this.associateSVGContext(ctx, e, (GraphicsNode)cgn);
        Node child = this.getFirstChild(e);
        while (child != null) {
            if (child.getNodeType() == 1) {
                this.addContextToChild(ctx, (Element)child);
            }
            child = this.getNextSibling(child);
        }
        return cgn;
    }

    @Override
    protected GraphicsNode instantiateGraphicsNode() {
        return new FlowTextNode();
    }

    @Override
    protected Point2D getLocation(BridgeContext ctx, Element e) {
        return new Point2D.Float(0.0f, 0.0f);
    }

    @Override
    protected boolean isTextElement(Element e) {
        if (!"http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            return false;
        }
        String nodeName = e.getLocalName();
        return nodeName.equals("flowDiv") || nodeName.equals("flowLine") || nodeName.equals("flowPara") || nodeName.equals("flowRegionBreak") || nodeName.equals("flowSpan");
    }

    @Override
    protected boolean isTextChild(Element e) {
        if (!"http://www.w3.org/2000/svg".equals(e.getNamespaceURI())) {
            return false;
        }
        String nodeName = e.getLocalName();
        return nodeName.equals("a") || nodeName.equals("flowLine") || nodeName.equals("flowPara") || nodeName.equals("flowRegionBreak") || nodeName.equals("flowSpan");
    }

    @Override
    public void buildGraphicsNode(BridgeContext ctx, Element e, GraphicsNode node) {
        boolean isStatic;
        CompositeGraphicsNode cgn = (CompositeGraphicsNode)node;
        boolean bl = isStatic = !ctx.isDynamic();
        if (isStatic) {
            this.flowRegionNodes = new HashMap();
        } else {
            this.regionChangeListener = new RegionChangeListener();
        }
        CompositeGraphicsNode cgn2 = (CompositeGraphicsNode)cgn.get(0);
        GVTBuilder builder = ctx.getGVTBuilder();
        Node n = this.getFirstChild(e);
        while (n != null) {
            if (n instanceof SVGOMFlowRegionElement) {
                Node m = this.getFirstChild(n);
                while (m != null) {
                    GraphicsNode gn;
                    if (m.getNodeType() == 1 && (gn = builder.build(ctx, (Element)m)) != null) {
                        cgn2.add((Object)gn);
                        if (isStatic) {
                            this.flowRegionNodes.put(m, gn);
                        }
                    }
                    m = this.getNextSibling(m);
                }
                if (!isStatic) {
                    AbstractNode an = (AbstractNode)n;
                    XBLEventSupport es = (XBLEventSupport)an.initializeEventSupport();
                    es.addImplementationEventListenerNS("http://www.w3.org/2000/svg", "shapechange", (EventListener)this.regionChangeListener, false);
                }
            }
            n = this.getNextSibling(n);
        }
        GraphicsNode tn = (GraphicsNode)cgn.get(1);
        super.buildGraphicsNode(ctx, e, tn);
        this.flowRegionNodes = null;
    }

    @Override
    protected void computeLaidoutText(BridgeContext ctx, Element e, GraphicsNode node) {
        super.computeLaidoutText(ctx, this.getFlowDivElement(e), node);
    }

    @Override
    protected void addContextToChild(BridgeContext ctx, Element e) {
        String ln;
        if ("http://www.w3.org/2000/svg".equals(e.getNamespaceURI()) && ((ln = e.getLocalName()).equals("flowDiv") || ln.equals("flowLine") || ln.equals("flowPara") || ln.equals("flowSpan"))) {
            ((SVGOMElement)e).setSVGContext((SVGContext)new FlowContentBridge(ctx, (SVGTextElementBridge)this, e));
        }
        Node child = this.getFirstChild(e);
        while (child != null) {
            if (child.getNodeType() == 1) {
                this.addContextToChild(ctx, (Element)child);
            }
            child = this.getNextSibling(child);
        }
    }

    @Override
    protected void removeContextFromChild(BridgeContext ctx, Element e) {
        String ln;
        if ("http://www.w3.org/2000/svg".equals(e.getNamespaceURI()) && ((ln = e.getLocalName()).equals("flowDiv") || ln.equals("flowLine") || ln.equals("flowPara") || ln.equals("flowSpan"))) {
            ((SVGTextElementBridge.AbstractTextChildBridgeUpdateHandler)((SVGOMElement)e).getSVGContext()).dispose();
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
    protected AttributedString buildAttributedString(BridgeContext ctx, Element element) {
        if (element == null) {
            return null;
        }
        List rgns = this.getRegions(ctx, element);
        AttributedString ret = this.getFlowDiv(ctx, element);
        if (ret == null) {
            return ret;
        }
        ret.addAttribute(FLOW_REGIONS, rgns, 0, 1);
        TextLineBreaks.findLineBrk((AttributedString)ret);
        return ret;
    }

    protected void dumpACIWord(AttributedString as) {
        if (as == null) {
            return;
        }
        StringBuffer chars = new StringBuffer();
        StringBuffer brkStr = new StringBuffer();
        AttributedCharacterIterator aci = as.getIterator();
        AttributedCharacterIterator.Attribute WORD_LIMIT = TextLineBreaks.WORD_LIMIT;
        char ch = aci.current();
        while (ch != '\uffff') {
            chars.append(ch).append(' ').append(' ');
            int w = (Integer)aci.getAttribute(WORD_LIMIT);
            brkStr.append(w).append(' ');
            if (w < 10) {
                brkStr.append(' ');
            }
            ch = aci.next();
        }
        System.out.println(chars.toString());
        System.out.println(brkStr.toString());
    }

    protected Element getFlowDivElement(Element elem) {
        String eNS = elem.getNamespaceURI();
        if (!eNS.equals("http://www.w3.org/2000/svg")) {
            return null;
        }
        String nodeName = elem.getLocalName();
        if (nodeName.equals("flowDiv")) {
            return elem;
        }
        if (!nodeName.equals("flowRoot")) {
            return null;
        }
        Node n = this.getFirstChild(elem);
        while (n != null) {
            Element e;
            String ln;
            String nNS;
            if (n.getNodeType() == 1 && "http://www.w3.org/2000/svg".equals(nNS = n.getNamespaceURI()) && (ln = (e = (Element)n).getLocalName()).equals("flowDiv")) {
                return e;
            }
            n = this.getNextSibling(n);
        }
        return null;
    }

    protected AttributedString getFlowDiv(BridgeContext ctx, Element element) {
        Element flowDiv = this.getFlowDivElement(element);
        if (flowDiv == null) {
            return null;
        }
        return this.gatherFlowPara(ctx, flowDiv);
    }

    protected AttributedString gatherFlowPara(BridgeContext ctx, Element div) {
        TextPaintInfo divTPI = new TextPaintInfo();
        divTPI.visible = true;
        divTPI.fillPaint = Color.black;
        this.elemTPI.put(div, divTPI);
        SVGTextElementBridge.AttributedStringBuffer asb = new SVGTextElementBridge.AttributedStringBuffer();
        ArrayList<Integer> paraEnds = new ArrayList<Integer>();
        ArrayList<Element> paraElems = new ArrayList<Element>();
        ArrayList lnLocs = new ArrayList();
        Node n = this.getFirstChild(div);
        while (n != null) {
            if (n.getNodeType() == 1 && this.getNamespaceURI().equals(n.getNamespaceURI())) {
                Element e = (Element)n;
                String ln = e.getLocalName();
                if (ln.equals("flowPara")) {
                    this.fillAttributedStringBuffer(ctx, e, true, null, null, asb, lnLocs);
                    paraElems.add(e);
                    paraEnds.add(asb.length());
                } else if (ln.equals("flowRegionBreak")) {
                    this.fillAttributedStringBuffer(ctx, e, true, null, null, asb, lnLocs);
                    paraElems.add(e);
                    paraEnds.add(asb.length());
                }
            }
            n = this.getNextSibling(n);
        }
        divTPI.startChar = 0;
        divTPI.endChar = asb.length() - 1;
        AttributedString ret = asb.toAttributedString();
        if (ret == null) {
            return null;
        }
        int prevLN = 0;
        for (Object lnLoc : lnLocs) {
            int nextLN = (Integer)lnLoc;
            if (nextLN == prevLN) continue;
            ret.addAttribute(FLOW_LINE_BREAK, new Object(), prevLN, nextLN);
            prevLN = nextLN;
        }
        int start = 0;
        LinkedList<BlockInfo> emptyPara = null;
        for (int i = 0; i < paraElems.size(); ++i) {
            Element elem = (Element)paraElems.get(i);
            int end = (Integer)paraEnds.get(i);
            if (start == end) {
                if (emptyPara == null) {
                    emptyPara = new LinkedList<BlockInfo>();
                }
                emptyPara.add(this.makeBlockInfo(ctx, elem));
            } else {
                ret.addAttribute(FLOW_PARAGRAPH, this.makeBlockInfo(ctx, elem), start, end);
                if (emptyPara != null) {
                    ret.addAttribute(FLOW_EMPTY_PARAGRAPH, emptyPara, start, end);
                    emptyPara = null;
                }
            }
            start = end;
        }
        return ret;
    }

    protected List getRegions(BridgeContext ctx, Element element) {
        element = (Element)element.getParentNode();
        LinkedList ret = new LinkedList();
        Node n = this.getFirstChild(element);
        while (n != null) {
            Element e;
            String ln;
            if (n.getNodeType() == 1 && "http://www.w3.org/2000/svg".equals(n.getNamespaceURI()) && "flowRegion".equals(ln = (e = (Element)n).getLocalName())) {
                float verticalAlignment = 0.0f;
                this.gatherRegionInfo(ctx, e, verticalAlignment, ret);
            }
            n = this.getNextSibling(n);
        }
        return ret;
    }

    protected void gatherRegionInfo(BridgeContext ctx, Element rgn, float verticalAlign, List regions) {
        boolean isStatic = !ctx.isDynamic();
        Node n = this.getFirstChild(rgn);
        while (n != null) {
            GraphicsNode gn;
            Shape s;
            if (n.getNodeType() == 1 && (s = (gn = isStatic ? (GraphicsNode)this.flowRegionNodes.get(n) : ctx.getGraphicsNode(n)).getOutline()) != null) {
                AffineTransform at = gn.getTransform();
                if (at != null) {
                    s = at.createTransformedShape(s);
                }
                regions.add(new RegionInfo(s, verticalAlign));
            }
            n = this.getNextSibling(n);
        }
    }

    protected void fillAttributedStringBuffer(BridgeContext ctx, Element element, boolean top, Integer bidiLevel, Map initialAttributes, SVGTextElementBridge.AttributedStringBuffer asb, List lnLocs) {
        Integer i;
        if (!SVGUtilities.matchUserAgent(element, ctx.getUserAgent()) || !CSSUtilities.convertDisplay(element)) {
            return;
        }
        String s = XMLSupport.getXMLSpace((Element)element);
        boolean preserve = s.equals("preserve");
        Element nodeElement = element;
        int elementStartChar = asb.length();
        if (top) {
            this.endLimit = this.startLen = asb.length();
        }
        if (preserve) {
            this.endLimit = this.startLen;
        }
        HashMap map = initialAttributes == null ? new HashMap() : new HashMap(initialAttributes);
        initialAttributes = this.getAttributeMap(ctx, element, null, bidiLevel, map);
        Object o = map.get(TextAttribute.BIDI_EMBEDDING);
        Integer subBidiLevel = bidiLevel;
        if (o != null) {
            subBidiLevel = (Integer)o;
        }
        int lineBreak = -1;
        if (lnLocs.size() != 0) {
            lineBreak = (Integer)lnLocs.get(lnLocs.size() - 1);
        }
        Node n = this.getFirstChild(element);
        while (n != null) {
            boolean prevEndsWithSpace;
            if (preserve) {
                prevEndsWithSpace = false;
            } else {
                int len = asb.length();
                if (len == this.startLen) {
                    prevEndsWithSpace = true;
                } else {
                    prevEndsWithSpace = asb.getLastChar() == 32;
                    int idx = lnLocs.size() - 1;
                    if (!prevEndsWithSpace && idx >= 0 && (i = (Integer)lnLocs.get(idx)) == len) {
                        prevEndsWithSpace = true;
                    }
                }
            }
            switch (n.getNodeType()) {
                case 1: {
                    int before;
                    if (!"http://www.w3.org/2000/svg".equals(n.getNamespaceURI())) break;
                    nodeElement = (Element)n;
                    String ln = n.getLocalName();
                    if (ln.equals("flowLine")) {
                        before = asb.length();
                        this.fillAttributedStringBuffer(ctx, nodeElement, false, subBidiLevel, initialAttributes, asb, lnLocs);
                        lineBreak = asb.length();
                        lnLocs.add(lineBreak);
                        if (before == lineBreak) break;
                        initialAttributes = null;
                        break;
                    }
                    if (ln.equals("flowSpan") || ln.equals("altGlyph")) {
                        before = asb.length();
                        this.fillAttributedStringBuffer(ctx, nodeElement, false, subBidiLevel, initialAttributes, asb, lnLocs);
                        if (asb.length() == before) break;
                        initialAttributes = null;
                        break;
                    }
                    if (ln.equals("a")) {
                        if (ctx.isInteractive()) {
                            NodeEventTarget target = (NodeEventTarget)nodeElement;
                            UserAgent ua = ctx.getUserAgent();
                            SVGAElementBridge.CursorHolder ch = new SVGAElementBridge.CursorHolder(CursorManager.DEFAULT_CURSOR);
                            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "click", (EventListener)new SVGAElementBridge.AnchorListener(ua, ch), false, null);
                            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseover", (EventListener)new SVGAElementBridge.CursorMouseOverListener(ua, ch), false, null);
                            target.addEventListenerNS("http://www.w3.org/2001/xml-events", "mouseout", (EventListener)new SVGAElementBridge.CursorMouseOutListener(ua, ch), false, null);
                        }
                        before = asb.length();
                        this.fillAttributedStringBuffer(ctx, nodeElement, false, subBidiLevel, initialAttributes, asb, lnLocs);
                        if (asb.length() == before) break;
                        initialAttributes = null;
                        break;
                    }
                    if (!ln.equals("tref")) break;
                    String uriStr = XLinkSupport.getXLinkHref((Element)((Element)n));
                    Element ref = ctx.getReferencedElement((Element)n, uriStr);
                    s = TextUtilities.getElementContent(ref);
                    if ((s = this.normalizeString(s, preserve, prevEndsWithSpace)).length() == 0) break;
                    int trefStart = asb.length();
                    HashMap m = new HashMap();
                    this.getAttributeMap(ctx, nodeElement, null, bidiLevel, m);
                    asb.append(s, m);
                    int trefEnd = asb.length() - 1;
                    TextPaintInfo tpi = (TextPaintInfo)this.elemTPI.get(nodeElement);
                    tpi.startChar = trefStart;
                    tpi.endChar = trefEnd;
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
                int idx = lnLocs.size() - 1;
                int len = asb.length();
                if (idx >= 0 && (i = (Integer)lnLocs.get(idx)) >= len) {
                    i = len - 1;
                    lnLocs.set(idx, i);
                    --idx;
                    while (idx >= 0 && (i = (Integer)lnLocs.get(idx)) >= len - 1) {
                        lnLocs.remove(idx);
                        --idx;
                    }
                }
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

    @Override
    protected Map getAttributeMap(BridgeContext ctx, Element element, TextPath textPath, Integer bidiLevel, Map result) {
        Map inheritingMap = super.getAttributeMap(ctx, element, textPath, bidiLevel, result);
        float fontSize = TextUtilities.convertFontSize(element).floatValue();
        float lineHeight = this.getLineHeight(ctx, element, fontSize);
        result.put(LINE_HEIGHT, Float.valueOf(lineHeight));
        return inheritingMap;
    }

    protected void checkMap(Map attrs) {
        if (attrs.containsKey(TEXTPATH)) {
            return;
        }
        if (attrs.containsKey(ANCHOR_TYPE)) {
            return;
        }
        if (attrs.containsKey(LETTER_SPACING)) {
            return;
        }
        if (attrs.containsKey(WORD_SPACING)) {
            return;
        }
        if (attrs.containsKey(KERNING)) {
            return;
        }
    }

    protected void initCSSPropertyIndexes(Element e) {
        CSSEngine eng = CSSUtilities.getCSSEngine(e);
        this.marginTopIndex = eng.getPropertyIndex("margin-top");
        this.marginRightIndex = eng.getPropertyIndex("margin-right");
        this.marginBottomIndex = eng.getPropertyIndex("margin-bottom");
        this.marginLeftIndex = eng.getPropertyIndex("margin-left");
        this.indentIndex = eng.getPropertyIndex("indent");
        this.textAlignIndex = eng.getPropertyIndex("text-align");
        this.lineHeightIndex = eng.getPropertyIndex("line-height");
    }

    public BlockInfo makeBlockInfo(BridgeContext ctx, Element element) {
        if (this.marginTopIndex == -1) {
            this.initCSSPropertyIndexes(element);
        }
        Value v = CSSUtilities.getComputedStyle(element, this.marginTopIndex);
        float top = v.getFloatValue();
        v = CSSUtilities.getComputedStyle(element, this.marginRightIndex);
        float right = v.getFloatValue();
        v = CSSUtilities.getComputedStyle(element, this.marginBottomIndex);
        float bottom = v.getFloatValue();
        v = CSSUtilities.getComputedStyle(element, this.marginLeftIndex);
        float left = v.getFloatValue();
        v = CSSUtilities.getComputedStyle(element, this.indentIndex);
        float indent = v.getFloatValue();
        v = CSSUtilities.getComputedStyle(element, this.textAlignIndex);
        if (v == ValueConstants.INHERIT_VALUE) {
            v = CSSUtilities.getComputedStyle(element, 11);
            v = v == ValueConstants.LTR_VALUE ? SVG12ValueConstants.START_VALUE : SVG12ValueConstants.END_VALUE;
        }
        int textAlign = v == SVG12ValueConstants.START_VALUE ? 0 : (v == SVG12ValueConstants.MIDDLE_VALUE ? 1 : (v == SVG12ValueConstants.END_VALUE ? 2 : 3));
        HashMap fontAttrs = new HashMap(20);
        List fontList = this.getFontList(ctx, element, fontAttrs);
        Float fs = (Float)fontAttrs.get(TextAttribute.SIZE);
        float fontSize = fs.floatValue();
        float lineHeight = this.getLineHeight(ctx, element, fontSize);
        String ln = element.getLocalName();
        boolean rgnBr = ln.equals("flowRegionBreak");
        return new BlockInfo(top, right, bottom, left, indent, textAlign, lineHeight, fontList, fontAttrs, rgnBr);
    }

    protected float getLineHeight(BridgeContext ctx, Element element, float fontSize) {
        Value v;
        if (this.lineHeightIndex == -1) {
            this.initCSSPropertyIndexes(element);
        }
        if ((v = CSSUtilities.getComputedStyle(element, this.lineHeightIndex)) == ValueConstants.INHERIT_VALUE || v == SVG12ValueConstants.NORMAL_VALUE) {
            return fontSize * 1.1f;
        }
        float lineHeight = v.getFloatValue();
        if (v instanceof ComputedValue) {
            v = ((ComputedValue)v).getComputedValue();
        }
        if (v instanceof LineHeightValue && ((LineHeightValue)v).getFontSizeRelative()) {
            lineHeight *= fontSize;
        }
        return lineHeight;
    }

    protected class RegionChangeListener
    implements EventListener {
        protected RegionChangeListener() {
        }

        @Override
        public void handleEvent(Event evt) {
            SVGFlowRootElementBridge.this.laidoutText = null;
            SVGFlowRootElementBridge.this.computeLaidoutText(SVGFlowRootElementBridge.this.ctx, SVGFlowRootElementBridge.this.e, (GraphicsNode)SVGFlowRootElementBridge.this.getTextNode());
        }
    }

    protected class FlowContentBridge
    extends SVGTextElementBridge.AbstractTextChildTextContent {
        public FlowContentBridge(BridgeContext ctx, SVGTextElementBridge parent, Element e) {
            super(ctx, parent, e);
        }
    }
}

