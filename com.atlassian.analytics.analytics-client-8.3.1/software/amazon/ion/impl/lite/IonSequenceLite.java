/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import software.amazon.ion.ContainedValueException;
import software.amazon.ion.IonSequence;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.IonWriter;
import software.amazon.ion.ValueFactory;
import software.amazon.ion.impl.PrivateCurriedValueFactory;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContainerLite;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonValueLite;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class IonSequenceLite
extends IonContainerLite
implements IonSequence {
    protected static final IonValueLite[] EMPTY_VALUE_ARRAY = new IonValueLite[0];

    IonSequenceLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonSequenceLite(IonSequenceLite existing, IonContext context) {
        super(existing, context, false);
    }

    IonSequenceLite(ContainerlessContext context, Collection<? extends IonValue> elements) throws ContainedValueException, NullPointerException, IllegalArgumentException {
        this(context, elements == null);
        assert (this._children == null);
        if (elements != null) {
            this._children = new IonValueLite[elements.size()];
            for (IonValueLite ionValueLite : elements) {
                super.add(ionValueLite);
            }
        }
    }

    @Override
    public abstract IonSequenceLite clone();

    protected int sequenceHashCode(int seed, PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        int prime = 8191;
        int result = seed;
        if (!this.isNullValue()) {
            for (IonValue v : this) {
                IonValueLite vLite = (IonValueLite)v;
                result = 8191 * result + vLite.hashCode(symbolTableProvider);
                result ^= result << 29 ^ result >> 3;
            }
        }
        return this.hashTypeAnnotations(result, symbolTableProvider);
    }

    @Override
    public boolean add(IonValue element) throws ContainedValueException, NullPointerException {
        return super.add(element);
    }

    @Override
    public boolean addAll(Collection<? extends IonValue> c) {
        this.checkForLock();
        if (c == null) {
            throw new NullPointerException();
        }
        boolean changed = false;
        for (IonValue ionValue : c) {
            changed = this.add(ionValue) || changed;
        }
        return changed;
    }

    @Override
    public boolean addAll(int index, Collection<? extends IonValue> c) {
        this.checkForLock();
        if (c == null) {
            throw new NullPointerException();
        }
        if (index < 0 || index > this.size()) {
            throw new IndexOutOfBoundsException();
        }
        boolean changed = false;
        for (IonValue ionValue : c) {
            this.add(index++, ionValue);
            changed = true;
        }
        if (changed) {
            this.patch_elements_helper(index);
        }
        return changed;
    }

    @Override
    public ValueFactory add() {
        return new PrivateCurriedValueFactory(this.getSystem()){

            protected void handle(IonValue newValue) {
                IonSequenceLite.this.add(newValue);
            }
        };
    }

    @Override
    public void add(int index, IonValue element) throws ContainedValueException, NullPointerException {
        this.add(index, (IonValueLite)element);
    }

    @Override
    public ValueFactory add(final int index) {
        return new PrivateCurriedValueFactory(this.getSystem()){

            protected void handle(IonValue newValue) {
                IonSequenceLite.this.add(index, newValue);
                IonSequenceLite.this.patch_elements_helper(index + 1);
            }
        };
    }

    @Override
    public IonValue set(int index, IonValue element) {
        this.checkForLock();
        IonValueLite concrete = (IonValueLite)element;
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException("" + index);
        }
        this.validateNewChild(element);
        assert (this._children != null);
        concrete._context = this.getContextForIndex(element, index);
        IonValueLite removed = this.set_child(index, concrete);
        concrete._elementid(index);
        removed.detachFromContainer();
        return removed;
    }

    @Override
    public IonValue remove(int index) {
        this.checkForLock();
        if (index < 0 || index >= this.get_child_count()) {
            throw new IndexOutOfBoundsException("" + index);
        }
        IonValueLite v = this.get_child(index);
        assert (v._elementid() == index);
        this.remove_child(index);
        this.patch_elements_helper(index);
        return v;
    }

    @Override
    public boolean remove(Object o) {
        this.checkForLock();
        int idx = this.lastIndexOf(o);
        if (idx < 0) {
            return false;
        }
        assert (o instanceof IonValueLite);
        assert (((IonValueLite)o)._elementid() == idx);
        this.remove_child(idx);
        this.patch_elements_helper(idx);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        this.checkForLock();
        for (Object o : c) {
            int idx = this.lastIndexOf(o);
            if (idx < 0) continue;
            assert (o == this.get_child(idx));
            this.remove_child(idx);
            this.patch_elements_helper(idx);
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        IonValue v;
        this.checkForLock();
        if (this.get_child_count() < 1) {
            return false;
        }
        IdentityHashMap<IonValue, IonValue> keepers = new IdentityHashMap<IonValue, IonValue>();
        for (Object o : c) {
            v = (IonValue)o;
            if (this != v.getContainer()) continue;
            keepers.put(v, v);
        }
        boolean changed = false;
        int ii = this.get_child_count();
        while (ii > 0) {
            if (keepers.containsKey(v = this.get_child(--ii))) continue;
            this.remove(v);
            this.patch_elements_helper(ii);
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        if (!(o instanceof IonValue)) {
            throw new ClassCastException();
        }
        return ((IonValue)o).getContainer() == this;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (this.contains(o)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int indexOf(Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        PrivateIonValue v = (PrivateIonValue)o;
        if (this != v.getContainer()) {
            return -1;
        }
        return v.getElementId();
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.indexOf(o);
    }

    @Override
    public List<IonValue> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("issue amznlabs/ion-java#52");
    }

    @Override
    public IonValue[] toArray() {
        if (this.get_child_count() < 1) {
            return EMPTY_VALUE_ARRAY;
        }
        IonValue[] array = new IonValue[this.get_child_count()];
        System.arraycopy(this._children, 0, array, 0, this.get_child_count());
        return array;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int size = this.get_child_count();
        if (a.length < size) {
            Class<?> type = a.getClass().getComponentType();
            a = (Object[])Array.newInstance(type, size);
        }
        if (size > 0) {
            System.arraycopy(this._children, 0, a, 0, size);
        }
        if (size < a.length) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public <T extends IonValue> T[] extract(Class<T> type) {
        this.checkForLock();
        if (this.isNullValue()) {
            return null;
        }
        IonValue[] array = (IonValue[])Array.newInstance(type, this.size());
        this.toArray(array);
        this.clear();
        return array;
    }

    @Override
    void writeBodyTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) throws IOException {
        IonType type = this.getType();
        if (this.isNullValue()) {
            writer.writeNull(type);
        } else {
            writer.stepIn(type);
            this.writeChildren(writer, this, symbolTableProvider);
            writer.stepOut();
        }
    }
}

