/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.Image;
import java.awt.RenderingHints;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class AWTImageDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "AWTImage"}, {"LocalName", "AWTImage"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("AWTImageDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AWTImageDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("AWTImageDescriptor1")}};
    private static final Class[] paramClasses = new Class[]{class$java$awt$Image == null ? (class$java$awt$Image = AWTImageDescriptor.class$("java.awt.Image")) : class$java$awt$Image};
    private static final String[] paramNames = new String[]{"awtImage"};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT};
    static /* synthetic */ Class class$java$awt$Image;

    public AWTImageDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    public static RenderedOp create(Image awtImage, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("AWTImage", "rendered");
        pb.setParameter("awtImage", awtImage);
        return JAI.create("AWTImage", pb, hints);
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

