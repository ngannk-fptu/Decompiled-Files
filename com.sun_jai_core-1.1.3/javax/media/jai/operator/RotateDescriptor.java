/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;
import javax.media.jai.operator.RotatePropertyGenerator;

public class RotateDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Rotate"}, {"LocalName", "Rotate"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("RotateDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/RotateDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("RotateDescriptor1")}, {"arg1Desc", JaiI18N.getString("RotateDescriptor2")}, {"arg2Desc", JaiI18N.getString("RotateDescriptor3")}, {"arg3Desc", JaiI18N.getString("RotateDescriptor4")}, {"arg4Desc", JaiI18N.getString("RotateDescriptor5")}};
    private static final String[] paramNames = new String[]{"xOrigin", "yOrigin", "angle", "interpolation", "backgroundValues"};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Float == null ? (class$java$lang$Float = RotateDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = RotateDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = RotateDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$javax$media$jai$Interpolation == null ? (class$javax$media$jai$Interpolation = RotateDescriptor.class$("javax.media.jai.Interpolation")) : class$javax$media$jai$Interpolation, array$D == null ? (array$D = RotateDescriptor.class$("[D")) : array$D};
    private static final Object[] paramDefaults = new Object[]{new Float(0.0f), new Float(0.0f), new Float(0.0f), Interpolation.getInstance(0), new double[]{0.0}};
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$javax$media$jai$Interpolation;
    static /* synthetic */ Class array$D;

    public RotateDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public boolean isRenderableSupported() {
        return true;
    }

    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[]{new RotatePropertyGenerator()};
        return pg;
    }

    public static RenderedOp create(RenderedImage source0, Float xOrigin, Float yOrigin, Float angle, Interpolation interpolation, double[] backgroundValues, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Rotate", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("xOrigin", xOrigin);
        pb.setParameter("yOrigin", yOrigin);
        pb.setParameter("angle", angle);
        pb.setParameter("interpolation", interpolation);
        pb.setParameter("backgroundValues", backgroundValues);
        return JAI.create("Rotate", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, Float xOrigin, Float yOrigin, Float angle, Interpolation interpolation, double[] backgroundValues, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Rotate", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("xOrigin", xOrigin);
        pb.setParameter("yOrigin", yOrigin);
        pb.setParameter("angle", angle);
        pb.setParameter("interpolation", interpolation);
        pb.setParameter("backgroundValues", backgroundValues);
        return JAI.createRenderable("Rotate", pb, hints);
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

