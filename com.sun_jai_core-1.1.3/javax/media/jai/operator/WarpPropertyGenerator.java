/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;
import javax.media.jai.Warp;

class WarpPropertyGenerator
extends PropertyGeneratorImpl {
    static /* synthetic */ Class class$javax$media$jai$ROI;
    static /* synthetic */ Class class$javax$media$jai$RenderedOp;

    public WarpPropertyGenerator() {
        super(new String[]{"ROI"}, new Class[]{class$javax$media$jai$ROI == null ? (class$javax$media$jai$ROI = WarpPropertyGenerator.class$("javax.media.jai.ROI")) : class$javax$media$jai$ROI}, new Class[]{class$javax$media$jai$RenderedOp == null ? (class$javax$media$jai$RenderedOp = WarpPropertyGenerator.class$("javax.media.jai.RenderedOp")) : class$javax$media$jai$RenderedOp});
    }

    public Object getProperty(String name, Object opNode) {
        this.validate(name, opNode);
        if (opNode instanceof RenderedOp && name.equalsIgnoreCase("roi")) {
            RenderedOp op = (RenderedOp)opNode;
            ParameterBlock pb = op.getParameterBlock();
            RenderedImage src = pb.getRenderedSource(0);
            Object property = src.getProperty("ROI");
            if (property == null || property.equals(Image.UndefinedProperty) || !(property instanceof ROI)) {
                return Image.UndefinedProperty;
            }
            ROI srcROI = (ROI)property;
            if (srcROI.getBounds().isEmpty()) {
                return Image.UndefinedProperty;
            }
            Interpolation interp = (Interpolation)pb.getObjectParameter(1);
            Rectangle srcBounds = null;
            PlanarImage dst = op.getRendering();
            srcBounds = dst instanceof GeometricOpImage && ((GeometricOpImage)dst).getBorderExtender() == null ? new Rectangle(src.getMinX() + interp.getLeftPadding(), src.getMinY() + interp.getTopPadding(), src.getWidth() - interp.getWidth() + 1, src.getHeight() - interp.getHeight() + 1) : new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
            if (!srcBounds.contains(srcROI.getBounds())) {
                srcROI = srcROI.intersect(new ROIShape(srcBounds));
            }
            Interpolation interpNN = interp instanceof InterpolationNearest ? interp : Interpolation.getInstance(0);
            Warp warp = (Warp)pb.getObjectParameter(0);
            ROI dstROI = new ROI(JAI.create("warp", (RenderedImage)srcROI.getAsImage(), (Object)warp, (Object)interpNN));
            Rectangle dstBounds = op.getBounds();
            if (!dstBounds.contains(dstROI.getBounds())) {
                dstROI = dstROI.intersect(new ROIShape(dstBounds));
            }
            return dstROI;
        }
        return Image.UndefinedProperty;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

