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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class ObservableSet<E>
implements Set<E> {
    private Set<E> delegate;
    private PropertyChangeSupport pcs;
    private Closure test;
    public static final String SIZE_PROPERTY = "size";
    public static final String CONTENT_PROPERTY = "content";

    public ObservableSet() {
        this(new HashSet(), null);
    }

    public ObservableSet(Set<E> delegate) {
        this(delegate, null);
    }

    public ObservableSet(Closure test) {
        this(new HashSet(), test);
    }

    public ObservableSet(Set<E> delegate, Closure test) {
        this.delegate = delegate;
        this.test = test;
        this.pcs = new PropertyChangeSupport(this);
    }

    public Set<E> getContent() {
        return Collections.unmodifiableSet(this.delegate);
    }

    protected Set<E> getDelegateSet() {
        return this.delegate;
    }

    protected Closure getTest() {
        return this.test;
    }

    protected void fireElementAddedEvent(Object element) {
        this.fireElementEvent(new ElementAddedEvent(this, element));
    }

    protected void fireMultiElementAddedEvent(List values) {
        this.fireElementEvent(new MultiElementAddedEvent(this, values));
    }

    protected void fireElementClearedEvent(List values) {
        this.fireElementEvent(new ElementClearedEvent(this, values));
    }

    protected void fireElementRemovedEvent(Object element) {
        this.fireElementEvent(new ElementRemovedEvent(this, element));
    }

    protected void fireMultiElementRemovedEvent(List values) {
        this.fireElementEvent(new MultiElementRemovedEvent(this, values));
    }

    protected void fireElementEvent(ElementEvent event) {
        this.pcs.firePropertyChange(event);
    }

    protected void fireSizeChangedEvent(int oldValue, int newValue) {
        this.pcs.firePropertyChange(new PropertyChangeEvent(this, SIZE_PROPERTY, oldValue, newValue));
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

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return new ObservableIterator<E>(this.delegate.iterator());
    }

    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return this.delegate.toArray(ts);
    }

    @Override
    public boolean add(E e) {
        int oldSize = this.size();
        boolean success = this.delegate.add(e);
        if (success) {
            if (this.test != null) {
                Object result = this.test.call((Object)e);
                if (result != null && result instanceof Boolean && ((Boolean)result).booleanValue()) {
                    this.fireElementAddedEvent(e);
                    this.fireSizeChangedEvent(oldSize, this.size());
                }
            } else {
                this.fireElementAddedEvent(e);
                this.fireSizeChangedEvent(oldSize, this.size());
            }
        }
        return success;
    }

    @Override
    public boolean remove(Object o) {
        int oldSize = this.size();
        boolean success = this.delegate.remove(o);
        if (success) {
            this.fireElementRemovedEvent(o);
            this.fireSizeChangedEvent(oldSize, this.size());
        }
        return success;
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return this.delegate.containsAll(objects);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        HashSet<E> duplicates = new HashSet<E>();
        if (null != c) {
            for (E e : c) {
                if (!this.delegate.contains(e)) continue;
                duplicates.add(e);
            }
        }
        int oldSize = this.size();
        boolean success = this.delegate.addAll(c);
        if (success && c != null) {
            ArrayList<E> values = new ArrayList<E>();
            for (E element : c) {
                if (this.test != null) {
                    Object result = this.test.call((Object)element);
                    if (result == null || !(result instanceof Boolean) || !((Boolean)result).booleanValue() || duplicates.contains(element)) continue;
                    values.add(element);
                    continue;
                }
                if (duplicates.contains(element)) continue;
                values.add(element);
            }
            if (!values.isEmpty()) {
                this.fireMultiElementAddedEvent(values);
                this.fireSizeChangedEvent(oldSize, this.size());
            }
        }
        return success;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) {
            return false;
        }
        ArrayList<E> values = new ArrayList<E>();
        if (!(c instanceof Set)) {
            c = new HashSet(c);
        }
        for (E element : this.delegate) {
            if (c.contains(element)) continue;
            values.add(element);
        }
        int oldSize = this.size();
        boolean success = this.delegate.retainAll(c);
        if (success && !values.isEmpty()) {
            this.fireMultiElementRemovedEvent(values);
            this.fireSizeChangedEvent(oldSize, this.size());
        }
        return success;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) {
            return false;
        }
        ArrayList values = new ArrayList();
        for (Object element : c) {
            if (!this.delegate.contains(element)) continue;
            values.add(element);
        }
        int oldSize = this.size();
        boolean success = this.delegate.removeAll(c);
        if (success && !values.isEmpty()) {
            this.fireMultiElementRemovedEvent(values);
            this.fireSizeChangedEvent(oldSize, this.size());
        }
        return success;
    }

    @Override
    public void clear() {
        int oldSize = this.size();
        ArrayList<E> values = new ArrayList<E>();
        values.addAll(this.delegate);
        this.delegate.clear();
        if (!values.isEmpty()) {
            this.fireElementClearedEvent(values);
        }
        this.fireSizeChangedEvent(oldSize, this.size());
    }

    public static class MultiElementRemovedEvent
    extends ElementEvent {
        private List values = new ArrayList();

        public MultiElementRemovedEvent(Object source, List values) {
            super(source, ChangeType.oldValue, ChangeType.newValue, ChangeType.MULTI_REMOVE);
            if (values != null) {
                this.values.addAll(values);
            }
        }

        public List getValues() {
            return Collections.unmodifiableList(this.values);
        }
    }

    public static class MultiElementAddedEvent
    extends ElementEvent {
        private List values = new ArrayList();

        public MultiElementAddedEvent(Object source, List values) {
            super(source, ChangeType.oldValue, ChangeType.newValue, ChangeType.MULTI_ADD);
            if (values != null) {
                this.values.addAll(values);
            }
        }

        public List getValues() {
            return Collections.unmodifiableList(this.values);
        }
    }

    public static class ElementClearedEvent
    extends ElementEvent {
        private List values = new ArrayList();

        public ElementClearedEvent(Object source, List values) {
            super(source, ChangeType.oldValue, ChangeType.newValue, ChangeType.CLEARED);
            if (values != null) {
                this.values.addAll(values);
            }
        }

        public List getValues() {
            return Collections.unmodifiableList(this.values);
        }
    }

    public static class ElementRemovedEvent
    extends ElementEvent {
        public ElementRemovedEvent(Object source, Object value) {
            super(source, value, null, ChangeType.REMOVED);
        }
    }

    public static class ElementAddedEvent
    extends ElementEvent {
        public ElementAddedEvent(Object source, Object newValue) {
            super(source, null, newValue, ChangeType.ADDED);
        }
    }

    public static abstract class ElementEvent
    extends PropertyChangeEvent {
        private final ChangeType type;

        public ElementEvent(Object source, Object oldValue, Object newValue, ChangeType type) {
            super(source, ObservableSet.CONTENT_PROPERTY, oldValue, newValue);
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
        REMOVED,
        CLEARED,
        MULTI_ADD,
        MULTI_REMOVE,
        NONE;

        public static final Object oldValue;
        public static final Object newValue;

        static {
            oldValue = new Object();
            newValue = new Object();
        }
    }

    protected class ObservableIterator<E>
    implements Iterator<E> {
        private Iterator<E> iterDelegate;
        private final Stack<E> stack = new Stack();

        public ObservableIterator(Iterator<E> iterDelegate) {
            this.iterDelegate = iterDelegate;
        }

        public Iterator<E> getDelegate() {
            return this.iterDelegate;
        }

        @Override
        public boolean hasNext() {
            return this.iterDelegate.hasNext();
        }

        @Override
        public E next() {
            this.stack.push(this.iterDelegate.next());
            return this.stack.peek();
        }

        @Override
        public void remove() {
            int oldSize = ObservableSet.this.size();
            this.iterDelegate.remove();
            ObservableSet.this.fireElementRemovedEvent(this.stack.pop());
            ObservableSet.this.fireSizeChangedEvent(oldSize, ObservableSet.this.size());
        }
    }
}

