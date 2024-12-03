/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class MatchCDFDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "MatchCDF"}, {"LocalName", "MatchCDF"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("MatchCDFDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/MatchCDFDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", "The desired Cumulative Distribution Function."}};
    private static final Class[] paramClasses = new Class[]{array$$F == null ? (array$$F = MatchCDFDescriptor.class$("[[F")) : array$$F};
    private static final String[] paramNames = new String[]{"CDF"};
    private static final Object[] paramDefaults = new Object[]{null};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};
    static /* synthetic */ Class array$$F;

    public MatchCDFDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg) {
        int b;
        if (!super.validateArguments(modeName, args, msg)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        RenderedImage src = args.getRenderedSource(0);
        Object CDF = (float[][])args.getObjectParameter(0);
        Object prop = src.getProperty("histogram");
        if (prop == null || prop.equals(Image.UndefinedProperty)) {
            msg.append(this.getName() + " " + JaiI18N.getString("MatchCDFDescriptor1"));
            return false;
        }
        if (!(prop instanceof Histogram)) {
            msg.append(this.getName() + " " + JaiI18N.getString("MatchCDFDescriptor2"));
            return false;
        }
        Histogram hist = (Histogram)prop;
        int numBands = hist.getNumBands();
        if (CDF == null) {
            int[] numBins = hist.getNumBins();
            CDF = new float[numBands][];
            for (int b2 = 0; b2 < numBands; ++b2) {
                CDF[b2] = new float[numBins[b2]];
                for (int i = 0; i < numBins[b2]; ++i) {
                    CDF[b2][i] = (i + 1) / numBins[b2];
                }
            }
        }
        if (((float[][])CDF).length != numBands) {
            msg.append(this.getName() + " " + JaiI18N.getString("MatchCDFDescriptor3"));
            return false;
        }
        for (b = 0; b < numBands; ++b) {
            if (CDF[b].length == hist.getNumBins(b)) continue;
            msg.append(this.getName() + " " + JaiI18N.getString("MatchCDFDescriptor4"));
            return false;
        }
        for (b = 0; b < numBands; ++b) {
            float[] CDFband = CDF[b];
            int length = CDFband.length;
            if ((double)CDFband[length - 1] != 1.0) {
                msg.append(this.getName() + " " + JaiI18N.getString("MatchCDFDescriptor7"));
                return false;
            }
            for (int i = 0; i < length; ++i) {
                if (CDFband[i] < 0.0f) {
                    msg.append(this.getName() + " " + JaiI18N.getString("MatchCDFDescriptor5"));
                    return false;
                }
                if (i == 0 || !(CDFband[i] < CDFband[i - 1])) continue;
                msg.append(this.getName() + " " + JaiI18N.getString("MatchCDFDescriptor6"));
                return false;
            }
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, float[][] CDF, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("MatchCDF", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("CDF", CDF);
        return JAI.create("MatchCDF", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, float[][] CDF, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("MatchCDF", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("CDF", CDF);
        return JAI.createRenderable("MatchCDF", pb, hints);
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

