/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;
import javax.media.jai.operator.ShearDir;
import javax.media.jai.operator.ShearPropertyGenerator;

public class ShearDescriptor
extends OperationDescriptorImpl {
    public static final ShearDir SHEAR_HORIZONTAL = new ShearDir("SHEAR_HORIZONTAL", 0);
    public static final ShearDir SHEAR_VERTICAL = new ShearDir("SHEAR_VERTICAL", 1);
    private static final String[][] resources = new String[][]{{"GlobalName", "Shear"}, {"LocalName", "Shear"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("ShearDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ShearDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion2")}, {"arg0Desc", JaiI18N.getString("ShearDescriptor1")}, {"arg1Desc", JaiI18N.getString("ShearDescriptor2")}, {"arg2Desc", JaiI18N.getString("ShearDescriptor3")}, {"arg3Desc", JaiI18N.getString("ShearDescriptor4")}, {"arg4Desc", JaiI18N.getString("ShearDescriptor5")}, {"arg5Desc", JaiI18N.getString("ShearDescriptor6")}};
    private static final String[] paramNames = new String[]{"shear", "shearDir", "xTrans", "yTrans", "interpolation", "backgroundValues"};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Float == null ? (class$java$lang$Float = ShearDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$javax$media$jai$operator$ShearDir == null ? (class$javax$media$jai$operator$ShearDir = ShearDescriptor.class$("javax.media.jai.operator.ShearDir")) : class$javax$media$jai$operator$ShearDir, class$java$lang$Float == null ? (class$java$lang$Float = ShearDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = ShearDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$javax$media$jai$Interpolation == null ? (class$javax$media$jai$Interpolation = ShearDescriptor.class$("javax.media.jai.Interpolation")) : class$javax$media$jai$Interpolation, array$D == null ? (array$D = ShearDescriptor.class$("[D")) : array$D};
    private static final Object[] paramDefaults = new Object[]{new Float(0.0f), SHEAR_HORIZONTAL, new Float(0.0f), new Float(0.0f), Interpolation.getInstance(0), new double[]{0.0}};
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$javax$media$jai$operator$ShearDir;
    static /* synthetic */ Class class$javax$media$jai$Interpolation;
    static /* synthetic */ Class array$D;

    public ShearDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[]{new ShearPropertyGenerator()};
        return pg;
    }

    public static RenderedOp create(RenderedImage source0, Float shear, ShearDir shearDir, Float xTrans, Float yTrans, Interpolation interpolation, double[] backgroundValues, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Shear", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("shear", shear);
        pb.setParameter("shearDir", shearDir);
        pb.setParameter("xTrans", xTrans);
        pb.setParameter("yTrans", yTrans);
        pb.setParameter("interpolation", interpolation);
        pb.setParameter("backgroundValues", backgroundValues);
        return JAI.create("Shear", pb, hints);
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

