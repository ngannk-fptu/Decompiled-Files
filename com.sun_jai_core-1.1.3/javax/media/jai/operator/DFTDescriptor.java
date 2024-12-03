/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.DFTDataNature;
import javax.media.jai.operator.DFTPropertyGenerator;
import javax.media.jai.operator.DFTScalingType;
import javax.media.jai.operator.JaiI18N;

public class DFTDescriptor
extends OperationDescriptorImpl {
    public static final DFTScalingType SCALING_NONE = new DFTScalingType("SCALING_NONE", 1);
    public static final DFTScalingType SCALING_UNITARY = new DFTScalingType("SCALING_UNITARY", 2);
    public static final DFTScalingType SCALING_DIMENSIONS = new DFTScalingType("SCALING_DIMENSIONS", 3);
    public static final DFTDataNature REAL_TO_COMPLEX = new DFTDataNature("REAL_TO_COMPLEX", 1);
    public static final DFTDataNature COMPLEX_TO_COMPLEX = new DFTDataNature("COMPLEX_TO_COMPLEX", 2);
    public static final DFTDataNature COMPLEX_TO_REAL = new DFTDataNature("COMPLEX_TO_REAL", 3);
    private static final String[][] resources = new String[][]{{"GlobalName", "DFT"}, {"LocalName", "DFT"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("DFTDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/DFTDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion2")}, {"arg0Desc", JaiI18N.getString("DFTDescriptor1")}, {"arg1Desc", JaiI18N.getString("DFTDescriptor2")}};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$operator$DFTScalingType == null ? (class$javax$media$jai$operator$DFTScalingType = DFTDescriptor.class$("javax.media.jai.operator.DFTScalingType")) : class$javax$media$jai$operator$DFTScalingType, class$javax$media$jai$operator$DFTDataNature == null ? (class$javax$media$jai$operator$DFTDataNature = DFTDescriptor.class$("javax.media.jai.operator.DFTDataNature")) : class$javax$media$jai$operator$DFTDataNature};
    private static final String[] paramNames = new String[]{"scalingType", "dataNature"};
    private static final Object[] paramDefaults = new Object[]{SCALING_NONE, REAL_TO_COMPLEX};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};
    static /* synthetic */ Class class$javax$media$jai$operator$DFTScalingType;
    static /* synthetic */ Class class$javax$media$jai$operator$DFTDataNature;

    public DFTDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg) {
        RenderedImage src;
        if (!super.validateArguments(modeName, args, msg)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        EnumeratedParameter dataNature = (EnumeratedParameter)args.getObjectParameter(1);
        if (!dataNature.equals(REAL_TO_COMPLEX) && (src = args.getRenderedSource(0)).getSampleModel().getNumBands() % 2 != 0) {
            msg.append(this.getName() + " " + JaiI18N.getString("DFTDescriptor5"));
            return false;
        }
        return true;
    }

    public PropertyGenerator[] getPropertyGenerators(String modeName) {
        PropertyGenerator[] pg = new PropertyGenerator[]{new DFTPropertyGenerator()};
        return pg;
    }

    public static RenderedOp create(RenderedImage source0, DFTScalingType scalingType, DFTDataNature dataNature, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("DFT", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("scalingType", scalingType);
        pb.setParameter("dataNature", dataNature);
        return JAI.create("DFT", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, DFTScalingType scalingType, DFTDataNature dataNature, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("DFT", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("scalingType", scalingType);
        pb.setParameter("dataNature", dataNature);
        return JAI.createRenderable("DFT", pb, hints);
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

