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
import java.util.ListIterator;
import java.util.Set;

public class ObservableList
implements List {
    private List delegate;
    private PropertyChangeSupport pcs;
    private Closure test;
    public static final String SIZE_PROPERTY = "size";
    public static final String CONTENT_PROPERTY = "content";

    public ObservableList() {
        this(new ArrayList(), null);
    }

    public ObservableList(List delegate) {
        this(delegate, null);
    }

    public ObservableList(Closure test) {
        this(new ArrayList(), test);
    }

    public ObservableList(List delegate, Closure test) {
        this.delegate = delegate;
        this.test = test;
        this.pcs = new PropertyChangeSupport(this);
    }

    public List getContent() {
        return Collections.unmodifiableList(this.delegate);
    }

    protected List getDelegateList() {
        return this.delegate;
    }

    protected Closure getTest() {
        return this.test;
    }

    protected void fireElementAddedEvent(int index, Object element) {
        this.fireElementEvent(new ElementAddedEvent(this, element, index));
    }

    protected void fireMultiElementAddedEvent(int index, List values) {
        this.fireElementEvent(new MultiElementAddedEvent(this, index, values));
    }

    protected void fireElementClearedEvent(List values) {
        this.fireElementEvent(new ElementClearedEvent(this, values));
    }

    protected void fireElementRemovedEvent(int index, Object element) {
        this.fireElementEvent(new ElementRemovedEvent(this, element, index));
    }

    protected void fireMultiElementRemovedEvent(List values) {
        this.fireElementEvent(new MultiElementRemovedEvent(this, values));
    }

    protected void fireElementUpdatedEvent(int index, Object oldValue, Object newValue) {
        this.fireElementEvent(new ElementUpdatedEvent((Object)this, oldValue, newValue, index));
    }

    protected void fireElementEvent(ElementEvent event) {
        this.pcs.firePropertyChange(event);
    }

    protected void fireSizeChangedEvent(int oldValue, int newValue) {
        this.pcs.firePropertyChange(new PropertyChangeEvent(this, SIZE_PROPERTY, oldValue, newValue));
    }

    public void add(int index, Object element) {
        int oldSize = this.size();
        this.delegate.add(index, element);
        this.fireAddWithTest(element, index, oldSize);
    }

    @Override
    public boolean add(Object o) {
        int oldSize = this.size();
        boolean success = this.delegate.add(o);
        if (success) {
            this.fireAddWithTest(o, oldSize, oldSize);
        }
        return success;
    }

    private void fireAddWithTest(Object element, int index, int oldSize) {
        if (this.test != null) {
            Object result = this.test.call(element);
            if (result != null && result instanceof Boolean && ((Boolean)result).booleanValue()) {
                this.fireElementAddedEvent(index, element);
                this.fireSizeChangedEvent(oldSize, this.size());
            }
        } else {
            this.fireElementAddedEvent(index, element);
            this.fireSizeChangedEvent(oldSize, this.size());
        }
    }

    @Override
    public boolean addAll(Collection c) {
        return this.addAll(this.size(), c);
    }

    public boolean addAll(int index, Collection c) {
        int oldSize = this.size();
        boolean success = this.delegate.addAll(index, c);
        if (success && c != null) {
            ArrayList values = new ArrayList();
            for (Object element : c) {
                if (this.test != null) {
                    Object result = this.test.call(element);
                    if (result == null || !(result instanceof Boolean) || !((Boolean)result).booleanValue()) continue;
                    values.add(element);
                    continue;
                }
                values.add(element);
            }
            if (!values.isEmpty()) {
                this.fireMultiElementAddedEvent(index, values);
                this.fireSizeChangedEvent(oldSize, this.size());
            }
        }
        return success;
    }

    @Override
    public void clear() {
        int oldSize = this.size();
        ArrayList values = new ArrayList();
        values.addAll(this.delegate);
        this.delegate.clear();
        if (!values.isEmpty()) {
            this.fireElementClearedEvent(values);
        }
        this.fireSizeChangedEvent(oldSize, this.size());
    }

    @Override
    public boolean contains(Object o) {
        return this.delegate.contains(o);
    }

    @Override
    public boolean containsAll(Collection c) {
        return this.delegate.containsAll(c);
    }

    @Override
    public boolean equals(Object o) {
        return this.delegate.equals(o);
    }

    public Object get(int index) {
        return this.delegate.get(index);
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public int indexOf(Object o) {
        return this.delegate.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public Iterator iterator() {
        return new ObservableIterator(this.delegate.iterator());
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.delegate.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        return new ObservableListIterator(this.delegate.listIterator(), 0);
    }

    public ListIterator listIterator(int index) {
        return new ObservableListIterator(this.delegate.listIterator(index), index);
    }

    public Object remove(int index) {
        int oldSize = this.size();
        Object element = this.delegate.remove(index);
        this.fireElementRemovedEvent(index, element);
        this.fireSizeChangedEvent(oldSize, this.size());
        return element;
    }

    @Override
    public boolean remove(Object o) {
        int oldSize = this.size();
        int index = this.delegate.indexOf(o);
        boolean success = this.delegate.remove(o);
        if (success) {
            this.fireElementRemovedEvent(index, o);
            this.fireSizeChangedEvent(oldSize, this.size());
        }
        return success;
    }

    @Override
    public boolean removeAll(Collection c) {
        if (c == null) {
            return false;
        }
        ArrayList values = new ArrayList();
        HashSet delegateSet = new HashSet(this.delegate);
        if (!(c instanceof Set)) {
            c = new HashSet(c);
        }
        for (Object element : c) {
            if (!delegateSet.contains(element)) continue;
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
    public boolean retainAll(Collection c) {
        if (c == null) {
            return false;
        }
        ArrayList values = new ArrayList();
        if (!(c instanceof Set)) {
            c = new HashSet(c);
        }
        for (Object element : this.delegate) {
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

    public Object set(int index, Object element) {
        Object oldValue = this.delegate.set(index, element);
        if (this.test != null) {
            Object result = this.test.call(element);
            if (result != null && result instanceof Boolean && ((Boolean)result).booleanValue()) {
                this.fireElementUpdatedEvent(index, oldValue, element);
            }
        } else {
            this.fireElementUpdatedEvent(index, oldValue, element);
        }
        return oldValue;
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    public int getSize() {
        return this.size();
    }

    public List subList(int fromIndex, int toIndex) {
        return this.delegate.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return this.delegate.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        return this.delegate.toArray(a);
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

    public static class MultiElementRemovedEvent
    extends ElementEvent {
        private List values = new ArrayList();

        public MultiElementRemovedEvent(Object source, List values) {
            super(source, ChangeType.oldValue, ChangeType.newValue, 0, ChangeType.MULTI_REMOVE);
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

        public MultiElementAddedEvent(Object source, int index, List values) {
            super(source, ChangeType.oldValue, ChangeType.newValue, index, ChangeType.MULTI_ADD);
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
            super(source, ChangeType.oldValue, ChangeType.newValue, 0, ChangeType.CLEARED);
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
        public ElementRemovedEvent(Object source, Object value, int index) {
            super(source, value, null, index, ChangeType.REMOVED);
        }
    }

    public static class ElementUpdatedEvent
    extends ElementEvent {
        public ElementUpdatedEvent(Object source, Object oldValue, Object newValue, int index) {
            super(source, oldValue, newValue, index, ChangeType.UPDATED);
        }
    }

    public static class ElementAddedEvent
    extends ElementEvent {
        public ElementAddedEvent(Object source, Object newValue, int index) {
            super(source, null, newValue, index, ChangeType.ADDED);
        }
    }

    public static abstract class ElementEvent
    extends PropertyChangeEvent {
        private final ChangeType type;
        private final int index;

        public ElementEvent(Object source, Object oldValue, Object newValue, int index, ChangeType type) {
            super(source, ObservableList.CONTENT_PROPERTY, oldValue, newValue);
            this.type = type;
            this.index = index;
        }

        public int getIndex() {
            return this.index;
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
        MULTI_ADD,
        MULTI_REMOVE,
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
                    return MULTI_ADD;
                }
                case 5: {
                    return MULTI_REMOVE;
                }
                case 6: {
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

    protected class ObservableListIterator
    extends ObservableIterator
    implements ListIterator {
        public ObservableListIterator(ListIterator iterDelegate, int index) {
            super(iterDelegate);
            this.cursor = index - 1;
        }

        public ListIterator getListIterator() {
            return (ListIterator)this.getDelegate();
        }

        public void add(Object o) {
            ObservableList.this.add(o);
            ++this.cursor;
        }

        @Override
        public boolean hasPrevious() {
            return this.getListIterator().hasPrevious();
        }

        @Override
        public int nextIndex() {
            return this.getListIterator().nextIndex();
        }

        public Object previous() {
            return this.getListIterator().previous();
        }

        @Override
        public int previousIndex() {
            return this.getListIterator().previousIndex();
        }

        public void set(Object o) {
            ObservableList.this.set(this.cursor, o);
        }
    }

    protected class ObservableIterator
    implements Iterator {
        private Iterator iterDelegate;
        protected int cursor = -1;

        public ObservableIterator(Iterator iterDelegate) {
            this.iterDelegate = iterDelegate;
        }

        public Iterator getDelegate() {
            return this.iterDelegate;
        }

        @Override
        public boolean hasNext() {
            return this.iterDelegate.hasNext();
        }

        public Object next() {
            ++this.cursor;
            return this.iterDelegate.next();
        }

        @Override
        public void remove() {
            int oldSize = ObservableList.this.size();
            Object element = ObservableList.this.get(this.cursor);
            this.iterDelegate.remove();
            ObservableList.this.fireElementRemovedEvent(this.cursor, element);
            ObservableList.this.fireSizeChangedEvent(oldSize, ObservableList.this.size());
            --this.cursor;
        }
    }
}

