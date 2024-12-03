/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.PropertyUtil;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.media.jai.CollectionImageFactory;
import javax.media.jai.ImageJAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.PropertyChangeSupportJAI;
import javax.media.jai.WritablePropertySourceImpl;

public abstract class CollectionImage
implements ImageJAI,
Collection {
    protected Collection imageCollection;
    protected CollectionImageFactory imageFactory;
    private Boolean isFactorySet = Boolean.FALSE;
    protected PropertyChangeSupportJAI eventManager = new PropertyChangeSupportJAI(this);
    protected WritablePropertySourceImpl properties = new WritablePropertySourceImpl(null, null, this.eventManager);
    protected Set sinks;

    protected CollectionImage() {
    }

    public CollectionImage(Collection collection) {
        this();
        if (collection == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.imageCollection = collection;
    }

    public Object get(int index) {
        if (index < 0 || index >= this.imageCollection.size()) {
            throw new IndexOutOfBoundsException();
        }
        if (this.imageCollection instanceof List) {
            return ((List)this.imageCollection).get(index);
        }
        return this.imageCollection.toArray((Object[])null)[index];
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setImageFactory(CollectionImageFactory imageFactory) {
        Boolean bl = this.isFactorySet;
        synchronized (bl) {
            if (this.isFactorySet.booleanValue()) {
                throw new IllegalStateException();
            }
            this.imageFactory = imageFactory;
            this.isFactorySet = Boolean.TRUE;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CollectionImageFactory getImageFactory() {
        Boolean bl = this.isFactorySet;
        synchronized (bl) {
            return this.imageFactory;
        }
    }

    public synchronized boolean addSink(Object sink) {
        if (sink == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.sinks == null) {
            this.sinks = new HashSet();
        }
        return this.sinks.add(new WeakReference<Object>(sink));
    }

    public synchronized boolean removeSink(Object sink) {
        if (sink == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (this.sinks == null) {
            return false;
        }
        boolean result = false;
        Iterator it = this.sinks.iterator();
        while (it.hasNext()) {
            Object referent = ((WeakReference)it.next()).get();
            if (referent == sink) {
                it.remove();
                result = true;
                continue;
            }
            if (referent != null) continue;
            it.remove();
        }
        return result;
    }

    public synchronized Set getSinks() {
        HashSet v = null;
        if (this.sinks != null && this.sinks.size() > 0) {
            v = new HashSet(this.sinks.size());
            Iterator it = this.sinks.iterator();
            while (it.hasNext()) {
                Object o = ((WeakReference)it.next()).get();
                if (o == null) continue;
                v.add(o);
            }
            if (v.size() == 0) {
                v = null;
            }
        }
        return v;
    }

    public synchronized void removeSinks() {
        this.sinks = null;
    }

    public String[] getPropertyNames() {
        return this.properties.getPropertyNames();
    }

    public String[] getPropertyNames(String prefix) {
        return PropertyUtil.getPropertyNames(this.getPropertyNames(), prefix);
    }

    public Class getPropertyClass(String name) {
        return this.properties.getPropertyClass(name);
    }

    public Object getProperty(String name) {
        return this.properties.getProperty(name);
    }

    public Object getProperty(String name, Collection collection) {
        return Image.UndefinedProperty;
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

    public int size() {
        return this.imageCollection.size();
    }

    public boolean isEmpty() {
        return this.imageCollection.isEmpty();
    }

    public boolean contains(Object o) {
        return this.imageCollection.contains(o);
    }

    public Iterator iterator() {
        return this.imageCollection.iterator();
    }

    public Object[] toArray() {
        return this.imageCollection.toArray();
    }

    public Object[] toArray(Object[] a) {
        return this.imageCollection.toArray(a);
    }

    public boolean add(Object o) {
        return this.imageCollection.add(o);
    }

    public boolean remove(Object o) {
        return this.imageCollection.remove(o);
    }

    public boolean containsAll(Collection c) {
        return this.imageCollection.containsAll(c);
    }

    public boolean addAll(Collection c) {
        return this.imageCollection.addAll(c);
    }

    public boolean removeAll(Collection c) {
        return this.imageCollection.removeAll(c);
    }

    public boolean retainAll(Collection c) {
        return this.imageCollection.retainAll(c);
    }

    public void clear() {
        this.imageCollection.clear();
    }
}

