/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.GeometricOpImage;
import javax.media.jai.Interpolation;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.ROI;
import javax.media.jai.ROIShape;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

class SubsampleAveragePropertyGenerator
implements PropertyGenerator {
    static /* synthetic */ Class class$javax$media$jai$ROI;

    public String[] getPropertyNames() {
        String[] properties = new String[]{"ROI"};
        return properties;
    }

    public Class getClass(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SubsampleAveragePropertyGenerator0"));
        }
        if (propertyName.equalsIgnoreCase("roi")) {
            return class$javax$media$jai$ROI == null ? (class$javax$media$jai$ROI = SubsampleAveragePropertyGenerator.class$("javax.media.jai.ROI")) : class$javax$media$jai$ROI;
        }
        return null;
    }

    public boolean canGenerateProperties(Object opNode) {
        if (opNode == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SubsampleAveragePropertyGenerator1"));
        }
        return opNode instanceof RenderedOp;
    }

    public Object getProperty(String name, Object opNode) {
        if (name == null || opNode == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SubsampleAveragePropertyGenerator2"));
        }
        if (!this.canGenerateProperties(opNode)) {
            throw new IllegalArgumentException(opNode.getClass().getName() + JaiI18N.getString("SubsampleAveragePropertyGenerator3"));
        }
        return opNode instanceof RenderedOp ? this.getProperty(name, (RenderedOp)opNode) : null;
    }

    public Object getProperty(String name, RenderedOp op) {
        if (name == null || op == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SubsampleAveragePropertyGenerator4"));
        }
        if (name.equals("roi")) {
            ParameterBlock pb = op.getParameterBlock();
            PlanarImage src = (PlanarImage)pb.getRenderedSource(0);
            Object property = src.getProperty("ROI");
            if (property == null || property.equals(Image.UndefinedProperty) || !(property instanceof ROI)) {
                return null;
            }
            ROI srcROI = (ROI)property;
            Rectangle srcBounds = null;
            PlanarImage dst = op.getRendering();
            if (dst instanceof GeometricOpImage && ((GeometricOpImage)dst).getBorderExtender() == null) {
                GeometricOpImage geomIm = (GeometricOpImage)dst;
                Interpolation interp = geomIm.getInterpolation();
                srcBounds = new Rectangle(src.getMinX() + interp.getLeftPadding(), src.getMinY() + interp.getTopPadding(), src.getWidth() - interp.getWidth() + 1, src.getHeight() - interp.getHeight() + 1);
            } else {
                srcBounds = src.getBounds();
            }
            if (!srcBounds.contains(srcROI.getBounds())) {
                srcROI = srcROI.intersect(new ROIShape(srcBounds));
            }
            double sx = pb.getDoubleParameter(0);
            double sy = pb.getDoubleParameter(1);
            AffineTransform transform = new AffineTransform(sx, 0.0, 0.0, sy, 0.0, 0.0);
            ROI dstROI = srcROI.transform(transform);
            Rectangle dstBounds = op.getBounds();
            if (!dstBounds.contains(dstROI.getBounds())) {
                dstROI = dstROI.intersect(new ROIShape(dstBounds));
            }
            return dstROI;
        }
        return null;
    }

    public Object getProperty(String name, RenderableOp op) {
        if (name == null || op == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SubsampleAveragePropertyGenerator2"));
        }
        return null;
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

