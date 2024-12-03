/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.ext.awt.image.renderable.ClipRable
 *  org.apache.batik.ext.awt.image.renderable.ClipRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.ImageNode
 *  org.apache.batik.parser.UnitProcessor$Context
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.bridge.svg12;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.MultiResGraphicsNode;
import org.apache.batik.bridge.SVGImageElementBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.bridge.Viewport;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ImageNode;
import org.apache.batik.parser.UnitProcessor;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class SVGMultiImageElementBridge
extends SVGImageElementBridge {
    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2000/svg";
    }

    @Override
    public String getLocalName() {
        return "multiImage";
    }

    @Override
    public Bridge getInstance() {
        return new SVGMultiImageElementBridge();
    }

    @Override
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }
        ImageNode imgNode = (ImageNode)this.instantiateGraphicsNode();
        if (imgNode == null) {
            return null;
        }
        this.associateSVGContext(ctx, e, (GraphicsNode)imgNode);
        Rectangle2D b = SVGMultiImageElementBridge.getImageBounds(ctx, e);
        AffineTransform at = null;
        String s = e.getAttribute("transform");
        at = s.length() != 0 ? SVGUtilities.convertTransform(e, "transform", s, ctx) : new AffineTransform();
        at.translate(b.getX(), b.getY());
        imgNode.setTransform(at);
        imgNode.setVisible(CSSUtilities.convertVisibility(e));
        Rectangle2D.Double clip = new Rectangle2D.Double(0.0, 0.0, b.getWidth(), b.getHeight());
        Filter filter = imgNode.getGraphicsNodeRable(true);
        imgNode.setClip((ClipRable)new ClipRable8Bit(filter, (Shape)clip));
        Rectangle2D r = CSSUtilities.convertEnableBackground(e);
        if (r != null) {
            imgNode.setBackgroundEnable(r);
        }
        ctx.openViewport(e, new MultiImageElementViewport((float)b.getWidth(), (float)b.getHeight()));
        LinkedList elems = new LinkedList();
        LinkedList minDim = new LinkedList();
        LinkedList maxDim = new LinkedList();
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != 1) continue;
            Element se = (Element)n;
            if (!this.getNamespaceURI().equals(se.getNamespaceURI())) continue;
            if (se.getLocalName().equals("subImage")) {
                this.addInfo(se, elems, minDim, maxDim, b);
            }
            if (!se.getLocalName().equals("subImageRef")) continue;
            this.addRefInfo(se, elems, minDim, maxDim, b);
        }
        Dimension[] mindary = new Dimension[elems.size()];
        Dimension[] maxdary = new Dimension[elems.size()];
        Element[] elemary = new Element[elems.size()];
        Iterator mindi = minDim.iterator();
        Iterator maxdi = maxDim.iterator();
        Iterator ei = elems.iterator();
        int n = 0;
        while (mindi.hasNext()) {
            int i;
            Dimension minD = (Dimension)mindi.next();
            Dimension maxD = (Dimension)maxdi.next();
            if (minD != null) {
                for (i = 0; i < n && (mindary[i] == null || minD.width >= mindary[i].width); ++i) {
                }
            }
            for (int j = n; j > i; --j) {
                elemary[j] = elemary[j - 1];
                mindary[j] = mindary[j - 1];
                maxdary[j] = maxdary[j - 1];
            }
            elemary[i] = (Element)ei.next();
            mindary[i] = minD;
            maxdary[i] = maxD;
            ++n;
        }
        MultiResGraphicsNode node = new MultiResGraphicsNode(e, clip, elemary, mindary, maxdary, ctx);
        imgNode.setImage((GraphicsNode)node);
        return imgNode;
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public void buildGraphicsNode(BridgeContext ctx, Element e, GraphicsNode node) {
        this.initializeDynamicSupport(ctx, e, node);
        ctx.closeViewport(e);
    }

    @Override
    protected void initializeDynamicSupport(BridgeContext ctx, Element e, GraphicsNode node) {
        if (ctx.isInteractive()) {
            ImageNode imgNode = (ImageNode)node;
            ctx.bind(e, imgNode.getImage());
        }
    }

    @Override
    public void dispose() {
        this.ctx.removeViewport(this.e);
        super.dispose();
    }

    protected static Rectangle2D getImageBounds(BridgeContext ctx, Element element) {
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, element);
        String s = element.getAttributeNS(null, "x");
        float x = 0.0f;
        if (s.length() != 0) {
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace(s, "x", uctx);
        }
        s = element.getAttributeNS(null, "y");
        float y = 0.0f;
        if (s.length() != 0) {
            y = UnitProcessor.svgVerticalCoordinateToUserSpace(s, "y", uctx);
        }
        if ((s = element.getAttributeNS(null, "width")).length() == 0) {
            throw new BridgeException(ctx, element, "attribute.missing", new Object[]{"width"});
        }
        float w = UnitProcessor.svgHorizontalLengthToUserSpace(s, "width", uctx);
        s = element.getAttributeNS(null, "height");
        if (s.length() == 0) {
            throw new BridgeException(ctx, element, "attribute.missing", new Object[]{"height"});
        }
        float h = UnitProcessor.svgVerticalLengthToUserSpace(s, "height", uctx);
        return new Rectangle2D.Float(x, y, w, h);
    }

    protected void addInfo(Element e, Collection elems, Collection minDim, Collection maxDim, Rectangle2D bounds) {
        Document doc = e.getOwnerDocument();
        Element gElem = doc.createElementNS("http://www.w3.org/2000/svg", "g");
        NamedNodeMap attrs = e.getAttributes();
        int len = attrs.getLength();
        for (int i = 0; i < len; ++i) {
            Attr attr = (Attr)attrs.item(i);
            gElem.setAttributeNS(attr.getNamespaceURI(), attr.getName(), attr.getValue());
        }
        Node n = e.getFirstChild();
        while (n != null) {
            gElem.appendChild(n);
            n = e.getFirstChild();
        }
        e.appendChild(gElem);
        elems.add(gElem);
        minDim.add(this.getElementMinPixel(e, bounds));
        maxDim.add(this.getElementMaxPixel(e, bounds));
    }

    protected void addRefInfo(Element e, Collection elems, Collection minDim, Collection maxDim, Rectangle2D bounds) {
        String uriStr = XLinkSupport.getXLinkHref((Element)e);
        if (uriStr.length() == 0) {
            throw new BridgeException(this.ctx, e, "attribute.missing", new Object[]{"xlink:href"});
        }
        String baseURI = AbstractNode.getBaseURI((Node)e);
        ParsedURL purl = baseURI == null ? new ParsedURL(uriStr) : new ParsedURL(baseURI, uriStr);
        Document doc = e.getOwnerDocument();
        Element imgElem = doc.createElementNS("http://www.w3.org/2000/svg", "image");
        imgElem.setAttributeNS("http://www.w3.org/1999/xlink", "href", purl.toString());
        NamedNodeMap attrs = e.getAttributes();
        int len = attrs.getLength();
        for (int i = 0; i < len; ++i) {
            Attr attr = (Attr)attrs.item(i);
            imgElem.setAttributeNS(attr.getNamespaceURI(), attr.getName(), attr.getValue());
        }
        String s = e.getAttribute("x");
        if (s.length() == 0) {
            imgElem.setAttribute("x", "0");
        }
        if ((s = e.getAttribute("y")).length() == 0) {
            imgElem.setAttribute("y", "0");
        }
        if ((s = e.getAttribute("width")).length() == 0) {
            imgElem.setAttribute("width", "100%");
        }
        if ((s = e.getAttribute("height")).length() == 0) {
            imgElem.setAttribute("height", "100%");
        }
        e.appendChild(imgElem);
        elems.add(imgElem);
        minDim.add(this.getElementMinPixel(e, bounds));
        maxDim.add(this.getElementMaxPixel(e, bounds));
    }

    protected Dimension getElementMinPixel(Element e, Rectangle2D bounds) {
        return this.getElementPixelSize(e, "max-pixel-size", bounds);
    }

    protected Dimension getElementMaxPixel(Element e, Rectangle2D bounds) {
        return this.getElementPixelSize(e, "min-pixel-size", bounds);
    }

    protected Dimension getElementPixelSize(Element e, String attr, Rectangle2D bounds) {
        float xPixSz;
        String s = e.getAttribute(attr);
        if (s.length() == 0) {
            return null;
        }
        Float[] vals = SVGUtilities.convertSVGNumberOptionalNumber(e, attr, s, this.ctx);
        if (vals[0] == null) {
            return null;
        }
        float yPixSz = xPixSz = vals[0].floatValue();
        if (vals[1] != null) {
            yPixSz = vals[1].floatValue();
        }
        return new Dimension((int)(bounds.getWidth() / (double)xPixSz + 0.5), (int)(bounds.getHeight() / (double)yPixSz + 0.5));
    }

    public static class MultiImageElementViewport
    implements Viewport {
        private float width;
        private float height;

        public MultiImageElementViewport(float w, float h) {
            this.width = w;
            this.height = h;
        }

        @Override
        public float getWidth() {
            return this.width;
        }

        @Override
        public float getHeight() {
            return this.height;
        }
    }
}

