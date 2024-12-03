/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.color.ICC_Profile;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.renderable.ParameterBlock;
import java.net.URL;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class IIPDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "IIP"}, {"LocalName", "IIP"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("IIPDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/IIPDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("IIPDescriptor1")}, {"arg1Desc", JaiI18N.getString("IIPDescriptor2")}, {"arg2Desc", JaiI18N.getString("IIPDescriptor3")}, {"arg3Desc", JaiI18N.getString("IIPDescriptor4")}, {"arg4Desc", JaiI18N.getString("IIPDescriptor5")}, {"arg5Desc", JaiI18N.getString("IIPDescriptor6")}, {"arg6Desc", JaiI18N.getString("IIPDescriptor7")}, {"arg7Desc", JaiI18N.getString("IIPDescriptor8")}, {"arg8Desc", JaiI18N.getString("IIPDescriptor9")}, {"arg9Desc", JaiI18N.getString("IIPDescriptor10")}, {"arg10Desc", JaiI18N.getString("IIPDescriptor11")}, {"arg11Desc", JaiI18N.getString("IIPDescriptor12")}, {"arg12Desc", JaiI18N.getString("IIPDescriptor13")}, {"arg13Desc", JaiI18N.getString("IIPDescriptor14")}};
    private static final Class[] paramClasses = new Class[]{class$java$lang$String == null ? (class$java$lang$String = IIPDescriptor.class$("java.lang.String")) : class$java$lang$String, array$I == null ? (array$I = IIPDescriptor.class$("[I")) : array$I, class$java$lang$Float == null ? (class$java$lang$Float = IIPDescriptor.class$("java.lang.Float")) : class$java$lang$Float, array$F == null ? (array$F = IIPDescriptor.class$("[F")) : array$F, class$java$lang$Float == null ? (class$java$lang$Float = IIPDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$awt$geom$Rectangle2D$Float == null ? (class$java$awt$geom$Rectangle2D$Float = IIPDescriptor.class$("java.awt.geom.Rectangle2D$Float")) : class$java$awt$geom$Rectangle2D$Float, class$java$awt$geom$AffineTransform == null ? (class$java$awt$geom$AffineTransform = IIPDescriptor.class$("java.awt.geom.AffineTransform")) : class$java$awt$geom$AffineTransform, class$java$lang$Float == null ? (class$java$lang$Float = IIPDescriptor.class$("java.lang.Float")) : class$java$lang$Float, class$java$awt$geom$Rectangle2D$Float == null ? (class$java$awt$geom$Rectangle2D$Float = IIPDescriptor.class$("java.awt.geom.Rectangle2D$Float")) : class$java$awt$geom$Rectangle2D$Float, class$java$lang$Integer == null ? (class$java$lang$Integer = IIPDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$String == null ? (class$java$lang$String = IIPDescriptor.class$("java.lang.String")) : class$java$lang$String, class$java$awt$color$ICC_Profile == null ? (class$java$awt$color$ICC_Profile = IIPDescriptor.class$("java.awt.color.ICC_Profile")) : class$java$awt$color$ICC_Profile, class$java$lang$Integer == null ? (class$java$lang$Integer = IIPDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Integer == null ? (class$java$lang$Integer = IIPDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer};
    private static final String[] paramNames = new String[]{"URL", "subImages", "filter", "colorTwist", "contrast", "sourceROI", "transform", "aspectRatio", "destROI", "rotation", "mirrorAxis", "ICCProfile", "JPEGQuality", "JPEGTable"};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT, new int[]{0}, new Float(0.0f), null, new Float(1.0f), null, new AffineTransform(), null, null, new Integer(0), null, null, null, null};
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class array$I;
    static /* synthetic */ Class class$java$lang$Float;
    static /* synthetic */ Class array$F;
    static /* synthetic */ Class class$java$awt$geom$Rectangle2D$Float;
    static /* synthetic */ Class class$java$awt$geom$AffineTransform;
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$java$awt$color$ICC_Profile;

    public IIPDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    public boolean isRenderableSupported() {
        return true;
    }

    public Number getParamMinValue(int index) {
        if (index == 0 || index == 1 || index == 3 || index == 5 || index == 6 || index == 8 || index == 10 || index == 11) {
            return null;
        }
        if (index == 2) {
            return new Float(-3.4028235E38f);
        }
        if (index == 7) {
            return new Float(0.0f);
        }
        if (index == 4) {
            return new Float(1.0f);
        }
        if (index == 12 || index == 9) {
            return new Integer(0);
        }
        if (index == 13) {
            return new Integer(1);
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public Number getParamMaxValue(int index) {
        if (index == 0 || index == 1 || index == 3 || index == 5 || index == 6 || index == 8 || index == 10 || index == 11) {
            return null;
        }
        if (index == 2 || index == 4 || index == 7) {
            return new Float(Float.MAX_VALUE);
        }
        if (index == 9) {
            return new Integer(270);
        }
        if (index == 12) {
            return new Integer(100);
        }
        if (index == 13) {
            return new Integer(255);
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
        int JPEGIndex;
        int JPEGQuality;
        float aspectRatio;
        float contrast;
        float[] colorTwist;
        if (!super.validateParameters(args, msg)) {
            return false;
        }
        try {
            new URL((String)args.getObjectParameter(0));
        }
        catch (Exception e) {
            msg.append(this.getName() + " " + JaiI18N.getString("IIPDescriptor15"));
            return false;
        }
        int[] subImages = (int[])args.getObjectParameter(1);
        if (subImages.length < 1) {
            args.set(paramDefaults[1], 1);
        }
        if ((colorTwist = (float[])args.getObjectParameter(3)) != null) {
            if (colorTwist.length < 16) {
                msg.append(this.getName() + " " + JaiI18N.getString("IIPDescriptor16"));
                return false;
            }
            colorTwist[12] = 0.0f;
            colorTwist[13] = 0.0f;
            colorTwist[14] = 0.0f;
            args.set(colorTwist, 3);
        }
        if ((contrast = args.getFloatParameter(4)) < 1.0f) {
            msg.append(this.getName() + " " + JaiI18N.getString("IIPDescriptor20"));
            return false;
        }
        Rectangle2D.Float sourceROI = (Rectangle2D.Float)args.getObjectParameter(5);
        if (sourceROI != null && (sourceROI.getWidth() < 0.0 || sourceROI.getHeight() < 0.0)) {
            msg.append(this.getName() + " " + JaiI18N.getString("IIPDescriptor17"));
            return false;
        }
        AffineTransform tf = (AffineTransform)args.getObjectParameter(6);
        if (tf.getDeterminant() == 0.0) {
            msg.append(this.getName() + " " + JaiI18N.getString("IIPDescriptor24"));
            return false;
        }
        if (args.getObjectParameter(7) != null && (aspectRatio = args.getFloatParameter(7)) < 0.0f) {
            msg.append(this.getName() + " " + JaiI18N.getString("IIPDescriptor21"));
            return false;
        }
        Rectangle2D.Float destROI = (Rectangle2D.Float)args.getObjectParameter(8);
        if (destROI != null && (destROI.getWidth() < 0.0 || destROI.getHeight() < 0.0)) {
            msg.append(this.getName() + " " + JaiI18N.getString("IIPDescriptor17"));
            return false;
        }
        int rotation = args.getIntParameter(9);
        if (rotation != 0 && rotation != 90 && rotation != 180 && rotation != 270) {
            msg.append(this.getName() + " " + JaiI18N.getString("IIPDescriptor18"));
            return false;
        }
        String mirrorAxis = (String)args.getObjectParameter(10);
        if (mirrorAxis != null && !mirrorAxis.equalsIgnoreCase("x") && !mirrorAxis.equalsIgnoreCase("y")) {
            msg.append(this.getName() + " " + JaiI18N.getString("IIPDescriptor19"));
            return false;
        }
        if (args.getObjectParameter(12) != null && ((JPEGQuality = args.getIntParameter(12)) < 0 || JPEGQuality > 100)) {
            msg.append(this.getName() + " " + JaiI18N.getString("IIPDescriptor22"));
            return false;
        }
        if (args.getObjectParameter(13) != null && ((JPEGIndex = args.getIntParameter(13)) < 1 || JPEGIndex > 255)) {
            msg.append(this.getName() + " " + JaiI18N.getString("IIPDescriptor23"));
            return false;
        }
        return true;
    }

    public static RenderedOp create(String URL2, int[] subImages, Float filter, float[] colorTwist, Float contrast, Rectangle2D.Float sourceROI, AffineTransform transform, Float aspectRatio, Rectangle2D.Float destROI, Integer rotation, String mirrorAxis, ICC_Profile ICCProfile, Integer JPEGQuality, Integer JPEGTable, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("IIP", "rendered");
        pb.setParameter("URL", URL2);
        pb.setParameter("subImages", subImages);
        pb.setParameter("filter", filter);
        pb.setParameter("colorTwist", colorTwist);
        pb.setParameter("contrast", contrast);
        pb.setParameter("sourceROI", sourceROI);
        pb.setParameter("transform", transform);
        pb.setParameter("aspectRatio", aspectRatio);
        pb.setParameter("destROI", destROI);
        pb.setParameter("rotation", rotation);
        pb.setParameter("mirrorAxis", mirrorAxis);
        pb.setParameter("ICCProfile", ICCProfile);
        pb.setParameter("JPEGQuality", JPEGQuality);
        pb.setParameter("JPEGTable", JPEGTable);
        return JAI.create("IIP", pb, hints);
    }

    public static RenderableOp createRenderable(String URL2, int[] subImages, Float filter, float[] colorTwist, Float contrast, Rectangle2D.Float sourceROI, AffineTransform transform, Float aspectRatio, Rectangle2D.Float destROI, Integer rotation, String mirrorAxis, ICC_Profile ICCProfile, Integer JPEGQuality, Integer JPEGTable, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("IIP", "renderable");
        pb.setParameter("URL", URL2);
        pb.setParameter("subImages", subImages);
        pb.setParameter("filter", filter);
        pb.setParameter("colorTwist", colorTwist);
        pb.setParameter("contrast", contrast);
        pb.setParameter("sourceROI", sourceROI);
        pb.setParameter("transform", transform);
        pb.setParameter("aspectRatio", aspectRatio);
        pb.setParameter("destROI", destROI);
        pb.setParameter("rotation", rotation);
        pb.setParameter("mirrorAxis", mirrorAxis);
        pb.setParameter("ICCProfile", ICCProfile);
        pb.setParameter("JPEGQuality", JPEGQuality);
        pb.setParameter("JPEGTable", JPEGTable);
        return JAI.createRenderable("IIP", pb, hints);
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

