/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import software.amazon.ion.ContainedValueException;
import software.amazon.ion.IonContainer;
import software.amazon.ion.IonDatagram;
import software.amazon.ion.IonException;
import software.amazon.ion.IonValue;
import software.amazon.ion.NullValueException;
import software.amazon.ion.ReadOnlyValueException;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateIonContainer;
import software.amazon.ion.impl.PrivateUtils;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonDatagramLite;
import software.amazon.ion.impl.lite.IonValueLite;
import software.amazon.ion.impl.lite.TopLevelContext;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class IonContainerLite
extends IonValueLite
implements PrivateIonContainer,
IonContext {
    protected int _child_count;
    protected IonValueLite[] _children;
    static final int[] INITIAL_SIZE = IonContainerLite.make_initial_size_array();
    static final int[] NEXT_SIZE = IonContainerLite.make_next_size_array();

    protected IonContainerLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    IonContainerLite(IonContainerLite existing, IonContext context, boolean isStruct) {
        super(existing, context);
        int childCount;
        this._child_count = childCount = existing._child_count;
        if (existing._children != null) {
            boolean isDatagram = this instanceof IonDatagramLite;
            this._children = new IonValueLite[childCount];
            for (int i = 0; i < childCount; ++i) {
                IonValueLite child = existing._children[i];
                IonContainerLite childContext = isDatagram ? TopLevelContext.wrap(child.getAssignedSymbolTable(), (IonDatagramLite)this) : this;
                IonValueLite copy = child.clone(childContext);
                if (isStruct) {
                    copy.setFieldName(child.getFieldName());
                }
                this._children[i] = copy;
            }
        }
    }

    @Override
    public abstract void accept(ValueVisitor var1) throws Exception;

    @Override
    public abstract IonContainer clone();

    @Override
    public void clear() {
        this.checkForLock();
        if (this._isNullValue()) {
            assert (this._children == null);
            assert (this._child_count == 0);
            this._isNullValue(false);
        } else if (!this.isEmpty()) {
            this.detachAllChildren();
            this._child_count = 0;
        }
    }

    private void detachAllChildren() {
        for (int ii = 0; ii < this._child_count; ++ii) {
            IonValueLite child = this._children[ii];
            child.detachFromContainer();
            this._children[ii] = null;
        }
    }

    @Override
    public boolean isEmpty() throws NullValueException {
        this.validateThisNotNull();
        return this.size() == 0;
    }

    public IonValue get(int index) throws NullValueException {
        this.validateThisNotNull();
        IonValueLite value = this.get_child(index);
        assert (!value._isAutoCreated());
        return value;
    }

    @Override
    public final Iterator<IonValue> iterator() {
        return this.listIterator(0);
    }

    public final ListIterator<IonValue> listIterator() {
        return this.listIterator(0);
    }

    public ListIterator<IonValue> listIterator(int index) {
        if (this.isNullValue()) {
            if (index != 0) {
                throw new IndexOutOfBoundsException();
            }
            return PrivateUtils.emptyIterator();
        }
        return new SequenceContentIterator(index, this.isReadOnly());
    }

    @Override
    public void makeNull() {
        this.clear();
        this._isNullValue(true);
    }

    @Override
    public boolean remove(IonValue element) {
        this.checkForLock();
        if (element.getContainer() != this) {
            return false;
        }
        IonValueLite concrete = (IonValueLite)element;
        int pos = concrete._elementid();
        IonValueLite child = this.get_child(pos);
        if (child == concrete) {
            this.remove_child(pos);
            this.patch_elements_helper(pos);
            return true;
        }
        throw new AssertionError((Object)"element's index is not correct");
    }

    @Override
    public int size() {
        if (this.isNullValue()) {
            return 0;
        }
        return this.get_child_count();
    }

    @Override
    void makeReadOnlyInternal() {
        if (this._isLocked()) {
            return;
        }
        if (this._children != null) {
            for (int ii = 0; ii < this._child_count; ++ii) {
                IonValueLite child = this._children[ii];
                child.makeReadOnlyInternal();
            }
        }
        super.clearSymbolIDValues();
        this._isLocked(true);
    }

    @Override
    public final IonContainerLite getContextContainer() {
        return this;
    }

    @Override
    public final SymbolTable getContextSymbolTable() {
        return null;
    }

    @Override
    void clearSymbolIDValues() {
        super.clearSymbolIDValues();
        for (int ii = 0; ii < this.get_child_count(); ++ii) {
            IonValueLite child = this.get_child(ii);
            child.clearSymbolIDValues();
        }
    }

    public boolean add(IonValue child) throws NullPointerException, IllegalArgumentException, ContainedValueException {
        int size = this.get_child_count();
        this.add(size, (IonValueLite)child);
        return true;
    }

    void validateNewChild(IonValue child) throws ContainedValueException, NullPointerException, IllegalArgumentException {
        if (child.getContainer() != null) {
            throw new ContainedValueException();
        }
        if (child.isReadOnly()) {
            throw new ReadOnlyValueException();
        }
        if (child instanceof IonDatagram) {
            String message = "IonDatagram can not be inserted into another IonContainer.";
            throw new IllegalArgumentException(message);
        }
        assert (child instanceof IonValueLite) : "Child was not created by the same ValueFactory";
        assert (this.getSystem() == child.getSystem() || this.getSystem().getClass().equals(child.getSystem().getClass()));
    }

    void add(int index, IonValueLite child) throws ContainedValueException, NullPointerException {
        if (index < 0 || index > this.get_child_count()) {
            throw new IndexOutOfBoundsException();
        }
        this.checkForLock();
        this.validateNewChild(child);
        this.add_child(index, child);
        this.patch_elements_helper(index + 1);
        assert (index >= 0 && index < this.get_child_count() && child == this.get_child(index) && child._elementid() == index);
    }

    static int[] make_initial_size_array() {
        int[] sizes = new int[17];
        sizes[11] = 1;
        sizes[12] = 4;
        sizes[13] = 5;
        sizes[16] = 3;
        return sizes;
    }

    static int[] make_next_size_array() {
        int[] sizes = new int[17];
        sizes[11] = 4;
        sizes[12] = 8;
        sizes[13] = 8;
        sizes[16] = 10;
        return sizes;
    }

    protected final int initialSize() {
        switch (this.getType()) {
            case LIST: {
                return 1;
            }
            case SEXP: {
                return 4;
            }
            case STRUCT: {
                return 5;
            }
            case DATAGRAM: {
                return 3;
            }
        }
        return 4;
    }

    protected final int nextSize(int current_size, boolean call_transition) {
        int next_size;
        if (current_size == 0) {
            int new_size = this.initialSize();
            return new_size;
        }
        switch (this.getType()) {
            case LIST: {
                next_size = 4;
                break;
            }
            case SEXP: {
                next_size = 8;
                break;
            }
            case STRUCT: {
                next_size = 8;
                break;
            }
            case DATAGRAM: {
                next_size = 10;
                break;
            }
            default: {
                return current_size * 2;
            }
        }
        if (next_size > current_size) {
            if (call_transition) {
                this.transitionToLargeSize(next_size);
            }
        } else {
            next_size = current_size * 2;
        }
        return next_size;
    }

    void transitionToLargeSize(int size) {
    }

    @Override
    public final int get_child_count() {
        return this._child_count;
    }

    @Override
    public final IonValueLite get_child(int idx) {
        if (idx < 0 || idx >= this._child_count) {
            throw new IndexOutOfBoundsException(Integer.toString(idx));
        }
        return this._children[idx];
    }

    final IonValueLite set_child(int idx, IonValueLite child) {
        if (idx < 0 || idx >= this._child_count) {
            throw new IndexOutOfBoundsException(Integer.toString(idx));
        }
        if (child == null) {
            throw new NullPointerException();
        }
        IonValueLite prev = this._children[idx];
        this._children[idx] = child;
        return prev;
    }

    protected int add_child(int idx, IonValueLite child) {
        this._isNullValue(false);
        child.setContext(this.getContextForIndex(child, idx));
        if (this._children == null || this._child_count >= this._children.length) {
            int old_len = this._children == null ? 0 : this._children.length;
            int new_len = this.nextSize(old_len, true);
            assert (new_len > idx);
            IonValueLite[] temp = new IonValueLite[new_len];
            if (old_len > 0) {
                System.arraycopy(this._children, 0, temp, 0, old_len);
            }
            this._children = temp;
        }
        if (idx < this._child_count) {
            System.arraycopy(this._children, idx, this._children, idx + 1, this._child_count - idx);
        }
        ++this._child_count;
        this._children[idx] = child;
        child._elementid(idx);
        return idx;
    }

    IonContext getContextForIndex(IonValue element, int index) {
        return this;
    }

    void remove_child(int idx) {
        assert (idx >= 0);
        assert (idx < this.get_child_count());
        assert (this.get_child(idx) != null) : "No child at index " + idx;
        this._children[idx].detachFromContainer();
        int children_to_move = this._child_count - idx - 1;
        if (children_to_move > 0) {
            System.arraycopy(this._children, idx + 1, this._children, idx, children_to_move);
        }
        --this._child_count;
        this._children[this._child_count] = null;
    }

    public final void patch_elements_helper(int lowest_bad_idx) {
        for (int ii = lowest_bad_idx; ii < this.get_child_count(); ++ii) {
            IonValueLite child = this.get_child(ii);
            child._elementid(ii);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    protected class SequenceContentIterator
    implements ListIterator<IonValue> {
        protected final boolean __readOnly;
        protected boolean __lastMoveWasPrevious;
        protected int __pos;
        protected IonValueLite __current;

        public SequenceContentIterator(int index, boolean readOnly) {
            if (IonContainerLite.this._isLocked() && !readOnly) {
                throw new IllegalStateException("you can't open an updatable iterator on a read only value");
            }
            if (index < 0 || index > IonContainerLite.this._child_count) {
                throw new IndexOutOfBoundsException(Integer.toString(index));
            }
            this.__pos = index;
            this.__readOnly = readOnly;
        }

        protected final void force_position_sync() {
            if (this.__pos <= 0 || this.__pos > IonContainerLite.this._child_count) {
                return;
            }
            if (this.__current == null || this.__current == IonContainerLite.this._children[this.__pos - 1]) {
                return;
            }
            this.force_position_sync_helper();
        }

        private final void force_position_sync_helper() {
            int ii;
            if (this.__readOnly) {
                throw new IonException("read only sequence was changed");
            }
            for (ii = this.__pos; ii < IonContainerLite.this._child_count; ++ii) {
                if (IonContainerLite.this._children[ii] != this.__current) continue;
                this.__pos = ii;
                if (!this.__lastMoveWasPrevious) {
                    ++this.__pos;
                }
                return;
            }
            for (ii = this.__pos - 1; ii >= 0; --ii) {
                if (IonContainerLite.this._children[ii] != this.__current) continue;
                this.__pos = ii;
                if (!this.__lastMoveWasPrevious) {
                    ++this.__pos;
                }
                return;
            }
            throw new IonException("current member of iterator has been removed from the containing sequence");
        }

        @Override
        public void add(IonValue element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean hasNext() {
            return this.nextIndex() < IonContainerLite.this._child_count;
        }

        @Override
        public final boolean hasPrevious() {
            return this.previousIndex() >= 0;
        }

        @Override
        public IonValue next() {
            int next_idx = this.nextIndex();
            if (next_idx >= IonContainerLite.this._child_count) {
                throw new NoSuchElementException();
            }
            this.__current = IonContainerLite.this._children[next_idx];
            this.__pos = next_idx + 1;
            this.__lastMoveWasPrevious = false;
            return this.__current;
        }

        @Override
        public final int nextIndex() {
            this.force_position_sync();
            if (this.__pos >= IonContainerLite.this._child_count) {
                return IonContainerLite.this._child_count;
            }
            int next_idx = this.__pos;
            return next_idx;
        }

        @Override
        public IonValue previous() {
            this.force_position_sync();
            int prev_idx = this.previousIndex();
            if (prev_idx < 0) {
                throw new NoSuchElementException();
            }
            this.__current = IonContainerLite.this._children[prev_idx];
            this.__pos = prev_idx;
            this.__lastMoveWasPrevious = true;
            return this.__current;
        }

        @Override
        public final int previousIndex() {
            this.force_position_sync();
            int prev_idx = this.__pos - 1;
            if (prev_idx < 0) {
                return -1;
            }
            return prev_idx;
        }

        @Override
        public void remove() {
            if (this.__readOnly) {
                throw new UnsupportedOperationException();
            }
            this.force_position_sync();
            int idx = this.__pos;
            if (!this.__lastMoveWasPrevious) {
                --idx;
            }
            if (idx < 0) {
                throw new ArrayIndexOutOfBoundsException();
            }
            IonValueLite concrete = this.__current;
            int concrete_idx = concrete._elementid();
            assert (concrete_idx == idx);
            IonContainerLite.this.remove_child(idx);
            IonContainerLite.this.patch_elements_helper(concrete_idx);
            if (!this.__lastMoveWasPrevious) {
                --this.__pos;
            }
            this.__current = null;
        }

        @Override
        public void set(IonValue element) {
            throw new UnsupportedOperationException();
        }
    }
}

