/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.media.jai.codec.SeekableStream
 *  com.sun.media.jai.codec.TIFFDecodeParam
 */
package javax.media.jai.operator;

import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import java.awt.RenderingHints;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class TIFFDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "TIFF"}, {"LocalName", "TIFF"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("TIFFDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/TIFFDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("TIFFDescriptor1")}, {"arg1Desc", JaiI18N.getString("TIFFDescriptor2")}, {"arg2Desc", JaiI18N.getString("TIFFDescriptor3")}};
    private static final String[] paramNames = new String[]{"stream", "param", "page"};
    private static final Class[] paramClasses = new Class[]{class$com$sun$media$jai$codec$SeekableStream == null ? (class$com$sun$media$jai$codec$SeekableStream = TIFFDescriptor.class$("com.sun.media.jai.codec.SeekableStream")) : class$com$sun$media$jai$codec$SeekableStream, class$com$sun$media$jai$codec$TIFFDecodeParam == null ? (class$com$sun$media$jai$codec$TIFFDecodeParam = TIFFDescriptor.class$("com.sun.media.jai.codec.TIFFDecodeParam")) : class$com$sun$media$jai$codec$TIFFDecodeParam, class$java$lang$Integer == null ? (class$java$lang$Integer = TIFFDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT, null, new Integer(0)};
    static /* synthetic */ Class class$com$sun$media$jai$codec$SeekableStream;
    static /* synthetic */ Class class$com$sun$media$jai$codec$TIFFDecodeParam;
    static /* synthetic */ Class class$java$lang$Integer;

    public TIFFDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    public static RenderedOp create(SeekableStream stream, TIFFDecodeParam param, Integer page, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("TIFF", "rendered");
        pb.setParameter("stream", stream);
        pb.setParameter("param", param);
        pb.setParameter("page", page);
        return JAI.create("TIFF", pb, hints);
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

