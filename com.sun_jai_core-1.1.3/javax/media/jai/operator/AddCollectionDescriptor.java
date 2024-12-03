/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.util.Collection;
import java.util.Iterator;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class AddCollectionDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "AddCollection"}, {"LocalName", "AddCollection"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("AddCollectionDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AddCollectionDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}};
    private static final Class[][] sourceClasses = new Class[][]{{class$java$util$Collection == null ? (class$java$util$Collection = AddCollectionDescriptor.class$("java.util.Collection")) : class$java$util$Collection}, {class$java$util$Collection == null ? (class$java$util$Collection = AddCollectionDescriptor.class$("java.util.Collection")) : class$java$util$Collection}};
    private static final String[] supportedModes = new String[]{"rendered", "renderable"};
    static /* synthetic */ Class class$java$util$Collection;

    public AddCollectionDescriptor() {
        super(resources, supportedModes, null, sourceClasses, (ParameterListDescriptor)null);
    }

    protected boolean validateSources(String modeName, ParameterBlock args, StringBuffer msg) {
        block5: {
            Iterator iter;
            block4: {
                if (!super.validateSources(modeName, args, msg)) {
                    return false;
                }
                Collection col = (Collection)args.getSource(0);
                if (col.size() < 2) {
                    msg.append(this.getName() + " " + JaiI18N.getString("AddCollectionDescriptor1"));
                    return false;
                }
                iter = col.iterator();
                if (!modeName.equalsIgnoreCase("rendered")) break block4;
                while (iter.hasNext()) {
                    Object o = iter.next();
                    if (o instanceof RenderedImage) continue;
                    msg.append(this.getName() + " " + JaiI18N.getString("AddCollectionDescriptor2"));
                    return false;
                }
                break block5;
            }
            if (!modeName.equalsIgnoreCase("renderable")) break block5;
            while (iter.hasNext()) {
                Object o = iter.next();
                if (o instanceof RenderableImage) continue;
                msg.append(this.getName() + " " + JaiI18N.getString("AddCollectionDescriptor3"));
                return false;
            }
        }
        return true;
    }

    public static RenderedOp create(Collection source0, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("AddCollection", "rendered");
        pb.setSource("source0", source0);
        return JAI.create("AddCollection", pb, hints);
    }

    public static RenderableOp createRenderable(Collection source0, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("AddCollection", "renderable");
        pb.setSource("source0", source0);
        return JAI.createRenderable("AddCollection", pb, hints);
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

