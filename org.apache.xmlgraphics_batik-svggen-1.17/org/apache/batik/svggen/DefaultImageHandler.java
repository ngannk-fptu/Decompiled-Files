/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.constants.XMLConstants
 */
package org.apache.batik.svggen;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import org.apache.batik.constants.XMLConstants;
import org.apache.batik.svggen.ErrorConstants;
import org.apache.batik.svggen.ImageHandler;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.w3c.dom.Element;

public class DefaultImageHandler
implements ImageHandler,
ErrorConstants,
XMLConstants {
    @Override
    public void handleImage(Image image, Element imageElement, SVGGeneratorContext generatorContext) {
        imageElement.setAttributeNS(null, "width", String.valueOf(image.getWidth(null)));
        imageElement.setAttributeNS(null, "height", String.valueOf(image.getHeight(null)));
        try {
            this.handleHREF(image, imageElement, generatorContext);
        }
        catch (SVGGraphics2DIOException e) {
            try {
                generatorContext.errorHandler.handleError(e);
            }
            catch (SVGGraphics2DIOException io) {
                throw new SVGGraphics2DRuntimeException(io);
            }
        }
    }

    @Override
    public void handleImage(RenderedImage image, Element imageElement, SVGGeneratorContext generatorContext) {
        imageElement.setAttributeNS(null, "width", String.valueOf(image.getWidth()));
        imageElement.setAttributeNS(null, "height", String.valueOf(image.getHeight()));
        try {
            this.handleHREF(image, imageElement, generatorContext);
        }
        catch (SVGGraphics2DIOException e) {
            try {
                generatorContext.errorHandler.handleError(e);
            }
            catch (SVGGraphics2DIOException io) {
                throw new SVGGraphics2DRuntimeException(io);
            }
        }
    }

    @Override
    public void handleImage(RenderableImage image, Element imageElement, SVGGeneratorContext generatorContext) {
        imageElement.setAttributeNS(null, "width", String.valueOf(image.getWidth()));
        imageElement.setAttributeNS(null, "height", String.valueOf(image.getHeight()));
        try {
            this.handleHREF(image, imageElement, generatorContext);
        }
        catch (SVGGraphics2DIOException e) {
            try {
                generatorContext.errorHandler.handleError(e);
            }
            catch (SVGGraphics2DIOException io) {
                throw new SVGGraphics2DRuntimeException(io);
            }
        }
    }

    protected void handleHREF(Image image, Element imageElement, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", image.toString());
    }

    protected void handleHREF(RenderedImage image, Element imageElement, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", image.toString());
    }

    protected void handleHREF(RenderableImage image, Element imageElement, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", image.toString());
    }
}

