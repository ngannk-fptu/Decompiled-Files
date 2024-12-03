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

public class BinarizeDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Binarize"}, {"LocalName", "Binarize"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("BinarizeDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BinarizeDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("BinarizeDescriptor1")}};
    private static final String[] paramNames = new String[]{"threshold"};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Double == null ? (class$java$lang$Double = BinarizeDescriptor.class$("java.lang.Double")) : class$java$lang$Double};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};
    static /* synthetic */ Class class$java$lang$Double;

    public BinarizeDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateSources(modeName, args, msg)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        RenderedImage source = (RenderedImage)args.getSource(0);
        int numBands = source.getSampleModel().getNumBands();
        if (numBands != 1) {
            msg.append(this.getName() + " " + JaiI18N.getString("BinarizeDescriptor2"));
            return false;
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, Double threshold, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Binarize", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("threshold", threshold);
        return JAI.create("Binarize", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, Double threshold, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Binarize", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("threshold", threshold);
        return JAI.createRenderable("Binarize", pb, hints);
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

