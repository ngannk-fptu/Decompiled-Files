/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.svggen.ExtensionHandler
 *  org.apache.batik.svggen.SVGGeneratorContext
 *  org.apache.batik.svggen.SVGGeneratorContext$GraphicContextDefaults
 *  org.apache.batik.svggen.SVGGraphics2D
 */
package org.apache.poi.xslf.draw;

import java.awt.RenderingHints;
import java.util.Map;
import org.apache.batik.svggen.ExtensionHandler;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.poi.util.Internal;
import org.apache.poi.xslf.draw.SVGRenderExtension;
import org.w3c.dom.Document;

@Internal
public class SVGPOIGraphics2D
extends SVGGraphics2D {
    private final RenderingHints hints = this.getGeneratorContext().getGraphicContextDefaults().getRenderingHints();

    public SVGPOIGraphics2D(Document document, boolean textAsShapes) {
        super(SVGPOIGraphics2D.getCtx(document), textAsShapes);
        ((SVGRenderExtension)this.getGeneratorContext().getExtensionHandler()).setSvgGraphics2D(this);
    }

    private static SVGGeneratorContext getCtx(Document document) {
        SVGGeneratorContext context = SVGGeneratorContext.createDefault((Document)document);
        context.setExtensionHandler((ExtensionHandler)new SVGRenderExtension());
        SVGGeneratorContext.GraphicContextDefaults defs = new SVGGeneratorContext.GraphicContextDefaults();
        defs.setRenderingHints(new RenderingHints(null));
        context.setGraphicContextDefaults(defs);
        return context;
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        this.hints.put(hintKey, hintValue);
        super.setRenderingHint(hintKey, hintValue);
    }

    public void setRenderingHints(Map hints) {
        this.hints.clear();
        this.hints.putAll((Map<?, ?>)hints);
        super.setRenderingHints(hints);
    }

    public void addRenderingHints(Map hints) {
        this.hints.putAll((Map<?, ?>)hints);
        super.addRenderingHints(hints);
    }
}

