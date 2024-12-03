/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 *  org.apache.batik.svggen.DOMGroupManager
 *  org.apache.batik.svggen.DOMTreeManager
 *  org.apache.batik.svggen.ExtensionHandler
 *  org.apache.batik.svggen.ImageHandler
 *  org.apache.batik.svggen.SVGGeneratorContext
 *  org.apache.batik.svggen.SVGGeneratorContext$GraphicContextDefaults
 *  org.apache.batik.svggen.SVGGraphics2D
 *  org.apache.batik.svggen.SVGShape
 */
package com.atlassian.plugins.roadmap.renderer;

import java.awt.geom.AffineTransform;
import java.util.Map;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.svggen.DOMGroupManager;
import org.apache.batik.svggen.DOMTreeManager;
import org.apache.batik.svggen.ExtensionHandler;
import org.apache.batik.svggen.ImageHandler;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGShape;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ConfluenceSVGGraphics2D
extends SVGGraphics2D {
    protected Element lastAddedElement;
    private Element rootElement;

    public ConfluenceSVGGraphics2D(Document document) {
        super(document);
    }

    public ConfluenceSVGGraphics2D(Document document, ImageHandler imageHandler, ExtensionHandler extensionHandler, boolean b) {
        super(document, imageHandler, extensionHandler, b);
    }

    public ConfluenceSVGGraphics2D(SVGGeneratorContext svgGeneratorContext, boolean b) {
        super(svgGeneratorContext, b);
    }

    public ConfluenceSVGGraphics2D(SVGGraphics2D svgGraphics2D) {
        super(svgGraphics2D);
        this.domTreeManager.removeGroupManager(this.domGroupManager);
        this.domGroupManager = new DOMGroupManagerEx(this.gc, this.domTreeManager);
        this.domTreeManager.addGroupManager(this.domGroupManager);
    }

    protected void setGeneratorContext(SVGGeneratorContext generatorCtx) {
        this.generatorCtx = generatorCtx;
        this.gc = new GraphicContext(new AffineTransform());
        SVGGeneratorContext.GraphicContextDefaults gcDefaults = generatorCtx.getGraphicContextDefaults();
        if (gcDefaults != null) {
            if (gcDefaults.getPaint() != null) {
                this.gc.setPaint(gcDefaults.getPaint());
            }
            if (gcDefaults.getStroke() != null) {
                this.gc.setStroke(gcDefaults.getStroke());
            }
            if (gcDefaults.getComposite() != null) {
                this.gc.setComposite(gcDefaults.getComposite());
            }
            if (gcDefaults.getClip() != null) {
                this.gc.setClip(gcDefaults.getClip());
            }
            if (gcDefaults.getRenderingHints() != null) {
                this.gc.setRenderingHints((Map)gcDefaults.getRenderingHints());
            }
            if (gcDefaults.getFont() != null) {
                this.gc.setFont(gcDefaults.getFont());
            }
            if (gcDefaults.getBackground() != null) {
                this.gc.setBackground(gcDefaults.getBackground());
            }
        }
        this.shapeConverter = new SVGShape(generatorCtx);
        this.domTreeManager = new DOMTreeManager(this.gc, generatorCtx, 3);
        this.domGroupManager = new DOMGroupManagerEx(this.gc, this.domTreeManager);
        this.domTreeManager.addGroupManager(this.domGroupManager);
        this.setDOMTreeManager(this.domTreeManager);
    }

    public Element getRoot() {
        if (this.rootElement == null) {
            this.rootElement = super.getRoot();
        }
        return this.rootElement;
    }

    public Element getLastAddedElement() {
        return this.lastAddedElement;
    }

    public class DOMGroupManagerEx
    extends DOMGroupManager {
        public DOMGroupManagerEx(GraphicContext graphicContext, DOMTreeManager domTreeManager) {
            super(graphicContext, domTreeManager);
        }

        public void addElement(Element element, short i) {
            ConfluenceSVGGraphics2D.this.lastAddedElement = element;
            super.addElement(element, i);
        }
    }
}

