/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AffinePropertyGenerator;
import javax.media.jai.operator.JaiI18N;

public class AffineDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Affine"}, {"LocalName", "Affine"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("AffineDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AffineDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("AffineDescriptor1")}, {"arg1Desc", JaiI18N.getString("AffineDescriptor2")}, {"arg2Desc", JaiI18N.getString("AffineDescriptor3")}};
    private static final Class[] paramClasses = new Class[]{class$java$awt$geom$AffineTransform == null ? (class$java$awt$geom$AffineTransform = AffineDescriptor.class$("java.awt.geom.AffineTransform")) : class$java$awt$geom$AffineTransform, class$javax$media$jai$Interpolation == null ? (class$javax$media$jai$Interpolation = AffineDescriptor.class$("javax.media.jai.Interpolation")) : class$javax$media$jai$Interpolation, array$D == null ? (array$D = AffineDescriptor.class$("[D")) : array$D};
    private static final String[] paramNames = new String[]{"transform", "interpolation", "backgroundValues"};
    private static final Object[] paramDefaults = new Object[]{new AffineTransform(), Interpolation.getInstance(0), new double[]{0.0}};
    static /* synthetic */ Class class$java$awt$geom$AffineTransform;
    static /* synthetic */ Class class$javax$media$jai$Interpolation;
    static /* synthetic */ Class array$D;

    public AffineDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public boolean isRenderableSupported() {
        return true;
    }

    public PropertyGenerator[] getPropertyGenerators() {
        PropertyGenerator[] pg = new PropertyGenerator[]{new AffinePropertyGenerator()};
        return pg;
    }

    protected boolean validateParameters(ParameterBlock args, StringBuffer message) {
        if (!super.validateParameters(args, message)) {
            return false;
        }
        AffineTransform transform = (AffineTransform)args.getObjectParameter(0);
        try {
            AffineTransform itransform = transform.createInverse();
        }
        catch (NoninvertibleTransformException e) {
            message.append(this.getName() + " " + JaiI18N.getString("AffineDescriptor4"));
            return false;
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, AffineTransform transform, Interpolation interpolation, double[] backgroundValues, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Affine", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("transform", transform);
        pb.setParameter("interpolation", interpolation);
        pb.setParameter("backgroundValues", backgroundValues);
        return JAI.create("Affine", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, AffineTransform transform, Interpolation interpolation, double[] backgroundValues, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Affine", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("transform", transform);
        pb.setParameter("interpolation", interpolation);
        pb.setParameter("backgroundValues", backgroundValues);
        return JAI.createRenderable("Affine", pb, hints);
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

