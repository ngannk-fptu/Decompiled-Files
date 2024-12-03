/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.media.jai.codec.SeekableStream
 */
package javax.media.jai.operator;

import com.sun.media.jai.codec.SeekableStream;
import java.awt.RenderingHints;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class JPEGDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "JPEG"}, {"LocalName", "JPEG"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("JPEGDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/JPEGDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("JPEGDescriptor1")}};
    private static final String[] paramNames = new String[]{"stream"};
    private static final Class[] paramClasses = new Class[]{class$com$sun$media$jai$codec$SeekableStream == null ? (class$com$sun$media$jai$codec$SeekableStream = JPEGDescriptor.class$("com.sun.media.jai.codec.SeekableStream")) : class$com$sun$media$jai$codec$SeekableStream};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT};
    static /* synthetic */ Class class$com$sun$media$jai$codec$SeekableStream;

    public JPEGDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    public static RenderedOp create(SeekableStream stream, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("JPEG", "rendered");
        pb.setParameter("stream", stream);
        return JAI.create("JPEG", pb, hints);
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

