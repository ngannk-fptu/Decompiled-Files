/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.media.jai.codec.FPXDecodeParam
 *  com.sun.media.jai.codec.SeekableStream
 */
package javax.media.jai.operator;

import com.sun.media.jai.codec.FPXDecodeParam;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.RenderingHints;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class FPXDescriptor
extends OperationDescriptorImpl {
    public static final Integer MAX_RESOLUTION = new Integer(-1);
    private static final String[][] resources = new String[][]{{"GlobalName", "FPX"}, {"LocalName", "FPX"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("FPXDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/FPXDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("FPXDescriptor1")}, {"arg1Desc", JaiI18N.getString("FPXDescriptor2")}};
    private static final String[] paramNames = new String[]{"stream", "param"};
    private static final Class[] paramClasses = new Class[]{class$com$sun$media$jai$codec$SeekableStream == null ? (class$com$sun$media$jai$codec$SeekableStream = FPXDescriptor.class$("com.sun.media.jai.codec.SeekableStream")) : class$com$sun$media$jai$codec$SeekableStream, class$com$sun$media$jai$codec$FPXDecodeParam == null ? (class$com$sun$media$jai$codec$FPXDecodeParam = FPXDescriptor.class$("com.sun.media.jai.codec.FPXDecodeParam")) : class$com$sun$media$jai$codec$FPXDecodeParam};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT, null};
    static /* synthetic */ Class class$com$sun$media$jai$codec$SeekableStream;
    static /* synthetic */ Class class$com$sun$media$jai$codec$FPXDecodeParam;

    public FPXDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    public static RenderedOp create(SeekableStream stream, FPXDecodeParam param, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("FPX", "rendered");
        pb.setParameter("stream", stream);
        pb.setParameter("param", param);
        return JAI.create("FPX", pb, hints);
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

