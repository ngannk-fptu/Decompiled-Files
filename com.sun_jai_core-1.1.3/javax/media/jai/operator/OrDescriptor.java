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
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class OrDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Or"}, {"LocalName", "Or"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("OrDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/OrDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};

    public OrDescriptor() {
        super(resources, supportedModes, 2, null, null, null, null);
    }

    protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateSources(modeName, args, msg)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        for (int i = 0; i < 2; ++i) {
            RenderedImage src = args.getRenderedSource(i);
            int dtype = src.getSampleModel().getDataType();
            if (dtype == 0 || dtype == 1 || dtype == 2 || dtype == 3) continue;
            msg.append(this.getName() + " " + JaiI18N.getString("OrDescriptor1"));
            return false;
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, RenderedImage source1, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Or", "rendered");
        pb.setSource("source0", source0);
        pb.setSource("source1", source1);
        return JAI.create("Or", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, RenderableImage source1, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Or", "renderable");
        pb.setSource("source0", source0);
        pb.setSource("source1", source1);
        return JAI.createRenderable("Or", pb, hints);
    }
}

