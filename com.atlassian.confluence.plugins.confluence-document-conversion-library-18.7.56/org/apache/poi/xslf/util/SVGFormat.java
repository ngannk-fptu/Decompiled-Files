/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.GenericDOMImplementation
 *  org.apache.batik.svggen.SVGGraphics2D
 */
package org.apache.poi.xslf.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.util.Internal;
import org.apache.poi.xslf.draw.SVGPOIGraphics2D;
import org.apache.poi.xslf.util.MFProxy;
import org.apache.poi.xslf.util.OutputFormat;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

@Internal
public class SVGFormat
implements OutputFormat {
    static final String svgNS = "http://www.w3.org/2000/svg";
    private SVGGraphics2D svgGenerator;
    private final boolean textAsShapes;

    public SVGFormat(boolean textAsShapes) {
        this.textAsShapes = textAsShapes;
    }

    @Override
    public Graphics2D addSlide(double width, double height) {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(svgNS, "svg", null);
        this.svgGenerator = new SVGPOIGraphics2D(document, this.textAsShapes);
        this.svgGenerator.setSVGCanvasSize(new Dimension((int)width, (int)height));
        this.svgGenerator.setRenderingHint((RenderingHints.Key)Drawable.CACHE_IMAGE_SOURCE, (Object)true);
        return this.svgGenerator;
    }

    @Override
    public void writeSlide(MFProxy proxy, File outFile) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter((OutputStream)new FileOutputStream(outFile.getCanonicalPath()), StandardCharsets.UTF_8);){
            this.svgGenerator.stream((Writer)writer, true);
        }
    }

    @Override
    public void close() throws IOException {
        this.svgGenerator.dispose();
    }
}

