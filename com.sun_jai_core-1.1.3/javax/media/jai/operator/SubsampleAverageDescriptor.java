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
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;
import javax.media.jai.operator.SubsampleAveragePropertyGenerator;
import javax.media.jai.util.Range;

public class SubsampleAverageDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "SubsampleAverage"}, {"LocalName", "SubsampleAverage"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("SubsampleAverageDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/SubsampleAverageDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("SubsampleAverageDescriptor1")}, {"arg1Desc", JaiI18N.getString("SubsampleAverageDescriptor2")}};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Double == null ? (class$java$lang$Double = SubsampleAverageDescriptor.class$("java.lang.Double")) : class$java$lang$Double, class$java$lang$Double == null ? (class$java$lang$Double = SubsampleAverageDescriptor.class$("java.lang.Double")) : class$java$lang$Double};
    private static final String[] paramNames = new String[]{"scaleX", "scaleY"};
    private static final Object[] paramDefaults = new Object[]{new Double(0.5), null};
    private static final Object[] validParamValues = new Object[]{new Range(class$java$lang$Double == null ? (class$java$lang$Double = SubsampleAverageDescriptor.class$("java.lang.Double")) : class$java$lang$Double, new Double(Double.MIN_VALUE), new Double(1.0)), new Range(class$java$lang$Double == null ? (class$java$lang$Double = SubsampleAverageDescriptor.class$("java.lang.Double")) : class$java$lang$Double, new Double(Double.MIN_VALUE), new Double(1.0))};
    static /* synthetic */ Class class$java$lang$Double;

    public SubsampleAverageDescriptor() {
        super(resources, new String[]{"rendered", "renderable"}, 1, paramNames, paramClasses, paramDefaults, validParamValues);
    }

    public PropertyGenerator[] getPropertyGenerators(String modeName) {
        if (modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("SubsampleAverageDescriptor3"));
        }
        if (!"rendered".equalsIgnoreCase(modeName)) {
            PropertyGenerator[] pg = new PropertyGenerator[]{new SubsampleAveragePropertyGenerator()};
            return pg;
        }
        return null;
    }

    protected boolean validateParameters(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateParameters(modeName, args, msg)) {
            return false;
        }
        if (args.getNumParameters() < 2 || args.getObjectParameter(1) == null) {
            args.set(args.getObjectParameter(0), 1);
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, Double scaleX, Double scaleY, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("SubsampleAverage", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("scaleX", scaleX);
        pb.setParameter("scaleY", scaleY);
        return JAI.create("SubsampleAverage", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, Double scaleX, Double scaleY, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("SubsampleAverage", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("scaleX", scaleX);
        pb.setParameter("scaleY", scaleY);
        return JAI.createRenderable("SubsampleAverage", pb, hints);
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

