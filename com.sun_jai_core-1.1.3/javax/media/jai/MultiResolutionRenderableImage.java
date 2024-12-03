/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;
import javax.media.jai.Interpolation;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.PropertyChangeSupportJAI;
import javax.media.jai.WritablePropertySource;
import javax.media.jai.WritablePropertySourceImpl;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;

public class MultiResolutionRenderableImage
implements WritablePropertySource,
RenderableImage,
Serializable {
    protected transient RenderedImage[] renderedSource;
    private int numSources;
    protected float aspect;
    protected float minX;
    protected float minY;
    protected float width;
    protected float height;
    protected PropertyChangeSupportJAI eventManager = new PropertyChangeSupportJAI(this);
    protected WritablePropertySourceImpl properties = new WritablePropertySourceImpl(null, null, this.eventManager);

    private MultiResolutionRenderableImage() {
    }

    public MultiResolutionRenderableImage(Vector renderedSources, float minX, float minY, float height) {
        this();
        if (height <= 0.0f) {
            throw new IllegalArgumentException(JaiI18N.getString("MultiResolutionRenderableImage0"));
        }
        this.numSources = renderedSources.size();
        this.renderedSource = new RenderedImage[this.numSources];
        for (int i = 0; i < this.numSources; ++i) {
            this.renderedSource[i] = (RenderedImage)renderedSources.elementAt(i);
        }
        int maxResWidth = this.renderedSource[0].getWidth();
        int maxResHeight = this.renderedSource[0].getHeight();
        this.aspect = (float)maxResWidth / (float)maxResHeight;
        this.minX = minX;
        this.width = height * this.aspect;
        this.minY = minY;
        this.height = height;
    }

    public Vector getSources() {
        return null;
    }

    public String[] getPropertyNames() {
        return this.properties.getPropertyNames();
    }

    public String[] getPropertyNames(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
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

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public float getMinX() {
        return this.minX;
    }

    public float getMaxX() {
        return this.minX + this.width;
    }

    public float getMinY() {
        return this.minY;
    }

    public float getMaxY() {
        return this.minY + this.height;
    }

    public boolean isDynamic() {
        return false;
    }

    public RenderedImage createScaledRendering(int width, int height, RenderingHints hints) {
        Object obj;
        int imw;
        int imh;
        int res;
        if (width <= 0 && height <= 0) {
            throw new IllegalArgumentException(JaiI18N.getString("MultiResolutionRenderableImage1"));
        }
        for (res = this.numSources - 1; res > 0 && !(height > 0 ? (imh = this.renderedSource[res].getHeight()) >= height : (imw = this.renderedSource[res].getWidth()) >= width); --res) {
        }
        RenderedImage source = this.renderedSource[res];
        if (width <= 0) {
            width = Math.round(height * source.getWidth() / source.getHeight());
        } else if (height <= 0) {
            height = Math.round(width * source.getHeight() / source.getWidth());
        }
        double sx = (double)width / (double)source.getWidth();
        double sy = (double)height / (double)source.getHeight();
        double tx = (double)(this.getMinX() - (float)source.getMinX()) * sx;
        double ty = (double)(this.getMinY() - (float)source.getMinY()) * sy;
        Interpolation interp = Interpolation.getInstance(0);
        if (hints != null && (obj = hints.get(JAI.KEY_INTERPOLATION)) != null) {
            interp = (Interpolation)obj;
        }
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add((float)sx);
        pb.add((float)sy);
        pb.add((float)tx);
        pb.add((float)ty);
        pb.add(interp);
        return JAI.create("scale", pb, null);
    }

    public RenderedImage createDefaultRendering() {
        return this.renderedSource[0];
    }

    public RenderedImage createRendering(RenderContext renderContext) {
        Object obj;
        int imh;
        int res;
        if (renderContext == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        AffineTransform usr2dev = renderContext.getTransform();
        RenderingHints hints = renderContext.getRenderingHints();
        int type = usr2dev.getType();
        if (type == 2 || type == 4) {
            int width = (int)Math.ceil(usr2dev.getScaleX() * (double)this.getWidth());
            int height = (int)Math.ceil(usr2dev.getScaleY() * (double)this.getHeight());
            return this.createScaledRendering(width, height, hints);
        }
        int height = (int)Math.ceil(Math.sqrt(usr2dev.getDeterminant()) * (double)this.getHeight());
        for (res = this.numSources - 1; res > 0 && (imh = this.renderedSource[res].getHeight()) < height; --res) {
        }
        RenderedImage source = this.renderedSource[res];
        double sx = (double)this.getWidth() / (double)source.getWidth();
        double sy = (double)this.getHeight() / (double)source.getHeight();
        AffineTransform transform = new AffineTransform();
        transform.translate(-source.getMinX(), -source.getMinY());
        transform.scale(sx, sy);
        transform.translate(this.getMinX(), this.getMinY());
        transform.preConcatenate(usr2dev);
        Interpolation interp = Interpolation.getInstance(0);
        if (hints != null && (obj = hints.get(JAI.KEY_INTERPOLATION)) != null) {
            interp = (Interpolation)obj;
        }
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add(transform);
        pb.add(interp);
        return JAI.create("affine", pb, null);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        Object[] sources = new Object[this.numSources];
        for (int i = 0; i < this.numSources; ++i) {
            sources[i] = this.renderedSource[i] instanceof Serializable ? this.renderedSource[i] : SerializerFactory.getState(this.renderedSource[i]);
        }
        out.defaultWriteObject();
        out.writeObject(sources);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Object[] source = (Object[])in.readObject();
        this.numSources = source.length;
        this.renderedSource = new RenderedImage[this.numSources];
        for (int i = 0; i < this.numSources; ++i) {
            if (source[i] instanceof SerializableState) {
                SerializableState ss = (SerializableState)source[i];
                this.renderedSource[i] = (RenderedImage)ss.getObject();
                continue;
            }
            this.renderedSource[i] = (RenderedImage)source[i];
        }
    }
}

