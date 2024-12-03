/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class ErrorDiffusionDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "ErrorDiffusion"}, {"LocalName", "ErrorDiffusion"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("ErrorDiffusionDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ErrorDiffusionDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("ErrorDiffusionDescriptor1")}, {"arg1Desc", JaiI18N.getString("ErrorDiffusionDescriptor2")}};
    private static final String[] paramNames = new String[]{"colorMap", "errorKernel"};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$LookupTableJAI == null ? (class$javax$media$jai$LookupTableJAI = ErrorDiffusionDescriptor.class$("javax.media.jai.LookupTableJAI")) : class$javax$media$jai$LookupTableJAI, class$javax$media$jai$KernelJAI == null ? (class$javax$media$jai$KernelJAI = ErrorDiffusionDescriptor.class$("javax.media.jai.KernelJAI")) : class$javax$media$jai$KernelJAI};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT, KernelJAI.ERROR_FILTER_FLOYD_STEINBERG};
    static /* synthetic */ Class class$javax$media$jai$LookupTableJAI;
    static /* synthetic */ Class class$javax$media$jai$KernelJAI;

    public ErrorDiffusionDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public static RenderedOp create(RenderedImage source0, LookupTableJAI colorMap, KernelJAI errorKernel, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("ErrorDiffusion", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("colorMap", colorMap);
        pb.setParameter("errorKernel", errorKernel);
        return JAI.create("ErrorDiffusion", pb, hints);
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

