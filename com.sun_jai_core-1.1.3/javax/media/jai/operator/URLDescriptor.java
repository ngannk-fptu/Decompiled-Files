/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.media.jai.codec.ImageDecodeParam
 */
package javax.media.jai.operator;

import com.sun.media.jai.codec.ImageDecodeParam;
import java.awt.RenderingHints;
import java.net.URL;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class URLDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "URL"}, {"LocalName", "URL"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("URLDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/URLDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("URLDescriptor1")}, {"arg1Desc", JaiI18N.getString("URLDescriptor2")}};
    private static final String[] paramNames = new String[]{"URL", "param"};
    private static final Class[] paramClasses = new Class[]{class$java$net$URL == null ? (class$java$net$URL = URLDescriptor.class$("java.net.URL")) : class$java$net$URL, class$com$sun$media$jai$codec$ImageDecodeParam == null ? (class$com$sun$media$jai$codec$ImageDecodeParam = URLDescriptor.class$("com.sun.media.jai.codec.ImageDecodeParam")) : class$com$sun$media$jai$codec$ImageDecodeParam};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT, null};
    static /* synthetic */ Class class$java$net$URL;
    static /* synthetic */ Class class$com$sun$media$jai$codec$ImageDecodeParam;

    public URLDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    public static RenderedOp create(URL URL2, ImageDecodeParam param, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("URL", "rendered");
        pb.setParameter("URL", URL2);
        pb.setParameter("param", param);
        return JAI.create("URL", pb, hints);
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

