/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.ext.awt.image.ConcreteComponentTransferFunction
 *  org.apache.batik.ext.awt.image.renderable.ComponentTransferRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.gvt.AbstractGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.PatternPaint
 *  org.apache.batik.gvt.RootGraphicsNode
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.bridge;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.bridge.AnimatableGenericSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.PaintBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.ext.awt.image.ConcreteComponentTransferFunction;
import org.apache.batik.ext.awt.image.renderable.ComponentTransferRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.AbstractGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.PatternPaint;
import org.apache.batik.gvt.RootGraphicsNode;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGPatternElementBridge
extends AnimatableGenericSVGBridge
implements PaintBridge,
ErrorConstants {
    @Override
    public String getLocalName() {
        return "pattern";
    }

    @Override
    public Paint createPaint(BridgeContext ctx, Element patternElement, Element paintedElement, GraphicsNode paintedNode, float opacity) {
        RootGraphicsNode patternContentNode = (RootGraphicsNode)ctx.getElementData(patternElement);
        if (patternContentNode == null) {
            patternContentNode = SVGPatternElementBridge.extractPatternContent(patternElement, ctx);
            ctx.setElementData(patternElement, patternContentNode);
        }
        if (patternContentNode == null) {
            return null;
        }
        Rectangle2D patternRegion = SVGUtilities.convertPatternRegion(patternElement, paintedElement, paintedNode, ctx);
        String s = SVGUtilities.getChainableAttributeNS(patternElement, null, "patternTransform", ctx);
        AffineTransform patternTransform = s.length() != 0 ? SVGUtilities.convertTransform(patternElement, "patternTransform", s, ctx) : new AffineTransform();
        boolean overflowIsHidden = CSSUtilities.convertOverflow(patternElement);
        s = SVGUtilities.getChainableAttributeNS(patternElement, null, "patternContentUnits", ctx);
        short contentCoordSystem = s.length() == 0 ? (short)1 : SVGUtilities.parseCoordinateSystem(patternElement, "patternContentUnits", s, ctx);
        AffineTransform patternContentTransform = new AffineTransform();
        patternContentTransform.translate(patternRegion.getX(), patternRegion.getY());
        String viewBoxStr = SVGUtilities.getChainableAttributeNS(patternElement, null, "viewBox", ctx);
        if (viewBoxStr.length() > 0) {
            String aspectRatioStr = SVGUtilities.getChainableAttributeNS(patternElement, null, "preserveAspectRatio", ctx);
            float w = (float)patternRegion.getWidth();
            float h = (float)patternRegion.getHeight();
            AffineTransform preserveAspectRatioTransform = ViewBox.getPreserveAspectRatioTransform(patternElement, viewBoxStr, aspectRatioStr, w, h, ctx);
            patternContentTransform.concatenate(preserveAspectRatioTransform);
        } else if (contentCoordSystem == 2) {
            AffineTransform patternContentUnitsTransform = new AffineTransform();
            Rectangle2D objectBoundingBox = paintedNode.getGeometryBounds();
            patternContentUnitsTransform.translate(objectBoundingBox.getX(), objectBoundingBox.getY());
            patternContentUnitsTransform.scale(objectBoundingBox.getWidth(), objectBoundingBox.getHeight());
            patternContentTransform.concatenate(patternContentUnitsTransform);
        }
        PatternGraphicsNode gn = new PatternGraphicsNode((GraphicsNode)patternContentNode);
        gn.setTransform(patternContentTransform);
        if (opacity != 1.0f) {
            Filter filter = gn.getGraphicsNodeRable(true);
            filter = new ComponentTransferRable8Bit(filter, ConcreteComponentTransferFunction.getLinearTransfer((float)opacity, (float)0.0f), ConcreteComponentTransferFunction.getIdentityTransfer(), ConcreteComponentTransferFunction.getIdentityTransfer(), ConcreteComponentTransferFunction.getIdentityTransfer());
            gn.setFilter(filter);
        }
        return new PatternPaint((GraphicsNode)gn, patternRegion, !overflowIsHidden, patternTransform);
    }

    protected static RootGraphicsNode extractPatternContent(Element patternElement, BridgeContext ctx) {
        LinkedList<ParsedURL> refs = new LinkedList<ParsedURL>();
        RootGraphicsNode content;
        while ((content = SVGPatternElementBridge.extractLocalPatternContent(patternElement, ctx)) == null) {
            String uri = XLinkSupport.getXLinkHref((Element)patternElement);
            if (uri.length() == 0) {
                return null;
            }
            SVGOMDocument doc = (SVGOMDocument)patternElement.getOwnerDocument();
            ParsedURL purl = new ParsedURL(doc.getURL(), uri);
            if (!purl.complete()) {
                throw new BridgeException(ctx, patternElement, "uri.malformed", new Object[]{uri});
            }
            if (SVGPatternElementBridge.contains(refs, purl)) {
                throw new BridgeException(ctx, patternElement, "xlink.href.circularDependencies", new Object[]{uri});
            }
            refs.add(purl);
            patternElement = ctx.getReferencedElement(patternElement, uri);
        }
        return content;
    }

    protected static RootGraphicsNode extractLocalPatternContent(Element e, BridgeContext ctx) {
        GVTBuilder builder = ctx.getGVTBuilder();
        RootGraphicsNode content = null;
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            GraphicsNode gn;
            if (n.getNodeType() != 1 || (gn = builder.build(ctx, (Element)n)) == null) continue;
            if (content == null) {
                content = new RootGraphicsNode();
            }
            content.getChildren().add(gn);
        }
        return content;
    }

    private static boolean contains(List urls, ParsedURL key) {
        for (Object url : urls) {
            if (!key.equals(url)) continue;
            return true;
        }
        return false;
    }

    public static class PatternGraphicsNode
    extends AbstractGraphicsNode {
        GraphicsNode pcn;
        Rectangle2D pBounds;
        Rectangle2D gBounds;
        Rectangle2D sBounds;
        Shape oShape;

        public PatternGraphicsNode(GraphicsNode gn) {
            this.pcn = gn;
        }

        public void primitivePaint(Graphics2D g2d) {
            this.pcn.paint(g2d);
        }

        public Rectangle2D getPrimitiveBounds() {
            if (this.pBounds != null) {
                return this.pBounds;
            }
            this.pBounds = this.pcn.getTransformedBounds(IDENTITY);
            return this.pBounds;
        }

        public Rectangle2D getGeometryBounds() {
            if (this.gBounds != null) {
                return this.gBounds;
            }
            this.gBounds = this.pcn.getTransformedGeometryBounds(IDENTITY);
            return this.gBounds;
        }

        public Rectangle2D getSensitiveBounds() {
            if (this.sBounds != null) {
                return this.sBounds;
            }
            this.sBounds = this.pcn.getTransformedSensitiveBounds(IDENTITY);
            return this.sBounds;
        }

        public Shape getOutline() {
            if (this.oShape != null) {
                return this.oShape;
            }
            this.oShape = this.pcn.getOutline();
            AffineTransform tr = this.pcn.getTransform();
            if (tr != null) {
                this.oShape = tr.createTransformedShape(this.oShape);
            }
            return this.oShape;
        }

        protected void invalidateGeometryCache() {
            this.pBounds = null;
            this.gBounds = null;
            this.sBounds = null;
            this.oShape = null;
            super.invalidateGeometryCache();
        }
    }
}

