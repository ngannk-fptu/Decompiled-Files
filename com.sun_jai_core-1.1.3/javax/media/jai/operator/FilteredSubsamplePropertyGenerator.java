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
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderedOp;
import javax.media.jai.WarpOpImage;

class FilteredSubsamplePropertyGenerator
extends PropertyGeneratorImpl {
    static /* synthetic */ Class class$javax$media$jai$RenderedOp;
    static /* synthetic */ Class class$javax$media$jai$RenderableOp;

    public FilteredSubsamplePropertyGenerator() {
        super(new String[]{"FilteredSubsample"}, new Class[]{Boolean.TYPE}, new Class[]{class$javax$media$jai$RenderedOp == null ? (class$javax$media$jai$RenderedOp = FilteredSubsamplePropertyGenerator.class$("javax.media.jai.RenderedOp")) : class$javax$media$jai$RenderedOp, class$javax$media$jai$RenderableOp == null ? (class$javax$media$jai$RenderableOp = FilteredSubsamplePropertyGenerator.class$("javax.media.jai.RenderableOp")) : class$javax$media$jai$RenderableOp});
    }

    public Object getProperty(String name, Object opNode) {
        this.validate(name, opNode);
        if (opNode instanceof RenderedOp && name.equalsIgnoreCase("roi")) {
            RenderedOp op = (RenderedOp)opNode;
            ParameterBlock pb = op.getParameterBlock();
            RenderedImage src = pb.getRenderedSource(0);
            Object property = src.getProperty("ROI");
            if (property == null || property.equals(Image.UndefinedProperty) || !(property instanceof ROI)) {
                return null;
            }
            ROI srcROI = (ROI)property;
            Rectangle srcBounds = null;
            PlanarImage dst = op.getRendering();
            if (dst instanceof WarpOpImage && !((OpImage)dst).hasExtender(0)) {
                WarpOpImage warpIm = (WarpOpImage)dst;
                srcBounds = new Rectangle(src.getMinX() + warpIm.getLeftPadding(), src.getMinY() + warpIm.getTopPadding(), src.getWidth() - warpIm.getWidth() + 1, src.getHeight() - warpIm.getHeight() + 1);
            } else {
                srcBounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
            }
            if (!srcBounds.contains(srcROI.getBounds())) {
                srcROI = srcROI.intersect(new ROIShape(srcBounds));
            }
            float sx = 1.0f / (float)pb.getIntParameter(1);
            float sy = 1.0f / (float)pb.getIntParameter(2);
            AffineTransform transform = new AffineTransform((double)sx, 0.0, 0.0, (double)sy, 0.0, 0.0);
            ROI dstROI = srcROI.transform(transform);
            Rectangle dstBounds = op.getBounds();
            if (!dstBounds.contains(dstROI.getBounds())) {
                dstROI = dstROI.intersect(new ROIShape(dstBounds));
            }
            return dstROI;
        }
        return null;
    }

    public String[] getPropertyNames() {
        String[] properties = new String[]{"roi"};
        return properties;
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

