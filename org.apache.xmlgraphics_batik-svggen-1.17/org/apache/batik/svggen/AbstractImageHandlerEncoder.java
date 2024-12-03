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
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import org.apache.batik.svggen.DefaultImageHandler;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.w3c.dom.Element;

public abstract class AbstractImageHandlerEncoder
extends DefaultImageHandler {
    private static final AffineTransform IDENTITY = new AffineTransform();
    private String imageDir = "";
    private String urlRoot = "";
    private static Method createGraphics = null;
    private static boolean initDone = false;
    private static final Class[] paramc = new Class[]{BufferedImage.class};
    private static Object[] paramo = null;

    private static Graphics2D createGraphics(BufferedImage buf) {
        if (!initDone) {
            try {
                Class<?> clazz = Class.forName("org.apache.batik.ext.awt.image.GraphicsUtil");
                createGraphics = clazz.getMethod("createGraphics", paramc);
                paramo = new Object[1];
            }
            catch (ThreadDeath td) {
                throw td;
            }
            catch (Throwable td) {
            }
            finally {
                initDone = true;
            }
        }
        if (createGraphics == null) {
            return buf.createGraphics();
        }
        AbstractImageHandlerEncoder.paramo[0] = buf;
        Graphics2D g2d = null;
        try {
            g2d = (Graphics2D)createGraphics.invoke(null, paramo);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return g2d;
    }

    public AbstractImageHandlerEncoder(String imageDir, String urlRoot) throws SVGGraphics2DIOException {
        if (imageDir == null) {
            throw new SVGGraphics2DRuntimeException("imageDir should not be null");
        }
        File imageDirFile = new File(imageDir);
        if (!imageDirFile.exists()) {
            throw new SVGGraphics2DRuntimeException("imageDir does not exist");
        }
        this.imageDir = imageDir;
        if (urlRoot != null) {
            this.urlRoot = urlRoot;
        } else {
            try {
                this.urlRoot = imageDirFile.toURI().toURL().toString();
            }
            catch (MalformedURLException e) {
                throw new SVGGraphics2DIOException("cannot convert imageDir to a URL value : " + e.getMessage(), e);
            }
        }
    }

    @Override
    protected void handleHREF(Image image, Element imageElement, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
        BufferedImage buf = this.buildBufferedImage(size);
        Graphics2D g = AbstractImageHandlerEncoder.createGraphics(buf);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        this.saveBufferedImageToFile(imageElement, buf, generatorContext);
    }

    @Override
    protected void handleHREF(RenderedImage image, Element imageElement, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        Dimension size = new Dimension(image.getWidth(), image.getHeight());
        BufferedImage buf = this.buildBufferedImage(size);
        Graphics2D g = AbstractImageHandlerEncoder.createGraphics(buf);
        g.drawRenderedImage(image, IDENTITY);
        g.dispose();
        this.saveBufferedImageToFile(imageElement, buf, generatorContext);
    }

    @Override
    protected void handleHREF(RenderableImage image, Element imageElement, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        Dimension size = new Dimension((int)Math.ceil(image.getWidth()), (int)Math.ceil(image.getHeight()));
        BufferedImage buf = this.buildBufferedImage(size);
        Graphics2D g = AbstractImageHandlerEncoder.createGraphics(buf);
        g.drawRenderableImage(image, IDENTITY);
        g.dispose();
        this.saveBufferedImageToFile(imageElement, buf, generatorContext);
    }

    private void saveBufferedImageToFile(Element imageElement, BufferedImage buf, SVGGeneratorContext generatorContext) throws SVGGraphics2DIOException {
        if (generatorContext == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        File imageFile = null;
        while (imageFile == null) {
            String fileId = generatorContext.idGenerator.generateID(this.getPrefix());
            imageFile = new File(this.imageDir, fileId + this.getSuffix());
            if (!imageFile.exists()) continue;
            imageFile = null;
        }
        this.encodeImage(buf, imageFile);
        imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", this.urlRoot + "/" + imageFile.getName());
    }

    public abstract String getSuffix();

    public abstract String getPrefix();

    public abstract void encodeImage(BufferedImage var1, File var2) throws SVGGraphics2DIOException;

    public abstract BufferedImage buildBufferedImage(Dimension var1);
}

