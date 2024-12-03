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

public class ExtremaDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Extrema"}, {"LocalName", "Extrema"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("ExtremaDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ExtremaDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("ExtremaDescriptor1")}, {"arg1Desc", JaiI18N.getString("ExtremaDescriptor2")}, {"arg2Desc", JaiI18N.getString("ExtremaDescriptor3")}, {"arg3Desc", JaiI18N.getString("ExtremaDescriptor4")}, {"arg4Desc", JaiI18N.getString("ExtremaDescriptor5")}};
    private static final String[] paramNames = new String[]{"roi", "xPeriod", "yPeriod", "saveLocations", "maxRuns"};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$ROI == null ? (class$javax$media$jai$ROI = ExtremaDescriptor.class$("javax.media.jai.ROI")) : class$javax$media$jai$ROI, class$java$lang$Integer == null ? (class$java$lang$Integer = ExtremaDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Integer == null ? (class$java$lang$Integer = ExtremaDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Boolean == null ? (class$java$lang$Boolean = ExtremaDescriptor.class$("java.lang.Boolean")) : class$java$lang$Boolean, class$java$lang$Integer == null ? (class$java$lang$Integer = ExtremaDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer};
    private static final Object[] paramDefaults = new Object[]{null, new Integer(1), new Integer(1), Boolean.FALSE, new Integer(1)};
    static /* synthetic */ Class class$javax$media$jai$ROI;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Boolean;

    public ExtremaDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public Number getParamMinValue(int index) {
        if (index == 0 || index == 3) {
            return null;
        }
        if (index == 1 || index == 2 || index == 4) {
            return new Integer(1);
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static RenderedOp create(RenderedImage source0, ROI roi, Integer xPeriod, Integer yPeriod, Boolean saveLocations, Integer maxRuns, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Extrema", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("roi", roi);
        pb.setParameter("xPeriod", xPeriod);
        pb.setParameter("yPeriod", yPeriod);
        pb.setParameter("saveLocations", saveLocations);
        pb.setParameter("maxRuns", maxRuns);
        return JAI.create("Extrema", pb, hints);
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

