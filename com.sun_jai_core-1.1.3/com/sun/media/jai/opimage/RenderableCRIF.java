/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.lang.ref.SoftReference;
import java.util.Hashtable;
import java.util.Vector;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageMIPMap;
import javax.media.jai.MultiResolutionRenderableImage;
import javax.media.jai.RenderedOp;

public class RenderableCRIF
extends CRIFImpl {
    private Hashtable mresTable = null;

    private static final Object getKey(ParameterBlock paramBlock) {
        String key = new String();
        key = key + String.valueOf(paramBlock.getRenderedSource(0).hashCode());
        key = key + RenderableCRIF.getKey((RenderedOp)paramBlock.getObjectParameter(0));
        key = key + String.valueOf(paramBlock.getIntParameter(1));
        key = key + String.valueOf(paramBlock.getFloatParameter(2));
        key = key + String.valueOf(paramBlock.getFloatParameter(3));
        key = key + String.valueOf(paramBlock.getFloatParameter(4));
        return key;
    }

    private static final String getKey(RenderedOp op) {
        String key = new String(String.valueOf(op.hashCode()));
        ParameterBlock pb = op.getParameterBlock();
        int numSources = pb.getNumSources();
        for (int s = 0; s < numSources; ++s) {
            RenderedImage src = pb.getRenderedSource(s);
            key = src instanceof RenderedOp ? key + RenderableCRIF.getKey((RenderedOp)src) : key + String.valueOf(src.hashCode());
        }
        int numParameters = pb.getNumParameters();
        for (int p = 0; p < numParameters; ++p) {
            key = key + pb.getObjectParameter(p).toString();
        }
        return key;
    }

    private RenderableImage createRenderable(ParameterBlock paramBlock) {
        if (this.mresTable == null) {
            this.mresTable = new Hashtable();
        }
        Object key = RenderableCRIF.getKey(paramBlock);
        SoftReference ref = (SoftReference)this.mresTable.get(key);
        RenderableImage mres = null;
        if (ref != null && (mres = (RenderableImage)ref.get()) == null) {
            this.mresTable.remove(key);
        }
        if (mres == null) {
            RenderedImage source = paramBlock.getRenderedSource(0);
            RenderedOp downSampler = (RenderedOp)paramBlock.getObjectParameter(0);
            int maxLowResDim = paramBlock.getIntParameter(1);
            float minX = paramBlock.getFloatParameter(2);
            float minY = paramBlock.getFloatParameter(3);
            float height = paramBlock.getFloatParameter(4);
            ImageMIPMap pyramid = new ImageMIPMap(source, downSampler);
            Vector<RenderedImage> sourceVector = new Vector<RenderedImage>();
            RenderedImage currentImage = pyramid.getCurrentImage();
            sourceVector.add(currentImage);
            while (currentImage.getWidth() > maxLowResDim || currentImage.getHeight() > maxLowResDim) {
                RenderedImage nextImage = pyramid.getDownImage();
                if (nextImage.getWidth() >= currentImage.getWidth() || nextImage.getHeight() >= currentImage.getHeight()) {
                    throw new IllegalArgumentException(JaiI18N.getString("RenderableCRIF0"));
                }
                sourceVector.add(nextImage);
                currentImage = nextImage;
            }
            mres = new MultiResolutionRenderableImage(sourceVector, minX, minY, height);
            this.mresTable.put(key, new SoftReference<RenderableImage>(mres));
        }
        return mres;
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        return paramBlock.getRenderedSource(0);
    }

    public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock) {
        RenderableImage mres = this.createRenderable(paramBlock);
        return mres.createRendering(renderContext);
    }

    public Rectangle2D getBounds2D(ParameterBlock paramBlock) {
        RenderableImage mres = this.createRenderable(paramBlock);
        return new Rectangle2D.Float(mres.getMinX(), mres.getMinY(), mres.getWidth(), mres.getHeight());
    }
}

