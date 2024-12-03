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
import javax.media.jai.operator.MinFilterShape;

public class MinFilterDescriptor
extends OperationDescriptorImpl {
    public static final MinFilterShape MIN_MASK_SQUARE = new MinFilterShape("MIN_MASK_SQUARE", 1);
    public static final MinFilterShape MIN_MASK_PLUS = new MinFilterShape("MIN_MASK_PLUS", 2);
    public static final MinFilterShape MIN_MASK_X = new MinFilterShape("MIN_MASK_X", 3);
    public static final MinFilterShape MIN_MASK_SQUARE_SEPARABLE = new MinFilterShape("MIN_MASK_SQUARE_SEPARABLE", 4);
    private static final String[][] resources = new String[][]{{"GlobalName", "MinFilter"}, {"LocalName", "MinFilter"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("MinFilterDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jaiapi/javax.media.jai.operator.MinFilterDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion2")}, {"arg0Desc", JaiI18N.getString("MinFilterDescriptor1")}, {"arg1Desc", JaiI18N.getString("MinFilterDescriptor2")}};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$operator$MinFilterShape == null ? (class$javax$media$jai$operator$MinFilterShape = MinFilterDescriptor.class$("javax.media.jai.operator.MinFilterShape")) : class$javax$media$jai$operator$MinFilterShape, class$java$lang$Integer == null ? (class$java$lang$Integer = MinFilterDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer};
    private static final String[] paramNames = new String[]{"maskShape", "maskSize"};
    private static final Object[] paramDefaults = new Object[]{MIN_MASK_SQUARE, new Integer(3)};
    static /* synthetic */ Class class$javax$media$jai$operator$MinFilterShape;
    static /* synthetic */ Class class$java$lang$Integer;

    public MinFilterDescriptor() {
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

    public static RenderedOp create(RenderedImage source0, MinFilterShape maskShape, Integer maskSize, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("MinFilter", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("maskShape", maskShape);
        pb.setParameter("maskSize", maskSize);
        return JAI.create("MinFilter", pb, hints);
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

