/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.operator;

import com.sun.media.jai.util.PropertyGeneratorImpl;
import java.awt.Image;

class ComplexPropertyGenerator
extends PropertyGeneratorImpl {
    static /* synthetic */ Class class$java$lang$Boolean;
    static /* synthetic */ Class class$javax$media$jai$RenderedOp;
    static /* synthetic */ Class class$javax$media$jai$RenderableOp;

    public ComplexPropertyGenerator() {
        super(new String[]{"COMPLEX"}, new Class[]{class$java$lang$Boolean == null ? (class$java$lang$Boolean = ComplexPropertyGenerator.class$("java.lang.Boolean")) : class$java$lang$Boolean}, new Class[]{class$javax$media$jai$RenderedOp == null ? (class$javax$media$jai$RenderedOp = ComplexPropertyGenerator.class$("javax.media.jai.RenderedOp")) : class$javax$media$jai$RenderedOp, class$javax$media$jai$RenderableOp == null ? (class$javax$media$jai$RenderableOp = ComplexPropertyGenerator.class$("javax.media.jai.RenderableOp")) : class$javax$media$jai$RenderableOp});
    }

    public Object getProperty(String name, Object op) {
        this.validate(name, op);
        return name.equalsIgnoreCase("complex") ? Boolean.TRUE : Image.UndefinedProperty;
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

