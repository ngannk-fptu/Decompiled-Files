/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.BorderExtender;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class RenderableDescriptor
extends OperationDescriptorImpl {
    private static final float[] DEFAULT_KERNEL_1D = new float[]{0.05f, 0.25f, 0.4f, 0.25f, 0.05f};
    private static final String[][] resources = new String[][]{{"GlobalName", "Renderable"}, {"LocalName", "Renderable"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("RenderableDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/RenderableDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("RenderableDescriptor1")}, {"arg1Desc", JaiI18N.getString("RenderableDescriptor2")}, {"arg2Desc", JaiI18N.getString("RenderableDescriptor3")}, {"arg3Desc", JaiI18N.getString("RenderableDescriptor4")}, {"arg4Desc", JaiI18N.getString("RenderableDescriptor5")}};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$RenderedOp == null ? (class$javax$media$jai$RenderedOp = RenderableDescriptor.class$("javax.media.jai.RenderedOp")) : class$javax$media$jai$RenderedOp, class$java$lang$Integer == null ? (class$java$lang$Integer = RenderableDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Float == null ? (class$java$lang$Float = RenderableDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = RenderableDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = RenderableDescriptor.class$("java.lang.Float")) : class$java$lang$Float};
    private static final String[] paramNames = new String[]{"downSampler", "maxLowResDim", "minX", "minY", "height"};
    private static final Object[] paramDefaults = new Object[]{null, new Integer(64), new Float(0.0f), new Float(0.0f), new Float(1.0f)};
    static /* synthetic */ Class class$javax$media$jai$RenderedOp;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class class$java$awt$image$RenderedImage;

    public RenderableDescriptor() {
        super(resources, null, new Class[]{class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = RenderableDescriptor.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage}, paramClasses, paramNames, paramDefaults);
    }

    public boolean isRenderedSupported() {
        return false;
    }

    public boolean isRenderableSupported() {
        return true;
    }

    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
        if (args.getNumParameters() == 0 || args.getObjectParameter(0) == null) {
            ParameterBlock pb = new ParameterBlock();
            KernelJAI kernel = new KernelJAI(DEFAULT_KERNEL_1D.length, DEFAULT_KERNEL_1D.length, DEFAULT_KERNEL_1D.length / 2, DEFAULT_KERNEL_1D.length / 2, DEFAULT_KERNEL_1D, DEFAULT_KERNEL_1D);
            pb.add(kernel);
            BorderExtender extender = BorderExtender.createInstance(1);
            RenderingHints hints = JAI.getDefaultInstance().getRenderingHints();
            if (hints == null) {
                hints = new RenderingHints(JAI.KEY_BORDER_EXTENDER, extender);
            } else {
                hints.put(JAI.KEY_BORDER_EXTENDER, extender);
            }
            RenderedOp filter = new RenderedOp("convolve", pb, hints);
            pb = new ParameterBlock();
            pb.addSource(filter);
            pb.add(new Float(0.5f)).add(new Float(0.5f));
            pb.add(new Float(0.0f)).add(new Float(0.0f));
            pb.add(Interpolation.getInstance(0));
            RenderedOp downSampler = new RenderedOp("scale", pb, null);
            args.set(downSampler, 0);
        }
        if (!super.validateParameters(args, msg)) {
            return false;
        }
        if (args.getIntParameter(1) <= 0) {
            msg.append(this.getName() + " " + JaiI18N.getString("RenderableDescriptor6"));
            return false;
        }
        if (args.getFloatParameter(4) <= 0.0f) {
            msg.append(this.getName() + " " + JaiI18N.getString("RenderableDescriptor7"));
            return false;
        }
        return true;
    }

    public static RenderableOp createRenderable(RenderedImage source0, RenderedOp downSampler, Integer maxLowResDim, Float minX, Float minY, Float height, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Renderable", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("downSampler", downSampler);
        pb.setParameter("maxLowResDim", maxLowResDim);
        pb.setParameter("minX", minX);
        pb.setParameter("minY", minY);
        pb.setParameter("height", height);
        return JAI.createRenderable("Renderable", pb, hints);
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

