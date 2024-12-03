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

public class AndConstDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "AndConst"}, {"LocalName", "AndConst"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("AndConstDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AndConstDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("AndConstDescriptor1")}};
    private static final Class[] paramClasses = new Class[]{array$I == null ? (array$I = AndConstDescriptor.class$("[I")) : array$I};
    private static final String[] paramNames = new String[]{"constants"};
    private static final Object[] paramDefaults = new Object[]{new int[]{-1}};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};
    static /* synthetic */ Class array$I;

    public AndConstDescriptor() {
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
            message.append(this.getName() + " " + JaiI18N.getString("AndConstDescriptor1"));
            return false;
        }
        int length = ((int[])args.getObjectParameter(0)).length;
        if (length < 1) {
            message.append(this.getName() + " " + JaiI18N.getString("AndConstDescriptor2"));
            return false;
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, int[] constants, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("AndConst", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("constants", constants);
        return JAI.create("AndConst", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, int[] constants, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("AndConst", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("constants", constants);
        return JAI.createRenderable("AndConst", pb, hints);
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

