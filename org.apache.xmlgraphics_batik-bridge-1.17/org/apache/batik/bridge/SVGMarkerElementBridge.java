/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.value.Value
 *  org.apache.batik.ext.awt.image.renderable.ClipRable
 *  org.apache.batik.ext.awt.image.renderable.ClipRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.Marker
 *  org.apache.batik.parser.UnitProcessor$Context
 */
package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.bridge.AnimatableGenericSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.MarkerBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.ext.awt.image.renderable.ClipRable;
import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.Marker;
import org.apache.batik.parser.UnitProcessor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGMarkerElementBridge
extends AnimatableGenericSVGBridge
implements MarkerBridge,
ErrorConstants {
    protected SVGMarkerElementBridge() {
    }

    @Override
    public String getLocalName() {
        return "marker";
    }

    @Override
    public Marker createMarker(BridgeContext ctx, Element markerElement, Element paintedElement) {
        AffineTransform markerTxf;
        double orient;
        GVTBuilder builder = ctx.getGVTBuilder();
        CompositeGraphicsNode markerContentNode = new CompositeGraphicsNode();
        boolean hasChildren = false;
        for (Node n = markerElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            Element child;
            GraphicsNode markerNode;
            if (n.getNodeType() != 1 || (markerNode = builder.build(ctx, child = (Element)n)) == null) continue;
            hasChildren = true;
            markerContentNode.getChildren().add(markerNode);
        }
        if (!hasChildren) {
            return null;
        }
        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, paintedElement);
        float markerWidth = 3.0f;
        String s = markerElement.getAttributeNS(null, "markerWidth");
        if (s.length() != 0) {
            markerWidth = UnitProcessor.svgHorizontalLengthToUserSpace(s, "markerWidth", uctx);
        }
        if (markerWidth == 0.0f) {
            return null;
        }
        float markerHeight = 3.0f;
        s = markerElement.getAttributeNS(null, "markerHeight");
        if (s.length() != 0) {
            markerHeight = UnitProcessor.svgVerticalLengthToUserSpace(s, "markerHeight", uctx);
        }
        if (markerHeight == 0.0f) {
            return null;
        }
        s = markerElement.getAttributeNS(null, "orient");
        if (s.length() == 0) {
            orient = 0.0;
        } else if ("auto".equals(s)) {
            orient = Double.NaN;
        } else {
            try {
                orient = SVGUtilities.convertSVGNumber(s);
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(ctx, markerElement, nfEx, "attribute.malformed", new Object[]{"orient", s});
            }
        }
        Value val = CSSUtilities.getComputedStyle(paintedElement, 52);
        float strokeWidth = val.getFloatValue();
        s = markerElement.getAttributeNS(null, "markerUnits");
        int unitsType = s.length() == 0 ? 3 : (int)SVGUtilities.parseMarkerCoordinateSystem(markerElement, "markerUnits", s, ctx);
        if (unitsType == 3) {
            markerTxf = new AffineTransform();
            markerTxf.scale(strokeWidth, strokeWidth);
        } else {
            markerTxf = new AffineTransform();
        }
        AffineTransform preserveAspectRatioTransform = ViewBox.getPreserveAspectRatioTransform(markerElement, markerWidth, markerHeight, ctx);
        if (preserveAspectRatioTransform == null) {
            return null;
        }
        markerTxf.concatenate(preserveAspectRatioTransform);
        markerContentNode.setTransform(markerTxf);
        if (CSSUtilities.convertOverflow(markerElement)) {
            float[] offsets = CSSUtilities.convertClip(markerElement);
            Rectangle2D.Float markerClip = offsets == null ? new Rectangle2D.Float(0.0f, 0.0f, strokeWidth * markerWidth, strokeWidth * markerHeight) : new Rectangle2D.Float(offsets[3], offsets[0], strokeWidth * markerWidth - offsets[1] - offsets[3], strokeWidth * markerHeight - offsets[2] - offsets[0]);
            CompositeGraphicsNode comp = new CompositeGraphicsNode();
            comp.getChildren().add(markerContentNode);
            Filter clipSrc = comp.getGraphicsNodeRable(true);
            comp.setClip((ClipRable)new ClipRable8Bit(clipSrc, (Shape)markerClip));
            markerContentNode = comp;
        }
        float refX = 0.0f;
        s = markerElement.getAttributeNS(null, "refX");
        if (s.length() != 0) {
            refX = UnitProcessor.svgHorizontalCoordinateToUserSpace(s, "refX", uctx);
        }
        float refY = 0.0f;
        s = markerElement.getAttributeNS(null, "refY");
        if (s.length() != 0) {
            refY = UnitProcessor.svgVerticalCoordinateToUserSpace(s, "refY", uctx);
        }
        float[] ref = new float[]{refX, refY};
        markerTxf.transform(ref, 0, ref, 0, 1);
        Marker marker = new Marker((GraphicsNode)markerContentNode, (Point2D)new Point2D.Float(ref[0], ref[1]), orient);
        return marker;
    }
}

