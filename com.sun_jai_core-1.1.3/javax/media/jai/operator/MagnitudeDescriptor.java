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

public class MagnitudeDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Magnitude"}, {"LocalName", "Magnitude"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("MagnitudeDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MagnitudeDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};

    public MagnitudeDescriptor() {
        super(resources, supportedModes, 1, null, null, null, null);
    }

    protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateSources(modeName, args, msg)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        RenderedImage src = args.getRenderedSource(0);
        int bands = src.getSampleModel().getNumBands();
        if (bands % 2 != 0) {
            msg.append(this.getName() + " " + JaiI18N.getString("MagnitudeDescriptor1"));
            return false;
        }
        return true;
    }

    public PropertyGenerator[] getPropertyGenerators(String modeName) {
        PropertyGenerator[] pg = new PropertyGenerator[]{new ComplexPropertyGenerator()};
        return pg;
    }

    public static RenderedOp create(RenderedImage source0, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Magnitude", "rendered");
        pb.setSource("source0", source0);
        return JAI.create("Magnitude", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Magnitude", "renderable");
        pb.setSource("source0", source0);
        return JAI.createRenderable("Magnitude", pb, hints);
    }
}

