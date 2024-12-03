/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.ColorCube;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class OrderedDitherDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "OrderedDither"}, {"LocalName", "OrderedDither"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("OrderedDitherDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/OrderedDitherDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("OrderedDitherDescriptor1")}, {"arg1Desc", JaiI18N.getString("OrderedDitherDescriptor2")}};
    private static final String[] paramNames = new String[]{"colorMap", "ditherMask"};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$ColorCube == null ? (class$javax$media$jai$ColorCube = OrderedDitherDescriptor.class$("javax.media.jai.ColorCube")) : class$javax$media$jai$ColorCube, array$Ljavax$media$jai$KernelJAI == null ? (array$Ljavax$media$jai$KernelJAI = OrderedDitherDescriptor.class$("[Ljavax.media.jai.KernelJAI;")) : array$Ljavax$media$jai$KernelJAI};
    private static final Object[] paramDefaults = new Object[]{ColorCube.BYTE_496, KernelJAI.DITHER_MASK_443};
    private static final String[] supportedModes = new String[]{"rendered"};
    static /* synthetic */ Class class$javax$media$jai$ColorCube;
    static /* synthetic */ Class array$Ljavax$media$jai$KernelJAI;

    private static boolean isValidColorMap(RenderedImage sourceImage, ColorCube colorMap, StringBuffer msg) {
        SampleModel srcSampleModel = sourceImage.getSampleModel();
        if (colorMap.getDataType() != srcSampleModel.getTransferType()) {
            msg.append(JaiI18N.getString("OrderedDitherDescriptor3"));
            return false;
        }
        if (colorMap.getNumBands() != srcSampleModel.getNumBands()) {
            msg.append(JaiI18N.getString("OrderedDitherDescriptor4"));
            return false;
        }
        return true;
    }

    private static boolean isValidDitherMask(RenderedImage sourceImage, KernelJAI[] ditherMask, StringBuffer msg) {
        if (ditherMask.length != sourceImage.getSampleModel().getNumBands()) {
            msg.append(JaiI18N.getString("OrderedDitherDescriptor5"));
            return false;
        }
        int maskWidth = ditherMask[0].getWidth();
        int maskHeight = ditherMask[0].getHeight();
        for (int band = 0; band < ditherMask.length; ++band) {
            if (ditherMask[band].getWidth() != maskWidth || ditherMask[band].getHeight() != maskHeight) {
                msg.append(JaiI18N.getString("OrderedDitherDescriptor6"));
                return false;
            }
            float[] kernelData = ditherMask[band].getKernelData();
            for (int i = 0; i < kernelData.length; ++i) {
                if (!(kernelData[i] < 0.0f) && !((double)kernelData[i] > 1.0)) continue;
                msg.append(JaiI18N.getString("OrderedDitherDescriptor7"));
                return false;
            }
        }
        return true;
    }

    public OrderedDitherDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateArguments(modeName, args, msg)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        RenderedImage src = args.getRenderedSource(0);
        ColorCube colorMap = (ColorCube)args.getObjectParameter(0);
        KernelJAI[] ditherMask = (KernelJAI[])args.getObjectParameter(1);
        if (!OrderedDitherDescriptor.isValidColorMap(src, colorMap, msg)) {
            return false;
        }
        return OrderedDitherDescriptor.isValidDitherMask(src, ditherMask, msg);
    }

    public static RenderedOp create(RenderedImage source0, ColorCube colorMap, KernelJAI[] ditherMask, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("OrderedDither", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("colorMap", colorMap);
        pb.setParameter("ditherMask", ditherMask);
        return JAI.create("OrderedDither", pb, hints);
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

