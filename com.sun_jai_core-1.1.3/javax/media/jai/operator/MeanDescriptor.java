/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.ROI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class MeanDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Mean"}, {"LocalName", "Mean"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("MeanDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MeanDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("MeanDescriptor1")}, {"arg1Desc", JaiI18N.getString("MeanDescriptor2")}, {"arg2Desc", JaiI18N.getString("MeanDescriptor3")}};
    private static final String[] paramNames = new String[]{"roi", "xPeriod", "yPeriod"};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$ROI == null ? (class$javax$media$jai$ROI = MeanDescriptor.class$("javax.media.jai.ROI")) : class$javax$media$jai$ROI, class$java$lang$Integer == null ? (class$java$lang$Integer = MeanDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Integer == null ? (class$java$lang$Integer = MeanDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer};
    private static final Object[] paramDefaults = new Object[]{null, new Integer(1), new Integer(1)};
    static /* synthetic */ Class class$javax$media$jai$ROI;
    static /* synthetic */ Class class$java$lang$Integer;

    public MeanDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public Number getParamMinValue(int index) {
        if (index == 0) {
            return null;
        }
        if (index == 1 || index == 2) {
            return new Integer(1);
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static RenderedOp create(RenderedImage source0, ROI roi, Integer xPeriod, Integer yPeriod, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Mean", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("roi", roi);
        pb.setParameter("xPeriod", xPeriod);
        pb.setParameter("yPeriod", yPeriod);
        return JAI.create("Mean", pb, hints);
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

