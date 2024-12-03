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

public class DilateDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Dilate"}, {"LocalName", "Dilate"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("DilateDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jaiapi/<br>javax.media.jai.operator.DilateDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("DilateDescriptor1")}};
    private static final String[] paramNames = new String[]{"kernel"};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$KernelJAI == null ? (class$javax$media$jai$KernelJAI = DilateDescriptor.class$("javax.media.jai.KernelJAI")) : class$javax$media$jai$KernelJAI};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT};
    static /* synthetic */ Class class$javax$media$jai$KernelJAI;

    public DilateDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[]{new AreaOpPropertyGenerator()};
        return pg;
    }

    public static RenderedOp create(RenderedImage source0, KernelJAI kernel, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Dilate", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("kernel", kernel);
        return JAI.create("Dilate", pb, hints);
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

