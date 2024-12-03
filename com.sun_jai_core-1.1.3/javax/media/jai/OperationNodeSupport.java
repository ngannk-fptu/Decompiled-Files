/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.RenderingHints;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import javax.media.jai.DeferredData;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.OperationNode;
import javax.media.jai.OperationRegistry;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertyChangeEventJAI;
import javax.media.jai.PropertyChangeSupportJAI;
import javax.media.jai.PropertyEnvironment;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.PropertySource;
import javax.media.jai.RegistryMode;
import javax.media.jai.remote.SerializableState;
import javax.media.jai.remote.SerializerFactory;

public class OperationNodeSupport
implements Serializable {
    private static final int PB_EQUAL = 0;
    private static final int PB_SOURCES_DIFFER = 1;
    private static final int PB_PARAMETERS_DIFFER = 2;
    private static final int PB_DIFFER = 3;
    private String registryModeName;
    private String opName;
    private transient OperationRegistry registry;
    private transient ParameterBlock pb;
    private transient RenderingHints hints;
    private PropertyChangeSupportJAI eventManager;
    private transient PropertyEnvironment propertySource = null;
    private Vector localPropEnv = new Vector();
    private Hashtable paramObservers = new Hashtable();

    private static int compare(ParameterBlock pb1, ParameterBlock pb2) {
        if (pb1 == null && pb2 == null) {
            return 0;
        }
        if (pb1 == null && pb2 != null || pb1 != null && pb2 == null) {
            return 3;
        }
        int result = 0;
        if (!OperationNodeSupport.equals(pb1.getSources(), pb2.getSources())) {
            result |= 1;
        }
        if (!OperationNodeSupport.equals(pb1.getParameters(), pb2.getParameters())) {
            result |= 2;
        }
        return result;
    }

    private static boolean equals(ParameterBlock pb1, ParameterBlock pb2) {
        return pb1 == null ? pb2 == null : OperationNodeSupport.equals(pb1.getSources(), pb2.getSources()) && OperationNodeSupport.equals(pb1.getParameters(), pb2.getParameters());
    }

    private static boolean equals(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    public OperationNodeSupport(String registryModeName, String opName, OperationRegistry registry, ParameterBlock pb, RenderingHints hints, PropertyChangeSupportJAI eventManager) {
        if (registryModeName == null || opName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.registryModeName = registryModeName;
        this.opName = opName;
        this.registry = registry == null ? JAI.getDefaultInstance().getOperationRegistry() : registry;
        this.pb = pb;
        this.hints = hints;
        this.eventManager = eventManager;
        if (pb != null) {
            this.updateObserverMap(pb.getParameters());
        }
    }

    private void updateObserverMap(Vector parameters) {
        if (parameters == null) {
            return;
        }
        int numParameters = parameters.size();
        for (int i = 0; i < numParameters; ++i) {
            Object oldObs;
            ParamObserver obs;
            Object parameter = parameters.get(i);
            Integer index = new Integer(i);
            if (parameter instanceof DeferredData) {
                obs = new ParamObserver(i, (DeferredData)parameter);
                oldObs = this.paramObservers.put(index, obs);
            } else {
                oldObs = this.paramObservers.remove(index);
            }
            if (oldObs == null) continue;
            obs = (ParamObserver)oldObs;
            obs.dd.deleteObserver(obs);
        }
    }

    public String getRegistryModeName() {
        return this.registryModeName;
    }

    public String getOperationName() {
        return this.opName;
    }

    public void setOperationName(String opName) {
        if (opName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (opName.equalsIgnoreCase(this.opName)) {
            return;
        }
        String oldOpName = this.opName;
        this.opName = opName;
        this.fireEvent("OperationName", oldOpName, opName);
        this.resetPropertyEnvironment(false);
    }

    public OperationRegistry getRegistry() {
        return this.registry;
    }

    public void setRegistry(OperationRegistry registry) {
        if (registry == null) {
            registry = JAI.getDefaultInstance().getOperationRegistry();
        }
        if (registry != this.registry) {
            OperationRegistry oldRegistry = this.registry;
            this.registry = registry;
            this.fireEvent("OperationRegistry", oldRegistry, registry);
            this.resetPropertyEnvironment(false);
        }
    }

    public ParameterBlock getParameterBlock() {
        return this.pb;
    }

    public void setParameterBlock(ParameterBlock pb) {
        int comparison = OperationNodeSupport.compare(this.pb, pb);
        if (comparison == 0) {
            return;
        }
        ParameterBlock oldPB = this.pb;
        this.pb = pb;
        if (pb != null) {
            this.updateObserverMap(pb.getParameters());
        }
        if (comparison == 1) {
            this.fireEvent("Sources", oldPB.getSources(), pb.getSources());
        } else if (comparison == 2) {
            this.fireEvent("Parameters", oldPB.getParameters(), pb.getParameters());
        } else {
            this.fireEvent("ParameterBlock", oldPB, pb);
        }
        this.resetPropertyEnvironment(false);
    }

    public RenderingHints getRenderingHints() {
        return this.hints;
    }

    public void setRenderingHints(RenderingHints hints) {
        if (OperationNodeSupport.equals(this.hints, hints)) {
            return;
        }
        RenderingHints oldHints = this.hints;
        this.hints = hints;
        this.fireEvent("RenderingHints", oldHints, hints);
        this.resetPropertyEnvironment(false);
    }

    public void addPropertyGenerator(PropertyGenerator pg) {
        if (pg == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.localPropEnv.add(pg);
        if (this.propertySource != null) {
            this.propertySource.addPropertyGenerator(pg);
        }
    }

    public void copyPropertyFromSource(String propertyName, int sourceIndex) {
        if (propertyName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.localPropEnv.add(new CopyDirective(propertyName, sourceIndex));
        if (this.propertySource != null) {
            this.propertySource.copyPropertyFromSource(propertyName, sourceIndex);
        }
    }

    public void suppressProperty(String name) {
        if (name == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.localPropEnv.add(name);
        if (this.propertySource != null) {
            this.propertySource.suppressProperty(name);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PropertySource getPropertySource(OperationNode opNode, PropertySource defaultPS) {
        if (opNode == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.propertySource == null) {
            OperationNodeSupport operationNodeSupport = this;
            synchronized (operationNodeSupport) {
                RegistryMode regMode = RegistryMode.getMode(this.registryModeName);
                this.propertySource = regMode != null && regMode.arePropertiesSupported() ? (PropertyEnvironment)this.registry.getPropertySource(opNode) : new PropertyEnvironment(this.pb != null ? this.pb.getSources() : null, null, null, null, opNode);
                this.updatePropertyEnvironment(this.propertySource);
            }
        }
        this.propertySource.setDefaultPropertySource(defaultPS);
        return this.propertySource;
    }

    public void resetPropertyEnvironment(boolean resetLocalEnvironment) {
        this.propertySource = null;
        if (resetLocalEnvironment) {
            this.localPropEnv.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updatePropertyEnvironment(PropertyEnvironment pe) {
        if (pe != null) {
            OperationNodeSupport operationNodeSupport = this;
            synchronized (operationNodeSupport) {
                int size = this.localPropEnv.size();
                for (int i = 0; i < size; ++i) {
                    Object element = this.localPropEnv.get(i);
                    if (element instanceof String) {
                        pe.suppressProperty((String)element);
                        continue;
                    }
                    if (element instanceof CopyDirective) {
                        CopyDirective cd = (CopyDirective)element;
                        pe.copyPropertyFromSource(cd.getName(), cd.getIndex());
                        continue;
                    }
                    if (!(element instanceof PropertyGenerator)) continue;
                    pe.addPropertyGenerator((PropertyGenerator)element);
                }
            }
        }
    }

    private void fireEvent(String propName, Object oldVal, Object newVal) {
        if (this.eventManager != null) {
            Object eventSource = this.eventManager.getPropertyChangeEventSource();
            PropertyChangeEventJAI evt = new PropertyChangeEventJAI(eventSource, propName, oldVal, newVal);
            this.eventManager.firePropertyChange(evt);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        int index;
        ParameterBlock pbClone = this.pb;
        boolean pbCloned = false;
        for (index = 0; index < pbClone.getNumSources(); ++index) {
            Object source = pbClone.getSource(index);
            if (source == null || source instanceof Serializable) continue;
            if (!pbCloned) {
                pbClone = (ParameterBlock)this.pb.clone();
                pbCloned = true;
            }
            if (source instanceof RenderedImage) {
                SerializableState serializableImage = SerializerFactory.getState(source, null);
                pbClone.setSource(serializableImage, index);
                continue;
            }
            throw new RuntimeException(source.getClass().getName() + JaiI18N.getString("OperationNodeSupport0"));
        }
        for (index = 0; index < pbClone.getNumParameters(); ++index) {
            Object parameter = pbClone.getObjectParameter(index);
            if (parameter == null || parameter instanceof Serializable) continue;
            if (!pbCloned) {
                pbClone = (ParameterBlock)this.pb.clone();
                pbCloned = true;
            }
            if (parameter instanceof Raster) {
                pbClone.set(SerializerFactory.getState(parameter, null), index);
                continue;
            }
            if (parameter instanceof RenderedImage) {
                RenderedImage ri = (RenderedImage)parameter;
                RenderingHints hints = new RenderingHints(null);
                hints.put(JAI.KEY_SERIALIZE_DEEP_COPY, new Boolean(true));
                pbClone.set(SerializerFactory.getState(ri, hints), index);
                continue;
            }
            throw new RuntimeException(parameter.getClass().getName() + JaiI18N.getString("OperationNodeSupport1"));
        }
        out.defaultWriteObject();
        out.writeObject(pbClone);
        out.writeObject(SerializerFactory.getState(this.hints, null));
    }

    private synchronized void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int index;
        in.defaultReadObject();
        this.pb = (ParameterBlock)in.readObject();
        SerializableState ss = (SerializableState)in.readObject();
        this.hints = (RenderingHints)ss.getObject();
        for (index = 0; index < this.pb.getNumSources(); ++index) {
            Object source = this.pb.getSource(index);
            if (!(source instanceof SerializableState)) continue;
            ss = (SerializableState)source;
            PlanarImage pi = PlanarImage.wrapRenderedImage((RenderedImage)ss.getObject());
            this.pb.setSource(pi, index);
        }
        for (index = 0; index < this.pb.getNumParameters(); ++index) {
            Object parameter = this.pb.getObjectParameter(index);
            if (!(parameter instanceof SerializableState)) continue;
            Object object = ((SerializableState)parameter).getObject();
            if (object instanceof Raster) {
                this.pb.set(object, index);
                continue;
            }
            if (object instanceof RenderedImage) {
                this.pb.set(PlanarImage.wrapRenderedImage((RenderedImage)object), index);
                continue;
            }
            this.pb.set(object, index);
        }
        this.registry = JAI.getDefaultInstance().getOperationRegistry();
    }

    private class ParamObserver
    implements Observer {
        final int paramIndex;
        final DeferredData dd;

        ParamObserver(int paramIndex, DeferredData dd) {
            if (dd == null) {
                throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
            }
            if (paramIndex < 0 || OperationNodeSupport.this.pb != null && paramIndex >= OperationNodeSupport.this.pb.getNumParameters()) {
                throw new ArrayIndexOutOfBoundsException();
            }
            this.paramIndex = paramIndex;
            this.dd = dd;
            dd.addObserver(this);
        }

        public synchronized void update(Observable o, Object arg) {
            if (o != this.dd) {
                return;
            }
            if (arg != null && OperationNodeSupport.this.eventManager != null) {
                Vector<Object> params = OperationNodeSupport.this.pb.getParameters();
                Vector oldParams = (Vector)params.clone();
                Vector newParams = (Vector)params.clone();
                oldParams.set(this.paramIndex, arg);
                newParams.set(this.paramIndex, this.dd.getData());
                OperationNodeSupport.this.fireEvent("Parameters", oldParams, newParams);
            }
        }
    }

    private class CopyDirective
    implements Serializable {
        private String name;
        private int index;

        CopyDirective(String name, int index) {
            if (name == null) {
                throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
            }
            this.name = name;
            this.index = index;
        }

        String getName() {
            return this.name;
        }

        int getIndex() {
            return this.index;
        }
    }
}

