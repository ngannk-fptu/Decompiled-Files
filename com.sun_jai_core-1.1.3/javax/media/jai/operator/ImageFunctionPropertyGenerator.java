/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.ImageFunction;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;

class ImageFunctionPropertyGenerator
extends PropertyGeneratorImpl {
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$javax$media$jai$RenderedOp;
    static /* synthetic */ Class class$javax$media$jai$RenderableOp;

    public ImageFunctionPropertyGenerator() {
        super(new String[]{"COMPLEX"}, new Class[]{class$java$lang$Boolean == null ? (class$java$lang$Boolean = ImageFunctionPropertyGenerator.class$("java.lang.Boolean")) : class$java$lang$Boolean}, new Class[]{class$javax$media$jai$RenderedOp == null ? (class$javax$media$jai$RenderedOp = ImageFunctionPropertyGenerator.class$("javax.media.jai.RenderedOp")) : class$javax$media$jai$RenderedOp, class$javax$media$jai$RenderableOp == null ? (class$javax$media$jai$RenderableOp = ImageFunctionPropertyGenerator.class$("javax.media.jai.RenderableOp")) : class$javax$media$jai$RenderableOp});
    }

    public Object getProperty(String name, Object opNode) {
        this.validate(name, opNode);
        if (name.equalsIgnoreCase("complex")) {
            if (opNode instanceof RenderedOp) {
                RenderedOp op = (RenderedOp)opNode;
                ParameterBlock pb = op.getParameterBlock();
                ImageFunction imFunc = (ImageFunction)pb.getObjectParameter(0);
                return imFunc.isComplex() ? Boolean.TRUE : Boolean.FALSE;
            }
            if (opNode instanceof RenderableOp) {
                RenderableOp op = (RenderableOp)opNode;
                ParameterBlock pb = op.getParameterBlock();
                ImageFunction imFunc = (ImageFunction)pb.getObjectParameter(0);
                return imFunc.isComplex() ? Boolean.TRUE : Boolean.FALSE;
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

