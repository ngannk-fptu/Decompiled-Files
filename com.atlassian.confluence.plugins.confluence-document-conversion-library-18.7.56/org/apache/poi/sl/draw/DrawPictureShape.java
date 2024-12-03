/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.sl.draw.BitmapImageRenderer;
import org.apache.poi.sl.draw.DrawSimpleShape;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.sl.draw.ImageRenderer;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.sl.usermodel.PictureShape;
import org.apache.poi.sl.usermodel.RectAlign;
import org.apache.poi.sl.usermodel.SimpleShape;

public class DrawPictureShape
extends DrawSimpleShape {
    private static final Logger LOG = LogManager.getLogger(DrawPictureShape.class);

    public DrawPictureShape(PictureShape<?, ?> shape) {
        super((SimpleShape<?, ?>)shape);
    }

    @Override
    public void drawContent(Graphics2D graphics) {
        PictureData[] pics;
        SimpleShape ps = this.getShape();
        Rectangle2D anchor = DrawPictureShape.getAnchor(graphics, ps);
        Insets insets = ps.getClipping();
        for (PictureData data : pics = new PictureData[]{ps.getAlternativePictureData(), ps.getPictureData()}) {
            if (data == null) continue;
            try {
                byte[] dataBytes = data.getData();
                PictureType type = PictureType.valueOf(FileMagic.valueOf(dataBytes));
                String ct = type == PictureType.UNKNOWN ? data.getContentType() : type.getContentType();
                ImageRenderer renderer = DrawPictureShape.getImageRenderer(graphics, ct);
                if (!renderer.canRender(ct)) continue;
                renderer.loadImage(dataBytes, ct);
                renderer.drawImage(graphics, anchor, insets);
                return;
            }
            catch (IOException e) {
                LOG.atError().withThrowable(e).log("image can't be loaded/rendered.");
            }
        }
    }

    public static ImageRenderer getImageRenderer(Graphics2D graphics, String contentType) {
        ImageRenderer renderer;
        ImageRenderer imageRenderer = renderer = graphics != null ? (ImageRenderer)graphics.getRenderingHint(Drawable.IMAGE_RENDERER) : null;
        if (renderer != null && renderer.canRender(contentType)) {
            return renderer;
        }
        BitmapImageRenderer fallback = new BitmapImageRenderer();
        if (fallback.canRender(contentType)) {
            return fallback;
        }
        ClassLoader cl = DrawPictureShape.class.getClassLoader();
        Iterator<ImageRenderer> iter = ServiceLoader.load(ImageRenderer.class, cl).iterator();
        while (true) {
            try {
                ImageRenderer ir;
                while (!(ir = iter.next()).canRender(contentType)) {
                }
                return ir;
            }
            catch (NoSuchElementException ignored) {
            }
            catch (Exception | ServiceConfigurationError throwable) {
                continue;
            }
            break;
        }
        LOG.atWarn().log("No suitable image renderer found for content-type '{}' - include poi-scratchpad (for wmf/emf) or poi-ooxml (for svg) jars - svgs/batik doesn't work on the module-path!", (Object)contentType);
        return fallback;
    }

    @Override
    protected Paint getFillPaint(Graphics2D graphics) {
        return null;
    }

    @Override
    protected PictureShape<?, ?> getShape() {
        return (PictureShape)this.shape;
    }

    public void resize() {
        SimpleShape ps = this.getShape();
        Dimension dim = ps.getPictureData().getImageDimension();
        Rectangle2D origRect = ps.getAnchor();
        double x = origRect.getX();
        double y = origRect.getY();
        double w = dim.getWidth();
        double h = dim.getHeight();
        ps.setAnchor(new Rectangle2D.Double(x, y, w, h));
    }

    public void resize(Rectangle2D target) {
        this.resize(target, RectAlign.CENTER);
    }

    public void resize(Rectangle2D target, RectAlign align) {
        SimpleShape ps = this.getShape();
        Dimension dim = ps.getPictureData().getImageDimension();
        if (dim.width <= 0 || dim.height <= 0) {
            ps.setAnchor(target);
            return;
        }
        double w = target.getWidth();
        double h = target.getHeight();
        double sx = w / (double)dim.width;
        double sy = h / (double)dim.height;
        double dx = 0.0;
        double dy = 0.0;
        if (sx > sy) {
            w = sy * (double)dim.width;
            dx = target.getWidth() - w;
        } else if (sy > sx) {
            h = sx * (double)dim.height;
            dy = target.getHeight() - h;
        } else {
            ps.setAnchor(target);
            return;
        }
        double x = target.getX();
        double y = target.getY();
        switch (align) {
            case TOP: {
                x += dx / 2.0;
                break;
            }
            case TOP_RIGHT: {
                x += dx;
                break;
            }
            case RIGHT: {
                x += dx;
                y += dy / 2.0;
                break;
            }
            case BOTTOM_RIGHT: {
                x += dx;
                y += dy;
                break;
            }
            case BOTTOM: {
                x += dx / 2.0;
                y += dy;
                break;
            }
            case BOTTOM_LEFT: {
                y += dy;
                break;
            }
            case LEFT: {
                y += dy / 2.0;
                break;
            }
            case TOP_LEFT: {
                break;
            }
            default: {
                x += dx / 2.0;
                y += dy / 2.0;
            }
        }
        ps.setAnchor(new Rectangle2D.Double(x, y, w, h));
    }
}

