/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import org.apache.batik.svggen.CachedImageHandler;
import org.apache.batik.svggen.DOMTreeManager;
import org.apache.batik.svggen.ErrorConstants;
import org.apache.batik.svggen.ImageCacher;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.apache.batik.svggen.SVGSyntax;
import org.w3c.dom.Element;

public abstract class DefaultCachedImageHandler
implements CachedImageHandler,
SVGSyntax,
ErrorConstants {
    static final String XLINK_NAMESPACE_URI = "http://www.w3.org/1999/xlink";
    static final AffineTransform IDENTITY = new AffineTransform();
    private static Method createGraphics = null;
    private static boolean initDone = false;
    private static final Class[] paramc = new Class[]{BufferedImage.class};
    private static Object[] paramo = null;
    protected ImageCacher imageCacher;

    @Override
    public ImageCacher getImageCacher() {
        return this.imageCacher;
    }

    void setImageCacher(ImageCacher imageCacher) {
        if (imageCacher == null) {
            throw new IllegalArgumentException();
        }
        DOMTreeManager dtm = null;
        if (this.imageCacher != null) {
            dtm = this.imageCacher.getDOMTreeManager();
        }
        this.imageCacher = imageCacher;
        if (dtm != null) {
            this.imageCacher.setDOMTreeManager(dtm);
        }
    }

    @Override
    public void setDOMTreeManager(DOMTreeManager domTreeManager) {
        this.imageCacher.setDOMTreeManager(domTreeManager);
    }

    private static Graphics2D createGraphics(BufferedImage buf) {
        if (!initDone) {
            try {
                Class<?> clazz = Class.forName("org.apache.batik.ext.awt.image.GraphicsUtil");
                createGraphics = clazz.getMethod("createGraphics", paramc);
                paramo = new Object[1];
            }
            catch (Throwable clazz) {
            }
            finally {
                initDone = true;
            }
        }
        if (createGraphics == null) {
            return buf.createGraphics();
        }
        DefaultCachedImageHandler.paramo[0] = buf;
        Graphics2D g2d = null;
        try {
            g2d = (Graphics2D)createGraphics.invoke(null, paramo);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return g2d;
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
        AffineTransform af = null;
        if (imageWidth == 0 || imageHeight == 0 || width == 0 || height == 0) {
            this.handleEmptyImage(imageElement);
        } else {
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
            af = this.handleTransform(imageElement, x, y, imageWidth, imageHeight, width, height, generatorContext);
        }
        return af;
    }

    @Override
    public AffineTransform handleImage(RenderedImage image, Element imageElement, int x, int y, int width, int height, SVGGeneratorContext generatorContext) {
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        AffineTransform af = null;
        if (imageWidth == 0 || imageHeight == 0 || width == 0 || height == 0) {
            this.handleEmptyImage(imageElement);
        } else {
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
            af = this.handleTransform(imageElement, x, y, imageWidth, imageHeight, width, height, generatorContext);
        }
        return af;
    }

    @Override
    public AffineTransform handleImage(RenderableImage image, Element imageElement, double x, double y, double width, double height, SVGGeneratorContext generatorContext) {
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        AffineTransform af = null;
        if (imageWidth == 0.0 || imageHeight == 0.0 || width == 0.0 || height == 0.0) {
            this.handleEmptyImage(imageElement);
        } else {
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
            af = this.handleTransform(imageElement, x, y, imageWidth, imageHeight, width, height, generatorContext);
        }
        return af;
    }

    protected AffineTransform handleTransform(Element imageElement, double x, double y, double srcWidth, double srcHeight, double dstWidth, double dstHeight, SVGGeneratorContext generatorContext) {
        imageElement.setAttributeNS(null, "x", generatorContext.doubleString(x));
        imageElement.setAttributeNS(null, "y", generatorContext.doubleString(y));
        imageElement.setAttributeNS(null, "width", generatorContext.doubleString(dstWidth));
        imageElement.setAttributeNS(null, "height", generatorContext.doubleString(dstHeight));
        return null;
    }

    protected void handleEmptyImage(Element imageElement) {
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI, "xlink:href", "");
        imageElement.setAttributeNS(null, "width", "0");
        imageElement.setAttributeNS(null, "height", "0");
    }

    public void handleHREF(Image image, Element imageElement, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        if (image == null) {
            throw new SVGGraphics2DRuntimeException("image should not be null");
        }
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        if (width == 0 || height == 0) {
            this.handleEmptyImage(imageElement);
        } else if (image instanceof RenderedImage) {
            this.handleHREF((RenderedImage)((Object)image), imageElement, generatorContext);
        } else {
            BufferedImage buf = this.buildBufferedImage(new Dimension(width, height));
            Graphics2D g = DefaultCachedImageHandler.createGraphics(buf);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            this.handleHREF(buf, imageElement, generatorContext);
        }
    }

    public BufferedImage buildBufferedImage(Dimension size) {
        return new BufferedImage(size.width, size.height, this.getBufferedImageType());
    }

    protected void handleHREF(RenderedImage image, Element imageElement, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        BufferedImage buf = null;
        if (image instanceof BufferedImage && ((BufferedImage)image).getType() == this.getBufferedImageType()) {
            buf = (BufferedImage)image;
        } else {
            Dimension size = new Dimension(image.getWidth(), image.getHeight());
            buf = this.buildBufferedImage(size);
            Graphics2D g = DefaultCachedImageHandler.createGraphics(buf);
            g.drawRenderedImage(image, IDENTITY);
            g.dispose();
        }
        this.cacheBufferedImage(imageElement, buf, generatorContext);
    }

    protected void handleHREF(RenderableImage image, Element imageElement, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        Dimension size = new Dimension((int)Math.ceil(image.getWidth()), (int)Math.ceil(image.getHeight()));
        BufferedImage buf = this.buildBufferedImage(size);
        Graphics2D g = DefaultCachedImageHandler.createGraphics(buf);
        g.drawRenderableImage(image, IDENTITY);
        g.dispose();
        this.handleHREF(buf, imageElement, generatorContext);
    }

    protected void cacheBufferedImage(Element imageElement, BufferedImage buf, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        ByteArrayOutputStream os;
        if (generatorContext == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        try {
            os = new ByteArrayOutputStream();
            this.encodeImage(buf, os);
            os.flush();
            os.close();
        }
        catch (IOException e) {
            throw new SVGGraphics2DIOException("unexpected exception", e);
        }
        String ref = this.imageCacher.lookup(os, buf.getWidth(), buf.getHeight(), generatorContext);
        imageElement.setAttributeNS(XLINK_NAMESPACE_URI, "xlink:href", this.getRefPrefix() + ref);
    }

    public abstract String getRefPrefix();

    public abstract void encodeImage(BufferedImage var1, OutputStream var2) throws IOException;

    public abstract int getBufferedImageType();
}

