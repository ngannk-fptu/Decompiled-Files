/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import com.sun.media.jai.util.AreaOpPropertyGenerator;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;
import javax.media.jai.operator.MaxFilterShape;

public class MaxFilterDescriptor
extends OperationDescriptorImpl {
    public static final MaxFilterShape MAX_MASK_SQUARE = new MaxFilterShape("MAX_MASK_SQUARE", 1);
    public static final MaxFilterShape MAX_MASK_PLUS = new MaxFilterShape("MAX_MASK_PLUS", 2);
    public static final MaxFilterShape MAX_MASK_X = new MaxFilterShape("MAX_MASK_X", 3);
    public static final MaxFilterShape MAX_MASK_SQUARE_SEPARABLE = new MaxFilterShape("MAX_MASK_SQUARE_SEPARABLE", 4);
    private static final String[][] resources = new String[][]{{"GlobalName", "MaxFilter"}, {"LocalName", "MaxFilter"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("MaxFilterDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jaiapi/javax.media.jai.operator.MaxFilterDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion2")}, {"arg0Desc", JaiI18N.getString("MaxFilterDescriptor1")}, {"arg1Desc", JaiI18N.getString("MaxFilterDescriptor2")}};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$operator$MaxFilterShape == null ? (class$javax$media$jai$operator$MaxFilterShape = MaxFilterDescriptor.class$("javax.media.jai.operator.MaxFilterShape")) : class$javax$media$jai$operator$MaxFilterShape, class$java$lang$Integer == null ? (class$java$lang$Integer = MaxFilterDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer};
    private static final String[] paramNames = new String[]{"maskShape", "maskSize"};
    private static final Object[] paramDefaults = new Object[]{MAX_MASK_SQUARE, new Integer(3)};
    static /* synthetic */ Class class$javax$media$jai$operator$MaxFilterShape;
    static /* synthetic */ Class class$java$lang$Integer;

    public MaxFilterDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public Number getParamMinValue(int index) {
        if (index == 0) {
            return null;
        }
        if (index == 1) {
            return new Integer(1);
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public Number getParamMaxValue(int index) {
        if (index == 0) {
            return null;
        }
        if (index == 1) {
            return new Integer(Integer.MAX_VALUE);
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[]{new AreaOpPropertyGenerator()};
        return pg;
    }

    public static RenderedOp create(RenderedImage source0, MaxFilterShape maskShape, Integer maskSize, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("MaxFilter", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("maskShape", maskShape);
        pb.setParameter("maskSize", maskSize);
        return JAI.create("MaxFilter", pb, hints);
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

