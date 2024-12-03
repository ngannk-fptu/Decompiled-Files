/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PixelAccessor;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;
import javax.media.jai.operator.SubsampleBinaryToGrayPropertyGenerator;

public class SubsampleBinaryToGrayDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "SubsampleBinaryToGray"}, {"LocalName", "SubsampleBinaryToGray"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("SubsampleBinaryToGray0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/SubsampleBinaryToGrayDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("SubsampleBinaryToGray1")}, {"arg1Desc", JaiI18N.getString("SubsampleBinaryToGray2")}};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Float == null ? (class$java$lang$Float = SubsampleBinaryToGrayDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = SubsampleBinaryToGrayDescriptor.class$("java.lang.Float")) : class$java$lang$Float};
    private static final String[] paramNames = new String[]{"xScale", "yScale"};
    private static final Object[] paramDefaults = new Object[]{new Float(1.0f), new Float(1.0f)};
    static /* synthetic */ Class class$java$lang$Float;

    public SubsampleBinaryToGrayDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public boolean isRenderableSupported() {
        return true;
    }

    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[]{new SubsampleBinaryToGrayPropertyGenerator()};
        return pg;
    }

    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
        if (!super.validateParameters(args, msg)) {
            return false;
        }
        RenderedImage src = (RenderedImage)args.getSource(0);
        PixelAccessor srcPA = new PixelAccessor(src);
        if (!srcPA.isPacked || !srcPA.isMultiPixelPackedSM) {
            msg.append(this.getName() + " " + JaiI18N.getString("SubsampleBinaryToGray3"));
            return false;
        }
        float xScale = args.getFloatParameter(0);
        float yScale = args.getFloatParameter(1);
        if (xScale <= 0.0f || yScale <= 0.0f || xScale > 1.0f || yScale > 1.0f) {
            msg.append(this.getName() + " " + JaiI18N.getString("SubsampleBinaryToGray1") + " or " + JaiI18N.getString("SubsampleBinaryToGray2"));
            return false;
        }
        return true;
    }

    public Number getParamMinValue(int index) {
        if (index == 0 || index == 1) {
            return new Float(0.0f);
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public static RenderedOp create(RenderedImage source0, Float xScale, Float yScale, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("SubsampleBinaryToGray", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("xScale", xScale);
        pb.setParameter("yScale", yScale);
        return JAI.create("SubsampleBinaryToGray", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, Float xScale, Float yScale, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("SubsampleBinaryToGray", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("xScale", xScale);
        pb.setParameter("yScale", yScale);
        return JAI.createRenderable("SubsampleBinaryToGray", pb, hints);
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

