/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.TransposeType;

class TransposePropertyGenerator
extends PropertyGeneratorImpl {
    static /* synthetic */ Class class$javax$media$jai$ROI;
    static /* synthetic */ Class class$javax$media$jai$RenderedOp;

    public TransposePropertyGenerator() {
        super(new String[]{"ROI"}, new Class[]{class$javax$media$jai$ROI == null ? (class$javax$media$jai$ROI = TransposePropertyGenerator.class$("javax.media.jai.ROI")) : class$javax$media$jai$ROI}, new Class[]{class$javax$media$jai$RenderedOp == null ? (class$javax$media$jai$RenderedOp = TransposePropertyGenerator.class$("javax.media.jai.RenderedOp")) : class$javax$media$jai$RenderedOp});
    }

    public Object getProperty(String name, Object opNode) {
        this.validate(name, opNode);
        if (opNode instanceof RenderedOp && name.equalsIgnoreCase("roi")) {
            RenderedOp op = (RenderedOp)opNode;
            ParameterBlock pb = op.getParameterBlock();
            PlanarImage src = (PlanarImage)pb.getRenderedSource(0);
            Object property = src.getProperty("ROI");
            if (property == null || property.equals(Image.UndefinedProperty) || !(property instanceof ROI)) {
                return Image.UndefinedProperty;
            }
            ROI srcROI = (ROI)property;
            if (srcROI.getBounds().isEmpty()) {
                return Image.UndefinedProperty;
            }
            TransposeType transposeType = (TransposeType)pb.getObjectParameter(0);
            Interpolation interp = Interpolation.getInstance(0);
            return new ROI(JAI.create("transpose", (RenderedImage)srcROI.getAsImage(), (Object)transposeType));
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

