/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.ImageUtil;
import com.sun.media.jai.util.PropertyUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.media.jai.CollectionChangeEvent;
import javax.media.jai.CollectionImage;
import javax.media.jai.CollectionImageFactory;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.OperationNode;
import javax.media.jai.OperationNodeSupport;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.PropertyChangeSupportJAI;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.PropertySource;
import javax.media.jai.PropertySourceChangeEvent;
import javax.media.jai.RenderedOp;
import javax.media.jai.RenderingChangeEvent;
import javax.media.jai.WritablePropertySourceImpl;
import javax.media.jai.registry.CIFRegistry;
import javax.media.jai.registry.RCIFRegistry;

public class CollectionOp
extends CollectionImage
implements OperationNode,
PropertyChangeListener {
    protected OperationNodeSupport nodeSupport;
    protected PropertySource thePropertySource;
    protected boolean isRenderable = false;
    private transient RenderingHints oldHints;
    private static Set nodeEventNames = null;

    public CollectionOp(OperationRegistry registry, String opName, ParameterBlock pb, RenderingHints hints, boolean isRenderable) {
        if (opName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        pb = pb == null ? new ParameterBlock() : (ParameterBlock)pb.clone();
        if (hints != null) {
            hints = (RenderingHints)hints.clone();
        }
        this.eventManager = new PropertyChangeSupportJAI(this);
        this.properties = new WritablePropertySourceImpl(null, null, this.eventManager);
        this.nodeSupport = new OperationNodeSupport(this.getRegistryModeName(), opName, registry, pb, hints, this.eventManager);
        this.isRenderable = isRenderable;
        this.addPropertyChangeListener("OperationName", this);
        this.addPropertyChangeListener("OperationRegistry", this);
        this.addPropertyChangeListener("ParameterBlock", this);
        this.addPropertyChangeListener("Sources", this);
        this.addPropertyChangeListener("Parameters", this);
        this.addPropertyChangeListener("RenderingHints", this);
        Vector<Object> nodeSources = pb.getSources();
        if (nodeSources != null) {
            Iterator<Object> it = nodeSources.iterator();
            while (it.hasNext()) {
                Object src = it.next();
                if (src instanceof CollectionImage) {
                    ((CollectionImage)src).addSink(this);
                    continue;
                }
                if (!(src instanceof PlanarImage)) continue;
                ((PlanarImage)src).addSink(this);
            }
        }
    }

    public CollectionOp(OperationRegistry registry, String opName, ParameterBlock pb, RenderingHints hints) {
        this(registry, opName, pb, hints, false);
    }

    public CollectionOp(String opName, ParameterBlock pb, RenderingHints hints) {
        this(null, opName, pb, hints);
    }

    public CollectionOp(OperationRegistry registry, String opName, ParameterBlock pb) {
        this(registry, opName, pb, null);
    }

    public boolean isRenderable() {
        return this.isRenderable;
    }

    public String getRegistryModeName() {
        return this.isRenderable ? "renderableCollection" : "collection";
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
        Vector<Object> newSources;
        Vector<Object> nodeSources = this.nodeSupport.getParameterBlock().getSources();
        if (nodeSources != null && nodeSources.size() > 0) {
            Iterator<Object> it = nodeSources.iterator();
            while (it.hasNext()) {
                Object src = it.next();
                if (src instanceof PlanarImage) {
                    ((PlanarImage)src).removeSink(this);
                    continue;
                }
                if (!(src instanceof CollectionImage)) continue;
                ((CollectionImage)src).removeSink(this);
            }
        }
        if (pb != null && (newSources = pb.getSources()) != null && newSources.size() > 0) {
            Iterator<Object> it = newSources.iterator();
            while (it.hasNext()) {
                Object src = it.next();
                if (src instanceof PlanarImage) {
                    ((PlanarImage)src).addSink(this);
                    continue;
                }
                if (!(src instanceof CollectionImage)) continue;
                ((CollectionImage)src).addSink(this);
            }
        }
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

    public Collection getCollection() {
        this.createCollection();
        return this.imageCollection;
    }

    private synchronized void createCollection() {
        if (this.imageCollection == null) {
            this.imageCollection = this.createInstance(true);
        }
    }

    public synchronized Collection createInstance() {
        return this.createInstance(false);
    }

    private synchronized Collection createInstance(boolean isChainFrozen) {
        ParameterBlock args = ImageUtil.evaluateParameters(this.nodeSupport.getParameterBlock());
        ParameterBlock pb = new ParameterBlock();
        pb.setParameters(args.getParameters());
        int numSources = args.getNumSources();
        for (int i = 0; i < numSources; ++i) {
            Object source = args.getSource(i);
            Object src = null;
            if (source instanceof RenderedOp) {
                src = isChainFrozen ? ((RenderedOp)source).getRendering() : ((RenderedOp)source).createInstance();
            } else if (source instanceof CollectionOp) {
                CollectionOp co = (CollectionOp)source;
                src = isChainFrozen ? co.getCollection() : co.createInstance();
            } else {
                src = source instanceof RenderedImage || source instanceof RenderableImage || source instanceof Collection ? source : source;
            }
            pb.addSource(src);
        }
        CollectionImage instance = null;
        if (this.isRenderable) {
            instance = RCIFRegistry.create(this.nodeSupport.getRegistry(), this.nodeSupport.getOperationName(), pb);
        } else {
            CollectionImageFactory cif = CIFRegistry.get(this.nodeSupport.getRegistry(), this.nodeSupport.getOperationName());
            instance = cif.create(pb, this.nodeSupport.getRenderingHints());
            if (instance != null) {
                instance.setImageFactory(cif);
            }
        }
        if (instance == null) {
            throw new RuntimeException(JaiI18N.getString("CollectionOp0"));
        }
        this.oldHints = this.nodeSupport.getRenderingHints() == null ? null : (RenderingHints)this.nodeSupport.getRenderingHints().clone();
        return instance;
    }

    public Collection createRendering(RenderContext renderContext) {
        if (!this.isRenderable) {
            return this;
        }
        RenderingHints mergedHints = JAI.mergeRenderingHints(this.nodeSupport.getRenderingHints(), renderContext.getRenderingHints());
        if (mergedHints != renderContext.getRenderingHints()) {
            renderContext = (RenderContext)renderContext.clone();
            renderContext.setRenderingHints(mergedHints);
        }
        return this.renderCollection(this.imageCollection, renderContext);
    }

    private Collection renderCollection(Collection cIn, RenderContext rc) {
        Collection<Object> cOut;
        if (cIn == null || rc == null) {
            throw new IllegalArgumentException();
        }
        if (cIn instanceof Set) {
            cOut = Collections.synchronizedSet(new HashSet(cIn.size()));
        } else if (cIn instanceof SortedSet) {
            Comparator comparator = ((SortedSet)cIn).comparator();
            cOut = Collections.synchronizedSortedSet(new TreeSet(comparator));
        } else {
            cOut = new Vector(cIn.size());
        }
        Iterator it = cIn.iterator();
        while (it.hasNext()) {
            Object element = it.next();
            if (element instanceof RenderableImage) {
                cOut.add(((RenderableImage)((Object)cIn)).createRendering(rc));
                continue;
            }
            if (element instanceof Collection) {
                cOut.add(this.renderCollection((Collection)element, rc));
                continue;
            }
            cOut.add(element);
        }
        return cOut;
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (this.isRenderable()) {
            return;
        }
        Object evtSrc = evt.getSource();
        Vector<Object> nodeSources = this.nodeSupport.getParameterBlock().getSources();
        String propName = evt.getPropertyName().toLowerCase(Locale.ENGLISH);
        if (this.imageCollection != null && (evt instanceof PropertyChangeEventJAI && evtSrc == this && !(evt instanceof PropertySourceChangeEvent) && nodeEventNames.contains(propName) || (evt instanceof CollectionChangeEvent || evt instanceof RenderingChangeEvent) && nodeSources.contains(evtSrc))) {
            CollectionImageFactory oldCIF;
            Collection theOldCollection = this.imageCollection;
            boolean fireEvent = false;
            if (!(this.imageCollection instanceof CollectionImage)) {
                fireEvent = true;
                this.imageCollection = null;
            } else if (evtSrc == this && (propName.equals("operationname") || propName.equals("operationregistry"))) {
                fireEvent = true;
                this.imageCollection = null;
            } else if (evt instanceof CollectionChangeEvent) {
                fireEvent = true;
                oldCIF = ((CollectionImage)theOldCollection).getImageFactory();
                if (oldCIF == null) {
                    this.imageCollection = null;
                } else {
                    CollectionChangeEvent ccEvent = (CollectionChangeEvent)evt;
                    Vector parameters = this.nodeSupport.getParameterBlock().getParameters();
                    parameters = ImageUtil.evaluateParameters(parameters);
                    ParameterBlock oldPB = new ParameterBlock((Vector)nodeSources.clone(), parameters);
                    ParameterBlock newPB = new ParameterBlock((Vector)nodeSources.clone(), parameters);
                    int sourceIndex = nodeSources.indexOf(ccEvent.getSource());
                    oldPB.setSource(ccEvent.getOldValue(), sourceIndex);
                    newPB.setSource(ccEvent.getNewValue(), sourceIndex);
                    this.imageCollection = oldCIF.update(oldPB, this.oldHints, newPB, this.oldHints, (CollectionImage)theOldCollection, this);
                }
            } else {
                oldCIF = ((CollectionImage)theOldCollection).getImageFactory();
                if (oldCIF == null || oldCIF != CIFRegistry.get(this.nodeSupport.getRegistry(), this.nodeSupport.getOperationName())) {
                    this.imageCollection = null;
                    fireEvent = true;
                } else {
                    ParameterBlock oldPB = null;
                    ParameterBlock newPB = null;
                    boolean updateCollection = false;
                    if (propName.equals("parameterblock")) {
                        oldPB = (ParameterBlock)evt.getOldValue();
                        newPB = (ParameterBlock)evt.getNewValue();
                        updateCollection = true;
                    } else if (propName.equals("sources")) {
                        Vector<Object> params = this.nodeSupport.getParameterBlock().getParameters();
                        oldPB = new ParameterBlock((Vector)evt.getOldValue(), params);
                        newPB = new ParameterBlock((Vector)evt.getNewValue(), params);
                        updateCollection = true;
                    } else if (propName.equals("parameters")) {
                        oldPB = new ParameterBlock(nodeSources, (Vector)evt.getOldValue());
                        newPB = new ParameterBlock(nodeSources, (Vector)evt.getNewValue());
                        updateCollection = true;
                    } else if (propName.equals("renderinghints")) {
                        oldPB = newPB = this.nodeSupport.getParameterBlock();
                        updateCollection = true;
                    } else if (evt instanceof RenderingChangeEvent) {
                        int renderingIndex = nodeSources.indexOf(evt.getSource());
                        Vector oldSources = (Vector)nodeSources.clone();
                        Vector newSources = (Vector)nodeSources.clone();
                        oldSources.set(renderingIndex, evt.getOldValue());
                        newSources.set(renderingIndex, evt.getNewValue());
                        Vector<Object> params = this.nodeSupport.getParameterBlock().getParameters();
                        oldPB = new ParameterBlock(oldSources, params);
                        newPB = new ParameterBlock(newSources, params);
                        updateCollection = true;
                    }
                    if (updateCollection) {
                        RenderingHints newHints;
                        fireEvent = true;
                        this.imageCollection = oldCIF.update(oldPB = ImageUtil.evaluateParameters(oldPB), this.oldHints, newPB = ImageUtil.evaluateParameters(newPB), newHints = this.nodeSupport.getRenderingHints(), (CollectionImage)theOldCollection, this);
                        if (this.imageCollection != null) {
                            this.oldHints = newHints;
                        }
                    }
                }
            }
            this.getCollection();
            if (fireEvent) {
                this.resetProperties(true);
                CollectionChangeEvent ccEvent = new CollectionChangeEvent(this, theOldCollection, this.imageCollection);
                this.eventManager.firePropertyChange(ccEvent);
                Set sinks = this.getSinks();
                if (sinks != null) {
                    Iterator it = sinks.iterator();
                    while (it.hasNext()) {
                        Object sink = it.next();
                        if (!(sink instanceof PropertyChangeListener)) continue;
                        ((PropertyChangeListener)sink).propertyChange(ccEvent);
                    }
                }
            }
        }
    }

    private synchronized void createPropertySource() {
        if (this.thePropertySource == null) {
            this.getCollection();
            PropertySource defaultPS = null;
            if (this.imageCollection instanceof PropertySource) {
                defaultPS = new PropertySource(){

                    public String[] getPropertyNames() {
                        return ((PropertySource)((Object)CollectionOp.this.imageCollection)).getPropertyNames();
                    }

                    public String[] getPropertyNames(String prefix) {
                        return PropertyUtil.getPropertyNames(this.getPropertyNames(), prefix);
                    }

                    public Class getPropertyClass(String name) {
                        return null;
                    }

                    public Object getProperty(String name) {
                        return ((PropertySource)((Object)CollectionOp.this.imageCollection)).getProperty(name);
                    }
                };
            }
            this.thePropertySource = this.nodeSupport.getPropertySource(this, defaultPS);
            this.properties.addProperties(this.thePropertySource);
        }
    }

    protected synchronized void resetProperties(boolean resetPropertySource) {
        this.properties.clearCachedProperties();
        if (resetPropertySource && this.thePropertySource != null) {
            this.properties.removePropertySource(this.thePropertySource);
            this.thePropertySource = null;
        }
    }

    public synchronized String[] getPropertyNames() {
        this.createPropertySource();
        return this.properties.getPropertyNames();
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

    public int size() {
        this.createCollection();
        return this.imageCollection.size();
    }

    public boolean isEmpty() {
        this.createCollection();
        return this.imageCollection.isEmpty();
    }

    public boolean contains(Object o) {
        this.createCollection();
        return this.imageCollection.contains(o);
    }

    public Iterator iterator() {
        this.createCollection();
        return this.imageCollection.iterator();
    }

    public Object[] toArray() {
        this.createCollection();
        return this.imageCollection.toArray();
    }

    public Object[] toArray(Object[] a) {
        this.createCollection();
        return this.imageCollection.toArray(a);
    }

    public boolean add(Object o) {
        this.createCollection();
        return this.imageCollection.add(o);
    }

    public boolean remove(Object o) {
        this.createCollection();
        return this.imageCollection.remove(o);
    }

    public boolean containsAll(Collection c) {
        this.createCollection();
        return this.imageCollection.containsAll(c);
    }

    public boolean addAll(Collection c) {
        this.createCollection();
        return this.imageCollection.addAll(c);
    }

    public boolean removeAll(Collection c) {
        this.createCollection();
        return this.imageCollection.removeAll(c);
    }

    public boolean retainAll(Collection c) {
        this.createCollection();
        return this.imageCollection.retainAll(c);
    }

    public void clear() {
        this.createCollection();
        this.imageCollection.clear();
    }

    static {
        nodeEventNames = new HashSet();
        nodeEventNames.add("operationname");
        nodeEventNames.add("operationregistry");
        nodeEventNames.add("parameterblock");
        nodeEventNames.add("sources");
        nodeEventNames.add("parameters");
        nodeEventNames.add("renderinghints");
    }
}

