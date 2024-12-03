/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class LookupDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Lookup"}, {"LocalName", "Lookup"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("LookupDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/LookupDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("LookupDescriptor1")}};
    private static final Class[] paramClasses = new Class[]{class$javax$media$jai$LookupTableJAI == null ? (class$javax$media$jai$LookupTableJAI = LookupDescriptor.class$("javax.media.jai.LookupTableJAI")) : class$javax$media$jai$LookupTableJAI};
    private static final String[] paramNames = new String[]{"table"};
    private static final Object[] paramDefaults = new Object[]{NO_PARAMETER_DEFAULT};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};
    static /* synthetic */ Class class$javax$media$jai$LookupTableJAI;

    public LookupDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateSources(modeName, args, msg)) {
            return false;
        }
        if (!modeName.equalsIgnoreCase("rendered")) {
            return true;
        }
        RenderedImage src = args.getRenderedSource(0);
        int dtype = src.getSampleModel().getDataType();
        if (dtype != 0 && dtype != 1 && dtype != 2 && dtype != 3) {
            msg.append(this.getName() + " " + JaiI18N.getString("LookupDescriptor2"));
            return false;
        }
        return true;
    }

    public static RenderedOp create(RenderedImage source0, LookupTableJAI table, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Lookup", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("table", table);
        return JAI.create("Lookup", pb, hints);
    }

    public static RenderableOp createRenderable(RenderableImage source0, LookupTableJAI table, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Lookup", "renderable");
        pb.setSource("source0", source0);
        pb.setParameter("table", table);
        return JAI.createRenderable("Lookup", pb, hints);
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

