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

public class RescaleDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Rescale"}, {"LocalName", "Rescale"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("RescaleDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/RescaleDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("RescaleDescriptor1")}, {"arg1Desc", JaiI18N.getString("RescaleDescriptor2")}};
    private static final Class[] paramClasses = new Class[]{array$D == null ? (array$D = RescaleDescriptor.class$("[D")) : array$D, array$D == null ? (array$D = RescaleDescriptor.class$("[D")) : array$D};
    private static final String[] paramNames = new String[]{"constants", "offsets"};
    private static final Object[] paramDefaults = new Object[]{new double[]{1.0}, new double[]{0.0}};
    static /* synthetic */ Class array$D;

    public RescaleDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public boolean isRenderableSupported() {
        return true;
    }

    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
        if (!super.validateParameters(args, msg)) {
            return false;
        }
        int constantsLength = ((double[])args.getObjectParameter(0)).length;
        int offsetsLength = ((double[])args.getObjectParameter(1)).length;
        if (constantsLength < 1) {
            msg.append(this.getName() + " " + JaiI18N.getString("RescaleDescriptor3"));
            return false;
        }
        if (offsetsLength < 1) {
            msg.append(this.getName() + ": " + JaiI18N.getString("RescaleDescriptor4"));
            return false;
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, double[] constants, double[] offsets, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Rescale", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("constants", constants);
        pb.setParameter("offsets", offsets);
        return JAI.create("Rescale", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, double[] constants, double[] offsets, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Rescale", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("constants", constants);
        pb.setParameter("offsets", offsets);
        return JAI.createRenderable("Rescale", pb, hints);
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

