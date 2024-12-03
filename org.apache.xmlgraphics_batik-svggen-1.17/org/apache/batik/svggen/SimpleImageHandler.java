/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import org.apache.batik.svggen.DOMTreeManager;
import org.apache.batik.svggen.ErrorConstants;
import org.apache.batik.svggen.GenericImageHandler;
import org.apache.batik.svggen.ImageHandler;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGSyntax;
import org.w3c.dom.Element;

public class SimpleImageHandler
implements GenericImageHandler,
SVGSyntax,
ErrorConstants {
    static final String XLINK_NAMESPACE_URI = "http://www.w3.org/1999/xlink";
    protected ImageHandler imageHandler;

    public SimpleImageHandler(ImageHandler imageHandler) {
        if (imageHandler == null) {
            throw new IllegalArgumentException();
        }
        this.imageHandler = imageHandler;
    }

    @Override
    public void setDOMTreeManager(DOMTreeManager domTreeManager) {
    }

    @Override
    public Element createElement(SVGGeneratorContext generatorContext) {
        Element imageElement = generatorContext.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "image");
        return imageElement;
    }

    @Override
    public AffineTransform handleImage(Image image, Element imageElement, int x, int y, int width, int height, SVGGeneratorContext generatorContext) {
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        if (imageWidth == 0 || imageHeight == 0 || width == 0 || height == 0) {
            this.handleEmptyImage(imageElement);
        } else {
            this.imageHandler.handleImage(image, imageElement, generatorContext);
            this.setImageAttributes(imageElement, x, y, width, height, generatorContext);
        }
        return null;
    }

    @Override
    public AffineTransform handleImage(RenderedImage image, Element imageElement, int x, int y, int width, int height, SVGGeneratorContext generatorContext) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        if (imageWidth == 0 || imageHeight == 0 || width == 0 || height == 0) {
            this.handleEmptyImage(imageElement);
        } else {
            this.imageHandler.handleImage(image, imageElement, generatorContext);
            this.setImageAttributes(imageElement, x, y, width, height, generatorContext);
        }
        return null;
    }

    @Override
    public AffineTransform handleImage(RenderableImage image, Element imageElement, double x, double y, double width, double height, SVGGeneratorContext generatorContext) {
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        if (imageWidth == 0.0 || imageHeight == 0.0 || width == 0.0 || height == 0.0) {
            this.handleEmptyImage(imageElement);
        } else {
            this.imageHandler.handleImage(image, imageElement, generatorContext);
            this.setImageAttributes(imageElement, x, y, width, height, generatorContext);
        }
        return null;
    }

    protected void setImageAttributes(Element imageElement, double x, double y, double width, double height, SVGGeneratorContext generatorContext) {
        imageElement.setAttributeNS(null, "x", generatorContext.doubleString(x));
        imageElement.setAttributeNS(null, "y", generatorContext.doubleString(y));
        imageElement.setAttributeNS(null, "width", generatorContext.doubleString(width));
        imageElement.setAttributeNS(null, "height", generatorContext.doubleString(height));
        imageElement.setAttributeNS(null, "preserveAspectRatio", "none");
    }

    protected void handleEmptyImage(Element imageElement) {
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI, "xlink:href", "");
        imageElement.setAttributeNS(null, "width", "0");
        imageElement.setAttributeNS(null, "height", "0");
    }
}

