/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.DFTDataNature;
import javax.media.jai.operator.DFTDescriptor;

class DFTPropertyGenerator
extends PropertyGeneratorImpl {
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$javax$media$jai$RenderedOp;
    static /* synthetic */ Class class$javax$media$jai$RenderableOp;

    public DFTPropertyGenerator() {
        super(new String[]{"COMPLEX"}, new Class[]{class$java$lang$Boolean == null ? (class$java$lang$Boolean = DFTPropertyGenerator.class$("java.lang.Boolean")) : class$java$lang$Boolean}, new Class[]{class$javax$media$jai$RenderedOp == null ? (class$javax$media$jai$RenderedOp = DFTPropertyGenerator.class$("javax.media.jai.RenderedOp")) : class$javax$media$jai$RenderedOp, class$javax$media$jai$RenderableOp == null ? (class$javax$media$jai$RenderableOp = DFTPropertyGenerator.class$("javax.media.jai.RenderableOp")) : class$javax$media$jai$RenderableOp});
    }

    public Object getProperty(String name, Object opNode) {
        this.validate(name, opNode);
        if (name.equalsIgnoreCase("complex")) {
            if (opNode instanceof RenderedOp) {
                RenderedOp op = (RenderedOp)opNode;
                ParameterBlock pb = op.getParameterBlock();
                DFTDataNature dataNature = (DFTDataNature)pb.getObjectParameter(1);
                return dataNature.equals(DFTDescriptor.COMPLEX_TO_REAL) ? Boolean.FALSE : Boolean.TRUE;
            }
            if (opNode instanceof RenderableOp) {
                RenderableOp op = (RenderableOp)opNode;
                ParameterBlock pb = op.getParameterBlock();
                DFTDataNature dataNature = (DFTDataNature)pb.getObjectParameter(1);
                return dataNature.equals(DFTDescriptor.COMPLEX_TO_REAL) ? Boolean.FALSE : Boolean.TRUE;
            }
        }
        return Image.UndefinedProperty;
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

