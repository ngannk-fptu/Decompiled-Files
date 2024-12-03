/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ComplexPropertyGenerator;
import javax.media.jai.operator.JaiI18N;

public class MultiplyComplexDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "MultiplyComplex"}, {"LocalName", "MultiplyComplex"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("MultiplyComplexDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MultiplyComplexDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};

    public MultiplyComplexDescriptor() {
        super(resources, supportedModes, 2, null, null, null, null);
    }

    protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateSources(modeName, args, msg)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        RenderedImage src1 = args.getRenderedSource(0);
        RenderedImage src2 = args.getRenderedSource(1);
        if (src1.getSampleModel().getNumBands() % 2 != 0 || src2.getSampleModel().getNumBands() % 2 != 0) {
            msg.append(this.getName() + " " + JaiI18N.getString("MultiplyComplexDescriptor1"));
            return false;
        }
        return true;
    }

    public PropertyGenerator[] getPropertyGenerators(String modeName) {
        PropertyGenerator[] pg = new PropertyGenerator[]{new ComplexPropertyGenerator()};
        return pg;
    }

    public static RenderedOp create(RenderedImage source0, RenderedImage source1, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("MultiplyComplex", "rendered");
        pb.setSource("source0", source0);
        pb.setSource("source1", source1);
        return JAI.create("MultiplyComplex", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, RenderableImage source1, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("MultiplyComplex", "renderable");
        pb.setSource("source0", source0);
        pb.setSource("source1", source1);
        return JAI.createRenderable("MultiplyComplex", pb, hints);
    }
}

