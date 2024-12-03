/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMAnimatedRect
 *  org.apache.batik.dom.svg.LiveAttributeException
 *  org.apache.batik.dom.util.DOMUtilities
 *  org.apache.batik.parser.AWTTransformProducer
 *  org.apache.batik.parser.FragmentIdentifierHandler
 *  org.apache.batik.parser.FragmentIdentifierParser
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.parser.PreserveAspectRatioHandler
 *  org.apache.batik.parser.PreserveAspectRatioParser
 *  org.apache.batik.util.SVGConstants
 *  org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio
 *  org.w3c.dom.svg.SVGAnimatedRect
 *  org.w3c.dom.svg.SVGPreserveAspectRatio
 *  org.w3c.dom.svg.SVGRect
 */
package org.apache.batik.bridge;

import java.awt.geom.AffineTransform;
import java.util.StringTokenizer;
import org.apache.batik.anim.dom.SVGOMAnimatedRect;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.dom.svg.LiveAttributeException;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.parser.AWTTransformProducer;
import org.apache.batik.parser.FragmentIdentifierHandler;
import org.apache.batik.parser.FragmentIdentifierParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PreserveAspectRatioHandler;
import org.apache.batik.parser.PreserveAspectRatioParser;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGPreserveAspectRatio;
import org.w3c.dom.svg.SVGRect;

public abstract class ViewBox
implements SVGConstants,
ErrorConstants {
    protected ViewBox() {
    }

    public static AffineTransform getViewTransform(String ref, Element e, float w, float h, BridgeContext ctx) {
        boolean meet;
        short align;
        float[] vb;
        if (ref == null || ref.length() == 0) {
            return ViewBox.getPreserveAspectRatioTransform(e, w, h, ctx);
        }
        ViewHandler vh = new ViewHandler();
        FragmentIdentifierParser p = new FragmentIdentifierParser();
        p.setFragmentIdentifierHandler((FragmentIdentifierHandler)vh);
        p.parse(ref);
        Element viewElement = e;
        if (vh.hasId) {
            Document document = e.getOwnerDocument();
            viewElement = document.getElementById(vh.id);
        }
        if (viewElement == null) {
            throw new BridgeException(ctx, e, "uri.malformed", new Object[]{ref});
        }
        Element ancestorSVG = ViewBox.getClosestAncestorSVGElement(e);
        if (!viewElement.getNamespaceURI().equals("http://www.w3.org/2000/svg") || !viewElement.getLocalName().equals("view")) {
            viewElement = ancestorSVG;
        }
        if (vh.hasViewBox) {
            vb = vh.viewBox;
        } else {
            Element elt = DOMUtilities.isAttributeSpecifiedNS((Element)viewElement, null, (String)"viewBox") ? viewElement : ancestorSVG;
            String viewBoxStr = elt.getAttributeNS(null, "viewBox");
            vb = ViewBox.parseViewBoxAttribute(elt, viewBoxStr, ctx);
        }
        if (vh.hasPreserveAspectRatio) {
            align = vh.align;
            meet = vh.meet;
        } else {
            Element elt = DOMUtilities.isAttributeSpecifiedNS((Element)viewElement, null, (String)"preserveAspectRatio") ? viewElement : ancestorSVG;
            String aspectRatio = elt.getAttributeNS(null, "preserveAspectRatio");
            PreserveAspectRatioParser pp = new PreserveAspectRatioParser();
            ViewHandler ph = new ViewHandler();
            pp.setPreserveAspectRatioHandler((PreserveAspectRatioHandler)ph);
            try {
                pp.parse(aspectRatio);
            }
            catch (ParseException pEx) {
                throw new BridgeException(ctx, elt, (Exception)((Object)pEx), "attribute.malformed", new Object[]{"preserveAspectRatio", aspectRatio, pEx});
            }
            align = ph.align;
            meet = ph.meet;
        }
        AffineTransform transform = ViewBox.getPreserveAspectRatioTransform(vb, align, meet, w, h);
        if (vh.hasTransform) {
            transform.concatenate(vh.getAffineTransform());
        }
        return transform;
    }

    private static Element getClosestAncestorSVGElement(Element e) {
        for (Node n = e; n != null && n.getNodeType() == 1; n = n.getParentNode()) {
            Element tmp = n;
            if (!tmp.getNamespaceURI().equals("http://www.w3.org/2000/svg") || !tmp.getLocalName().equals("svg")) continue;
            return tmp;
        }
        return null;
    }

    public static AffineTransform getPreserveAspectRatioTransform(Element e, float w, float h) {
        return ViewBox.getPreserveAspectRatioTransform(e, w, h, null);
    }

    public static AffineTransform getPreserveAspectRatioTransform(Element e, float w, float h, BridgeContext ctx) {
        String viewBox = e.getAttributeNS(null, "viewBox");
        String aspectRatio = e.getAttributeNS(null, "preserveAspectRatio");
        return ViewBox.getPreserveAspectRatioTransform(e, viewBox, aspectRatio, w, h, ctx);
    }

    public static AffineTransform getPreserveAspectRatioTransform(Element e, String viewBox, String aspectRatio, float w, float h, BridgeContext ctx) {
        if (viewBox.length() == 0) {
            return new AffineTransform();
        }
        float[] vb = ViewBox.parseViewBoxAttribute(e, viewBox, ctx);
        PreserveAspectRatioParser p = new PreserveAspectRatioParser();
        ViewHandler ph = new ViewHandler();
        p.setPreserveAspectRatioHandler((PreserveAspectRatioHandler)ph);
        try {
            p.parse(aspectRatio);
        }
        catch (ParseException pEx) {
            throw new BridgeException(ctx, e, (Exception)((Object)pEx), "attribute.malformed", new Object[]{"preserveAspectRatio", aspectRatio, pEx});
        }
        return ViewBox.getPreserveAspectRatioTransform(vb, ph.align, ph.meet, w, h);
    }

    public static AffineTransform getPreserveAspectRatioTransform(Element e, float[] vb, float w, float h, BridgeContext ctx) {
        String aspectRatio = e.getAttributeNS(null, "preserveAspectRatio");
        PreserveAspectRatioParser p = new PreserveAspectRatioParser();
        ViewHandler ph = new ViewHandler();
        p.setPreserveAspectRatioHandler((PreserveAspectRatioHandler)ph);
        try {
            p.parse(aspectRatio);
        }
        catch (ParseException pEx) {
            throw new BridgeException(ctx, e, (Exception)((Object)pEx), "attribute.malformed", new Object[]{"preserveAspectRatio", aspectRatio, pEx});
        }
        return ViewBox.getPreserveAspectRatioTransform(vb, ph.align, ph.meet, w, h);
    }

    public static AffineTransform getPreserveAspectRatioTransform(Element e, float[] vb, float w, float h, SVGAnimatedPreserveAspectRatio aPAR, BridgeContext ctx) {
        try {
            SVGPreserveAspectRatio pAR = aPAR.getAnimVal();
            short align = pAR.getAlign();
            boolean meet = pAR.getMeetOrSlice() == 1;
            return ViewBox.getPreserveAspectRatioTransform(vb, align, meet, w, h);
        }
        catch (LiveAttributeException ex) {
            throw new BridgeException(ctx, ex);
        }
    }

    public static AffineTransform getPreserveAspectRatioTransform(Element e, SVGAnimatedRect aViewBox, SVGAnimatedPreserveAspectRatio aPAR, float w, float h, BridgeContext ctx) {
        if (!((SVGOMAnimatedRect)aViewBox).isSpecified()) {
            return new AffineTransform();
        }
        SVGRect viewBox = aViewBox.getAnimVal();
        float[] vb = new float[]{viewBox.getX(), viewBox.getY(), viewBox.getWidth(), viewBox.getHeight()};
        return ViewBox.getPreserveAspectRatioTransform(e, vb, w, h, aPAR, ctx);
    }

    public static float[] parseViewBoxAttribute(Element e, String value, BridgeContext ctx) {
        int i;
        if (value.length() == 0) {
            return null;
        }
        float[] vb = new float[4];
        StringTokenizer st = new StringTokenizer(value, " ,");
        try {
            for (i = 0; i < 4 && st.hasMoreTokens(); ++i) {
                vb[i] = Float.parseFloat(st.nextToken());
            }
        }
        catch (NumberFormatException nfEx) {
            throw new BridgeException(ctx, e, nfEx, "attribute.malformed", new Object[]{"viewBox", value, nfEx});
        }
        if (i != 4) {
            throw new BridgeException(ctx, e, "attribute.malformed", new Object[]{"viewBox", value});
        }
        if (vb[2] < 0.0f || vb[3] < 0.0f) {
            throw new BridgeException(ctx, e, "attribute.malformed", new Object[]{"viewBox", value});
        }
        if (vb[2] == 0.0f || vb[3] == 0.0f) {
            return null;
        }
        return vb;
    }

    public static AffineTransform getPreserveAspectRatioTransform(float[] vb, short align, boolean meet, float w, float h) {
        if (vb == null) {
            return new AffineTransform();
        }
        AffineTransform result = new AffineTransform();
        float vpar = vb[2] / vb[3];
        float svgar = w / h;
        if (align == 1) {
            result.scale(w / vb[2], h / vb[3]);
            result.translate(-vb[0], -vb[1]);
        } else if (vpar < svgar && meet || vpar >= svgar && !meet) {
            float sf = h / vb[3];
            result.scale(sf, sf);
            switch (align) {
                case 2: 
                case 5: 
                case 8: {
                    result.translate(-vb[0], -vb[1]);
                    break;
                }
                case 3: 
                case 6: 
                case 9: {
                    result.translate(-vb[0] - (vb[2] - w * vb[3] / h) / 2.0f, -vb[1]);
                    break;
                }
                default: {
                    result.translate(-vb[0] - (vb[2] - w * vb[3] / h), -vb[1]);
                    break;
                }
            }
        } else {
            float sf = w / vb[2];
            result.scale(sf, sf);
            switch (align) {
                case 2: 
                case 3: 
                case 4: {
                    result.translate(-vb[0], -vb[1]);
                    break;
                }
                case 5: 
                case 6: 
                case 7: {
                    result.translate(-vb[0], -vb[1] - (vb[3] - h * vb[2] / w) / 2.0f);
                    break;
                }
                default: {
                    result.translate(-vb[0], -vb[1] - (vb[3] - h * vb[2] / w));
                }
            }
        }
        return result;
    }

    protected static class ViewHandler
    extends AWTTransformProducer
    implements FragmentIdentifierHandler {
        public boolean hasTransform;
        public boolean hasId;
        public boolean hasViewBox;
        public boolean hasViewTargetParams;
        public boolean hasZoomAndPanParams;
        public String id;
        public float[] viewBox;
        public String viewTargetParams;
        public boolean isMagnify;
        public boolean hasPreserveAspectRatio;
        public short align;
        public boolean meet = true;

        protected ViewHandler() {
        }

        public void endTransformList() throws ParseException {
            super.endTransformList();
            this.hasTransform = true;
        }

        public void startFragmentIdentifier() throws ParseException {
        }

        public void idReference(String s) throws ParseException {
            this.id = s;
            this.hasId = true;
        }

        public void viewBox(float x, float y, float width, float height) throws ParseException {
            this.hasViewBox = true;
            this.viewBox = new float[4];
            this.viewBox[0] = x;
            this.viewBox[1] = y;
            this.viewBox[2] = width;
            this.viewBox[3] = height;
        }

        public void startViewTarget() throws ParseException {
        }

        public void viewTarget(String name) throws ParseException {
            this.viewTargetParams = name;
            this.hasViewTargetParams = true;
        }

        public void endViewTarget() throws ParseException {
        }

        public void zoomAndPan(boolean magnify) {
            this.isMagnify = magnify;
            this.hasZoomAndPanParams = true;
        }

        public void endFragmentIdentifier() throws ParseException {
        }

        public void startPreserveAspectRatio() throws ParseException {
        }

        public void none() throws ParseException {
            this.align = 1;
        }

        public void xMaxYMax() throws ParseException {
            this.align = (short)10;
        }

        public void xMaxYMid() throws ParseException {
            this.align = (short)7;
        }

        public void xMaxYMin() throws ParseException {
            this.align = (short)4;
        }

        public void xMidYMax() throws ParseException {
            this.align = (short)9;
        }

        public void xMidYMid() throws ParseException {
            this.align = (short)6;
        }

        public void xMidYMin() throws ParseException {
            this.align = (short)3;
        }

        public void xMinYMax() throws ParseException {
            this.align = (short)8;
        }

        public void xMinYMid() throws ParseException {
            this.align = (short)5;
        }

        public void xMinYMin() throws ParseException {
            this.align = (short)2;
        }

        public void meet() throws ParseException {
            this.meet = true;
        }

        public void slice() throws ParseException {
            this.meet = false;
        }

        public void endPreserveAspectRatio() throws ParseException {
            this.hasPreserveAspectRatio = true;
        }
    }
}

