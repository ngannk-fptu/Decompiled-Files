/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class CropDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Crop"}, {"LocalName", "Crop"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("CropDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/CropDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("CropDescriptor1")}, {"arg1Desc", JaiI18N.getString("CropDescriptor2")}, {"arg2Desc", JaiI18N.getString("CropDescriptor3")}, {"arg3Desc", JaiI18N.getString("CropDescriptor4")}};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Float == null ? (class$java$lang$Float = CropDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = CropDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = CropDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$lang$Float == null ? (class$java$lang$Float = CropDescriptor.class$("java.lang.Float")) : class$java$lang$Float};
    private static final String[] paramNames = new String[]{"x", "y", "width", "height"};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT, NO_PARAMETER_DEFAULT};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};
    static /* synthetic */ Class class$java$lang$Float;

    public CropDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateArguments(modeName, args, msg)) {
            return false;
        }
        if (modeName.equalsIgnoreCase("rendered")) {
            return this.validateRenderedArgs(args, msg);
        }
        if (modeName.equalsIgnoreCase("renderable")) {
            return this.validateRenderableArgs(args, msg);
        }
        return true;
    }

    private boolean validateRenderedArgs(ParameterBlock args, StringBuffer msg) {
        float h_req;
        float w_req;
        float y_req;
        float x_req = args.getFloatParameter(0);
        Rectangle rect_req = new Rectangle2D.Float(x_req, y_req = args.getFloatParameter(1), w_req = args.getFloatParameter(2), h_req = args.getFloatParameter(3)).getBounds();
        if (rect_req.isEmpty()) {
            msg.append(this.getName() + " " + JaiI18N.getString("CropDescriptor5"));
            return false;
        }
        RenderedImage src = (RenderedImage)args.getSource(0);
        Rectangle srcBounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
        if (!srcBounds.contains(rect_req)) {
            msg.append(this.getName() + " " + JaiI18N.getString("CropDescriptor6"));
            return false;
        }
        return true;
    }

    private boolean validateRenderableArgs(ParameterBlock args, StringBuffer msg) {
        float h_req;
        float w_req;
        float y_req;
        float x_req = args.getFloatParameter(0);
        Rectangle2D.Float rect_req = new Rectangle2D.Float(x_req, y_req = args.getFloatParameter(1), w_req = args.getFloatParameter(2), h_req = args.getFloatParameter(3));
        if (((RectangularShape)rect_req).isEmpty()) {
            msg.append(this.getName() + " " + JaiI18N.getString("CropDescriptor5"));
            return false;
        }
        RenderableImage src = (RenderableImage)args.getSource(0);
        Rectangle2D.Float rect_src = new Rectangle2D.Float(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
        if (!rect_src.contains(rect_req)) {
            msg.append(this.getName() + " " + JaiI18N.getString("CropDescriptor6"));
            return false;
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, Float x, Float y, Float width, Float height, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Crop", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("x", x);
        pb.setParameter("y", y);
        pb.setParameter("width", width);
        pb.setParameter("height", height);
        return JAI.create("Crop", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, Float x, Float y, Float width, Float height, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Crop", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("x", x);
        pb.setParameter("y", y);
        pb.setParameter("width", width);
        pb.setParameter("height", height);
        return JAI.createRenderable("Crop", pb, hints);
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

