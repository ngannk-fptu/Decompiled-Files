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

public class PiecewiseDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Piecewise"}, {"LocalName", "Piecewise"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("PiecewiseDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/PiecewiseDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", "The breakpoint array."}};
    private static final Class[] paramClasses = new Class[]{array$$$F == null ? (array$$$F = PiecewiseDescriptor.class$("[[[F")) : array$$$F};
    private static final String[] paramNames = new String[]{"breakPoints"};
    private static final Object[] paramDefaults = new Object[]{new float[][][]{new float[][]{{0.0f, 255.0f}, {0.0f, 255.0f}}}};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};
    static /* synthetic */ Class array$$$F;

    public PiecewiseDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg) {
        int b;
        if (!super.validateArguments(modeName, args, msg)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        RenderedImage src = args.getRenderedSource(0);
        float[][][] breakPoints = (float[][][])args.getObjectParameter(0);
        if (breakPoints.length != 1 && breakPoints.length != src.getSampleModel().getNumBands()) {
            msg.append(this.getName() + " " + JaiI18N.getString("PiecewiseDescriptor1"));
            return false;
        }
        int numBands = breakPoints.length;
        for (b = 0; b < numBands; ++b) {
            if (breakPoints[b].length != 2) {
                msg.append(this.getName() + " " + JaiI18N.getString("PiecewiseDescriptor2"));
                return false;
            }
            if (breakPoints[b][0].length == breakPoints[b][1].length) continue;
            msg.append(this.getName() + " " + JaiI18N.getString("PiecewiseDescriptor3"));
            return false;
        }
        for (b = 0; b < numBands; ++b) {
            int count = breakPoints[b][0].length - 1;
            float[] x = breakPoints[b][0];
            for (int i = 0; i < count; ++i) {
                if (!(x[i] >= x[i + 1])) continue;
                msg.append(this.getName() + " " + JaiI18N.getString("PiecewiseDescriptor4"));
                return false;
            }
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, float[][][] breakPoints, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Piecewise", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("breakPoints", breakPoints);
        return JAI.create("Piecewise", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, float[][][] breakPoints, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Piecewise", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("breakPoints", breakPoints);
        return JAI.createRenderable("Piecewise", pb, hints);
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

