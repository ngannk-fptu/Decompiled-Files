/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.GenericDOMImplementation
 */
package com.atlassian.plugins.roadmap.renderer;

import com.atlassian.plugins.roadmap.models.TimelinePlanner;
import com.atlassian.plugins.roadmap.renderer.AbstractTimelinePlannerRenderer;
import com.atlassian.plugins.roadmap.renderer.ConfluenceSVGGraphics2D;
import com.atlassian.plugins.roadmap.renderer.RenderedImageInfoEnricher;
import com.atlassian.plugins.roadmap.renderer.enricher.SVGElemInfoEnricher;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class SVGRoadMapRenderer
extends AbstractTimelinePlannerRenderer {
    private ConfluenceSVGGraphics2D graphics2D;

    public String renderAsString(TimelinePlanner roadmap) throws IOException {
        this.drawImage(roadmap, Optional.empty(), Optional.empty(), false);
        ConfluenceSVGGraphics2D confluenceSVGGraphics2D = this.graphics2D;
        StringWriter writer = new StringWriter();
        confluenceSVGGraphics2D.stream(writer, false);
        this.graphics2D.dispose();
        this.graphics2D = null;
        return writer.toString();
    }

    private ConfluenceSVGGraphics2D createConfluenceSVGGraphics2D(int width, int height) {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);
        ConfluenceSVGGraphics2D ret = new ConfluenceSVGGraphics2D(document);
        ret.setSVGCanvasSize(new Dimension(width, height));
        return ret;
    }

    @Override
    protected Graphics2D createDummyGraphics2D() {
        return this.createConfluenceSVGGraphics2D(1, 1);
    }

    @Override
    protected Graphics2D createGraphics2D(int width, int height) {
        this.graphics2D = this.createConfluenceSVGGraphics2D(width, height);
        return this.graphics2D;
    }

    @Override
    protected RenderedImageInfoEnricher createEnricher() {
        return new SVGElemInfoEnricher(this.graphics2D);
    }
}

