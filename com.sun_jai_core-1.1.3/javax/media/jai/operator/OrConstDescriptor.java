/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class OrConstDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "OrConst"}, {"LocalName", "OrConst"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("OrConstDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/OrConstDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("OrConstDescriptor1")}};
    private static final Class[] paramClasses = new Class[]{array$I == null ? (array$I = OrConstDescriptor.class$("[I")) : array$I};
    private static final String[] paramNames = new String[]{"constants"};
    private static final Object[] paramDefaults = new Object[]{new int[]{0}};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};
    static /* synthetic */ Class array$I;

    public OrConstDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer message) {
        if (!super.validateArguments(modeName, args, message)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        RenderedImage src = args.getRenderedSource(0);
        int dtype = src.getSampleModel().getDataType();
        if (dtype != 0 && dtype != 1 && dtype != 2 && dtype != 3) {
            message.append(this.getName() + " " + JaiI18N.getString("OrConstDescriptor2"));
            return false;
        }
        int length = ((int[])args.getObjectParameter(0)).length;
        if (length < 1) {
            message.append(this.getName() + " " + JaiI18N.getString("OrConstDescriptor3"));
            return false;
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, int[] constants, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("OrConst", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("constants", constants);
        return JAI.create("OrConst", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, int[] constants, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("OrConst", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("constants", constants);
        return JAI.createRenderable("OrConst", pb, hints);
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

