/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;
import javax.media.jai.util.Range;

public class ConstantDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Constant"}, {"LocalName", "Constant"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("ConstantDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ConstantDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("ConstantDescriptor1")}, {"arg1Desc", JaiI18N.getString("ConstantDescriptor2")}, {"arg2Desc", JaiI18N.getString("ConstantDescriptor3")}};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Float == null ? (class$java$lang$Float = ConstantDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = ConstantDescriptor.class$("java.lang.Float")) : class$java$lang$Float, array$Ljava$lang$Number == null ? (array$Ljava$lang$Number = ConstantDescriptor.class$("[Ljava.lang.Number;")) : array$Ljava$lang$Number};
    private static final String[] paramNames = new String[]{"width", "height", "bandValues"};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};
    private static final Object[] validParamValues = new Object[]{new Range(class$java$lang$Float == null ? (class$java$lang$Float = ConstantDescriptor.class$("java.lang.Float")) : class$java$lang$Float, new Float(0.0f), false, null, false), new Range(class$java$lang$Float == null ? (class$java$lang$Float = ConstantDescriptor.class$("java.lang.Float")) : class$java$lang$Float, new Float(0.0f), false, null, false), null};
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class array$Ljava$lang$Number;

    public ConstantDescriptor() {
        super(resources, supportedModes, 0, paramNames, paramClasses, paramDefaults, validParamValues);
    }

    protected boolean validateParameters(String modeName, ParameterBlock args, StringBuffer message) {
        if (!super.validateParameters(modeName, args, message)) {
            return false;
        }
        int length = ((Number[])args.getObjectParameter(2)).length;
        if (length < 1) {
            message.append(this.getName() + " " + JaiI18N.getString("ConstantDescriptor4"));
            return false;
        }
        if (modeName.equalsIgnoreCase("rendered")) {
            int width = Math.round(args.getFloatParameter(0));
            int height = Math.round(args.getFloatParameter(1));
            if (width < 1 || height < 1) {
                message.append(this.getName() + " " + JaiI18N.getString("ConstantDescriptor5"));
                return false;
            }
        } else if (modeName.equalsIgnoreCase("renderable")) {
            float width = args.getFloatParameter(0);
            float height = args.getFloatParameter(1);
            if (width <= 0.0f || height <= 0.0f) {
                message.append(this.getName() + " " + JaiI18N.getString("ConstantDescriptor6"));
                return false;
            }
        }
        return true;
    }

    public static RenderedOp create(Float width, Float height, Number[] bandValues, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Constant", "rendered");
        pb.setParameter("width", width);
        pb.setParameter("height", height);
        pb.setParameter("bandValues", bandValues);
        return JAI.create("Constant", pb, hints);
    }

    public static RenderableOp createRenderable(Float width, Float height, Number[] bandValues, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Constant", "renderable");
        pb.setParameter("width", width);
        pb.setParameter("height", height);
        pb.setParameter("bandValues", bandValues);
        return JAI.createRenderable("Constant", pb, hints);
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

