/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.media.jai.codec.ImageDecodeParam
 */
package javax.media.jai.operator;

import com.sun.media.jai.codec.ImageDecodeParam;
import java.awt.RenderingHints;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.InputStream;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class FileLoadDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "FileLoad"}, {"LocalName", "FileLoad"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("FileLoadDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/FileLoadDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("FileLoadDescriptor1")}, {"arg1Desc", JaiI18N.getString("FileLoadDescriptor4")}, {"arg2Desc", JaiI18N.getString("FileLoadDescriptor5")}};
    private static final String[] paramNames = new String[]{"filename", "param", "checkFileLocally"};
    private static final Class[] paramClasses = new Class[]{class$java$lang$String == null ? (class$java$lang$String = FileLoadDescriptor.class$("java.lang.String")) : class$java$lang$String, class$com$sun$media$jai$codec$ImageDecodeParam == null ? (class$com$sun$media$jai$codec$ImageDecodeParam = FileLoadDescriptor.class$("com.sun.media.jai.codec.ImageDecodeParam")) : class$com$sun$media$jai$codec$ImageDecodeParam, class$java$lang$Boolean == null ? (class$java$lang$Boolean = FileLoadDescriptor.class$("java.lang.Boolean")) : class$java$lang$Boolean};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT, null, Boolean.TRUE};
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$com$sun$media$jai$codec$ImageDecodeParam;
    static /* synthetic */ Class class$java$lang$Boolean;

    public FileLoadDescriptor() {
        super(resources, 0, paramClasses, paramNames, paramDefaults);
    }

    protected boolean validateParameters(ParameterBlock args, StringBuffer msg) {
        if (!super.validateParameters(args, msg)) {
            return false;
        }
        Boolean checkFile = (Boolean)args.getObjectParameter(2);
        if (checkFile.booleanValue()) {
            String filename = (String)args.getObjectParameter(0);
            File f = new File(filename);
            boolean fileExists = f.exists();
            if (!fileExists) {
                InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
                if (is == null) {
                    msg.append("\"" + filename + "\": " + JaiI18N.getString("FileLoadDescriptor2"));
                    return false;
                }
            } else if (!f.canRead()) {
                msg.append("\"" + filename + "\": " + JaiI18N.getString("FileLoadDescriptor3"));
                return false;
            }
        }
        return true;
    }

    public static RenderedOp create(String filename, ImageDecodeParam param, Boolean checkFileLocally, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("FileLoad", "rendered");
        pb.setParameter("filename", filename);
        pb.setParameter("param", param);
        pb.setParameter("checkFileLocally", checkFileLocally);
        return JAI.create("FileLoad", pb, hints);
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

