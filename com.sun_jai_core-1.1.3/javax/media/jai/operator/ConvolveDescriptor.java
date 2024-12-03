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

public class ConvolveDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Convolve"}, {"LocalName", "Convolve"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("ConvolveDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ConvolveDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("ConvolveDescriptor1")}};
    private static final String[] paramNames = new String[]{"kernel"};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$KernelJAI == null ? (class$javax$media$jai$KernelJAI = ConvolveDescriptor.class$("javax.media.jai.KernelJAI")) : class$javax$media$jai$KernelJAI};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT};
    static /* synthetic */ Class class$javax$media$jai$KernelJAI;

    public ConvolveDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[]{new AreaOpPropertyGenerator()};
        return pg;
    }

    public static RenderedOp create(RenderedImage source0, KernelJAI kernel, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Convolve", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("kernel", kernel);
        return JAI.create("Convolve", pb, hints);
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

