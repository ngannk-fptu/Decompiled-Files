/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.font.GVTFontFace
 *  org.apache.batik.gvt.font.Glyph
 *  org.apache.batik.gvt.text.TextPaintInfo
 *  org.apache.batik.parser.AWTPathProducer
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.parser.PathHandler
 *  org.apache.batik.parser.PathParser
 */
package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.parser.PathParser;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SVGGlyphElementBridge
extends AbstractSVGBridge
implements ErrorConstants {
    protected SVGGlyphElementBridge() {
    }

    @Override
    public String getLocalName() {
        return "glyph";
    }

    public Glyph createGlyph(BridgeContext ctx, Element glyphElement, Element textElement, int glyphCode, float fontSize, GVTFontFace fontFace, TextPaintInfo tpi) {
        float horizOriginY;
        float horizOriginX;
        float vertOriginY;
        float vertOriginX;
        float vertAdvY;
        float horizAdvX;
        float fontHeight = fontFace.getUnitsPerEm();
        float scale = fontSize / fontHeight;
        AffineTransform scaleTransform = AffineTransform.getScaleInstance(scale, -scale);
        String d = glyphElement.getAttributeNS(null, "d");
        Shape dShape = null;
        if (d.length() != 0) {
            AWTPathProducer app = new AWTPathProducer();
            app.setWindingRule(CSSUtilities.convertFillRule(textElement));
            try {
                PathParser pathParser = new PathParser();
                pathParser.setPathHandler((PathHandler)app);
                pathParser.parse(d);
            }
            catch (ParseException pEx) {
                throw new BridgeException(ctx, glyphElement, (Exception)((Object)pEx), "attribute.malformed", new Object[]{"d"});
            }
            finally {
                Shape transformedShape;
                Shape shape = app.getShape();
                dShape = transformedShape = scaleTransform.createTransformedShape(shape);
            }
        }
        NodeList glyphChildren = glyphElement.getChildNodes();
        int numChildren = glyphChildren.getLength();
        int numGlyphChildren = 0;
        for (int i = 0; i < numChildren; ++i) {
            Node childNode = glyphChildren.item(i);
            if (childNode.getNodeType() != 1) continue;
            ++numGlyphChildren;
        }
        CompositeGraphicsNode glyphContentNode = null;
        if (numGlyphChildren > 0) {
            GVTBuilder builder = ctx.getGVTBuilder();
            glyphContentNode = new CompositeGraphicsNode();
            Element fontElementClone = (Element)glyphElement.getParentNode().cloneNode(false);
            NamedNodeMap fontAttributes = glyphElement.getParentNode().getAttributes();
            int numAttributes = fontAttributes.getLength();
            for (int i = 0; i < numAttributes; ++i) {
                fontElementClone.setAttributeNode((Attr)fontAttributes.item(i));
            }
            Element clonedGlyphElement = (Element)glyphElement.cloneNode(true);
            fontElementClone.appendChild(clonedGlyphElement);
            textElement.appendChild(fontElementClone);
            CompositeGraphicsNode glyphChildrenNode = new CompositeGraphicsNode();
            glyphChildrenNode.setTransform(scaleTransform);
            NodeList clonedGlyphChildren = clonedGlyphElement.getChildNodes();
            int numClonedChildren = clonedGlyphChildren.getLength();
            for (int i = 0; i < numClonedChildren; ++i) {
                Node childNode = clonedGlyphChildren.item(i);
                if (childNode.getNodeType() != 1) continue;
                Element childElement = (Element)childNode;
                GraphicsNode childGraphicsNode = builder.build(ctx, childElement);
                glyphChildrenNode.add((Object)childGraphicsNode);
            }
            glyphContentNode.add((Object)glyphChildrenNode);
            textElement.removeChild(fontElementClone);
        }
        String unicode = glyphElement.getAttributeNS(null, "unicode");
        String nameList = glyphElement.getAttributeNS(null, "glyph-name");
        ArrayList<String> names = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(nameList, " ,");
        while (st.hasMoreTokens()) {
            names.add(st.nextToken());
        }
        String orientation = glyphElement.getAttributeNS(null, "orientation");
        String arabicForm = glyphElement.getAttributeNS(null, "arabic-form");
        String lang = glyphElement.getAttributeNS(null, "lang");
        Element parentFontElement = (Element)glyphElement.getParentNode();
        String s = glyphElement.getAttributeNS(null, "horiz-adv-x");
        if (s.length() == 0 && (s = parentFontElement.getAttributeNS(null, "horiz-adv-x")).length() == 0) {
            throw new BridgeException(ctx, parentFontElement, "attribute.missing", new Object[]{"horiz-adv-x"});
        }
        try {
            horizAdvX = SVGUtilities.convertSVGNumber(s) * scale;
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, glyphElement, nfEx, "attribute.malformed", new Object[]{"horiz-adv-x", s});
        }
        s = glyphElement.getAttributeNS(null, "vert-adv-y");
        if (s.length() == 0 && (s = parentFontElement.getAttributeNS(null, "vert-adv-y")).length() == 0) {
            s = String.valueOf(fontFace.getUnitsPerEm());
        }
        try {
            vertAdvY = SVGUtilities.convertSVGNumber(s) * scale;
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, glyphElement, nfEx, "attribute.malformed", new Object[]{"vert-adv-y", s});
        }
        s = glyphElement.getAttributeNS(null, "vert-origin-x");
        if (s.length() == 0 && (s = parentFontElement.getAttributeNS(null, "vert-origin-x")).length() == 0) {
            s = Float.toString(horizAdvX / 2.0f);
        }
        try {
            vertOriginX = SVGUtilities.convertSVGNumber(s) * scale;
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, glyphElement, nfEx, "attribute.malformed", new Object[]{"vert-origin-x", s});
        }
        s = glyphElement.getAttributeNS(null, "vert-origin-y");
        if (s.length() == 0 && (s = parentFontElement.getAttributeNS(null, "vert-origin-y")).length() == 0) {
            s = String.valueOf(fontFace.getAscent());
        }
        try {
            vertOriginY = SVGUtilities.convertSVGNumber(s) * -scale;
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, glyphElement, nfEx, "attribute.malformed", new Object[]{"vert-origin-y", s});
        }
        Point2D.Float vertOrigin = new Point2D.Float(vertOriginX, vertOriginY);
        s = parentFontElement.getAttributeNS(null, "horiz-origin-x");
        if (s.length() == 0) {
            s = "0";
        }
        try {
            horizOriginX = SVGUtilities.convertSVGNumber(s) * scale;
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, parentFontElement, nfEx, "attribute.malformed", new Object[]{"horiz-origin-x", s});
        }
        s = parentFontElement.getAttributeNS(null, "horiz-origin-y");
        if (s.length() == 0) {
            s = "0";
        }
        try {
            horizOriginY = SVGUtilities.convertSVGNumber(s) * -scale;
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, glyphElement, nfEx, "attribute.malformed", new Object[]{"horiz-origin-y", s});
        }
        Point2D.Float horizOrigin = new Point2D.Float(horizOriginX, horizOriginY);
        return new Glyph(unicode, names, orientation, arabicForm, lang, (Point2D)horizOrigin, (Point2D)vertOrigin, horizAdvX, vertAdvY, glyphCode, tpi, dShape, (GraphicsNode)glyphContentNode);
    }
}

