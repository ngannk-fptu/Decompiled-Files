/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObservableMap
implements Map {
    private Map delegate;
    private PropertyChangeSupport pcs;
    private Closure test;
    public static final String SIZE_PROPERTY = "size";
    public static final String CONTENT_PROPERTY = "content";
    public static final String CLEARED_PROPERTY = "cleared";

    public ObservableMap() {
        this(new LinkedHashMap(), null);
    }

    public ObservableMap(Closure test) {
        this(new LinkedHashMap(), test);
    }

    public ObservableMap(Map delegate) {
        this(delegate, null);
    }

    public ObservableMap(Map delegate, Closure test) {
        this.delegate = delegate;
        this.test = test;
        this.pcs = new PropertyChangeSupport(this);
    }

    protected Map getMapDelegate() {
        return this.delegate;
    }

    protected Closure getTest() {
        return this.test;
    }

    public Map getContent() {
        return Collections.unmodifiableMap(this.delegate);
    }

    protected void firePropertyClearedEvent(Map values) {
        this.firePropertyEvent(new PropertyClearedEvent(this, values));
    }

    protected void firePropertyAddedEvent(Object key, Object value) {
        this.firePropertyEvent(new PropertyAddedEvent(this, String.valueOf(key), value));
    }

    protected void firePropertyUpdatedEvent(Object key, Object oldValue, Object newValue) {
        this.firePropertyEvent(new PropertyUpdatedEvent(this, String.valueOf(key), oldValue, newValue));
    }

    protected void fireMultiPropertyEvent(List<PropertyEvent> events) {
        this.firePropertyEvent(new MultiPropertyEvent(this, events.toArray(new PropertyEvent[events.size()])));
    }

    protected void fireMultiPropertyEvent(PropertyEvent[] events) {
        this.firePropertyEvent(new MultiPropertyEvent(this, events));
    }

    protected void firePropertyRemovedEvent(Object key, Object value) {
        this.firePropertyEvent(new PropertyRemovedEvent(this, String.valueOf(key), value));
    }

    protected void firePropertyEvent(PropertyEvent event) {
        this.pcs.firePropertyChange(event);
    }

    protected void fireSizeChangedEvent(int oldValue, int newValue) {
        this.pcs.firePropertyChange(new PropertyChangeEvent(this, SIZE_PROPERTY, oldValue, newValue));
    }

    @Override
    public void clear() {
        int oldSize = this.size();
        HashMap values = new HashMap();
        if (!this.delegate.isEmpty()) {
            values.putAll(this.delegate);
        }
        this.delegate.clear();
        this.firePropertyClearedEvent(values);
        this.fireSizeChangedEvent(oldSize, this.size());
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    public Set entrySet() {
        return this.delegate.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return this.delegate.equals(o);
    }

    public Object get(Object key) {
        return this.delegate.get(key);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    public Set keySet() {
        return this.delegate.keySet();
    }

    public Object put(Object key, Object value) {
        boolean newKey;
        int oldSize = this.size();
        Object oldValue = null;
        boolean bl = newKey = !this.delegate.containsKey(key);
        if (this.test != null) {
            oldValue = this.delegate.put(key, value);
            Object result = null;
            result = this.test.getMaximumNumberOfParameters() == 2 ? this.test.call(key, value) : this.test.call(value);
            if (result != null && result instanceof Boolean && ((Boolean)result).booleanValue()) {
                if (newKey) {
                    this.firePropertyAddedEvent(key, value);
                    this.fireSizeChangedEvent(oldSize, this.size());
                } else if (oldValue != value) {
                    this.firePropertyUpdatedEvent(key, oldValue, value);
                }
            }
        } else {
            oldValue = this.delegate.put(key, value);
            if (newKey) {
                this.firePropertyAddedEvent(key, value);
                this.fireSizeChangedEvent(oldSize, this.size());
            } else if (oldValue != value) {
                this.firePropertyUpdatedEvent(key, oldValue, value);
            }
        }
        return oldValue;
    }

    public void putAll(Map map) {
        int oldSize = this.size();
        if (map != null) {
            ArrayList<PropertyEvent> events = new ArrayList<PropertyEvent>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                boolean newKey;
                Map.Entry o;
                Map.Entry entry = o = iterator.next();
                String key = String.valueOf(entry.getKey());
                Object newValue = entry.getValue();
                Object oldValue = null;
                boolean bl = newKey = !this.delegate.containsKey(key);
                if (this.test != null) {
                    oldValue = this.delegate.put(key, newValue);
                    Object result = null;
                    result = this.test.getMaximumNumberOfParameters() == 2 ? this.test.call(key, newValue) : this.test.call(newValue);
                    if (result == null || !(result instanceof Boolean) || !((Boolean)result).booleanValue()) continue;
                    if (newKey) {
                        events.add(new PropertyAddedEvent(this, key, newValue));
                        continue;
                    }
                    if (oldValue == newValue) continue;
                    events.add(new PropertyUpdatedEvent(this, key, oldValue, newValue));
                    continue;
                }
                oldValue = this.delegate.put(key, newValue);
                if (newKey) {
                    events.add(new PropertyAddedEvent(this, key, newValue));
                    continue;
                }
                if (oldValue == newValue) continue;
                events.add(new PropertyUpdatedEvent(this, key, oldValue, newValue));
            }
            if (!events.isEmpty()) {
                this.fireMultiPropertyEvent(events);
                this.fireSizeChangedEvent(oldSize, this.size());
            }
        }
    }

    public Object remove(Object key) {
        int oldSize = this.size();
        Object result = this.delegate.remove(key);
        if (key != null) {
            this.firePropertyRemovedEvent(key, result);
            this.fireSizeChangedEvent(oldSize, this.size());
        }
        return result;
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    public int getSize() {
        return this.size();
    }

    public Collection values() {
        return this.delegate.values();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return this.pcs.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return this.pcs.getPropertyChangeListeners(propertyName);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(propertyName, listener);
    }

    public boolean hasListeners(String propertyName) {
        return this.pcs.hasListeners(propertyName);
    }

    public static class MultiPropertyEvent
    extends PropertyEvent {
        public static final String MULTI_PROPERTY = "groovy_util_ObservableMap_MultiPropertyEvent_MULTI";
        private static final PropertyEvent[] EMPTY_PROPERTY_EVENTS = new PropertyEvent[0];
        private PropertyEvent[] events = EMPTY_PROPERTY_EVENTS;

        public MultiPropertyEvent(Object source, PropertyEvent[] events) {
            super(source, MULTI_PROPERTY, ChangeType.oldValue, ChangeType.newValue, ChangeType.MULTI);
            if (events != null && events.length > 0) {
                this.events = new PropertyEvent[events.length];
                System.arraycopy(events, 0, this.events, 0, events.length);
            }
        }

        public PropertyEvent[] getEvents() {
            PropertyEvent[] copy = new PropertyEvent[this.events.length];
            System.arraycopy(this.events, 0, copy, 0, this.events.length);
            return copy;
        }
    }

    public static class PropertyClearedEvent
    extends PropertyEvent {
        private Map values = new HashMap();

        public PropertyClearedEvent(Object source, Map values) {
            super(source, ObservableMap.CLEARED_PROPERTY, values, null, ChangeType.CLEARED);
            if (values != null) {
                this.values.putAll(values);
            }
        }

        public Map getValues() {
            return Collections.unmodifiableMap(this.values);
        }
    }

    public static class PropertyRemovedEvent
    extends PropertyEvent {
        public PropertyRemovedEvent(Object source, String propertyName, Object oldValue) {
            super(source, propertyName, oldValue, null, ChangeType.REMOVED);
        }
    }

    public static class PropertyUpdatedEvent
    extends PropertyEvent {
        public PropertyUpdatedEvent(Object source, String propertyName, Object oldValue, Object newValue) {
            super(source, propertyName, oldValue, newValue, ChangeType.UPDATED);
        }
    }

    public static class PropertyAddedEvent
    extends PropertyEvent {
        public PropertyAddedEvent(Object source, String propertyName, Object newValue) {
            super(source, propertyName, null, newValue, ChangeType.ADDED);
        }
    }

    public static abstract class PropertyEvent
    extends PropertyChangeEvent {
        private ChangeType type;

        public PropertyEvent(Object source, String propertyName, Object oldValue, Object newValue, ChangeType type) {
            super(source, propertyName, oldValue, newValue);
            this.type = type;
        }

        public int getType() {
            return this.type.ordinal();
        }

        public ChangeType getChangeType() {
            return this.type;
        }

        public String getTypeAsString() {
            return this.type.name().toUpperCase();
        }
    }

    public static enum ChangeType {
        ADDED,
        UPDATED,
        REMOVED,
        CLEARED,
        MULTI,
        NONE;

        public static final Object oldValue;
        public static final Object newValue;

        public static ChangeType resolve(int ordinal) {
            switch (ordinal) {
                case 0: {
                    return ADDED;
                }
                case 2: {
                    return REMOVED;
                }
                case 3: {
                    return CLEARED;
                }
                case 4: {
                    return MULTI;
                }
                case 5: {
                    return NONE;
                }
            }
            return UPDATED;
        }

        static {
            oldValue = new Object();
            newValue = new Object();
        }
    }
}

