/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.bridge.UserAgentAdapter
 *  org.apache.batik.bridge.ViewBox
 *  org.apache.batik.parser.DefaultLengthHandler
 *  org.apache.batik.parser.LengthHandler
 *  org.apache.batik.parser.LengthParser
 *  org.apache.batik.parser.ParseException
 *  org.w3c.dom.svg.SVGDocument
 *  org.w3c.dom.svg.SVGSVGElement
 */
package org.apache.poi.xslf.draw;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.bridge.ViewBox;
import org.apache.batik.parser.DefaultLengthHandler;
import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.LengthParser;
import org.apache.batik.parser.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.Dimension2DDouble;
import org.apache.poi.util.Internal;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;
import org.w3c.dom.svg.SVGSVGElement;

@Internal
public class SVGUserAgent
extends UserAgentAdapter {
    private static final Logger LOG = LogManager.getLogger(SVGUserAgent.class);
    private Rectangle2D viewbox;

    public SVGUserAgent() {
        this.addStdFeatures();
    }

    public Dimension2D getViewportSize() {
        return this.viewbox != null ? new Dimension2DDouble(this.viewbox.getWidth(), this.viewbox.getHeight()) : super.getViewportSize();
    }

    public Rectangle2D getViewbox() {
        return this.viewbox != null ? this.viewbox : new Rectangle2D.Double(0.0, 0.0, 1.0, 1.0);
    }

    public void initViewbox(SVGDocument doc) {
        this.viewbox = null;
        SVGSVGElement el = doc.getRootElement();
        if (el == null) {
            return;
        }
        String viewBoxStr = el.getAttributeNS(null, "viewBox");
        if (viewBoxStr != null && !viewBoxStr.isEmpty()) {
            float[] rect = ViewBox.parseViewBoxAttribute((Element)el, (String)viewBoxStr, null);
            this.viewbox = new Rectangle2D.Float(rect[0], rect[1], rect[2], rect[3]);
            return;
        }
        float w = SVGUserAgent.parseLength(el, "width");
        float h = SVGUserAgent.parseLength(el, "height");
        if (w != 0.0f && h != 0.0f) {
            this.viewbox = new Rectangle2D.Double(0.0, 0.0, w, h);
        }
    }

    private static float parseLength(SVGSVGElement el, String attr) {
        String a = el.getAttributeNS(null, attr);
        if (a == null || a.isEmpty()) {
            return 0.0f;
        }
        final float[] val = new float[]{0.0f};
        LengthParser lp = new LengthParser();
        DefaultLengthHandler lh = new DefaultLengthHandler(){

            public void lengthValue(float v) throws ParseException {
                val[0] = v;
            }
        };
        lp.setLengthHandler((LengthHandler)lh);
        lp.parse(a);
        return val[0];
    }

    public void displayMessage(String message) {
        LOG.atInfo().log(message);
    }

    public void displayError(String message) {
        LOG.atError().log(message);
    }

    public void displayError(Exception e) {
        LOG.atError().withThrowable(e).log(e.getMessage());
    }

    public void showAlert(String message) {
        LOG.atWarn().log(message);
    }
}

