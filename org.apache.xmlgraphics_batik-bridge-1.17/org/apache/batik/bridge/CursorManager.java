/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.value.Value
 *  org.apache.batik.dom.AbstractNode
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.AffineRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.PadRable8Bit
 *  org.apache.batik.ext.awt.image.spi.BrokenLinkProvider
 *  org.apache.batik.ext.awt.image.spi.ImageTagRegistry
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.parser.UnitProcessor$Context
 *  org.apache.batik.util.ParsedURL
 *  org.apache.batik.util.Platform
 *  org.apache.batik.util.SVGConstants
 *  org.apache.batik.util.SoftReferenceCache
 *  org.w3c.dom.svg.SVGDocument
 *  org.w3c.dom.svg.SVGSVGElement
 */
package org.apache.batik.bridge;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.Map;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.URIResolver;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.AffineRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.ext.awt.image.spi.BrokenLinkProvider;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.UnitProcessor;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.Platform;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.SoftReferenceCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

public class CursorManager
implements SVGConstants,
ErrorConstants {
    protected static Map cursorMap;
    public static final Cursor DEFAULT_CURSOR;
    public static final Cursor ANCHOR_CURSOR;
    public static final Cursor TEXT_CURSOR;
    public static final int DEFAULT_PREFERRED_WIDTH = 32;
    public static final int DEFAULT_PREFERRED_HEIGHT = 32;
    protected BridgeContext ctx;
    protected CursorCache cursorCache = new CursorCache();

    public CursorManager(BridgeContext ctx) {
        this.ctx = ctx;
    }

    public static Cursor getPredefinedCursor(String cursorName) {
        return (Cursor)cursorMap.get(cursorName);
    }

    public Cursor convertCursor(Element e) {
        Value cursorValue = CSSUtilities.getComputedStyle(e, 10);
        String cursorStr = "auto";
        if (cursorValue != null) {
            if (cursorValue.getCssValueType() == 1 && cursorValue.getPrimitiveType() == 21) {
                cursorStr = cursorValue.getStringValue();
                return this.convertBuiltInCursor(e, cursorStr);
            }
            if (cursorValue.getCssValueType() == 2) {
                int nValues = cursorValue.getLength();
                if (nValues == 1) {
                    if ((cursorValue = cursorValue.item(0)).getPrimitiveType() == 21) {
                        cursorStr = cursorValue.getStringValue();
                        return this.convertBuiltInCursor(e, cursorStr);
                    }
                } else if (nValues > 1) {
                    return this.convertSVGCursor(e, cursorValue);
                }
            }
        }
        return this.convertBuiltInCursor(e, cursorStr);
    }

    public Cursor convertBuiltInCursor(Element e, String cursorStr) {
        Cursor cursor = null;
        if (cursorStr.charAt(0) == 'a') {
            String nameSpaceURI = e.getNamespaceURI();
            if ("http://www.w3.org/2000/svg".equals(nameSpaceURI)) {
                String tag = e.getLocalName();
                if ("a".equals(tag)) {
                    cursor = ANCHOR_CURSOR;
                } else if ("text".equals(tag) || "tspan".equals(tag) || "tref".equals(tag)) {
                    cursor = TEXT_CURSOR;
                } else {
                    if ("image".equals(tag)) {
                        return null;
                    }
                    cursor = DEFAULT_CURSOR;
                }
            } else {
                cursor = DEFAULT_CURSOR;
            }
        } else {
            cursor = CursorManager.getPredefinedCursor(cursorStr);
        }
        return cursor;
    }

    public Cursor convertSVGCursor(Element e, Value l) {
        int nValues = l.getLength();
        Element cursorElement = null;
        for (int i = 0; i < nValues - 1; ++i) {
            Cursor c;
            String cursorNS;
            block4: {
                Value cursorValue = l.item(i);
                if (cursorValue.getPrimitiveType() != 20) continue;
                String uri = cursorValue.getStringValue();
                try {
                    cursorElement = this.ctx.getReferencedElement(e, uri);
                }
                catch (BridgeException be) {
                    if ("uri.badTarget".equals(be.getCode())) break block4;
                    throw be;
                }
            }
            if (cursorElement == null || !"http://www.w3.org/2000/svg".equals(cursorNS = cursorElement.getNamespaceURI()) || !"cursor".equals(cursorElement.getLocalName()) || (c = this.convertSVGCursorElement(cursorElement)) == null) continue;
            return c;
        }
        Value cursorValue = l.item(nValues - 1);
        String cursorStr = "auto";
        if (cursorValue.getPrimitiveType() == 21) {
            cursorStr = cursorValue.getStringValue();
        }
        return this.convertBuiltInCursor(e, cursorStr);
    }

    public Cursor convertSVGCursorElement(Element cursorElement) {
        CursorDescriptor desc;
        Cursor cachedCursor;
        String uriStr = XLinkSupport.getXLinkHref((Element)cursorElement);
        if (uriStr.length() == 0) {
            throw new BridgeException(this.ctx, cursorElement, "attribute.missing", new Object[]{"xlink:href"});
        }
        String baseURI = AbstractNode.getBaseURI((Node)cursorElement);
        ParsedURL purl = baseURI == null ? new ParsedURL(uriStr) : new ParsedURL(baseURI, uriStr);
        UnitProcessor.Context uctx = UnitProcessor.createContext(this.ctx, cursorElement);
        String s = cursorElement.getAttributeNS(null, "x");
        float x = 0.0f;
        if (s.length() != 0) {
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace(s, "x", uctx);
        }
        s = cursorElement.getAttributeNS(null, "y");
        float y = 0.0f;
        if (s.length() != 0) {
            y = UnitProcessor.svgVerticalCoordinateToUserSpace(s, "y", uctx);
        }
        if ((cachedCursor = this.cursorCache.getCursor(desc = new CursorDescriptor(purl, x, y))) != null) {
            return cachedCursor;
        }
        Point2D.Float hotSpot = new Point2D.Float(x, y);
        Filter f = this.cursorHrefToFilter(cursorElement, purl, hotSpot);
        if (f == null) {
            this.cursorCache.clearCursor(desc);
            return null;
        }
        Rectangle cursorSize = f.getBounds2D().getBounds();
        RenderedImage ri = f.createScaledRendering(cursorSize.width, cursorSize.height, null);
        Image img = null;
        img = ri instanceof Image ? (Image)((Object)ri) : this.renderedImageToImage(ri);
        hotSpot.x = hotSpot.x < 0.0f ? 0.0f : hotSpot.x;
        hotSpot.y = hotSpot.y < 0.0f ? 0.0f : hotSpot.y;
        hotSpot.x = hotSpot.x > (float)(cursorSize.width - 1) ? (float)(cursorSize.width - 1) : hotSpot.x;
        hotSpot.y = hotSpot.y > (float)(cursorSize.height - 1) ? (float)(cursorSize.height - 1) : hotSpot.y;
        Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(img, new Point(Math.round(hotSpot.x), Math.round(hotSpot.y)), purl.toString());
        this.cursorCache.putCursor(desc, c);
        return c;
    }

    protected Filter cursorHrefToFilter(Element cursorElement, ParsedURL purl, Point2D hotSpot) {
        AffineRable8Bit f = null;
        String uriStr = purl.toString();
        Dimension cursorSize = null;
        DocumentLoader loader = this.ctx.getDocumentLoader();
        SVGDocument svgDoc = (SVGDocument)cursorElement.getOwnerDocument();
        URIResolver resolver = this.ctx.createURIResolver(svgDoc, loader);
        try {
            SVGSVGElement rootElement = null;
            Node n = resolver.getNode(uriStr, cursorElement);
            if (n.getNodeType() != 9) {
                throw new BridgeException(this.ctx, cursorElement, "uri.image.invalid", new Object[]{uriStr});
            }
            SVGDocument doc = (SVGDocument)n;
            this.ctx.initializeDocument((Document)doc);
            rootElement = doc.getRootElement();
            GraphicsNode node = this.ctx.getGVTBuilder().build(this.ctx, (Element)rootElement);
            float width = 32.0f;
            float height = 32.0f;
            UnitProcessor.Context uctx = UnitProcessor.createContext(this.ctx, (Element)rootElement);
            String s = rootElement.getAttribute("width");
            if (s.length() != 0) {
                width = UnitProcessor.svgHorizontalLengthToUserSpace(s, "width", uctx);
            }
            if ((s = rootElement.getAttribute("height")).length() != 0) {
                height = UnitProcessor.svgVerticalLengthToUserSpace(s, "height", uctx);
            }
            cursorSize = Toolkit.getDefaultToolkit().getBestCursorSize(Math.round(width), Math.round(height));
            AffineTransform at = ViewBox.getPreserveAspectRatioTransform((Element)rootElement, cursorSize.width, cursorSize.height, this.ctx);
            Filter filter = node.getGraphicsNodeRable(true);
            f = new AffineRable8Bit(filter, at);
        }
        catch (BridgeException ex) {
            throw ex;
        }
        catch (SecurityException ex) {
            throw new BridgeException(this.ctx, cursorElement, ex, "uri.unsecure", new Object[]{uriStr});
        }
        catch (Exception ex) {
            // empty catch block
        }
        if (f == null) {
            ImageTagRegistry reg = ImageTagRegistry.getRegistry();
            Filter filter = reg.readURL(purl);
            if (filter == null) {
                return null;
            }
            if (BrokenLinkProvider.hasBrokenLinkProperty((Filter)filter)) {
                return null;
            }
            Rectangle preferredSize = filter.getBounds2D().getBounds();
            cursorSize = Toolkit.getDefaultToolkit().getBestCursorSize(preferredSize.width, preferredSize.height);
            if (preferredSize != null && preferredSize.width > 0 && preferredSize.height > 0) {
                AffineTransform at = new AffineTransform();
                if (preferredSize.width > cursorSize.width || preferredSize.height > cursorSize.height) {
                    at = ViewBox.getPreserveAspectRatioTransform(new float[]{0.0f, 0.0f, preferredSize.width, preferredSize.height}, (short)2, true, (float)cursorSize.width, cursorSize.height);
                }
                f = new AffineRable8Bit(filter, at);
            } else {
                return null;
            }
        }
        AffineTransform at = f.getAffine();
        at.transform(hotSpot, hotSpot);
        Rectangle cursorViewport = new Rectangle(0, 0, cursorSize.width, cursorSize.height);
        PadRable8Bit cursorImage = new PadRable8Bit(f, (Rectangle2D)cursorViewport, PadMode.ZERO_PAD);
        return cursorImage;
    }

    protected Image renderedImageToImage(RenderedImage ri) {
        int x = ri.getMinX();
        int y = ri.getMinY();
        SampleModel sm = ri.getSampleModel();
        ColorModel cm = ri.getColorModel();
        WritableRaster wr = Raster.createWritableRaster(sm, new Point(x, y));
        ri.copyData(wr);
        return new BufferedImage(cm, wr, cm.isAlphaPremultiplied(), null);
    }

    static {
        Cursor helpCursor;
        DEFAULT_CURSOR = Cursor.getPredefinedCursor(0);
        ANCHOR_CURSOR = Cursor.getPredefinedCursor(12);
        TEXT_CURSOR = Cursor.getPredefinedCursor(2);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        cursorMap = new HashMap();
        cursorMap.put("crosshair", Cursor.getPredefinedCursor(1));
        cursorMap.put("default", Cursor.getPredefinedCursor(0));
        cursorMap.put("pointer", Cursor.getPredefinedCursor(12));
        cursorMap.put("e-resize", Cursor.getPredefinedCursor(11));
        cursorMap.put("ne-resize", Cursor.getPredefinedCursor(7));
        cursorMap.put("nw-resize", Cursor.getPredefinedCursor(6));
        cursorMap.put("n-resize", Cursor.getPredefinedCursor(8));
        cursorMap.put("se-resize", Cursor.getPredefinedCursor(5));
        cursorMap.put("sw-resize", Cursor.getPredefinedCursor(4));
        cursorMap.put("s-resize", Cursor.getPredefinedCursor(9));
        cursorMap.put("w-resize", Cursor.getPredefinedCursor(10));
        cursorMap.put("text", Cursor.getPredefinedCursor(2));
        cursorMap.put("wait", Cursor.getPredefinedCursor(3));
        Cursor moveCursor = Cursor.getPredefinedCursor(13);
        if (Platform.isOSX) {
            try {
                Image img = toolkit.createImage(CursorManager.class.getResource("resources/move.gif"));
                moveCursor = toolkit.createCustomCursor(img, new Point(11, 11), "move");
            }
            catch (Exception img) {
                // empty catch block
            }
        }
        cursorMap.put("move", moveCursor);
        try {
            Image img = toolkit.createImage(CursorManager.class.getResource("resources/help.gif"));
            helpCursor = toolkit.createCustomCursor(img, new Point(1, 3), "help");
        }
        catch (Exception ex) {
            helpCursor = Cursor.getPredefinedCursor(12);
        }
        cursorMap.put("help", helpCursor);
    }

    static class CursorCache
    extends SoftReferenceCache {
        public Cursor getCursor(CursorDescriptor desc) {
            return (Cursor)this.requestImpl(desc);
        }

        public void putCursor(CursorDescriptor desc, Cursor cursor) {
            this.putImpl(desc, cursor);
        }

        public void clearCursor(CursorDescriptor desc) {
            this.clearImpl(desc);
        }
    }

    static class CursorDescriptor {
        ParsedURL purl;
        float x;
        float y;
        String desc;

        public CursorDescriptor(ParsedURL purl, float x, float y) {
            if (purl == null) {
                throw new IllegalArgumentException();
            }
            this.purl = purl;
            this.x = x;
            this.y = y;
            this.desc = this.getClass().getName() + "\n\t:[" + this.purl + "]\n\t:[" + x + "]:[" + y + "]";
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof CursorDescriptor)) {
                return false;
            }
            CursorDescriptor desc = (CursorDescriptor)obj;
            boolean isEqual = this.purl.equals((Object)desc.purl) && this.x == desc.x && this.y == desc.y;
            return isEqual;
        }

        public String toString() {
            return this.desc;
        }

        public int hashCode() {
            return this.desc.hashCode();
        }
    }
}

