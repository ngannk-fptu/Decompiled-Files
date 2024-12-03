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
import javax.media.jai.operator.TranslatePropertyGenerator;

public class TranslateDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Translate"}, {"LocalName", "Translate"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("TranslateDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/TranslateDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("TranslateDescriptor1")}, {"arg1Desc", JaiI18N.getString("TranslateDescriptor2")}, {"arg2Desc", JaiI18N.getString("TranslateDescriptor3")}};
    private static final String[] paramNames = new String[]{"xTrans", "yTrans", "interpolation"};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Float == null ? (class$java$lang$Float = TranslateDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = TranslateDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$javax$media$jai$Interpolation == null ? (class$javax$media$jai$Interpolation = TranslateDescriptor.class$("javax.media.jai.Interpolation")) : class$javax$media$jai$Interpolation};
    private static final Object[] paramDefaults = new Object[]{new Float(0.0f), new Float(0.0f), Interpolation.getInstance(0)};
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$javax$media$jai$Interpolation;

    public TranslateDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public boolean isRenderableSupported() {
        return true;
    }

    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[]{new TranslatePropertyGenerator()};
        return pg;
    }

    public static RenderedOp create(RenderedImage source0, Float xTrans, Float yTrans, Interpolation interpolation, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Translate", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("xTrans", xTrans);
        pb.setParameter("yTrans", yTrans);
        pb.setParameter("interpolation", interpolation);
        return JAI.create("Translate", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, Float xTrans, Float yTrans, Interpolation interpolation, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Translate", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("xTrans", xTrans);
        pb.setParameter("yTrans", yTrans);
        pb.setParameter("interpolation", interpolation);
        return JAI.createRenderable("Translate", pb, hints);
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

