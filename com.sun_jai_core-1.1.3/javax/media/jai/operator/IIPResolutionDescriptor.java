/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.net.URL;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class IIPResolutionDescriptor
extends OperationDescriptorImpl {
    public static final Integer MAX_RESOLUTION = new Integer(Integer.MAX_VALUE);
    private static final String[][] resources = new String[][]{{"GlobalName", "IIPResolution"}, {"LocalName", "IIPResolution"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("IIPResolutionDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/IIPResolutionDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("IIPResolutionDescriptor1")}, {"arg1Desc", JaiI18N.getString("IIPResolutionDescriptor2")}, {"arg2Desc", JaiI18N.getString("IIPResolutionDescriptor3")}};
    private static final Class[] paramClasses = new Class[]{class$java$lang$String == null ? (class$java$lang$String = IIPResolutionDescriptor.class$("java.lang.String")) : class$java$lang$String, class$java$lang$Integer == null ? (class$java$lang$Integer = IIPResolutionDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Integer == null ? (class$java$lang$Integer = IIPResolutionDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer};
    private static final String[] paramNames = new String[]{"URL", "resolution", "subImage"};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT, MAX_RESOLUTION, new Integer(0)};
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Integer;

    public IIPResolutionDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    public Number getParamMinValue(int index) {
        if (index == 0) {
            return null;
        }
        if (index == 1 || index == 2) {
            return new Integer(0);
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
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
        return true;
    }

    public static RenderedOp create(String URL2, Integer resolution, Integer subImage, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("IIPResolution", "rendered");
        pb.setParameter("URL", URL2);
        pb.setParameter("resolution", resolution);
        pb.setParameter("subImage", subImage);
        return JAI.create("IIPResolution", pb, hints);
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

