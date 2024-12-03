/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.PropertyUtil;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Vector;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.OperationNode;
import javax.media.jai.OperationNodeSupport;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PropertyChangeSupportJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.PropertySource;
import javax.media.jai.RegistryMode;
import javax.media.jai.RenderedOp;
import javax.media.jai.WritablePropertySource;
import javax.media.jai.WritablePropertySourceImpl;
import javax.media.jai.registry.CRIFRegistry;
import javax.media.jai.util.CaselessStringKey;

public class RenderableOp
implements RenderableImage,
OperationNode,
WritablePropertySource,
Serializable {
    protected PropertyChangeSupportJAI eventManager = null;
    protected WritablePropertySourceImpl properties = null;
    protected OperationNodeSupport nodeSupport;
    protected transient PropertySource thePropertySource;
    protected transient ContextualRenderedImageFactory crif = null;

    public RenderableOp(OperationRegistry registry, String opName, ParameterBlock pb, RenderingHints hints) {
        pb = pb == null ? new ParameterBlock() : (ParameterBlock)pb.clone();
        if (hints != null) {
            hints = (RenderingHints)hints.clone();
        }
        this.eventManager = new PropertyChangeSupportJAI(this);
        this.properties = new WritablePropertySourceImpl(null, null, this.eventManager);
        this.nodeSupport = new OperationNodeSupport(this.getRegistryModeName(), opName, registry, pb, hints, this.eventManager);
    }

    public RenderableOp(OperationRegistry registry, String opName, ParameterBlock pb) {
        this(registry, opName, pb, null);
    }

    public RenderableOp(String opName, ParameterBlock pb) {
        this(null, opName, pb);
    }

    public String getRegistryModeName() {
        return RegistryMode.getMode("renderable").getName();
    }

    public synchronized OperationRegistry getRegistry() {
        return this.nodeSupport.getRegistry();
    }

    public synchronized void setRegistry(OperationRegistry registry) {
        this.nodeSupport.setRegistry(registry);
    }

    public String getOperationName() {
        return this.nodeSupport.getOperationName();
    }

    public synchronized void setOperationName(String opName) {
        this.nodeSupport.setOperationName(opName);
    }

    public ParameterBlock getParameterBlock() {
        return (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
    }

    public synchronized void setParameterBlock(ParameterBlock pb) {
        this.nodeSupport.setParameterBlock(pb == null ? new ParameterBlock() : (ParameterBlock)pb.clone());
    }

    public RenderingHints getRenderingHints() {
        RenderingHints hints = this.nodeSupport.getRenderingHints();
        return hints == null ? null : (RenderingHints)hints.clone();
    }

    public synchronized void setRenderingHints(RenderingHints hints) {
        if (hints != null) {
            hints = (RenderingHints)hints.clone();
        }
        this.nodeSupport.setRenderingHints(hints);
    }

    private Vector getRenderableSources() {
        Vector<Object> sources = null;
        int numSrcs = this.nodeSupport.getParameterBlock().getNumSources();
        if (numSrcs > 0) {
            sources = new Vector<Object>();
            for (int i = 0; i < numSrcs; ++i) {
                Object o = this.nodeSupport.getParameterBlock().getSource(i);
                if (!(o instanceof RenderableImage)) continue;
                sources.add(o);
            }
        }
        return sources;
    }

    public Vector getSources() {
        return this.getRenderableSources();
    }

    private synchronized ContextualRenderedImageFactory findCRIF() {
        if (this.crif == null) {
            this.crif = CRIFRegistry.get(this.getRegistry(), this.getOperationName());
        }
        if (this.crif == null) {
            throw new RuntimeException(JaiI18N.getString("RenderableOp2"));
        }
        return this.crif;
    }

    public float getWidth() {
        this.findCRIF();
        ParameterBlock paramBlock = ImageUtil.evaluateParameters(this.nodeSupport.getParameterBlock());
        Rectangle2D boundingBox = this.crif.getBounds2D(paramBlock);
        return (float)boundingBox.getWidth();
    }

    public float getHeight() {
        this.findCRIF();
        ParameterBlock paramBlock = ImageUtil.evaluateParameters(this.nodeSupport.getParameterBlock());
        Rectangle2D boundingBox = this.crif.getBounds2D(paramBlock);
        return (float)boundingBox.getHeight();
    }

    public float getMinX() {
        this.findCRIF();
        ParameterBlock paramBlock = ImageUtil.evaluateParameters(this.nodeSupport.getParameterBlock());
        Rectangle2D boundingBox = this.crif.getBounds2D(paramBlock);
        return (float)boundingBox.getX();
    }

    public float getMinY() {
        this.findCRIF();
        ParameterBlock paramBlock = ImageUtil.evaluateParameters(this.nodeSupport.getParameterBlock());
        Rectangle2D boundingBox = this.crif.getBounds2D(paramBlock);
        return (float)boundingBox.getY();
    }

    public RenderedImage createDefaultRendering() {
        Dimension defaultDimension = null;
        RenderingHints hints = this.nodeSupport.getRenderingHints();
        if (hints != null && hints.containsKey(JAI.KEY_DEFAULT_RENDERING_SIZE)) {
            defaultDimension = (Dimension)hints.get(JAI.KEY_DEFAULT_RENDERING_SIZE);
        }
        if (defaultDimension == null || defaultDimension.width <= 0 && defaultDimension.height <= 0) {
            defaultDimension = JAI.getDefaultRenderingSize();
        }
        double sx = 1.0;
        double sy = 1.0;
        if (defaultDimension != null && (defaultDimension.width > 0 || defaultDimension.height > 0)) {
            if (defaultDimension.width > 0 && defaultDimension.height > 0) {
                sx = (float)defaultDimension.width / this.getWidth();
                sy = (float)defaultDimension.height / this.getHeight();
            } else {
                sx = defaultDimension.width > 0 ? (sy = (double)((float)defaultDimension.width / this.getWidth())) : (sy = (double)((float)defaultDimension.height / this.getHeight()));
            }
        }
        AffineTransform transform = AffineTransform.getScaleInstance(sx, sy);
        return this.createRendering(new RenderContext(transform));
    }

    public RenderedImage createScaledRendering(int w, int h, RenderingHints hints) {
        if (w == 0 && h == 0) {
            throw new IllegalArgumentException(JaiI18N.getString("RenderableOp3"));
        }
        if (w == 0) {
            w = Math.round((float)h * (this.getWidth() / this.getHeight()));
        } else if (h == 0) {
            h = Math.round((float)w * (this.getHeight() / this.getWidth()));
        }
        double sx = (double)w / (double)this.getWidth();
        double sy = (double)h / (double)this.getHeight();
        AffineTransform usr2dev = AffineTransform.getScaleInstance(sx, sy);
        RenderContext renderContext = new RenderContext(usr2dev, hints);
        return this.createRendering(renderContext);
    }

    public RenderedImage createRendering(RenderContext renderContext) {
        this.findCRIF();
        ParameterBlock nodePB = this.nodeSupport.getParameterBlock();
        Vector nodeParams = ImageUtil.evaluateParameters(nodePB.getParameters());
        ParameterBlock renderedPB = new ParameterBlock((Vector)nodePB.getSources().clone(), nodeParams);
        Vector sources = this.getRenderableSources();
        try {
            String[] propertyNames;
            RenderedImage rendering;
            RenderingHints hints;
            RenderingHints mergedHints;
            RenderContext rcIn = renderContext;
            RenderingHints nodeHints = this.nodeSupport.getRenderingHints();
            if (nodeHints != null && (mergedHints = JAI.mergeRenderingHints(nodeHints, hints = renderContext.getRenderingHints())) != hints) {
                rcIn = new RenderContext(renderContext.getTransform(), renderContext.getAreaOfInterest(), mergedHints);
            }
            if (sources != null) {
                Vector<Object> renderedSources = new Vector<Object>();
                for (int i = 0; i < sources.size(); ++i) {
                    RenderContext rcOut = this.crif.mapRenderContext(i, rcIn, renderedPB, this);
                    RenderableImage src = (RenderableImage)sources.elementAt(i);
                    RenderedImage renderedImage = src.createRendering(rcOut);
                    if (renderedImage == null) {
                        return null;
                    }
                    renderedSources.addElement(renderedImage);
                }
                if (renderedSources.size() > 0) {
                    renderedPB.setSources(renderedSources);
                }
            }
            if ((rendering = this.crif.create(rcIn, renderedPB)) instanceof RenderedOp) {
                rendering = ((RenderedOp)rendering).getRendering();
            }
            if (rendering != null && rendering instanceof WritablePropertySource && (propertyNames = this.getPropertyNames()) != null) {
                int j;
                WritablePropertySource wps = (WritablePropertySource)((Object)rendering);
                HashSet<CaselessStringKey> wpsNameSet = null;
                String[] wpsNames = wps.getPropertyNames();
                if (wpsNames != null) {
                    wpsNameSet = new HashSet<CaselessStringKey>();
                    for (j = 0; j < wpsNames.length; ++j) {
                        wpsNameSet.add(new CaselessStringKey(wpsNames[j]));
                    }
                }
                for (j = 0; j < propertyNames.length; ++j) {
                    Object value;
                    String name = propertyNames[j];
                    if (wpsNameSet != null && wpsNameSet.contains(new CaselessStringKey(name)) || (value = this.getProperty(name)) == null || value == Image.UndefinedProperty) continue;
                    wps.setProperty(name, value);
                }
            }
            return rendering;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public boolean isDynamic() {
        return false;
    }

    private synchronized void createPropertySource() {
        if (this.thePropertySource == null) {
            this.thePropertySource = this.nodeSupport.getPropertySource(this, null);
            this.properties.addProperties(this.thePropertySource);
        }
    }

    public String[] getPropertyNames() {
        this.createPropertySource();
        return this.properties.getPropertyNames();
    }

    public String[] getPropertyNames(String prefix) {
        return PropertyUtil.getPropertyNames(this.getPropertyNames(), prefix);
    }

    public Class getPropertyClass(String name) {
        this.createPropertySource();
        return this.properties.getPropertyClass(name);
    }

    public Object getProperty(String name) {
        this.createPropertySource();
        return this.properties.getProperty(name);
    }

    public void setProperty(String name, Object value) {
        this.createPropertySource();
        this.properties.setProperty(name, value);
    }

    public void removeProperty(String name) {
        this.createPropertySource();
        this.properties.removeProperty(name);
    }

    public synchronized Object getDynamicProperty(String name) {
        this.createPropertySource();
        return this.thePropertySource.getProperty(name);
    }

    public void addPropertyGenerator(PropertyGenerator pg) {
        this.nodeSupport.addPropertyGenerator(pg);
    }

    public synchronized void copyPropertyFromSource(String propertyName, int sourceIndex) {
        this.nodeSupport.copyPropertyFromSource(propertyName, sourceIndex);
    }

    public void suppressProperty(String name) {
        this.nodeSupport.suppressProperty(name);
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

    public Object getSource(int index) {
        Vector<Object> sources = this.nodeSupport.getParameterBlock().getSources();
        return sources.elementAt(index);
    }

    public void setSource(Object source, int index) {
        if (source == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ParameterBlock pb = (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
        pb.setSource(source, index);
        this.nodeSupport.setParameterBlock(pb);
    }

    public void removeSources() {
        ParameterBlock pb = (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
        pb.removeSources();
        this.nodeSupport.setParameterBlock(pb);
    }

    public byte getByteParameter(int index) {
        return this.nodeSupport.getParameterBlock().getByteParameter(index);
    }

    public char getCharParameter(int index) {
        return this.nodeSupport.getParameterBlock().getCharParameter(index);
    }

    public short getShortParameter(int index) {
        return this.nodeSupport.getParameterBlock().getShortParameter(index);
    }

    public int getIntParameter(int index) {
        return this.nodeSupport.getParameterBlock().getIntParameter(index);
    }

    public long getLongParameter(int index) {
        return this.nodeSupport.getParameterBlock().getLongParameter(index);
    }

    public float getFloatParameter(int index) {
        return this.nodeSupport.getParameterBlock().getFloatParameter(index);
    }

    public double getDoubleParameter(int index) {
        return this.nodeSupport.getParameterBlock().getDoubleParameter(index);
    }

    public Object getObjectParameter(int index) {
        return this.nodeSupport.getParameterBlock().getObjectParameter(index);
    }

    public void setParameter(byte param, int index) {
        this.setParameter(new Byte(param), index);
    }

    public void setParameter(char param, int index) {
        this.setParameter(new Character(param), index);
    }

    public void setParameter(short param, int index) {
        this.setParameter(new Short(param), index);
    }

    public void setParameter(int param, int index) {
        this.setParameter(new Integer(param), index);
    }

    public void setParameter(long param, int index) {
        this.setParameter(new Long(param), index);
    }

    public void setParameter(float param, int index) {
        this.setParameter(new Float(param), index);
    }

    public void setParameter(double param, int index) {
        this.setParameter(new Double(param), index);
    }

    public void setParameter(Object param, int index) {
        ParameterBlock pb = (ParameterBlock)this.nodeSupport.getParameterBlock().clone();
        pb.set(param, index);
        this.nodeSupport.setParameterBlock(pb);
    }
}

