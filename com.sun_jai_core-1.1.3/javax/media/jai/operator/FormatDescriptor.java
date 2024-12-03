/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class FormatDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Format"}, {"LocalName", "Format"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("FormatDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/FormatDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", "The output data type (from java.awt.image.DataBuffer)."}};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Integer == null ? (class$java$lang$Integer = FormatDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer};
    private static final String[] paramNames = new String[]{"dataType"};
    private static final Object[] paramDefaults = new Object[]{new Integer(0)};
    static /* synthetic */ Class class$java$lang$Integer;

    public FormatDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public boolean isRenderableSupported() {
        return true;
    }

    public Number getParamMinValue(int index) {
        if (index == 0) {
            return new Integer(0);
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public Number getParamMaxValue(int index) {
        if (index == 0) {
            return new Integer(5);
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static RenderedOp create(RenderedImage source0, Integer dataType, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Format", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("dataType", dataType);
        return JAI.create("Format", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, Integer dataType, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Format", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("dataType", dataType);
        return JAI.createRenderable("Format", pb, hints);
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

