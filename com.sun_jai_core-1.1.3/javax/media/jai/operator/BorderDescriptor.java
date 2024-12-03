/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.BorderExtender;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptorImpl;
import javax.media.jai.OperationNode;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.JaiI18N;

public class BorderDescriptor
extends OperationDescriptorImpl {
    private static final String[][] resources = new String[][]{{"GlobalName", "Border"}, {"LocalName", "Border"}, {"Vendor", "com.sun.media.jai"}, {"Description", JaiI18N.getString("BorderDescriptor0")}, {"DocURL", "http://java.sun.com/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/operator/BorderDescriptor.html"}, {"Version", JaiI18N.getString("DescriptorVersion2")}, {"arg0Desc", JaiI18N.getString("BorderDescriptor1")}, {"arg1Desc", JaiI18N.getString("BorderDescriptor2")}, {"arg2Desc", JaiI18N.getString("BorderDescriptor3")}, {"arg3Desc", JaiI18N.getString("BorderDescriptor4")}, {"arg4Desc", JaiI18N.getString("BorderDescriptor5")}};
    private static final String[] paramNames = new String[]{"leftPad", "rightPad", "topPad", "bottomPad", "type"};
    private static final Class[] paramClasses = new Class[]{class$java$lang$Integer == null ? (class$java$lang$Integer = BorderDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Integer == null ? (class$java$lang$Integer = BorderDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Integer == null ? (class$java$lang$Integer = BorderDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$java$lang$Integer == null ? (class$java$lang$Integer = BorderDescriptor.class$("java.lang.Integer")) : class$java$lang$Integer, class$javax$media$jai$BorderExtender == null ? (class$javax$media$jai$BorderExtender = BorderDescriptor.class$("javax.media.jai.BorderExtender")) : class$javax$media$jai$BorderExtender};
    private static final Object[] paramDefaults = new Object[]{new Integer(0), new Integer(0), new Integer(0), new Integer(0), BorderExtender.createInstance(0)};
    static /* synthetic */ Class class$java$lang$Integer;
    static /* synthetic */ Class class$javax$media$jai$BorderExtender;

    public BorderDescriptor() {
        super(resources, 1, paramClasses, paramNames, paramDefaults);
    }

    public Object getInvalidRegion(String modeName, ParameterBlock oldParamBlock, RenderingHints oldHints, ParameterBlock newParamBlock, RenderingHints newHints, OperationNode node) {
        if (modeName == null || (this.getNumSources() > 0 || this.getNumParameters() > 0) && (oldParamBlock == null || newParamBlock == null)) {
            throw new IllegalArgumentException(JaiI18N.getString("BorderDescriptor6"));
        }
        int numSources = this.getNumSources();
        if (numSources > 0 && (oldParamBlock.getNumSources() != numSources || newParamBlock.getNumSources() != numSources)) {
            throw new IllegalArgumentException(JaiI18N.getString("BorderDescriptor7"));
        }
        int numParams = this.getParameterListDescriptor(modeName).getNumParameters();
        if (numParams > 0 && (oldParamBlock.getNumParameters() != numParams || newParamBlock.getNumParameters() != numParams)) {
            throw new IllegalArgumentException(JaiI18N.getString("BorderDescriptor8"));
        }
        if (!modeName.equalsIgnoreCase("rendered") || oldHints == null && newHints != null || oldHints != null && newHints == null || oldHints != null && !oldHints.equals(newHints) || !oldParamBlock.getSource(0).equals(newParamBlock.getSource(0)) || oldParamBlock.getIntParameter(0) != newParamBlock.getIntParameter(0) || oldParamBlock.getIntParameter(2) != newParamBlock.getIntParameter(2)) {
            return null;
        }
        Shape invalidRegion = null;
        if (!oldParamBlock.getObjectParameter(4).equals(newParamBlock.getObjectParameter(4))) {
            RenderedImage src = oldParamBlock.getRenderedSource(0);
            int leftPad = oldParamBlock.getIntParameter(0);
            int topPad = oldParamBlock.getIntParameter(2);
            Rectangle srcBounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
            Rectangle dstBounds = new Rectangle(srcBounds.x - leftPad, srcBounds.y - topPad, srcBounds.width + leftPad + oldParamBlock.getIntParameter(1), srcBounds.height + topPad + oldParamBlock.getIntParameter(3));
            Area invalidArea = new Area(dstBounds);
            invalidArea.subtract(new Area(srcBounds));
            invalidRegion = invalidArea;
        } else if (newParamBlock.getIntParameter(1) < oldParamBlock.getIntParameter(1) && newParamBlock.getIntParameter(3) <= oldParamBlock.getIntParameter(3) || newParamBlock.getIntParameter(3) < oldParamBlock.getIntParameter(3) && newParamBlock.getIntParameter(1) <= oldParamBlock.getIntParameter(1)) {
            RenderedImage src = oldParamBlock.getRenderedSource(0);
            int leftPad = oldParamBlock.getIntParameter(0);
            int topPad = oldParamBlock.getIntParameter(2);
            Rectangle srcBounds = new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
            Rectangle oldBounds = new Rectangle(srcBounds.x - leftPad, srcBounds.y - topPad, srcBounds.width + leftPad + oldParamBlock.getIntParameter(1), srcBounds.height + topPad + oldParamBlock.getIntParameter(3));
            Rectangle newBounds = new Rectangle(srcBounds.x - leftPad, srcBounds.y - topPad, srcBounds.width + leftPad + newParamBlock.getIntParameter(1), srcBounds.height + topPad + newParamBlock.getIntParameter(3));
            Area invalidArea = new Area(oldBounds);
            invalidArea.subtract(new Area(newBounds));
            invalidRegion = invalidArea;
        } else {
            invalidRegion = new Rectangle();
        }
        return invalidRegion;
    }

    public static RenderedOp create(RenderedImage source0, Integer leftPad, Integer rightPad, Integer topPad, Integer bottomPad, BorderExtender type, RenderingHints hints) {
        ParameterBlockJAI pb = new ParameterBlockJAI("Border", "rendered");
        pb.setSource("source0", source0);
        pb.setParameter("leftPad", leftPad);
        pb.setParameter("rightPad", rightPad);
        pb.setParameter("topPad", topPad);
        pb.setParameter("bottomPad", bottomPad);
        pb.setParameter("type", type);
        return JAI.create("Border", pb, hints);
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

