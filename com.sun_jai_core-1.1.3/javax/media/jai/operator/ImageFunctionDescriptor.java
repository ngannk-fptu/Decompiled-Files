/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import javax.media.jai.ImageFunction;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ImageFunctionPropertyGenerator;
import javax.media.jai.operator.JaiI18N;

public class ImageFunctionDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "ImageFunction"}, {"LocalName", "ImageFunction"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("ImageFunctionDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ImageFunctionDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("ImageFunctionDescriptor1")}, {"arg1Desc", JaiI18N.getString("ImageFunctionDescriptor2")}, {"arg2Desc", JaiI18N.getString("ImageFunctionDescriptor3")}, {"arg3Desc", JaiI18N.getString("ImageFunctionDescriptor4")}, {"arg4Desc", JaiI18N.getString("ImageFunctionDescriptor5")}, {"arg5Desc", JaiI18N.getString("ImageFunctionDescriptor6")}, {"arg6Desc", JaiI18N.getString("ImageFunctionDescriptor7")}};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$ImageFunction == null ? (class$javax$media$jai$ImageFunction = ImageFunctionDescriptor.class$("javax.media.jai.ImageFunction")) : class$javax$media$jai$ImageFunction, class$java$lang$Integer == null ? (class$java$lang$Integer = ImageFunctionDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Integer == null ? (class$java$lang$Integer = ImageFunctionDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Float == null ? (class$java$lang$Float = ImageFunctionDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = ImageFunctionDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = ImageFunctionDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = ImageFunctionDescriptor.class$("java.lang.Float")) : class$java$lang$Float};
    private static final String[] paramNames = new String[]{"function", "width", "height", "xScale", "yScale", "xTrans", "yTrans"};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, new Float(1.0f), new Float(1.0f), new Float(0.0f), new Float(0.0f)};
    static /* synthetic */ Class class$javax$media$jai$ImageFunction;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Float;

    public ImageFunctionDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[]{new ImageFunctionPropertyGenerator()};
        return pg;
    }

    public static RenderedOp create(ImageFunction function, Integer width, Integer height, Float xScale, Float yScale, Float xTrans, Float yTrans, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("ImageFunction", "rendered");
        pb.setParameter("function", function);
        pb.setParameter("width", width);
        pb.setParameter("height", height);
        pb.setParameter("xScale", xScale);
        pb.setParameter("yScale", yScale);
        pb.setParameter("xTrans", xTrans);
        pb.setParameter("yTrans", yTrans);
        return JAI.create("ImageFunction", pb, hints);
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

