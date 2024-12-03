/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;

class ScalePropertyGenerator
extends PropertyGeneratorImpl {
    static /* synthetic */ Class class$javax$media$jai$ROI;
    static /* synthetic */ Class class$javax$media$jai$RenderedOp;

    public ScalePropertyGenerator() {
        super(new String[]{"ROI"}, new Class[]{class$javax$media$jai$ROI == null ? (class$javax$media$jai$ROI = ScalePropertyGenerator.class$("javax.media.jai.ROI")) : class$javax$media$jai$ROI}, new Class[]{class$javax$media$jai$RenderedOp == null ? (class$javax$media$jai$RenderedOp = ScalePropertyGenerator.class$("javax.media.jai.RenderedOp")) : class$javax$media$jai$RenderedOp});
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
            Interpolation interp = (Interpolation)pb.getObjectParameter(4);
            Rectangle srcBounds = null;
            PlanarImage dst = op.getRendering();
            srcBounds = dst instanceof GeometricOpImage && ((GeometricOpImage)dst).getBorderExtender() == null ? new Rectangle(src.getMinX() + interp.getLeftPadding(), src.getMinY() + interp.getTopPadding(), src.getWidth() - interp.getWidth() + 1, src.getHeight() - interp.getHeight() + 1) : new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
            if (!srcBounds.contains(srcROI.getBounds())) {
                srcROI = srcROI.intersect(new ROIShape(srcBounds));
            }
            float sx = pb.getFloatParameter(0);
            float sy = pb.getFloatParameter(1);
            float tx = pb.getFloatParameter(2);
            float ty = pb.getFloatParameter(3);
            AffineTransform transform = new AffineTransform((double)sx, 0.0, 0.0, (double)sy, (double)tx, (double)ty);
            ROI dstROI = srcROI.transform(transform);
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

