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

public class MultiplyConstDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "MultiplyConst"}, {"LocalName", "MultiplyConst"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("MultiplyConstDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MultiplyConstDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("MultiplyConstDescriptor1")}};
    private static final Class[] paramClasses = new Class[]{array$D == null ? (array$D = MultiplyConstDescriptor.class$("[D")) : array$D};
    private static final String[] paramNames = new String[]{"constants"};
    private static final Object[] paramDefaults = new Object[]{new double[]{1.0}};
    static /* synthetic */ Class array$D;

    public MultiplyConstDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public boolean isRenderableSupported() {
        return true;
    }

    protected boolean validateParameters(ParameterBlock args, StringBuffer message) {
        if (!super.validateParameters(args, message)) {
            return false;
        }
        int length = ((double[])args.getObjectParameter(0)).length;
        if (length < 1) {
            message.append(this.getName() + " " + JaiI18N.getString("MultiplyConstDescriptor2"));
            return false;
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, double[] constants, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("MultiplyConst", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("constants", constants);
        return JAI.create("MultiplyConst", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, double[] constants, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("MultiplyConst", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("constants", constants);
        return JAI.createRenderable("MultiplyConst", pb, hints);
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

