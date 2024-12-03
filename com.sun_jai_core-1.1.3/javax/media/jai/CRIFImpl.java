/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.util.ImagingListener;

public abstract class CRIFImpl
implements ContextualRenderedImageFactory {
    protected String operationName = null;
    static /* synthetic */ Class class$javax$media$jai$OperationDescriptor;
    static /* synthetic */ Class class$java$awt$image$RenderedImage;

    public CRIFImpl() {
        this.operationName = null;
    }

    public CRIFImpl(String operationName) {
        this.operationName = operationName;
    }

    public abstract RenderedImage create(ParameterBlock var1, RenderingHints var2);

    public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock) {
        RenderingHints renderHints = renderContext.getRenderingHints();
        if (this.operationName != null) {
            PlanarImage rendering;
            OperationRegistry registry;
            OperationRegistry operationRegistry = registry = renderHints == null ? null : (OperationRegistry)renderHints.get(JAI.KEY_OPERATION_REGISTRY);
            if (registry == null) {
                rendering = JAI.create(this.operationName, paramBlock, renderHints);
            } else {
                OperationDescriptor odesc = (OperationDescriptor)registry.getDescriptor(class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = CRIFImpl.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor, this.operationName);
                if (odesc == null) {
                    throw new IllegalArgumentException(this.operationName + ": " + JaiI18N.getString("JAI0"));
                }
                if (!odesc.isModeSupported("rendered")) {
                    throw new IllegalArgumentException(this.operationName + ": " + JaiI18N.getString("JAI1"));
                }
                if (!(class$java$awt$image$RenderedImage == null ? (class$java$awt$image$RenderedImage = CRIFImpl.class$("java.awt.image.RenderedImage")) : class$java$awt$image$RenderedImage).isAssignableFrom(odesc.getDestClass("rendered"))) {
                    throw new IllegalArgumentException(this.operationName + ": " + JaiI18N.getString("JAI2"));
                }
                StringBuffer msg = new StringBuffer();
                if (!odesc.validateArguments("rendered", paramBlock = (ParameterBlock)paramBlock.clone(), msg)) {
                    throw new IllegalArgumentException(msg.toString());
                }
                rendering = new RenderedOp(registry, this.operationName, paramBlock, renderHints);
            }
            if (rendering != null) {
                if (rendering instanceof RenderedOp) {
                    try {
                        rendering = rendering.getRendering();
                    }
                    catch (Exception e) {
                        ImagingListener listener = ImageUtil.getImagingListener(renderHints);
                        String message = JaiI18N.getString("CRIFImpl0") + this.operationName;
                        listener.errorOccurred(message, e, this, false);
                    }
                }
                return rendering;
            }
        }
        return this.create(paramBlock, renderHints);
    }

    public RenderContext mapRenderContext(int i, RenderContext renderContext, ParameterBlock paramBlock, RenderableImage image) {
        return renderContext;
    }

    public Rectangle2D getBounds2D(ParameterBlock paramBlock) {
        Rectangle2D.Float box2;
        int numSources = paramBlock.getNumSources();
        if (numSources == 0) {
            return null;
        }
        RenderableImage src = paramBlock.getRenderableSource(0);
        Rectangle2D.Float box1 = new Rectangle2D.Float(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight());
        for (int i = 1; i < numSources && !(box1 = (Rectangle2D.Float)box1.createIntersection(box2 = new Rectangle2D.Float((src = paramBlock.getRenderableSource(i)).getMinX(), src.getMinY(), src.getWidth(), src.getHeight()))).isEmpty(); ++i) {
        }
        return box1;
    }

    public Object getProperty(ParameterBlock paramBlock, String name) {
        return Image.UndefinedProperty;
    }

    public String[] getPropertyNames() {
        return null;
    }

    public boolean isDynamic() {
        return false;
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

