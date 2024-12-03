/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.svggen.DefaultExtensionHandler
 *  org.apache.batik.svggen.SVGColor
 *  org.apache.batik.svggen.SVGGeneratorContext
 *  org.apache.batik.svggen.SVGGraphics2D
 *  org.apache.batik.svggen.SVGPaintDescriptor
 *  org.apache.batik.svggen.SVGTexturePaint
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.xslf.draw;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import javax.imageio.ImageIO;
import org.apache.batik.svggen.DefaultExtensionHandler;
import org.apache.batik.svggen.SVGColor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGPaintDescriptor;
import org.apache.batik.svggen.SVGTexturePaint;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.sl.draw.BitmapImageRenderer;
import org.apache.poi.sl.draw.DrawTexturePaint;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.draw.ImageRenderer;
import org.apache.poi.sl.draw.PathGradientPaint;
import org.apache.poi.sl.usermodel.Insets2D;
import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.SimpleShape;
import org.apache.poi.util.Dimension2DDouble;
import org.apache.poi.util.Internal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Internal
public class SVGRenderExtension
extends DefaultExtensionHandler {
    private static final int LINE_LENGTH = 65;
    private static final String XLINK_NS = "http://www.w3.org/1999/xlink";
    private final Map<Long, String> imageMap = new HashMap<Long, String>();
    private WeakReference<SVGGraphics2D> svgGraphics2D = null;

    public SVGGraphics2D getSvgGraphics2D() {
        return this.svgGraphics2D != null ? (SVGGraphics2D)this.svgGraphics2D.get() : null;
    }

    public void setSvgGraphics2D(SVGGraphics2D svgGraphics2D) {
        this.svgGraphics2D = new WeakReference<SVGGraphics2D>(svgGraphics2D);
    }

    public SVGPaintDescriptor handlePaint(Paint paint, SVGGeneratorContext generatorContext) {
        if (paint instanceof LinearGradientPaint) {
            return this.getLgpDescriptor((LinearGradientPaint)paint, generatorContext);
        }
        if (paint instanceof RadialGradientPaint) {
            return this.getRgpDescriptor((RadialGradientPaint)paint, generatorContext);
        }
        if (paint instanceof PathGradientPaint) {
            return this.getPathDescriptor((PathGradientPaint)paint, generatorContext);
        }
        if (paint instanceof DrawTexturePaint) {
            return this.getDtpDescriptor((DrawTexturePaint)paint, generatorContext);
        }
        return super.handlePaint(paint, generatorContext);
    }

    private SVGPaintDescriptor getPathDescriptor(PathGradientPaint gradient, SVGGeneratorContext genCtx) {
        RenderingHints hints = genCtx.getGraphicContextDefaults().getRenderingHints();
        Shape shape = (Shape)hints.get(Drawable.GRADIENT_SHAPE);
        if (shape == null) {
            return null;
        }
        PathGradientPaint.PathGradientContext context = gradient.createContext(ColorModel.getRGBdefault(), shape.getBounds(), shape.getBounds2D(), new AffineTransform(), hints);
        WritableRaster raster = context.createRaster();
        BufferedImage img = new BufferedImage(context.getColorModel(), raster, false, null);
        SVGTexturePaint texturePaint = new SVGTexturePaint(genCtx);
        TexturePaint tp = new TexturePaint(img, shape.getBounds2D());
        return texturePaint.toSVG(tp);
    }

    private SVGPaintDescriptor getRgpDescriptor(RadialGradientPaint gradient, SVGGeneratorContext genCtx) {
        Element gradElem = genCtx.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "radialGradient");
        String id = genCtx.getIDGenerator().generateID("gradient");
        gradElem.setAttribute("id", id);
        SVGRenderExtension.setPoint(gradElem, gradient.getCenterPoint(), "cx", "cy");
        SVGRenderExtension.setPoint(gradElem, gradient.getFocusPoint(), "fx", "fy");
        gradElem.setAttribute("r", String.valueOf(gradient.getRadius()));
        this.addMgpAttributes(gradElem, genCtx, gradient);
        return new SVGPaintDescriptor("url(#" + id + ")", "1", gradElem);
    }

    private SVGPaintDescriptor getLgpDescriptor(LinearGradientPaint gradient, SVGGeneratorContext genCtx) {
        Element gradElem = genCtx.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "linearGradient");
        String id = genCtx.getIDGenerator().generateID("gradient");
        gradElem.setAttribute("id", id);
        SVGRenderExtension.setPoint(gradElem, gradient.getStartPoint(), "x1", "y1");
        SVGRenderExtension.setPoint(gradElem, gradient.getEndPoint(), "x2", "y2");
        this.addMgpAttributes(gradElem, genCtx, gradient);
        return new SVGPaintDescriptor("url(#" + id + ")", "1", gradElem);
    }

    private void addMgpAttributes(Element gradElem, SVGGeneratorContext genCtx, MultipleGradientPaint gradient) {
        String cycleVal;
        gradElem.setAttribute("gradientUnits", "userSpaceOnUse");
        switch (gradient.getCycleMethod()) {
            case REFLECT: {
                cycleVal = "reflect";
                break;
            }
            case REPEAT: {
                cycleVal = "repeat";
                break;
            }
            default: {
                cycleVal = "pad";
            }
        }
        gradElem.setAttribute("spreadMethod", cycleVal);
        String colorSpace = gradient.getColorSpace() == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB ? "linearRGB" : "sRGB";
        gradElem.setAttribute("color-interpolation", colorSpace);
        AffineTransform tf = gradient.getTransform();
        if (!tf.isIdentity()) {
            String matrix = "matrix(" + tf.getScaleX() + " " + tf.getShearY() + " " + tf.getShearX() + " " + tf.getScaleY() + " " + tf.getTranslateX() + " " + tf.getTranslateY() + ")";
            gradElem.setAttribute("gradientTransform", matrix);
        }
        Color[] colors = gradient.getColors();
        float[] fracs = gradient.getFractions();
        for (int i = 0; i < colors.length; ++i) {
            Element stop = genCtx.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "stop");
            SVGPaintDescriptor pd = SVGColor.toSVG((Color)colors[i], (SVGGeneratorContext)genCtx);
            stop.setAttribute("offset", (int)(fracs[i] * 100.0f) + "%");
            stop.setAttribute("stop-color", pd.getPaintValue());
            if (colors[i].getAlpha() != 255) {
                stop.setAttribute("stop-opacity", pd.getOpacityValue());
            }
            gradElem.appendChild(stop);
        }
    }

    private static void setPoint(Element gradElem, Point2D point, String x, String y) {
        gradElem.setAttribute(x, Double.toString(point.getX()));
        gradElem.setAttribute(y, Double.toString(point.getY()));
    }

    private SVGPaintDescriptor getDtpDescriptor(DrawTexturePaint tdp, SVGGeneratorContext genCtx) {
        double rot;
        PaintStyle.FlipMode flipMode;
        Point2D offset;
        String imgID = this.getImageID(tdp, genCtx);
        Document domFactory = genCtx.getDOMFactory();
        Element patternDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "pattern");
        String patID = genCtx.getIDGenerator().generateID("pattern");
        PaintStyle.TexturePaint fill = tdp.getFill();
        Insets2D stretch = fill.getStretch();
        if (stretch == null) {
            stretch = new Insets2D(0.0, 0.0, 0.0, 0.0);
        }
        Rectangle2D anchorRect = tdp.getAnchorRect();
        String x = genCtx.doubleString(-stretch.left / 100000.0 * anchorRect.getWidth());
        String y = genCtx.doubleString(-stretch.top / 100000.0 * anchorRect.getHeight());
        String w = genCtx.doubleString((100000.0 + stretch.left + stretch.right) / 100000.0 * anchorRect.getWidth());
        String h = genCtx.doubleString((100000.0 + stretch.top + stretch.bottom) / 100000.0 * anchorRect.getHeight());
        Dimension2D scale = fill.getScale();
        if (scale == null) {
            scale = new Dimension2DDouble(1.0, 1.0);
        }
        if ((offset = fill.getOffset()) == null) {
            offset = new Point2D.Double(0.0, 0.0);
        }
        if ((flipMode = fill.getFlipMode()) == null) {
            flipMode = PaintStyle.FlipMode.NONE;
        }
        SVGRenderExtension.setAttribute(genCtx, patternDef, null, "patternUnits", "objectBoundingBox", null, "id", patID, null, "x", offset.getX(), null, "y", offset.getY(), null, "width", genCtx.doubleString(scale.getWidth() * 100.0) + "%", null, "height", genCtx.doubleString(scale.getHeight() * 100.0) + "%", null, "preserveAspectRatio", "none", null, "viewBox", x + " " + y + " " + w + " " + h);
        org.apache.poi.sl.usermodel.Shape slShape = fill.getShape();
        if (!fill.isRotatedWithShape() && slShape instanceof SimpleShape && (rot = ((SimpleShape)slShape).getRotation()) != 0.0) {
            SVGRenderExtension.setAttribute(genCtx, patternDef, null, "patternTransform", "rotate(" + genCtx.doubleString(-rot) + ")");
        }
        Element useImageEl = domFactory.createElementNS("http://www.w3.org/2000/svg", "use");
        useImageEl.setAttributeNS(null, "href", "#" + imgID);
        patternDef.appendChild(useImageEl);
        String patternAttrBuf = "url(#" + patID + ")";
        return new SVGPaintDescriptor(patternAttrBuf, "1", patternDef);
    }

    private String getImageID(DrawTexturePaint tdp, SVGGeneratorContext genCtx) {
        BitmapImageRenderer bir;
        String ct;
        ImageRenderer imgRdr = tdp.getImageRenderer();
        byte[] imgData = null;
        String contentType = null;
        if (imgRdr instanceof BitmapImageRenderer && (PictureData.PictureType.PNG.contentType.equals(ct = (bir = (BitmapImageRenderer)imgRdr).getCachedContentType()) || PictureData.PictureType.JPEG.contentType.equals(ct) || PictureData.PictureType.GIF.contentType.equals(ct))) {
            contentType = ct;
            imgData = bir.getCachedImage();
        }
        if (imgData == null) {
            BufferedImage bi = imgRdr.getImage();
            UnsynchronizedByteArrayOutputStream bos = new UnsynchronizedByteArrayOutputStream();
            try {
                ImageIO.write((RenderedImage)bi, "PNG", (OutputStream)bos);
            }
            catch (IOException e) {
                return null;
            }
            imgData = bos.toByteArray();
            contentType = PictureData.PictureType.PNG.contentType;
        }
        CRC32 crc = new CRC32();
        crc.update(imgData);
        Long imageCrc = crc.getValue();
        String imgID = this.imageMap.get(imageCrc);
        if (imgID != null) {
            return imgID;
        }
        Document domFactory = genCtx.getDOMFactory();
        Rectangle2D anchorRect = tdp.getAnchorRect();
        imgID = genCtx.getIDGenerator().generateID("image");
        this.imageMap.put(imageCrc, imgID);
        int sbLen = 4 * imgData.length / 3 + 3 & 0xFFFFFFFC;
        sbLen += sbLen / 65 + 30;
        StringBuilder sb = new StringBuilder(sbLen);
        sb.append("data:");
        sb.append(contentType);
        sb.append(";base64,\n");
        sb.append(Base64.getMimeEncoder(65, "\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(imgData));
        Element imageEl = domFactory.createElementNS("http://www.w3.org/2000/svg", "image");
        SVGRenderExtension.setAttribute(genCtx, imageEl, null, "id", imgID, null, "preserveAspectRatio", "none", null, "x", anchorRect.getX(), null, "y", anchorRect.getY(), null, "width", anchorRect.getWidth(), null, "height", anchorRect.getHeight(), XLINK_NS, "xlink:href", sb.toString());
        this.getSvgGraphics2D().getDOMTreeManager().addOtherDef(imageEl);
        return imgID;
    }

    private static void setAttribute(SVGGeneratorContext genCtx, Element el, Object ... params) {
        for (int i = 0; i < params.length; i += 3) {
            String ns = (String)params[i];
            String name = (String)params[i + 1];
            Object oval = params[i + 2];
            String val = oval instanceof String ? (String)oval : (oval instanceof Number ? genCtx.doubleString(((Number)oval).doubleValue()) : (oval == null ? "" : oval.toString()));
            el.setAttributeNS(ns, name, val);
        }
    }
}

