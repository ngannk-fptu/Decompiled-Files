/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;
import javax.media.jai.operator.ScalePropertyGenerator;

public class ScaleDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Scale"}, {"LocalName", "Scale"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("ScaleDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ScaleDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("ScaleDescriptor1")}, {"arg1Desc", JaiI18N.getString("ScaleDescriptor2")}, {"arg2Desc", JaiI18N.getString("ScaleDescriptor3")}, {"arg3Desc", JaiI18N.getString("ScaleDescriptor4")}, {"arg4Desc", JaiI18N.getString("ScaleDescriptor5")}};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Float == null ? (class$java$lang$Float = ScaleDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = ScaleDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = ScaleDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = ScaleDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$javax$media$jai$Interpolation == null ? (class$javax$media$jai$Interpolation = ScaleDescriptor.class$("javax.media.jai.Interpolation")) : class$javax$media$jai$Interpolation};
    private static final String[] paramNames = new String[]{"xScale", "yScale", "xTrans", "yTrans", "interpolation"};
    private static final Object[] paramDefaults = new Object[]{new Float(1.0f), new Float(1.0f), new Float(0.0f), new Float(0.0f), Interpolation.getInstance(0)};
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$javax$media$jai$Interpolation;

    public ScaleDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public boolean isRenderableSupported() {
        return true;
    }

    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[]{new ScalePropertyGenerator()};
        return pg;
    }

    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
        if (!super.validateParameters(args, msg)) {
            return false;
        }
        float xScale = args.getFloatParameter(0);
        float yScale = args.getFloatParameter(1);
        if (xScale <= 0.0f || yScale <= 0.0f) {
            msg.append(this.getName() + " " + JaiI18N.getString("ScaleDescriptor6"));
            return false;
        }
        return true;
    }

    public Number getParamMinValue(int index) {
        if (index == 0 || index == 1) {
            return new Float(0.0f);
        }
        if (index == 2 || index == 3) {
            return new Float(-3.4028235E38f);
        }
        if (index == 4) {
            return null;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static RenderedOp create(RenderedImage source0, Float xScale, Float yScale, Float xTrans, Float yTrans, Interpolation interpolation, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Scale", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("xScale", xScale);
        pb.setParameter("yScale", yScale);
        pb.setParameter("xTrans", xTrans);
        pb.setParameter("yTrans", yTrans);
        pb.setParameter("interpolation", interpolation);
        return JAI.create("Scale", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, Float xScale, Float yScale, Float xTrans, Float yTrans, Interpolation interpolation, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Scale", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("xScale", xScale);
        pb.setParameter("yScale", yScale);
        pb.setParameter("xTrans", xTrans);
        pb.setParameter("yTrans", yTrans);
        pb.setParameter("interpolation", interpolation);
        return JAI.createRenderable("Scale", pb, hints);
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

