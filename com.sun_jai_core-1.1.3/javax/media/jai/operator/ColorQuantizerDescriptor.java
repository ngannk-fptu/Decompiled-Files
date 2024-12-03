/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.ROI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.ColorQuantizerType;
import javax.media.jai.operator.JaiI18N;
import javax.media.jai.util.Range;

public class ColorQuantizerDescriptor
extends OperationDescriptorImpl {
    public static final ColorQuantizerType MEDIANCUT = new ColorQuantizerType("MEDIANCUT", 1);
    public static final ColorQuantizerType NEUQUANT = new ColorQuantizerType("NEUQUANT", 2);
    public static final ColorQuantizerType OCTTREE = new ColorQuantizerType("OCTTREE", 3);
    private static final String[][] resources = new String[][]{{"GlobalName", "ColorQuantizer"}, {"LocalName", "ColorQuantizer"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("ColorQuantizerDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ColorQuantizerDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion2")}, {"arg0Desc", JaiI18N.getString("ColorQuantizerDescriptor1")}, {"arg1Desc", JaiI18N.getString("ColorQuantizerDescriptor2")}, {"arg2Desc", JaiI18N.getString("ColorQuantizerDescriptor3")}, {"arg3Desc", JaiI18N.getString("ColorQuantizerDescriptor4")}, {"arg4Desc", JaiI18N.getString("ColorQuantizerDescriptor5")}, {"arg5Desc", JaiI18N.getString("ColorQuantizerDescriptor6")}};
    private static final String[] paramNames = new String[]{"quantizationAlgorithm", "maxColorNum", "upperBound", "roi", "xPeriod", "yPeriod"};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$operator$ColorQuantizerType == null ? (class$javax$media$jai$operator$ColorQuantizerType = ColorQuantizerDescriptor.class$("javax.media.jai.operator.ColorQuantizerType")) : class$javax$media$jai$operator$ColorQuantizerType, class$java$lang$Integer == null ? (class$java$lang$Integer = ColorQuantizerDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Integer == null ? (class$java$lang$Integer = ColorQuantizerDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$javax$media$jai$ROI == null ? (class$javax$media$jai$ROI = ColorQuantizerDescriptor.class$("javax.media.jai.ROI")) : class$javax$media$jai$ROI, class$java$lang$Integer == null ? (class$java$lang$Integer = ColorQuantizerDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Integer == null ? (class$java$lang$Integer = ColorQuantizerDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer};
    private static final Object[] paramDefaults = new Object[]{MEDIANCUT, new Integer(256), null, null, new Integer(1), new Integer(1)};
    private static final String[] supportedModes = new String[]{"rendered"};
    static /* synthetic */ Class class$javax$media$jai$operator$ColorQuantizerType;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$javax$media$jai$ROI;

    public ColorQuantizerDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    public Range getParamValueRange(int index) {
        switch (index) {
            case 1: 
            case 2: 
            case 4: 
            case 5: {
                return new Range(class$java$lang$Integer == null ? (class$java$lang$Integer = ColorQuantizerDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, new Integer(1), null);
            }
        }
        return null;
    }

    protected boolean validateParameters(String modeName, ParameterBlock args, StringBuffer msg) {
        if (args == null || msg == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!super.validateParameters(modeName, args, msg)) {
            return false;
        }
        ColorQuantizerType algorithm = (ColorQuantizerType)args.getObjectParameter(0);
        if (algorithm != MEDIANCUT && algorithm != NEUQUANT && algorithm != OCTTREE) {
            msg.append(this.getName() + " " + JaiI18N.getString("ColorQuantizerDescriptor7"));
            return false;
        }
        Integer secondOne = (Integer)args.getObjectParameter(2);
        if (secondOne == null) {
            int upperBound = 0;
            if (algorithm.equals(MEDIANCUT)) {
                upperBound = 32768;
            } else if (algorithm.equals(NEUQUANT)) {
                upperBound = 100;
            } else if (algorithm.equals(OCTTREE)) {
                upperBound = 65536;
            }
            args.set(upperBound, 2);
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, ColorQuantizerType algorithm, Integer maxColorNum, Integer upperBound, ROI roi, Integer xPeriod, Integer yPeriod, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("ColorQuantizer", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("quantizationAlgorithm", algorithm);
        pb.setParameter("maxColorNum", maxColorNum);
        pb.setParameter("upperBound", upperBound);
        pb.setParameter("roi", roi);
        pb.setParameter("xPeriod", xPeriod);
        pb.setParameter("yPeriod", yPeriod);
        return JAI.create("ColorQuantizer", pb, hints);
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

