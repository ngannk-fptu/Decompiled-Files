/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.dom.util.XMLSupport
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.parser.AWTTransformProducer
 *  org.apache.batik.parser.ClockHandler
 *  org.apache.batik.parser.ClockParser
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.parser.UnitProcessor$Context
 *  org.apache.batik.util.ParsedURL
 *  org.apache.batik.util.SVGConstants
 *  org.w3c.dom.svg.SVGDocument
 *  org.w3c.dom.svg.SVGElement
 *  org.w3c.dom.svg.SVGLangSpace
 *  org.w3c.dom.svg.SVGNumberList
 */
package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.LinkedList;
import java.util.StringTokenizer;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.URIResolver;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.parser.ClockHandler;
import org.apache.batik.parser.ClockParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.UnitProcessor;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLangSpace;
import org.w3c.dom.svg.SVGNumberList;

public abstract class SVGUtilities
implements SVGConstants,
ErrorConstants {
    public static final short USER_SPACE_ON_USE = 1;
    public static final short OBJECT_BOUNDING_BOX = 2;
    public static final short STROKE_WIDTH = 3;

    protected SVGUtilities() {
    }

    public static Element getParentElement(Element elt) {
        Node n = CSSEngine.getCSSParentNode((Node)elt);
        while (n != null && n.getNodeType() != 1) {
            n = CSSEngine.getCSSParentNode((Node)n);
        }
        return (Element)n;
    }

    public static float[] convertSVGNumberList(SVGNumberList l) {
        int n = l.getNumberOfItems();
        if (n == 0) {
            return null;
        }
        float[] fl = new float[n];
        for (int i = 0; i < n; ++i) {
            fl[i] = l.getItem(i).getValue();
        }
        return fl;
    }

    public static float convertSVGNumber(String s) {
        return Float.parseFloat(s);
    }

    public static int convertSVGInteger(String s) {
        return Integer.parseInt(s);
    }

    public static float convertRatio(String v) {
        float r;
        float d = 1.0f;
        if (v.endsWith("%")) {
            v = v.substring(0, v.length() - 1);
            d = 100.0f;
        }
        if ((r = Float.parseFloat(v) / d) < 0.0f) {
            r = 0.0f;
        } else if (r > 1.0f) {
            r = 1.0f;
        }
        return r;
    }

    public static String getDescription(SVGElement elt) {
        String result = "";
        boolean preserve = false;
        Node n = elt.getFirstChild();
        if (n != null && n.getNodeType() == 1) {
            String name;
            String string = name = n.getPrefix() == null ? n.getNodeName() : n.getLocalName();
            if (name.equals("desc")) {
                preserve = ((SVGLangSpace)n).getXMLspace().equals("preserve");
                for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
                    if (n.getNodeType() != 3) continue;
                    result = result + n.getNodeValue();
                }
            }
        }
        return preserve ? XMLSupport.preserveXMLSpace((String)result) : XMLSupport.defaultXMLSpace((String)result);
    }

    public static boolean matchUserAgent(Element elt, UserAgent ua) {
        String s;
        StringTokenizer st;
        block9: {
            if (elt.hasAttributeNS(null, "systemLanguage")) {
                String sl = elt.getAttributeNS(null, "systemLanguage");
                if (sl.length() == 0) {
                    return false;
                }
                st = new StringTokenizer(sl, ", ");
                while (st.hasMoreTokens()) {
                    s = st.nextToken();
                    if (!SVGUtilities.matchUserLanguage(s, ua.getLanguages())) continue;
                    break block9;
                }
                return false;
            }
        }
        if (elt.hasAttributeNS(null, "requiredFeatures")) {
            String rf = elt.getAttributeNS(null, "requiredFeatures");
            if (rf.length() == 0) {
                return false;
            }
            st = new StringTokenizer(rf, " ");
            while (st.hasMoreTokens()) {
                s = st.nextToken();
                if (ua.hasFeature(s)) continue;
                return false;
            }
        }
        if (elt.hasAttributeNS(null, "requiredExtensions")) {
            String re = elt.getAttributeNS(null, "requiredExtensions");
            if (re.length() == 0) {
                return false;
            }
            st = new StringTokenizer(re, " ");
            while (st.hasMoreTokens()) {
                s = st.nextToken();
                if (ua.supportExtension(s)) continue;
                return false;
            }
        }
        return true;
    }

    protected static boolean matchUserLanguage(String s, String userLanguages) {
        StringTokenizer st = new StringTokenizer(userLanguages, ", ");
        while (st.hasMoreTokens()) {
            String t = st.nextToken();
            if (!s.startsWith(t)) continue;
            if (s.length() > t.length()) {
                return s.charAt(t.length()) == '-';
            }
            return true;
        }
        return false;
    }

    public static String getChainableAttributeNS(Element element, String namespaceURI, String attrName, BridgeContext ctx) {
        DocumentLoader loader = ctx.getDocumentLoader();
        Element e = element;
        LinkedList<ParsedURL> refs = new LinkedList<ParsedURL>();
        String v;
        while ((v = e.getAttributeNS(namespaceURI, attrName)).length() <= 0) {
            String uriStr = XLinkSupport.getXLinkHref((Element)e);
            if (uriStr.length() == 0) {
                return "";
            }
            String baseURI = e.getBaseURI();
            ParsedURL purl = new ParsedURL(baseURI, uriStr);
            for (Object e2 : refs) {
                if (!purl.equals(e2)) continue;
                throw new BridgeException(ctx, e, "xlink.href.circularDependencies", new Object[]{uriStr});
            }
            try {
                SVGDocument svgDoc = (SVGDocument)e.getOwnerDocument();
                URIResolver uRIResolver = ctx.createURIResolver(svgDoc, loader);
                e = uRIResolver.getElement(purl.toString(), e);
                refs.add(purl);
            }
            catch (IOException ioEx) {
                throw new BridgeException(ctx, e, ioEx, "uri.io", new Object[]{uriStr});
            }
            catch (SecurityException secEx) {
                throw new BridgeException(ctx, e, secEx, "uri.unsecure", new Object[]{uriStr});
            }
        }
        return v;
    }

    public static Point2D convertPoint(String xStr, String xAttr, String yStr, String yAttr, short unitsType, UnitProcessor.Context uctx) {
        float y;
        float x;
        switch (unitsType) {
            case 2: {
                x = UnitProcessor.svgHorizontalCoordinateToObjectBoundingBox(xStr, xAttr, uctx);
                y = UnitProcessor.svgVerticalCoordinateToObjectBoundingBox(yStr, yAttr, uctx);
                break;
            }
            case 1: {
                x = UnitProcessor.svgHorizontalCoordinateToUserSpace(xStr, xAttr, uctx);
                y = UnitProcessor.svgVerticalCoordinateToUserSpace(yStr, yAttr, uctx);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid unit type");
            }
        }
        return new Point2D.Float(x, y);
    }

    public static float convertLength(String length, String attr, short unitsType, UnitProcessor.Context uctx) {
        switch (unitsType) {
            case 2: {
                return UnitProcessor.svgOtherLengthToObjectBoundingBox(length, attr, uctx);
            }
            case 1: {
                return UnitProcessor.svgOtherLengthToUserSpace(length, attr, uctx);
            }
        }
        throw new IllegalArgumentException("Invalid unit type");
    }

    public static Rectangle2D convertMaskRegion(Element maskElement, Element maskedElement, GraphicsNode maskedNode, BridgeContext ctx) {
        String units;
        String hStr;
        String wStr;
        String yStr;
        String xStr = maskElement.getAttributeNS(null, "x");
        if (xStr.length() == 0) {
            xStr = "-10%";
        }
        if ((yStr = maskElement.getAttributeNS(null, "y")).length() == 0) {
            yStr = "-10%";
        }
        if ((wStr = maskElement.getAttributeNS(null, "width")).length() == 0) {
            wStr = "120%";
        }
        if ((hStr = maskElement.getAttributeNS(null, "height")).length() == 0) {
            hStr = "120%";
        }
        short unitsType = (units = maskElement.getAttributeNS(null, "maskUnits")).length() == 0 ? (short)2 : (short)SVGUtilities.parseCoordinateSystem(maskElement, "maskUnits", units, ctx);
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, maskedElement);
        return SVGUtilities.convertRegion(xStr, yStr, wStr, hStr, unitsType, maskedNode, uctx);
    }

    public static Rectangle2D convertPatternRegion(Element patternElement, Element paintedElement, GraphicsNode paintedNode, BridgeContext ctx) {
        String wStr;
        String yStr;
        String xStr = SVGUtilities.getChainableAttributeNS(patternElement, null, "x", ctx);
        if (xStr.length() == 0) {
            xStr = "0";
        }
        if ((yStr = SVGUtilities.getChainableAttributeNS(patternElement, null, "y", ctx)).length() == 0) {
            yStr = "0";
        }
        if ((wStr = SVGUtilities.getChainableAttributeNS(patternElement, null, "width", ctx)).length() == 0) {
            throw new BridgeException(ctx, patternElement, "attribute.missing", new Object[]{"width"});
        }
        String hStr = SVGUtilities.getChainableAttributeNS(patternElement, null, "height", ctx);
        if (hStr.length() == 0) {
            throw new BridgeException(ctx, patternElement, "attribute.missing", new Object[]{"height"});
        }
        String units = SVGUtilities.getChainableAttributeNS(patternElement, null, "patternUnits", ctx);
        short unitsType = units.length() == 0 ? (short)2 : (short)SVGUtilities.parseCoordinateSystem(patternElement, "patternUnits", units, ctx);
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, paintedElement);
        return SVGUtilities.convertRegion(xStr, yStr, wStr, hStr, unitsType, paintedNode, uctx);
    }

    public static float[] convertFilterRes(Element filterElement, BridgeContext ctx) {
        float[] filterRes = new float[2];
        String s = SVGUtilities.getChainableAttributeNS(filterElement, null, "filterRes", ctx);
        Float[] vals = SVGUtilities.convertSVGNumberOptionalNumber(filterElement, "filterRes", s, ctx);
        if (filterRes[0] < 0.0f || filterRes[1] < 0.0f) {
            throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"filterRes", s});
        }
        if (vals[0] == null) {
            filterRes[0] = -1.0f;
        } else {
            filterRes[0] = vals[0].floatValue();
            if (filterRes[0] < 0.0f) {
                throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"filterRes", s});
            }
        }
        if (vals[1] == null) {
            filterRes[1] = filterRes[0];
        } else {
            filterRes[1] = vals[1].floatValue();
            if (filterRes[1] < 0.0f) {
                throw new BridgeException(ctx, filterElement, "attribute.malformed", new Object[]{"filterRes", s});
            }
        }
        return filterRes;
    }

    public static Float[] convertSVGNumberOptionalNumber(Element elem, String attrName, String attrValue, BridgeContext ctx) {
        Float[] ret = new Float[2];
        if (attrValue.length() == 0) {
            return ret;
        }
        try {
            StringTokenizer tokens = new StringTokenizer(attrValue, " ");
            ret[0] = Float.valueOf(Float.parseFloat(tokens.nextToken()));
            if (tokens.hasMoreTokens()) {
                ret[1] = Float.valueOf(Float.parseFloat(tokens.nextToken()));
            }
            if (tokens.hasMoreTokens()) {
                throw new BridgeException(ctx, elem, "attribute.malformed", new Object[]{attrName, attrValue});
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, elem, nfEx, "attribute.malformed", new Object[]{attrName, attrValue, nfEx});
        }
        return ret;
    }

    public static Rectangle2D convertFilterChainRegion(Element filterElement, Element filteredElement, GraphicsNode filteredNode, BridgeContext ctx) {
        String dhStr;
        String dwStr;
        String dyStr;
        String units;
        String hStr;
        String wStr;
        String yStr;
        String xStr = SVGUtilities.getChainableAttributeNS(filterElement, null, "x", ctx);
        if (xStr.length() == 0) {
            xStr = "-10%";
        }
        if ((yStr = SVGUtilities.getChainableAttributeNS(filterElement, null, "y", ctx)).length() == 0) {
            yStr = "-10%";
        }
        if ((wStr = SVGUtilities.getChainableAttributeNS(filterElement, null, "width", ctx)).length() == 0) {
            wStr = "120%";
        }
        if ((hStr = SVGUtilities.getChainableAttributeNS(filterElement, null, "height", ctx)).length() == 0) {
            hStr = "120%";
        }
        short unitsType = (units = SVGUtilities.getChainableAttributeNS(filterElement, null, "filterUnits", ctx)).length() == 0 ? (short)2 : (short)SVGUtilities.parseCoordinateSystem(filterElement, "filterUnits", units, ctx);
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, filteredElement);
        Rectangle2D region = SVGUtilities.convertRegion(xStr, yStr, wStr, hStr, unitsType, filteredNode, uctx);
        units = SVGUtilities.getChainableAttributeNS(filterElement, null, "filterMarginsUnits", ctx);
        unitsType = units.length() == 0 ? (short)1 : SVGUtilities.parseCoordinateSystem(filterElement, "filterMarginsUnits", units, ctx);
        String dxStr = filterElement.getAttributeNS(null, "mx");
        if (dxStr.length() == 0) {
            dxStr = "0";
        }
        if ((dyStr = filterElement.getAttributeNS(null, "my")).length() == 0) {
            dyStr = "0";
        }
        if ((dwStr = filterElement.getAttributeNS(null, "mw")).length() == 0) {
            dwStr = "0";
        }
        if ((dhStr = filterElement.getAttributeNS(null, "mh")).length() == 0) {
            dhStr = "0";
        }
        return SVGUtilities.extendRegion(dxStr, dyStr, dwStr, dhStr, unitsType, filteredNode, region, uctx);
    }

    protected static Rectangle2D extendRegion(String dxStr, String dyStr, String dwStr, String dhStr, short unitsType, GraphicsNode filteredNode, Rectangle2D region, UnitProcessor.Context uctx) {
        float dh;
        float dw;
        float dy;
        float dx;
        switch (unitsType) {
            case 1: {
                dx = UnitProcessor.svgHorizontalCoordinateToUserSpace(dxStr, "mx", uctx);
                dy = UnitProcessor.svgVerticalCoordinateToUserSpace(dyStr, "my", uctx);
                dw = UnitProcessor.svgHorizontalCoordinateToUserSpace(dwStr, "mw", uctx);
                dh = UnitProcessor.svgVerticalCoordinateToUserSpace(dhStr, "mh", uctx);
                break;
            }
            case 2: {
                Rectangle2D bounds = filteredNode.getGeometryBounds();
                if (bounds == null) {
                    dh = 0.0f;
                    dw = 0.0f;
                    dy = 0.0f;
                    dx = 0.0f;
                    break;
                }
                dx = UnitProcessor.svgHorizontalCoordinateToObjectBoundingBox(dxStr, "mx", uctx);
                dx = (float)((double)dx * bounds.getWidth());
                dy = UnitProcessor.svgVerticalCoordinateToObjectBoundingBox(dyStr, "my", uctx);
                dy = (float)((double)dy * bounds.getHeight());
                dw = UnitProcessor.svgHorizontalCoordinateToObjectBoundingBox(dwStr, "mw", uctx);
                dw = (float)((double)dw * bounds.getWidth());
                dh = UnitProcessor.svgVerticalCoordinateToObjectBoundingBox(dhStr, "mh", uctx);
                dh = (float)((double)dh * bounds.getHeight());
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid unit type");
            }
        }
        region.setRect(region.getX() + (double)dx, region.getY() + (double)dy, region.getWidth() + (double)dw, region.getHeight() + (double)dh);
        return region;
    }

    public static Rectangle2D getBaseFilterPrimitiveRegion(Element filterPrimitiveElement, Element filteredElement, GraphicsNode filteredNode, Rectangle2D defaultRegion, BridgeContext ctx) {
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, filteredElement);
        double x = defaultRegion.getX();
        String s = filterPrimitiveElement.getAttributeNS(null, "x");
        if (s.length() != 0) {
            x = UnitProcessor.svgHorizontalCoordinateToUserSpace(s, "x", uctx);
        }
        double y = defaultRegion.getY();
        s = filterPrimitiveElement.getAttributeNS(null, "y");
        if (s.length() != 0) {
            y = UnitProcessor.svgVerticalCoordinateToUserSpace(s, "y", uctx);
        }
        double w = defaultRegion.getWidth();
        s = filterPrimitiveElement.getAttributeNS(null, "width");
        if (s.length() != 0) {
            w = UnitProcessor.svgHorizontalLengthToUserSpace(s, "width", uctx);
        }
        double h = defaultRegion.getHeight();
        s = filterPrimitiveElement.getAttributeNS(null, "height");
        if (s.length() != 0) {
            h = UnitProcessor.svgVerticalLengthToUserSpace(s, "height", uctx);
        }
        return new Rectangle2D.Double(x, y, w, h);
    }

    public static Rectangle2D convertFilterPrimitiveRegion(Element filterPrimitiveElement, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Rectangle2D defaultRegion, Rectangle2D filterRegion, BridgeContext ctx) {
        String units = "";
        if (filterElement != null) {
            units = SVGUtilities.getChainableAttributeNS(filterElement, null, "primitiveUnits", ctx);
        }
        short unitsType = units.length() == 0 ? (short)1 : SVGUtilities.parseCoordinateSystem(filterElement, "filterUnits", units, ctx);
        String xStr = "";
        String yStr = "";
        String wStr = "";
        String hStr = "";
        if (filterPrimitiveElement != null) {
            xStr = filterPrimitiveElement.getAttributeNS(null, "x");
            yStr = filterPrimitiveElement.getAttributeNS(null, "y");
            wStr = filterPrimitiveElement.getAttributeNS(null, "width");
            hStr = filterPrimitiveElement.getAttributeNS(null, "height");
        }
        double x = defaultRegion.getX();
        double y = defaultRegion.getY();
        double w = defaultRegion.getWidth();
        double h = defaultRegion.getHeight();
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, filteredElement);
        switch (unitsType) {
            case 2: {
                Rectangle2D bounds = filteredNode.getGeometryBounds();
                if (bounds == null) break;
                if (xStr.length() != 0) {
                    x = UnitProcessor.svgHorizontalCoordinateToObjectBoundingBox(xStr, "x", uctx);
                    x = bounds.getX() + x * bounds.getWidth();
                }
                if (yStr.length() != 0) {
                    y = UnitProcessor.svgVerticalCoordinateToObjectBoundingBox(yStr, "y", uctx);
                    y = bounds.getY() + y * bounds.getHeight();
                }
                if (wStr.length() != 0) {
                    w = UnitProcessor.svgHorizontalLengthToObjectBoundingBox(wStr, "width", uctx);
                    w *= bounds.getWidth();
                }
                if (hStr.length() == 0) break;
                h = UnitProcessor.svgVerticalLengthToObjectBoundingBox(hStr, "height", uctx);
                h *= bounds.getHeight();
                break;
            }
            case 1: {
                if (xStr.length() != 0) {
                    x = UnitProcessor.svgHorizontalCoordinateToUserSpace(xStr, "x", uctx);
                }
                if (yStr.length() != 0) {
                    y = UnitProcessor.svgVerticalCoordinateToUserSpace(yStr, "y", uctx);
                }
                if (wStr.length() != 0) {
                    w = UnitProcessor.svgHorizontalLengthToUserSpace(wStr, "width", uctx);
                }
                if (hStr.length() == 0) break;
                h = UnitProcessor.svgVerticalLengthToUserSpace(hStr, "height", uctx);
                break;
            }
            default: {
                throw new RuntimeException("invalid unitsType:" + unitsType);
            }
        }
        Rectangle2D region = new Rectangle2D.Double(x, y, w, h);
        units = "";
        if (filterElement != null) {
            units = SVGUtilities.getChainableAttributeNS(filterElement, null, "filterPrimitiveMarginsUnits", ctx);
        }
        unitsType = units.length() == 0 ? (short)1 : SVGUtilities.parseCoordinateSystem(filterElement, "filterPrimitiveMarginsUnits", units, ctx);
        String dxStr = "";
        String dyStr = "";
        String dwStr = "";
        String dhStr = "";
        if (filterPrimitiveElement != null) {
            dxStr = filterPrimitiveElement.getAttributeNS(null, "mx");
            dyStr = filterPrimitiveElement.getAttributeNS(null, "my");
            dwStr = filterPrimitiveElement.getAttributeNS(null, "mw");
            dhStr = filterPrimitiveElement.getAttributeNS(null, "mh");
        }
        if (dxStr.length() == 0) {
            dxStr = "0";
        }
        if (dyStr.length() == 0) {
            dyStr = "0";
        }
        if (dwStr.length() == 0) {
            dwStr = "0";
        }
        if (dhStr.length() == 0) {
            dhStr = "0";
        }
        region = SVGUtilities.extendRegion(dxStr, dyStr, dwStr, dhStr, unitsType, filteredNode, region, uctx);
        Rectangle2D.intersect(region, filterRegion, region);
        return region;
    }

    public static Rectangle2D convertFilterPrimitiveRegion(Element filterPrimitiveElement, Element filteredElement, GraphicsNode filteredNode, Rectangle2D defaultRegion, Rectangle2D filterRegion, BridgeContext ctx) {
        Node parentNode = filterPrimitiveElement.getParentNode();
        Element filterElement = null;
        if (parentNode != null && parentNode.getNodeType() == 1) {
            filterElement = (Element)parentNode;
        }
        return SVGUtilities.convertFilterPrimitiveRegion(filterPrimitiveElement, filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
    }

    public static short parseCoordinateSystem(Element e, String attr, String coordinateSystem, BridgeContext ctx) {
        if ("userSpaceOnUse".equals(coordinateSystem)) {
            return 1;
        }
        if ("objectBoundingBox".equals(coordinateSystem)) {
            return 2;
        }
        throw new BridgeException(ctx, e, "attribute.malformed", new Object[]{attr, coordinateSystem});
    }

    public static short parseMarkerCoordinateSystem(Element e, String attr, String coordinateSystem, BridgeContext ctx) {
        if ("userSpaceOnUse".equals(coordinateSystem)) {
            return 1;
        }
        if ("strokeWidth".equals(coordinateSystem)) {
            return 3;
        }
        throw new BridgeException(ctx, e, "attribute.malformed", new Object[]{attr, coordinateSystem});
    }

    protected static Rectangle2D convertRegion(String xStr, String yStr, String wStr, String hStr, short unitsType, GraphicsNode targetNode, UnitProcessor.Context uctx) {
        double h;
        double w;
        double y;
        double x;
        switch (unitsType) {
            case 2: {
                x = UnitProcessor.svgHorizontalCoordinateToObjectBoundingBox(xStr, "x", uctx);
                y = UnitProcessor.svgVerticalCoordinateToObjectBoundingBox(yStr, "y", uctx);
                w = UnitProcessor.svgHorizontalLengthToObjectBoundingBox(wStr, "width", uctx);
                h = UnitProcessor.svgVerticalLengthToObjectBoundingBox(hStr, "height", uctx);
                Rectangle2D bounds = targetNode.getGeometryBounds();
                if (bounds != null) {
                    x = bounds.getX() + x * bounds.getWidth();
                    y = bounds.getY() + y * bounds.getHeight();
                    w *= bounds.getWidth();
                    h *= bounds.getHeight();
                    break;
                }
                h = 0.0;
                w = 0.0;
                y = 0.0;
                x = 0.0;
                break;
            }
            case 1: {
                x = UnitProcessor.svgHorizontalCoordinateToUserSpace(xStr, "x", uctx);
                y = UnitProcessor.svgVerticalCoordinateToUserSpace(yStr, "y", uctx);
                w = UnitProcessor.svgHorizontalLengthToUserSpace(wStr, "width", uctx);
                h = UnitProcessor.svgVerticalLengthToUserSpace(hStr, "height", uctx);
                break;
            }
            default: {
                throw new RuntimeException("invalid unitsType:" + unitsType);
            }
        }
        return new Rectangle2D.Double(x, y, w, h);
    }

    public static AffineTransform convertTransform(Element e, String attr, String transform, BridgeContext ctx) {
        try {
            return AWTTransformProducer.createAffineTransform((String)transform);
        }
        catch (ParseException pEx) {
            throw new BridgeException(ctx, e, (Exception)((Object)pEx), "attribute.malformed", new Object[]{attr, transform, pEx});
        }
    }

    public static AffineTransform toObjectBBox(AffineTransform Tx, GraphicsNode node) {
        AffineTransform Mx = new AffineTransform();
        Rectangle2D bounds = node.getGeometryBounds();
        if (bounds != null) {
            Mx.translate(bounds.getX(), bounds.getY());
            Mx.scale(bounds.getWidth(), bounds.getHeight());
        }
        Mx.concatenate(Tx);
        return Mx;
    }

    public static Rectangle2D toObjectBBox(Rectangle2D r, GraphicsNode node) {
        Rectangle2D bounds = node.getGeometryBounds();
        if (bounds != null) {
            return new Rectangle2D.Double(bounds.getX() + r.getX() * bounds.getWidth(), bounds.getY() + r.getY() * bounds.getHeight(), r.getWidth() * bounds.getWidth(), r.getHeight() * bounds.getHeight());
        }
        return new Rectangle2D.Double();
    }

    public static float convertSnapshotTime(Element e, BridgeContext ctx) {
        if (!e.hasAttributeNS(null, "snapshotTime")) {
            return 0.0f;
        }
        String t = e.getAttributeNS(null, "snapshotTime");
        if (t.equals("none")) {
            return 0.0f;
        }
        ClockParser p = new ClockParser(false);
        class Handler
        implements ClockHandler {
            float time;

            Handler() {
            }

            public void clockValue(float t) {
                this.time = t;
            }
        }
        Handler h = new Handler();
        p.setClockHandler((ClockHandler)h);
        try {
            p.parse(t);
        }
        catch (ParseException pEx) {
            throw new BridgeException(null, e, (Exception)((Object)pEx), "attribute.malformed", new Object[]{"snapshotTime", t, pEx});
        }
        return h.time;
    }
}

