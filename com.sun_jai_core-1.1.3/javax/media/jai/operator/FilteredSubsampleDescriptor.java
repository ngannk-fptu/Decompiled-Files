/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class FilteredSubsampleDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "FilteredSubsample"}, {"LocalName", "FilteredSubsample"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("FilteredSubsampleDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/FilteredSubsampleDescriptor.html"}, {"Version", "1.0"}, {"arg0Desc", "The X subsample factor."}, {"arg1Desc", "The Y subsample factor."}, {"arg2Desc", "Symmetric filter coefficients."}, {"arg3Desc", "Interpolation object."}};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Integer == null ? (class$java$lang$Integer = FilteredSubsampleDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Integer == null ? (class$java$lang$Integer = FilteredSubsampleDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, array$F == null ? (array$F = FilteredSubsampleDescriptor.class$("[F")) : array$F, class$javax$media$jai$Interpolation == null ? (class$javax$media$jai$Interpolation = FilteredSubsampleDescriptor.class$("javax.media.jai.Interpolation")) : class$javax$media$jai$Interpolation};
    private static final String[] paramNames = new String[]{"scaleX", "scaleY", "qsFilterArray", "interpolation"};
    private static final Object[] paramDefaults = new Object[]{new Integer(2), new Integer(2), null, Interpolation.getInstance(0)};
    private static final String[] supportedModes = new String[]{"rendered"};
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class array$F;
    static /* synthetic */ Class class$javax$media$jai$Interpolation;

    public FilteredSubsampleDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    protected boolean validateParameters(String modeName, ParameterBlock args, StringBuffer msg) {
        Interpolation interp;
        if (!super.validateParameters(modeName, args, msg)) {
            return false;
        }
        int scaleX = args.getIntParameter(0);
        int scaleY = args.getIntParameter(1);
        if (scaleX < 1 || scaleY < 1) {
            msg.append(this.getName() + " " + JaiI18N.getString("FilteredSubsampleDescriptor1"));
            return false;
        }
        float[] filter = (float[])args.getObjectParameter(2);
        if (filter == null) {
            int i;
            int m;
            int n = m = scaleX > scaleY ? scaleX : scaleY;
            if ((m & 1) == 0) {
                ++m;
            }
            double sigma = (double)(m - 1) / 6.0;
            if (m == 1) {
                sigma = 1.0;
            }
            filter = new float[m / 2 + 1];
            float sum = 0.0f;
            for (i = 0; i < filter.length; ++i) {
                filter[i] = (float)this.gaussian(i, sigma);
                if (i == 0) {
                    sum += filter[i];
                    continue;
                }
                sum += filter[i] * 2.0f;
            }
            i = 0;
            while (i < filter.length) {
                int n2 = i++;
                filter[n2] = filter[n2] / sum;
            }
            args.set(filter, 2);
        }
        if (!((interp = (Interpolation)args.getObjectParameter(3)) instanceof InterpolationNearest || interp instanceof InterpolationBilinear || interp instanceof InterpolationBicubic || interp instanceof InterpolationBicubic2)) {
            msg.append(this.getName() + " " + JaiI18N.getString("FilteredSubsampleDescriptor2"));
            return false;
        }
        return true;
    }

    private double gaussian(double x, double sigma) {
        return Math.exp(-x * x / (2.0 * sigma * sigma)) / sigma / Math.sqrt(Math.PI * 2);
    }

    public static RenderedOp create(RenderedImage source0, Integer scaleX, Integer scaleY, float[] qsFilterArray, Interpolation interpolation, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("FilteredSubsample", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("scaleX", scaleX);
        pb.setParameter("scaleY", scaleY);
        pb.setParameter("qsFilterArray", qsFilterArray);
        pb.setParameter("interpolation", interpolation);
        return JAI.create("FilteredSubsample", pb, hints);
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

