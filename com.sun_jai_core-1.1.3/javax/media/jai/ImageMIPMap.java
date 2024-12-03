/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderableImage;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.media.jai.ImageJAI;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.MultiResolutionRenderableImage;
import javax.media.jai.PropertyChangeSupportJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.WritablePropertySourceImpl;

public class ImageMIPMap
implements ImageJAI {
    protected RenderedImage highestImage;
    protected RenderedImage currentImage;
    protected int currentLevel;
    protected RenderedOp downSampler;
    protected PropertyChangeSupportJAI eventManager;
    protected WritablePropertySourceImpl properties;

    protected ImageMIPMap() {
        this.currentLevel = 0;
        this.eventManager = null;
        this.properties = null;
        this.eventManager = new PropertyChangeSupportJAI(this);
        this.properties = new WritablePropertySourceImpl(null, null, this.eventManager);
    }

    public ImageMIPMap(RenderedImage image, AffineTransform transform, Interpolation interpolation) {
        this();
        if (image == null || transform == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(transform);
        pb.add(interpolation);
        this.downSampler = JAI.create("affine", pb);
        this.downSampler.removeSources();
        this.currentImage = this.highestImage = image;
    }

    public ImageMIPMap(RenderedImage image, RenderedOp downSampler) {
        this();
        if (image == null || downSampler == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.currentImage = this.highestImage = image;
        this.downSampler = downSampler;
    }

    public ImageMIPMap(RenderedOp downSampler) {
        block6: {
            Object src;
            this();
            if (downSampler == null) {
                throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
            }
            if (downSampler.getNumSources() == 0) {
                throw new IllegalArgumentException(JaiI18N.getString("ImageMIPMap0"));
            }
            RenderedOp op = downSampler;
            while ((src = op.getNodeSource(0)) instanceof RenderedOp) {
                RenderedOp srcOp = (RenderedOp)src;
                if (srcOp.getNumSources() == 0) {
                    this.highestImage = srcOp;
                    op.removeSources();
                    break block6;
                }
                op = srcOp;
            }
            if (src instanceof RenderedImage) {
                this.highestImage = (RenderedImage)src;
                op.removeSources();
            } else {
                throw new IllegalArgumentException(JaiI18N.getString("ImageMIPMap1"));
            }
        }
        this.currentImage = this.highestImage;
        this.downSampler = downSampler;
    }

    public String[] getPropertyNames() {
        return this.properties.getPropertyNames();
    }

    public String[] getPropertyNames(String prefix) {
        return this.properties.getPropertyNames(prefix);
    }

    public Class getPropertyClass(String name) {
        return this.properties.getPropertyClass(name);
    }

    public Object getProperty(String name) {
        return this.properties.getProperty(name);
    }

    public void setProperty(String name, Object value) {
        this.properties.setProperty(name, value);
    }

    public void removeProperty(String name) {
        this.properties.removeProperty(name);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.eventManager.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.eventManager.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.eventManager.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.eventManager.removePropertyChangeListener(propertyName, listener);
    }

    public int getCurrentLevel() {
        return this.currentLevel;
    }

    public RenderedImage getCurrentImage() {
        return this.currentImage;
    }

    public RenderedImage getImage(int level) {
        if (level < 0) {
            return null;
        }
        if (level < this.currentLevel) {
            this.currentImage = this.highestImage;
            this.currentLevel = 0;
        }
        while (this.currentLevel < level) {
            this.getDownImage();
        }
        return this.currentImage;
    }

    public RenderedImage getDownImage() {
        ++this.currentLevel;
        RenderedOp op = this.duplicate(this.downSampler, this.vectorize(this.currentImage));
        this.currentImage = op.getRendering();
        return this.currentImage;
    }

    protected RenderedOp duplicate(RenderedOp op, Vector images) {
        if (images == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        op = new RenderedOp(op.getRegistry(), op.getOperationName(), op.getParameterBlock(), op.getRenderingHints());
        ParameterBlock pb = new ParameterBlock();
        pb.setParameters(op.getParameters());
        Vector srcs = op.getSources();
        int numSrcs = srcs.size();
        if (numSrcs == 0) {
            pb.setSources(images);
        } else {
            pb.addSource(this.duplicate((RenderedOp)srcs.elementAt(0), images));
            for (int i = 1; i < numSrcs; ++i) {
                pb.addSource(srcs.elementAt(i));
            }
        }
        op.setParameterBlock(pb);
        return op;
    }

    public RenderableImage getAsRenderable(int numImages, float minX, float minY, float height) {
        RenderedOp op;
        Vector<RenderedImage> v = new Vector<RenderedImage>();
        v.add(this.currentImage);
        RenderedImage image = this.currentImage;
        for (int i = 1; i < numImages && (image = (op = this.duplicate(this.downSampler, this.vectorize(image))).getRendering()).getWidth() > 1 && image.getHeight() > 1; ++i) {
            v.add(image);
        }
        return new MultiResolutionRenderableImage(v, minX, minY, height);
    }

    public RenderableImage getAsRenderable() {
        return this.getAsRenderable(1, 0.0f, 0.0f, 1.0f);
    }

    protected final Vector vectorize(RenderedImage image) {
        Vector<RenderedImage> v = new Vector<RenderedImage>(1);
        v.add(image);
        return v;
    }

    protected final Vector vectorize(RenderedImage im1, RenderedImage im2) {
        Vector<RenderedImage> v = new Vector<RenderedImage>(2);
        v.add(im1);
        v.add(im2);
        return v;
    }
}

