/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.util.XLinkSupport
 *  org.apache.batik.gvt.text.TextPath
 *  org.apache.batik.parser.AWTPathProducer
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.parser.PathHandler
 *  org.apache.batik.parser.PathParser
 *  org.apache.batik.parser.UnitProcessor$Context
 */
package org.apache.batik.bridge;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import org.apache.batik.bridge.AnimatableGenericSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.text.TextPath;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;
import org.apache.batik.parser.PathParser;
import org.apache.batik.parser.UnitProcessor;
import org.w3c.dom.Element;

public class SVGTextPathElementBridge
extends AnimatableGenericSVGBridge
implements ErrorConstants {
    @Override
    public String getLocalName() {
        return "textPath";
    }

    @Override
    public void handleElement(BridgeContext ctx, Element e) {
    }

    public TextPath createTextPath(BridgeContext ctx, Element textPathElement) {
        String uri = XLinkSupport.getXLinkHref((Element)textPathElement);
        Element pathElement = ctx.getReferencedElement(textPathElement, uri);
        if (pathElement == null || !"http://www.w3.org/2000/svg".equals(pathElement.getNamespaceURI()) || !pathElement.getLocalName().equals("path")) {
            throw new BridgeException(ctx, textPathElement, "uri.badTarget", new Object[]{uri});
        }
        String s = pathElement.getAttributeNS(null, "d");
        Shape pathShape = null;
        if (s.length() != 0) {
            AWTPathProducer app = new AWTPathProducer();
            app.setWindingRule(CSSUtilities.convertFillRule(pathElement));
            try {
                PathParser pathParser = new PathParser();
                pathParser.setPathHandler((PathHandler)app);
                pathParser.parse(s);
            }
            catch (ParseException pEx) {
                throw new BridgeException(ctx, pathElement, (Exception)((Object)pEx), "attribute.malformed", new Object[]{"d"});
            }
            finally {
                pathShape = app.getShape();
            }
        } else {
            throw new BridgeException(ctx, pathElement, "attribute.missing", new Object[]{"d"});
        }
        s = pathElement.getAttributeNS(null, "transform");
        if (s.length() != 0) {
            AffineTransform tr = SVGUtilities.convertTransform(pathElement, "transform", s, ctx);
            pathShape = tr.createTransformedShape(pathShape);
        }
        TextPath textPath = new TextPath(new GeneralPath(pathShape));
        s = textPathElement.getAttributeNS(null, "startOffset");
        if (s.length() > 0) {
            float startOffset = 0.0f;
            int percentIndex = s.indexOf(37);
            if (percentIndex != -1) {
                float pathLength = textPath.lengthOfPath();
                String percentString = s.substring(0, percentIndex);
                float startOffsetPercent = 0.0f;
                try {
                    startOffsetPercent = SVGUtilities.convertSVGNumber(percentString);
                }
                catch (NumberFormatException e) {
                    throw new BridgeException(ctx, textPathElement, "attribute.malformed", new Object[]{"startOffset", s});
                }
                startOffset = (float)((double)(startOffsetPercent * pathLength) / 100.0);
            } else {
                UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, textPathElement);
                startOffset = UnitProcessor.svgOtherLengthToUserSpace(s, "startOffset", uctx);
            }
            textPath.setStartOffset(startOffset);
        }
        return textPath;
    }
}

