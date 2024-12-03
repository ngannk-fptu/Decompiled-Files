/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.ROI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;
import javax.media.jai.operator.MosaicType;

public class MosaicDescriptor
extends OperationDescriptorImpl {
    public static final MosaicType MOSAIC_TYPE_BLEND = new MosaicType("MOSAIC_TYPE_BLEND", 1);
    public static final MosaicType MOSAIC_TYPE_OVERLAY = new MosaicType("MOSAIC_TYPE_OVERLAY", 0);
    private static final String[][] resources = new String[][]{{"GlobalName", "Mosaic"}, {"LocalName", "Mosaic"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("MosaicDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MosaicDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("MosaicDescriptor1")}, {"arg1Desc", JaiI18N.getString("MosaicDescriptor2")}, {"arg2Desc", JaiI18N.getString("MosaicDescriptor3")}, {"arg3Desc", JaiI18N.getString("MosaicDescriptor4")}, {"arg4Desc", JaiI18N.getString("MosaicDescriptor5")}};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$operator$MosaicType == null ? (class$javax$media$jai$operator$MosaicType = MosaicDescriptor.class$("javax.media.jai.operator.MosaicType")) : class$javax$media$jai$operator$MosaicType, array$Ljavax$media$jai$PlanarImage == null ? (array$Ljavax$media$jai$PlanarImage = MosaicDescriptor.class$("[Ljavax.media.jai.PlanarImage;")) : array$Ljavax$media$jai$PlanarImage, array$Ljavax$media$jai$ROI == null ? (array$Ljavax$media$jai$ROI = MosaicDescriptor.class$("[Ljavax.media.jai.ROI;")) : array$Ljavax$media$jai$ROI, array$$D == null ? (array$$D = MosaicDescriptor.class$("[[D")) : array$$D, array$D == null ? (array$D = MosaicDescriptor.class$("[D")) : array$D};
    private static final String[] paramNames = new String[]{"mosaicType", "sourceAlpha", "sourceROI", "sourceThreshold", "backgroundValues"};
    private static final Object[] paramDefaults = new Object[]{MOSAIC_TYPE_OVERLAY, null, null, new double[][]{{1.0}}, new double[]{0.0}};
    static /* synthetic */ Class class$javax$media$jai$operator$MosaicType;
    static /* synthetic */ Class array$Ljavax$media$jai$PlanarImage;
    static /* synthetic */ Class array$Ljavax$media$jai$ROI;
    static /* synthetic */ Class array$$D;
    static /* synthetic */ Class array$D;

    public MosaicDescriptor() {
        super(resources, new String[]{"rendered"}, 0, paramNames, paramClasses, paramDefaults, null);
    }

    public static RenderedOp create(RenderedImage[] sources, MosaicType mosaicType, PlanarImage[] sourceAlpha, ROI[] sourceROI, double[][] sourceThreshold, double[] backgroundValues, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Mosaic", "rendered");
        int numSources = sources.length;
        for (int i = 0; i < numSources; ++i) {
            pb.addSource(sources[i]);
        }
        pb.setParameter("mosaicType", mosaicType);
        pb.setParameter("sourceAlpha", sourceAlpha);
        pb.setParameter("sourceROI", sourceROI);
        pb.setParameter("sourceThreshold", sourceThreshold);
        pb.setParameter("backgroundValues", backgroundValues);
        return JAI.create("Mosaic", pb, hints);
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

