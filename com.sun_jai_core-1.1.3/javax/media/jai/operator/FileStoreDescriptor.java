/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.media.jai.codec.ImageCodec
 *  com.sun.media.jai.codec.ImageEncodeParam
 */
package javax.media.jai.operator;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncodeParam;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class FileStoreDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "FileStore"}, {"LocalName", "FileStore"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("FileStoreDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/FileStoreDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("FileStoreDescriptor1")}, {"arg1Desc", JaiI18N.getString("FileStoreDescriptor2")}, {"arg2Desc", JaiI18N.getString("FileStoreDescriptor3")}, {"arg3Desc", JaiI18N.getString("FileStoreDescriptor11")}};
    private static final String[] paramNames = new String[]{"filename", "format", "param", "checkFileLocally"};
    private static final Class[] paramClasses = new Class[]{class$java$lang$String == null ? (class$java$lang$String = FileStoreDescriptor.class$("java.lang.String")) : class$java$lang$String, class$java$lang$String == null ? (class$java$lang$String = FileStoreDescriptor.class$("java.lang.String")) : class$java$lang$String, class$com$sun$media$jai$codec$ImageEncodeParam == null ? (class$com$sun$media$jai$codec$ImageEncodeParam = FileStoreDescriptor.class$("com.sun.media.jai.codec.ImageEncodeParam")) : class$com$sun$media$jai$codec$ImageEncodeParam, class$java$lang$Boolean == null ? (class$java$lang$Boolean = FileStoreDescriptor.class$("java.lang.Boolean")) : class$java$lang$Boolean};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT, "tiff", null, Boolean.TRUE};
    private static final String[] supportedModes = new String[]{"rendered"};
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$com$sun$media$jai$codec$ImageEncodeParam;
    static /* synthetic */ Class class$java$lang$Boolean;

    public FileStoreDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateArguments(modeName, args, msg)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        String format = (String)args.getObjectParameter(1);
        ImageCodec codec = ImageCodec.getCodec((String)format);
        if (codec == null) {
            msg.append(this.getName() + " " + JaiI18N.getString("FileStoreDescriptor4"));
            return false;
        }
        ImageEncodeParam param = (ImageEncodeParam)args.getObjectParameter(2);
        RenderedImage src = args.getRenderedSource(0);
        if (!codec.canEncodeImage(src, param)) {
            msg.append(this.getName() + " " + JaiI18N.getString("FileStoreDescriptor5"));
            return false;
        }
        String pathName = (String)args.getObjectParameter(0);
        if (pathName == null) {
            msg.append(this.getName() + " " + JaiI18N.getString("FileStoreDescriptor6"));
            return false;
        }
        Boolean checkFile = (Boolean)args.getObjectParameter(3);
        if (checkFile.booleanValue()) {
            try {
                File f = new File(pathName);
                if (f.exists()) {
                    if (!f.canWrite()) {
                        msg.append(this.getName() + " " + JaiI18N.getString("FileStoreDescriptor7"));
                        return false;
                    }
                } else {
                    if (!f.createNewFile()) {
                        msg.append(this.getName() + " " + JaiI18N.getString("FileStoreDescriptor8"));
                        return false;
                    }
                    f.delete();
                }
            }
            catch (IOException ioe) {
                msg.append(this.getName() + " " + JaiI18N.getString("FileStoreDescriptor9") + " " + ioe.getMessage());
                return false;
            }
            catch (SecurityException se) {
                msg.append(this.getName() + " " + JaiI18N.getString("FileStoreDescriptor10") + " " + se.getMessage());
                return false;
            }
        }
        return true;
    }

    public boolean isImmediate() {
        return true;
    }

    public static RenderedOp create(RenderedImage source0, String filename, String format, ImageEncodeParam param, Boolean checkFileLocally, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("FileStore", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("filename", filename);
        pb.setParameter("format", format);
        pb.setParameter("param", param);
        pb.setParameter("checkFileLocally", checkFileLocally);
        return JAI.create("FileStore", pb, hints);
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

