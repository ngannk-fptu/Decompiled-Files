/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class MinDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Min"}, {"LocalName", "Min"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("MinDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MinDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}};

    public MinDescriptor() {
        super(resources, 2, null, null, null);
    }

    public boolean isRenderableSupported() {
        return true;
    }

    public static RenderedOp create(RenderedImage source0, RenderedImage source1, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Min", "rendered");
        pb.setSource("source0", source0);
        pb.setSource("source1", source1);
        return JAI.create("Min", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, RenderableImage source1, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Min", "renderable");
        pb.setSource("source0", source0);
        pb.setSource("source1", source1);
        return JAI.createRenderable("Min", pb, hints);
    }
}

