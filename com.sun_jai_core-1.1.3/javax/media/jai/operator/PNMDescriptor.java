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

public class PNMDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "PNM"}, {"LocalName", "PNM"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("PNMDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/PNMDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("PNMDescriptor1")}};
    private static final String[] paramNames = new String[]{"stream"};
    private static final Class[] paramClasses = new Class[]{class$com$sun$media$jai$codec$SeekableStream == null ? (class$com$sun$media$jai$codec$SeekableStream = PNMDescriptor.class$("com.sun.media.jai.codec.SeekableStream")) : class$com$sun$media$jai$codec$SeekableStream};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT};
    static /* synthetic */ Class class$com$sun$media$jai$codec$SeekableStream;

    public PNMDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    public static RenderedOp create(SeekableStream stream, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("PNM", "rendered");
        pb.setParameter("stream", stream);
        return JAI.create("PNM", pb, hints);
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

