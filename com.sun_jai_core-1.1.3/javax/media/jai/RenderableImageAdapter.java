/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.PropertyUtil;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.beans.PropertyChangeListener;
import java.util.Vector;
import javax.media.jai.JaiI18N;
import javax.media.jai.PropertyChangeSupportJAI;
import javax.media.jai.RenderedImageAdapter;
import javax.media.jai.WritablePropertySource;
import javax.media.jai.WritablePropertySourceImpl;

public final class RenderableImageAdapter
implements RenderableImage,
WritablePropertySource {
    private RenderableImage im;
    private PropertyChangeSupportJAI eventManager = null;
    private WritablePropertySourceImpl properties = null;

    public static RenderableImageAdapter wrapRenderableImage(RenderableImage im) {
        if (im == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (im instanceof RenderableImageAdapter) {
            return (RenderableImageAdapter)im;
        }
        return new RenderableImageAdapter(im);
    }

    public RenderableImageAdapter(RenderableImage im) {
        if (im == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.im = im;
        this.eventManager = new PropertyChangeSupportJAI(this);
        this.properties = new WritablePropertySourceImpl(null, null, this.eventManager);
    }

    public final RenderableImage getWrappedImage() {
        return this.im;
    }

    public final Vector getSources() {
        return this.im.getSources();
    }

    public final Object getProperty(String name) {
        Object property = this.properties.getProperty(name);
        if (property == Image.UndefinedProperty) {
            property = this.im.getProperty(name);
        }
        return property;
    }

    public Class getPropertyClass(String name) {
        Object propValue;
        Class<?> propClass = this.properties.getPropertyClass(name);
        if (propClass == null && (propValue = this.getProperty(name)) != Image.UndefinedProperty) {
            propClass = propValue.getClass();
        }
        return propClass;
    }

    public final String[] getPropertyNames() {
        return RenderedImageAdapter.mergePropertyNames(this.properties.getPropertyNames(), this.im.getPropertyNames());
    }

    public String[] getPropertyNames(String prefix) {
        return PropertyUtil.getPropertyNames(this.getPropertyNames(), prefix);
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

    public final float getWidth() {
        return this.im.getWidth();
    }

    public final float getHeight() {
        return this.im.getHeight();
    }

    public final float getMinX() {
        return this.im.getMinX();
    }

    public final float getMinY() {
        return this.im.getMinY();
    }

    public final boolean isDynamic() {
        return this.im.isDynamic();
    }

    public final RenderedImage createScaledRendering(int w, int h, RenderingHints hints) {
        return this.im.createScaledRendering(w, h, hints);
    }

    public final RenderedImage createDefaultRendering() {
        return this.im.createDefaultRendering();
    }

    public final RenderedImage createRendering(RenderContext renderContext) {
        return this.im.createRendering(renderContext);
    }
}

