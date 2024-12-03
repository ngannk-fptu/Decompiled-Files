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

public class BandSelectDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "BandSelect"}, {"LocalName", "BandSelect"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("BandSelectDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BandSelectDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("BandSelectDescriptor1")}};
    private static final Class[] paramClasses = new Class[]{array$I == null ? (array$I = BandSelectDescriptor.class$("[I")) : array$I};
    private static final String[] paramNames = new String[]{"bandIndices"};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};
    static /* synthetic */ Class array$I;

    public BandSelectDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer message) {
        if (!super.validateArguments(modeName, args, message)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        int[] indices = (int[])args.getObjectParameter(0);
        if (indices.length < 1) {
            message.append(this.getName() + " " + JaiI18N.getString("BandSelectDescriptor2"));
            return false;
        }
        RenderedImage src = args.getRenderedSource(0);
        int bands = src.getSampleModel().getNumBands();
        for (int i = 0; i < indices.length; ++i) {
            if (indices[i] >= 0 && indices[i] < bands) continue;
            message.append(this.getName() + " " + JaiI18N.getString("BandSelectDescriptor3"));
            return false;
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, int[] bandIndices, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("BandSelect", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("bandIndices", bandIndices);
        return JAI.create("BandSelect", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, int[] bandIndices, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("BandSelect", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("bandIndices", bandIndices);
        return JAI.createRenderable("BandSelect", pb, hints);
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

