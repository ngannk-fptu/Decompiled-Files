/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import com.sun.media.jai.util.AreaOpPropertyGenerator;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class UnsharpMaskDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "UnsharpMask"}, {"LocalName", "UnsharpMask"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("UnsharpMaskDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/UnsharpMaskDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("UnsharpMaskDescriptor1")}, {"arg1Desc", JaiI18N.getString("UnsharpMaskDescriptor2")}};
    private static final String[] paramNames = new String[]{"kernel", "gain"};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$KernelJAI == null ? (class$javax$media$jai$KernelJAI = UnsharpMaskDescriptor.class$("javax.media.jai.KernelJAI")) : class$javax$media$jai$KernelJAI, class$java$lang$Float == null ? (class$java$lang$Float = UnsharpMaskDescriptor.class$("java.lang.Float")) : class$java$lang$Float};
    private static final Object[] paramDefaults = new Object[]{new KernelJAI(3, 3, 1, 1, new float[]{0.11111111f, 0.11111111f, 0.11111111f, 0.11111111f, 0.11111111f, 0.11111111f, 0.11111111f, 0.11111111f, 0.11111111f}), new Float(1.0f)};
    static /* synthetic */ Class class$javax$media$jai$KernelJAI;
    static /* synthetic */ Class class$java$lang$Float;

    public UnsharpMaskDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[]{new AreaOpPropertyGenerator()};
        return pg;
    }

    public static RenderedOp create(RenderedImage source0, KernelJAI kernel, Float gain, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("UnsharpMask", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("kernel", kernel);
        pb.setParameter("gain", gain);
        return JAI.create("UnsharpMask", pb, hints);
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

