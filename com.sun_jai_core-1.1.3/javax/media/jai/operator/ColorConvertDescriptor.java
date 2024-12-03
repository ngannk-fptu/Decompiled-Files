/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class ColorConvertDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "ColorConvert"}, {"LocalName", "ColorConvert"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("ColorConvertDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/ColorConvertDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion2")}, {"arg0Desc", JaiI18N.getString("ColorConvertDescriptor1")}};
    private static final Class[] paramClasses = new Class[]{class$java$awt$image$ColorModel == null ? (class$java$awt$image$ColorModel = ColorConvertDescriptor.class$("java.awt.image.ColorModel")) : class$java$awt$image$ColorModel};
    private static final String[] paramNames = new String[]{"colorModel"};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT};
    static /* synthetic */ Class class$java$awt$image$ColorModel;

    public ColorConvertDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public boolean isRenderableSupported() {
        return true;
    }

    public static RenderedOp create(RenderedImage source0, ColorModel colorModel, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("ColorConvert", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("colorModel", colorModel);
        return JAI.create("ColorConvert", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, ColorModel colorModel, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("ColorConvert", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("colorModel", colorModel);
        return JAI.createRenderable("ColorConvert", pb, hints);
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

