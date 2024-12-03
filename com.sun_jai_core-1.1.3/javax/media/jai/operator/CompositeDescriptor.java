/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.CompositeDestAlpha;
import javax.media.jai.operator.JaiI18N;

public class CompositeDescriptor
extends OperationDescriptorImpl {
    public static final CompositeDestAlpha NO_DESTINATION_ALPHA = new CompositeDestAlpha("NO_DESTINATION_ALPHA", 0);
    public static final CompositeDestAlpha DESTINATION_ALPHA_FIRST = new CompositeDestAlpha("DESTINATION_ALPHA_FIRST", 1);
    public static final CompositeDestAlpha DESTINATION_ALPHA_LAST = new CompositeDestAlpha("DESTINATION_ALPHA_LAST", 2);
    protected static final String[][] resources = new String[][]{{"GlobalName", "Composite"}, {"LocalName", "Composite"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("CompositeDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/CompositeDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion2")}, {"arg0Desc", JaiI18N.getString("CompositeDescriptor1")}, {"arg1Desc", JaiI18N.getString("CompositeDescriptor2")}, {"arg2Desc", JaiI18N.getString("CompositeDescriptor3")}, {"arg3Desc", JaiI18N.getString("CompositeDescriptor4")}};
    private static final Class[][] sourceClasses = new Class[][]{{class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = CompositeDescriptor.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage, class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = CompositeDescriptor.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage}, {class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = CompositeDescriptor.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage, class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = CompositeDescriptor.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage}};
    private static final Class[][] paramClasses = new Class[][]{{class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = CompositeDescriptor.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage, class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = CompositeDescriptor.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage, class$java$lang$Boolean == null ? (class$java$lang$Boolean = CompositeDescriptor.class$("java.lang.Boolean")) : class$java$lang$Boolean, class$javax$media$jai$operator$CompositeDestAlpha == null ? (class$javax$media$jai$operator$CompositeDestAlpha = CompositeDescriptor.class$("javax.media.jai.operator.CompositeDestAlpha")) : class$javax$media$jai$operator$CompositeDestAlpha}, {class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = CompositeDescriptor.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage, class$java$awt$image$renderable$RenderableImage == null ? (class$java$awt$image$renderable$RenderableImage = CompositeDescriptor.class$("java.awt.image.renderable.RenderableImage")) : class$java$awt$image$renderable$RenderableImage, class$java$lang$Boolean == null ? (class$java$lang$Boolean = CompositeDescriptor.class$("java.lang.Boolean")) : class$java$lang$Boolean, class$javax$media$jai$operator$CompositeDestAlpha == null ? (class$javax$media$jai$operator$CompositeDestAlpha = CompositeDescriptor.class$("javax.media.jai.operator.CompositeDestAlpha")) : class$javax$media$jai$operator$CompositeDestAlpha}};
    private static final String[] paramNames = new String[]{"source1Alpha", "source2Alpha", "alphaPremultiplied", "destAlpha"};
    private static final Object[][] paramDefaults = new Object[][]{{NO_PARAMETER_DEFAULT, null, Boolean.FALSE, NO_DESTINATION_ALPHA}, {NO_PARAMETER_DEFAULT, null, Boolean.FALSE, NO_DESTINATION_ALPHA}};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};
    static /* synthetic */ Class class$java$awt$image$RenderedImage;
    static /* synthetic */ Class class$java$awt$image$renderable$RenderableImage;
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$javax$media$jai$operator$CompositeDestAlpha;

    public CompositeDescriptor() {
        super(resources, supportedModes, null, sourceClasses, paramNames, paramClasses, paramDefaults, (Object[][])null);
    }

    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateArguments(modeName, args, msg)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        RenderedImage src1 = args.getRenderedSource(0);
        RenderedImage src2 = args.getRenderedSource(1);
        SampleModel s1sm = src1.getSampleModel();
        SampleModel s2sm = src2.getSampleModel();
        if (s1sm.getNumBands() != s2sm.getNumBands() || s1sm.getTransferType() != s2sm.getTransferType()) {
            msg.append(this.getName() + " " + JaiI18N.getString("CompositeDescriptor8"));
            return false;
        }
        RenderedImage afa1 = (RenderedImage)args.getObjectParameter(0);
        if (src1.getMinX() != afa1.getMinX() || src1.getMinY() != afa1.getMinY() || src1.getWidth() != afa1.getWidth() || src1.getHeight() != afa1.getHeight()) {
            msg.append(this.getName() + " " + JaiI18N.getString("CompositeDescriptor12"));
            return false;
        }
        SampleModel a1sm = afa1.getSampleModel();
        if (s1sm.getTransferType() != a1sm.getTransferType()) {
            msg.append(this.getName() + " " + JaiI18N.getString("CompositeDescriptor13"));
            return false;
        }
        RenderedImage afa2 = (RenderedImage)args.getObjectParameter(1);
        if (afa2 != null) {
            if (src2.getMinX() != afa2.getMinX() || src2.getMinY() != afa2.getMinY() || src2.getWidth() != afa2.getWidth() || src2.getHeight() != afa2.getHeight()) {
                msg.append(this.getName() + " " + JaiI18N.getString("CompositeDescriptor15"));
                return false;
            }
            SampleModel a2sm = afa2.getSampleModel();
            if (s2sm.getTransferType() != a2sm.getTransferType()) {
                msg.append(this.getName() + " " + JaiI18N.getString("CompositeDescriptor16"));
                return false;
            }
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, RenderedImage source1, RenderedImage source1Alpha, RenderedImage source2Alpha, Boolean alphaPremultiplied, CompositeDestAlpha destAlpha, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Composite", "rendered");
        pb.setSource("source0", source0);
        pb.setSource("source1", source1);
        pb.setParameter("source1Alpha", source1Alpha);
        pb.setParameter("source2Alpha", source2Alpha);
        pb.setParameter("alphaPremultiplied", alphaPremultiplied);
        pb.setParameter("destAlpha", destAlpha);
        return JAI.create("Composite", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, RenderableImage source1, RenderableImage source1Alpha, RenderableImage source2Alpha, Boolean alphaPremultiplied, CompositeDestAlpha destAlpha, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Composite", "renderable");
        pb.setSource("source0", source0);
        pb.setSource("source1", source1);
        pb.setParameter("source1Alpha", source1Alpha);
        pb.setParameter("source2Alpha", source2Alpha);
        pb.setParameter("alphaPremultiplied", alphaPremultiplied);
        pb.setParameter("destAlpha", destAlpha);
        return JAI.createRenderable("Composite", pb, hints);
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

