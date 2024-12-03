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
import javax.media.jai.Warp;
import javax.media.jai.operator.JaiI18N;
import javax.media.jai.operator.WarpPropertyGenerator;

public class WarpDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Warp"}, {"LocalName", "Warp"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("WarpDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/WarpDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("WarpDescriptor1")}, {"arg1Desc", JaiI18N.getString("WarpDescriptor2")}, {"arg2Desc", JaiI18N.getString("WarpDescriptor3")}};
    private static final String[] paramNames = new String[]{"warp", "interpolation", "backgroundValues"};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$Warp == null ? (class$javax$media$jai$Warp = WarpDescriptor.class$("javax.media.jai.Warp")) : class$javax$media$jai$Warp, class$javax$media$jai$Interpolation == null ? (class$javax$media$jai$Interpolation = WarpDescriptor.class$("javax.media.jai.Interpolation")) : class$javax$media$jai$Interpolation, array$D == null ? (array$D = WarpDescriptor.class$("[D")) : array$D};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT, Interpolation.getInstance(0), new double[]{0.0}};
    static /* synthetic */ Class class$javax$media$jai$Warp;
    static /* synthetic */ Class class$javax$media$jai$Interpolation;
    static /* synthetic */ Class array$D;

    public WarpDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[]{new WarpPropertyGenerator()};
        return pg;
    }

    public static RenderedOp create(RenderedImage source0, Warp warp, Interpolation interpolation, double[] backgroundValues, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Warp", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("warp", warp);
        pb.setParameter("interpolation", interpolation);
        pb.setParameter("backgroundValues", backgroundValues);
        return JAI.create("Warp", pb, hints);
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

