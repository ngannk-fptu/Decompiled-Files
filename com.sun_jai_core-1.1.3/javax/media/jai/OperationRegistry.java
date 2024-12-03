/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.PropertyUtil;
import com.sun.media.jai.util.Service;
import java.awt.RenderingHints;
import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.media.jai.CollectionImage;
import javax.media.jai.CollectionImageFactory;
import javax.media.jai.DescriptorCache;
import javax.media.jai.FactoryCache;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.OperationGraph;
import javax.media.jai.OperationNode;
import javax.media.jai.OperationRegistrySpi;
import javax.media.jai.PartialOrderNode;
import javax.media.jai.PlanarImage;
import javax.media.jai.PropertyGenerator;
import javax.media.jai.PropertySource;
import javax.media.jai.RegistryElementDescriptor;
import javax.media.jai.RegistryFileParser;
import javax.media.jai.RegistryMode;
import javax.media.jai.RenderableOp;
import javax.media.jai.RenderedOp;
import javax.media.jai.ThreadSafeOperationRegistry;
import javax.media.jai.registry.CIFRegistry;
import javax.media.jai.registry.CRIFRegistry;
import javax.media.jai.registry.RIFRegistry;
import javax.media.jai.util.CaselessStringKey;
import javax.media.jai.util.ImagingException;
import javax.media.jai.util.ImagingListener;

public class OperationRegistry
implements Externalizable {
    static String JAI_REGISTRY_FILE = "META-INF/javax.media.jai.registryFile.jai";
    static String USR_REGISTRY_FILE = "META-INF/registryFile.jai";
    private Hashtable descriptors;
    private Hashtable factories;
    static /* synthetic */ Class class$javax$media$jai$OperationRegistry;
    static /* synthetic */ Class class$javax$media$jai$OperationRegistrySpi;
    static /* synthetic */ Class class$javax$media$jai$OperationDescriptor;

    private FactoryCache getFactoryCache(String modeName) {
        CaselessStringKey key = new CaselessStringKey(modeName);
        FactoryCache fc = (FactoryCache)this.factories.get(key);
        if (fc == null) {
            if (RegistryMode.getMode(modeName) != null) {
                fc = new FactoryCache(modeName);
                this.factories.put(key, fc);
            } else {
                throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry0", new Object[]{modeName}));
            }
        }
        return fc;
    }

    private DescriptorCache getDescriptorCache(String modeName) {
        CaselessStringKey key = new CaselessStringKey(modeName);
        DescriptorCache dc = (DescriptorCache)this.descriptors.get(key);
        if (dc == null) {
            if (RegistryMode.getMode(modeName) != null) {
                dc = new DescriptorCache(modeName);
                this.descriptors.put(key, dc);
            } else {
                throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry0", new Object[]{modeName}));
            }
        }
        return dc;
    }

    private void initialize() {
        this.descriptors = new Hashtable();
        this.factories = new Hashtable();
    }

    public OperationRegistry() {
        this.initialize();
    }

    public static OperationRegistry getThreadSafeOperationRegistry() {
        return new ThreadSafeOperationRegistry();
    }

    static OperationRegistry initializeRegistry() {
        try {
            InputStream url = PropertyUtil.getFileFromClasspath(JAI_REGISTRY_FILE);
            if (url == null) {
                throw new RuntimeException(JaiI18N.getString("OperationRegistry1"));
            }
            ThreadSafeOperationRegistry registry = new ThreadSafeOperationRegistry();
            if (url != null) {
                RegistryFileParser.loadOperationRegistry((OperationRegistry)registry, null, url);
            }
            ((OperationRegistry)registry).registerServices(null);
            return registry;
        }
        catch (IOException ioe) {
            ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
            String message = JaiI18N.getString("OperationRegistry2");
            listener.errorOccurred(message, new ImagingException(message, ioe), class$javax$media$jai$OperationRegistry == null ? (class$javax$media$jai$OperationRegistry = OperationRegistry.class$("javax.media.jai.OperationRegistry")) : class$javax$media$jai$OperationRegistry, false);
            return null;
        }
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        try {
            RegistryFileParser.writeOperationRegistry(this, new BufferedWriter(sw));
            return sw.getBuffer().toString();
        }
        catch (Exception e) {
            return "\n[ERROR!] " + e.getMessage();
        }
    }

    public void writeToStream(OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        RegistryFileParser.writeOperationRegistry(this, out);
    }

    public void initializeFromStream(InputStream in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.initialize();
        this.updateFromStream(in);
    }

    public void updateFromStream(InputStream in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        RegistryFileParser.loadOperationRegistry(this, null, in);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        if (in == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        byte[] barray = (byte[])in.readObject();
        ByteArrayInputStream s = new ByteArrayInputStream(barray);
        this.initializeFromStream(s);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ByteArrayOutputStream bstream = new ByteArrayOutputStream();
        this.writeToStream(bstream);
        out.writeObject(bstream.toByteArray());
    }

    public void removeRegistryMode(String modeName) {
        if (this.getDescriptorCache(modeName) != null) {
            this.descriptors.remove(new CaselessStringKey(modeName));
        }
        if (this.getFactoryCache(modeName) != null) {
            this.factories.remove(new CaselessStringKey(modeName));
        }
    }

    public String[] getRegistryModes() {
        Enumeration e = this.descriptors.keys();
        int size = this.descriptors.size();
        String[] names = new String[size];
        for (int i = 0; i < size; ++i) {
            CaselessStringKey key = (CaselessStringKey)e.nextElement();
            names[i] = key.getName();
        }
        return names;
    }

    public void registerDescriptor(RegistryElementDescriptor descriptor) {
        int i;
        if (descriptor == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        String[] supportedModes = descriptor.getSupportedModes();
        String descriptorName = descriptor.getName();
        for (i = 0; i < supportedModes.length; ++i) {
            if (RegistryMode.getMode(supportedModes[i]) != null) continue;
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry3", new Object[]{descriptorName, supportedModes[i]}));
        }
        for (i = 0; i < supportedModes.length; ++i) {
            DescriptorCache dc = this.getDescriptorCache(supportedModes[i]);
            dc.addDescriptor(descriptor);
        }
    }

    public void unregisterDescriptor(RegistryElementDescriptor descriptor) {
        int i;
        if (descriptor == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        String descriptorName = descriptor.getName();
        String[] supportedModes = descriptor.getSupportedModes();
        for (i = 0; i < supportedModes.length; ++i) {
            if (RegistryMode.getMode(supportedModes[i]) != null) continue;
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry3", new Object[]{descriptorName, supportedModes[i]}));
        }
        for (i = 0; i < supportedModes.length; ++i) {
            DescriptorCache dc = this.getDescriptorCache(supportedModes[i]);
            dc.removeDescriptor(descriptor);
        }
    }

    public RegistryElementDescriptor getDescriptor(Class descriptorClass, String descriptorName) {
        if (descriptorClass == null || descriptorName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        String[] supportedModes = RegistryMode.getModeNames(descriptorClass);
        if (supportedModes == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry4", new Object[]{descriptorClass.getName()}));
        }
        for (int i = 0; i < supportedModes.length; ++i) {
            DescriptorCache dc = this.getDescriptorCache(supportedModes[i]);
            RegistryElementDescriptor red = dc.getDescriptor(descriptorName);
            if (red == null) continue;
            return red;
        }
        return null;
    }

    public List getDescriptors(Class descriptorClass) {
        if (descriptorClass == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        String[] supportedModes = RegistryMode.getModeNames(descriptorClass);
        if (supportedModes == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry4", new Object[]{descriptorClass.getName()}));
        }
        HashSet set = new HashSet();
        for (int i = 0; i < supportedModes.length; ++i) {
            DescriptorCache dc = this.getDescriptorCache(supportedModes[i]);
            List list = dc.getDescriptors();
            if (list == null) continue;
            set.addAll(list);
        }
        return new ArrayList(set);
    }

    public String[] getDescriptorNames(Class descriptorClass) {
        List dlist = this.getDescriptors(descriptorClass);
        if (dlist != null) {
            Iterator diter = dlist.iterator();
            String[] names = new String[dlist.size()];
            int i = 0;
            while (diter.hasNext()) {
                RegistryElementDescriptor red = (RegistryElementDescriptor)diter.next();
                names[i++] = red.getName();
            }
            return names;
        }
        return null;
    }

    public RegistryElementDescriptor getDescriptor(String modeName, String descriptorName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            return dc.getDescriptor(descriptorName);
        }
        return null;
    }

    public List getDescriptors(String modeName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            return dc.getDescriptors();
        }
        return null;
    }

    public String[] getDescriptorNames(String modeName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            return dc.getDescriptorNames();
        }
        return null;
    }

    public void setProductPreference(String modeName, String descriptorName, String preferredProductName, String otherProductName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            dc.setProductPreference(descriptorName, preferredProductName, otherProductName);
        }
    }

    public void unsetProductPreference(String modeName, String descriptorName, String preferredProductName, String otherProductName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            dc.unsetProductPreference(descriptorName, preferredProductName, otherProductName);
        }
    }

    public void clearProductPreferences(String modeName, String descriptorName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            dc.clearProductPreferences(descriptorName);
        }
    }

    public String[][] getProductPreferences(String modeName, String descriptorName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            return dc.getProductPreferences(descriptorName);
        }
        return null;
    }

    public Vector getOrderedProductList(String modeName, String descriptorName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            return dc.getOrderedProductList(descriptorName);
        }
        return null;
    }

    String getLocalName(String modeName, Object factoryInstance) {
        FactoryCache fc = this.getFactoryCache(modeName);
        if (fc != null) {
            return fc.getLocalName(factoryInstance);
        }
        return null;
    }

    public void registerFactory(String modeName, String descriptorName, String productName, Object factory) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        FactoryCache fc = this.getFactoryCache(modeName);
        if (dc.getDescriptor(descriptorName) == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
        }
        if (factory == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (dc.arePreferencesSupported) {
            OperationGraph og = dc.addProduct(descriptorName, productName);
            if (og == null) {
                throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
            }
            og.addOp(new PartialOrderNode(factory, factory.getClass().getName()));
        }
        fc.addFactory(descriptorName, productName, factory);
    }

    public void unregisterFactory(String modeName, String descriptorName, String productName, Object factory) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        FactoryCache fc = this.getFactoryCache(modeName);
        if (dc.getDescriptor(descriptorName) == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
        }
        if (factory == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        fc.removeFactory(descriptorName, productName, factory);
        if (dc.arePreferencesSupported) {
            OperationGraph og = dc.lookupProduct(descriptorName, productName);
            if (og == null) {
                throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
            }
            og.removeOp(factory);
        }
    }

    public void setFactoryPreference(String modeName, String descriptorName, String productName, Object preferredOp, Object otherOp) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        FactoryCache fc = this.getFactoryCache(modeName);
        if (dc.getDescriptor(descriptorName) == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
        }
        fc.setPreference(descriptorName, productName, preferredOp, otherOp);
        if (dc.arePreferencesSupported) {
            OperationGraph og = dc.lookupProduct(descriptorName, productName);
            if (og == null) {
                throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
            }
            og.setPreference(preferredOp, otherOp);
        }
    }

    public void unsetFactoryPreference(String modeName, String descriptorName, String productName, Object preferredOp, Object otherOp) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        FactoryCache fc = this.getFactoryCache(modeName);
        if (dc.getDescriptor(descriptorName) == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
        }
        fc.unsetPreference(descriptorName, productName, preferredOp, otherOp);
        if (dc.arePreferencesSupported) {
            OperationGraph og = dc.lookupProduct(descriptorName, productName);
            if (og == null) {
                throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
            }
            og.unsetPreference(preferredOp, otherOp);
        }
    }

    public void clearFactoryPreferences(String modeName, String descriptorName, String productName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        FactoryCache fc = this.getFactoryCache(modeName);
        if (dc.getDescriptor(descriptorName) == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
        }
        Object[][] prefs = fc.getPreferences(descriptorName, productName);
        if (prefs != null) {
            OperationGraph og = dc.lookupProduct(descriptorName, productName);
            if (og == null) {
                throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
            }
            for (int i = 0; i < prefs.length; ++i) {
                og.unsetPreference(prefs[i][0], prefs[i][1]);
            }
        }
        fc.clearPreferences(descriptorName, productName);
    }

    public Object[][] getFactoryPreferences(String modeName, String descriptorName, String productName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        FactoryCache fc = this.getFactoryCache(modeName);
        if (dc.getDescriptor(descriptorName) == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
        }
        return fc.getPreferences(descriptorName, productName);
    }

    public List getOrderedFactoryList(String modeName, String descriptorName, String productName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        FactoryCache fc = this.getFactoryCache(modeName);
        if (dc.getDescriptor(descriptorName) == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
        }
        if (dc.arePreferencesSupported) {
            OperationGraph og = dc.lookupProduct(descriptorName, productName);
            if (og == null) {
                return null;
            }
            Vector v = og.getOrderedOperationList();
            if (v == null || v.size() <= 0) {
                return null;
            }
            ArrayList<Object> list = new ArrayList<Object>(v.size());
            for (int i = 0; i < v.size(); ++i) {
                list.add(((PartialOrderNode)v.elementAt(i)).getData());
            }
            return list;
        }
        return fc.getFactoryList(descriptorName, productName);
    }

    public Iterator getFactoryIterator(String modeName, String descriptorName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        FactoryCache fc = this.getFactoryCache(modeName);
        if (dc.getDescriptor(descriptorName) == null) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("OperationRegistry5", new Object[]{descriptorName, modeName}));
        }
        if (dc.arePreferencesSupported) {
            Vector v = this.getOrderedProductList(modeName, descriptorName);
            if (v == null || v.size() <= 0) {
                return null;
            }
            ArrayList list = new ArrayList();
            for (int i = 0; i < v.size(); ++i) {
                List plist = this.getOrderedFactoryList(modeName, descriptorName, (String)v.get(i));
                if (plist == null) continue;
                list.addAll(plist);
            }
            return list.iterator();
        }
        List list = fc.getFactoryList(descriptorName, null);
        if (list != null) {
            return list.iterator();
        }
        return null;
    }

    public Object getFactory(String modeName, String descriptorName) {
        Iterator it = this.getFactoryIterator(modeName, descriptorName);
        if (it != null && it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public Object invokeFactory(String modeName, String descriptorName, Object[] args) {
        Iterator it = this.getFactoryIterator(modeName, descriptorName);
        if (it == null) {
            return null;
        }
        FactoryCache fc = this.getFactoryCache(modeName);
        ImagingListener listener = JAI.getDefaultInstance().getImagingListener();
        Exception savedOne = null;
        while (it.hasNext()) {
            Object factory = it.next();
            try {
                Object obj = fc.invoke(factory, args);
                if (obj != null) {
                    return obj;
                }
                savedOne = null;
            }
            catch (Exception e) {
                listener.errorOccurred(JaiI18N.getString("OperationRegistry6") + " \"" + descriptorName + "\"", e, this, false);
                savedOne = e;
            }
        }
        if (savedOne != null) {
            throw new ImagingException(JaiI18N.getString("OperationRegistry7") + " \"" + descriptorName + "\"", savedOne);
        }
        return null;
    }

    public void addPropertyGenerator(String modeName, String descriptorName, PropertyGenerator generator) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            dc.addPropertyGenerator(descriptorName, generator);
        }
    }

    public void removePropertyGenerator(String modeName, String descriptorName, PropertyGenerator generator) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            dc.removePropertyGenerator(descriptorName, generator);
        }
    }

    public void copyPropertyFromSource(String modeName, String descriptorName, String propertyName, int sourceIndex) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            dc.copyPropertyFromSource(descriptorName, propertyName, sourceIndex);
        }
    }

    public void suppressProperty(String modeName, String descriptorName, String propertyName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            dc.suppressProperty(descriptorName, propertyName);
        }
    }

    public void suppressAllProperties(String modeName, String descriptorName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            dc.suppressAllProperties(descriptorName);
        }
    }

    public void clearPropertyState(String modeName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            dc.clearPropertyState();
        }
    }

    public String[] getGeneratedPropertyNames(String modeName, String descriptorName) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            return dc.getGeneratedPropertyNames(descriptorName);
        }
        return null;
    }

    public PropertySource getPropertySource(String modeName, String descriptorName, Object op, Vector sources) {
        DescriptorCache dc = this.getDescriptorCache(modeName);
        if (dc != null) {
            return dc.getPropertySource(descriptorName, op, sources);
        }
        return null;
    }

    public PropertySource getPropertySource(OperationNode op) {
        Vector<Object> pv;
        if (op == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        ParameterBlock pb = op.getParameterBlock();
        Vector<Object> vector = pv = pb == null ? null : pb.getSources();
        if (pv == null) {
            pv = new Vector();
        }
        return this.getPropertySource(op.getRegistryModeName(), op.getOperationName(), op, pv);
    }

    public void registerServices(ClassLoader cl) throws IOException {
        Enumeration<URL> en = cl == null ? ClassLoader.getSystemResources(USR_REGISTRY_FILE) : cl.getResources(USR_REGISTRY_FILE);
        while (en.hasMoreElements()) {
            URL url = en.nextElement();
            RegistryFileParser.loadOperationRegistry(this, cl, url);
        }
        Iterator spitr = cl == null ? Service.providers(class$javax$media$jai$OperationRegistrySpi == null ? (class$javax$media$jai$OperationRegistrySpi = OperationRegistry.class$("javax.media.jai.OperationRegistrySpi")) : class$javax$media$jai$OperationRegistrySpi) : Service.providers(class$javax$media$jai$OperationRegistrySpi == null ? (class$javax$media$jai$OperationRegistrySpi = OperationRegistry.class$("javax.media.jai.OperationRegistrySpi")) : class$javax$media$jai$OperationRegistrySpi, cl);
        while (spitr.hasNext()) {
            OperationRegistrySpi ospi = (OperationRegistrySpi)spitr.next();
            ospi.updateRegistry(this);
        }
    }

    public void registerOperationDescriptor(OperationDescriptor odesc, String operationName) {
        this.registerDescriptor(odesc);
    }

    public void unregisterOperationDescriptor(String operationName) {
        String[] operationModes = RegistryMode.getModeNames(class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = OperationRegistry.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor);
        for (int i = 0; i < operationModes.length; ++i) {
            RegistryElementDescriptor red = this.getDescriptor(operationModes[i], operationName);
            if (red == null) continue;
            this.unregisterDescriptor(red);
        }
    }

    public OperationDescriptor getOperationDescriptor(String operationName) {
        return (OperationDescriptor)this.getDescriptor(class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = OperationRegistry.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor, operationName);
    }

    public Vector getOperationDescriptors() {
        List list = this.getDescriptors(class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = OperationRegistry.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor);
        return list == null ? null : new Vector(list);
    }

    public String[] getOperationNames() {
        return this.getDescriptorNames(class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = OperationRegistry.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor);
    }

    public void registerRIF(String operationName, String productName, RenderedImageFactory RIF) {
        this.registerFactory("rendered", operationName, productName, RIF);
    }

    public void unregisterRIF(String operationName, String productName, RenderedImageFactory RIF) {
        this.unregisterFactory("rendered", operationName, productName, RIF);
    }

    public void registerCRIF(String operationName, ContextualRenderedImageFactory CRIF) {
        this.registerFactory("renderable", operationName, null, CRIF);
    }

    public void unregisterCRIF(String operationName, ContextualRenderedImageFactory CRIF) {
        this.unregisterFactory("renderable", operationName, null, CRIF);
    }

    public void registerCIF(String operationName, String productName, CollectionImageFactory CIF) {
        this.registerFactory("collection", operationName, productName, CIF);
    }

    public void unregisterCIF(String operationName, String productName, CollectionImageFactory CIF) {
        this.unregisterFactory("collection", operationName, productName, CIF);
    }

    public void setProductPreference(String operationName, String preferredProductName, String otherProductName) {
        this.setProductPreference("rendered", operationName, preferredProductName, otherProductName);
    }

    public void unsetProductPreference(String operationName, String preferredProductName, String otherProductName) {
        this.unsetProductPreference("rendered", operationName, preferredProductName, otherProductName);
    }

    public void clearProductPreferences(String operationName) {
        this.clearProductPreferences("rendered", operationName);
    }

    public String[][] getProductPreferences(String operationName) {
        return this.getProductPreferences("rendered", operationName);
    }

    public Vector getOrderedProductList(String operationName) {
        return this.getOrderedProductList("rendered", operationName);
    }

    public void setRIFPreference(String operationName, String productName, RenderedImageFactory preferredRIF, RenderedImageFactory otherRIF) {
        this.setFactoryPreference("rendered", operationName, productName, preferredRIF, otherRIF);
    }

    public void setCIFPreference(String operationName, String productName, CollectionImageFactory preferredCIF, CollectionImageFactory otherCIF) {
        this.setFactoryPreference("collection", operationName, productName, preferredCIF, otherCIF);
    }

    public void unsetRIFPreference(String operationName, String productName, RenderedImageFactory preferredRIF, RenderedImageFactory otherRIF) {
        this.unsetFactoryPreference("rendered", operationName, productName, preferredRIF, otherRIF);
    }

    public void unsetCIFPreference(String operationName, String productName, CollectionImageFactory preferredCIF, CollectionImageFactory otherCIF) {
        this.unsetFactoryPreference("collection", operationName, productName, preferredCIF, otherCIF);
    }

    public void clearRIFPreferences(String operationName, String productName) {
        this.clearFactoryPreferences("rendered", operationName, productName);
    }

    public void clearCIFPreferences(String operationName, String productName) {
        this.clearFactoryPreferences("collection", operationName, productName);
    }

    public void clearOperationPreferences(String operationName, String productName) {
        String[] operationModes = RegistryMode.getModeNames(class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = OperationRegistry.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor);
        for (int i = 0; i < operationModes.length; ++i) {
            DescriptorCache dc = this.getDescriptorCache(operationModes[i]);
            if (!dc.arePreferencesSupported || this.getDescriptor(operationModes[i], operationName) == null) continue;
            this.clearFactoryPreferences(operationModes[i], operationName, productName);
        }
    }

    public Vector getOrderedRIFList(String operationName, String productName) {
        List list = this.getOrderedFactoryList("rendered", operationName, productName);
        return list == null ? null : new Vector(list);
    }

    public Vector getOrderedCIFList(String operationName, String productName) {
        List list = this.getOrderedFactoryList("collection", operationName, productName);
        return list == null ? null : new Vector(list);
    }

    public PlanarImage create(String operationName, ParameterBlock paramBlock, RenderingHints renderHints) {
        return PlanarImage.wrapRenderedImage(RIFRegistry.create(this, operationName, paramBlock, renderHints));
    }

    public ContextualRenderedImageFactory createRenderable(String operationName, ParameterBlock paramBlock) {
        return CRIFRegistry.get(this, operationName);
    }

    public CollectionImage createCollection(String operationName, ParameterBlock args, RenderingHints hints) {
        return CIFRegistry.create(this, operationName, args, hints);
    }

    public void clearPropertyState() {
        this.clearPropertyState("rendered");
    }

    public void addPropertyGenerator(String operationName, PropertyGenerator generator) {
        this.addPropertyGenerator("rendered", operationName, generator);
    }

    public void removePropertyGenerator(String operationName, PropertyGenerator generator) {
        this.removePropertyGenerator("rendered", operationName, generator);
    }

    public void suppressProperty(String operationName, String propertyName) {
        this.suppressProperty("rendered", operationName, propertyName);
    }

    public void suppressAllProperties(String operationName) {
        this.suppressAllProperties("rendered", operationName);
    }

    public void copyPropertyFromSource(String operationName, String propertyName, int sourceIndex) {
        this.copyPropertyFromSource("rendered", operationName, propertyName, sourceIndex);
    }

    public String[] getGeneratedPropertyNames(String operationName) {
        return this.getGeneratedPropertyNames("rendered", operationName);
    }

    public PropertySource getPropertySource(RenderedOp op) {
        return RIFRegistry.getPropertySource(op);
    }

    public PropertySource getPropertySource(RenderableOp op) {
        return CRIFRegistry.getPropertySource(op);
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

