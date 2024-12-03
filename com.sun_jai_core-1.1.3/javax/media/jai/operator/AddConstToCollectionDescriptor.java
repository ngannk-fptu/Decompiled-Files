/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Collection;
import java.util.Iterator;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.operator.JaiI18N;

public class AddConstToCollectionDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "AddConstToCollection"}, {"LocalName", "AddConstToCollection"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("AddConstToCollectionDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/AddConstToCollectionDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion")}, {"arg0Desc", JaiI18N.getString("AddConstToCollectionDescriptor1")}};
    private static final String[] paramNames = new String[]{"constants"};
    private static final Class[] paramClasses = new Class[]{array$D == null ? (array$D = AddConstToCollectionDescriptor.class$("[D")) : array$D};
    private static final Object[] paramDefaults = new Object[]{new double[]{0.0}};
    private static final String[] supportedModes = new String[]{"collection"};
    static /* synthetic */ Class array$D;

    public AddConstToCollectionDescriptor() {
        super(resources, supportedModes, 1, paramNames, paramClasses, paramDefaults, null);
    }

    public boolean validateArguments(String modeName, ParameterBlock args, StringBuffer msg) {
        if (!super.validateArguments(modeName, args, msg)) {
            return false;
        }
        Collection col = (Collection)args.getSource(0);
        if (col.size() < 1) {
            msg.append(this.getName() + " " + JaiI18N.getString("AddConstToCollectionDescriptor2"));
            return false;
        }
        Iterator iter = col.iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof RenderedImage) continue;
            msg.append(this.getName() + " " + JaiI18N.getString("AddConstToCollectionDescriptor3"));
            return false;
        }
        int length = ((double[])args.getObjectParameter(0)).length;
        if (length < 1) {
            msg.append(this.getName() + " " + JaiI18N.getString("AddConstToCollectionDescriptor4"));
            return false;
        }
        return true;
    }

    public static Collection createCollection(Collection source0, double[] constants, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("AddConstToCollection", "collection");
        pb.setSource("source0", source0);
        pb.setParameter("constants", constants);
        return JAI.createCollection("AddConstToCollection", pb, hints);
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

