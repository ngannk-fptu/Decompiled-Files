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

public class InvertDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Invert"}, {"LocalName", "Invert"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("InvertDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/InvertDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}};

    public InvertDescriptor() {
        super(resources, 1, null, null, null);
    }

    public boolean isRenderableSupported() {
        return true;
    }

    public static RenderedOp create(RenderedImage source0, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Invert", "rendered");
        pb.setSource("source0", source0);
        return JAI.create("Invert", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Invert", "renderable");
        pb.setSource("source0", source0);
        return JAI.createRenderable("Invert", pb, hints);
    }
}

