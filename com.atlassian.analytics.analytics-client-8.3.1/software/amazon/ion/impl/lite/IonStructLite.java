/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import software.amazon.ion.ContainedValueException;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonType;
import software.amazon.ion.IonValue;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.ValueFactory;
import software.amazon.ion.ValueVisitor;
import software.amazon.ion.impl.PrivateCurriedValueFactory;
import software.amazon.ion.impl.PrivateIonValue;
import software.amazon.ion.impl.lite.ContainerlessContext;
import software.amazon.ion.impl.lite.IonContainerLite;
import software.amazon.ion.impl.lite.IonContext;
import software.amazon.ion.impl.lite.IonValueLite;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class IonStructLite
extends IonContainerLite
implements IonStruct {
    private static final int HASH_SIGNATURE = IonType.STRUCT.toString().hashCode();
    private Map<String, Integer> _field_map;
    public int _field_map_duplicate_count;

    IonStructLite(ContainerlessContext context, boolean isNull) {
        super(context, isNull);
    }

    private IonStructLite(IonStructLite existing, IonContext context) {
        super(existing, context, true);
        this._field_map = null == this._field_map ? null : new HashMap<String, Integer>(existing._field_map);
        this._field_map_duplicate_count = existing._field_map_duplicate_count;
    }

    @Override
    IonStructLite clone(IonContext parentContext) {
        return new IonStructLite(this, parentContext);
    }

    @Override
    public IonStructLite clone() {
        return this.clone(ContainerlessContext.wrap(this.getSystem()));
    }

    @Override
    protected void transitionToLargeSize(int size) {
        if (this._field_map != null) {
            return;
        }
        this.build_field_map();
    }

    protected void build_field_map() {
        int size = this._children == null ? 0 : this._children.length;
        this._field_map = new HashMap<String, Integer>(size);
        this._field_map_duplicate_count = 0;
        int count = this.get_child_count();
        for (int ii = 0; ii < count; ++ii) {
            IonValueLite v = this.get_child(ii);
            SymbolToken fieldNameSymbol = v.getFieldNameSymbol();
            String name = fieldNameSymbol.getText();
            if (this._field_map.get(name) != null) {
                ++this._field_map_duplicate_count;
            }
            this._field_map.put(name, ii);
        }
    }

    private void add_field(String fieldName, int newFieldIdx) {
        Integer idx = this._field_map.get(fieldName);
        if (idx != null) {
            ++this._field_map_duplicate_count;
            if (idx > newFieldIdx) {
                newFieldIdx = idx;
            }
        }
        this._field_map.put(fieldName, newFieldIdx);
    }

    private void remove_field(String fieldName, int lowest_idx, int copies) {
        if (this._field_map == null) {
            return;
        }
        Integer field_idx = this._field_map.get(fieldName);
        assert (field_idx != null);
        this._field_map.remove(fieldName);
        this._field_map_duplicate_count -= copies - 1;
    }

    private void remove_field_from_field_map(String fieldName, int idx) {
        Integer field_idx = this._field_map.get(fieldName);
        assert (field_idx != null);
        if (field_idx != idx) {
            assert (this._field_map_duplicate_count > 0);
            --this._field_map_duplicate_count;
        } else if (this._field_map_duplicate_count > 0) {
            int ii = this.find_last_duplicate(fieldName, idx);
            if (ii == -1) {
                this._field_map.remove(fieldName);
            } else {
                this._field_map.put(fieldName, ii);
                --this._field_map_duplicate_count;
            }
        } else {
            this._field_map.remove(fieldName);
        }
    }

    private void patch_map_elements_helper(int removed_idx) {
        if (this._field_map == null) {
            return;
        }
        if (removed_idx >= this.get_child_count()) {
            return;
        }
        for (int ii = removed_idx; ii < this.get_child_count(); ++ii) {
            IonValueLite value = this.get_child(ii);
            String field_name = value.getFieldName();
            Integer map_idx = this._field_map.get(field_name);
            if (map_idx == ii) continue;
            this._field_map.put(field_name, ii);
        }
    }

    @Override
    public void dump(PrintWriter out) {
        super.dump(out);
        if (this._field_map == null) {
            return;
        }
        out.println("   dups: " + this._field_map_duplicate_count);
        Iterator<Map.Entry<String, Integer>> it = this._field_map.entrySet().iterator();
        out.print("   map: [");
        boolean first = true;
        while (it.hasNext()) {
            Map.Entry<String, Integer> e = it.next();
            if (!first) {
                out.print(",");
            }
            out.print(e.getKey() + ":" + e.getValue());
            first = false;
        }
        out.println("]");
    }

    @Override
    public String validate() {
        if (this._field_map == null) {
            return null;
        }
        String error = "";
        for (Map.Entry<String, Integer> e : this._field_map.entrySet()) {
            IonValueLite v;
            int idx = e.getValue();
            IonValueLite ionValueLite = v = idx >= 0 && idx < this.get_child_count() ? this.get_child(idx) : null;
            if (v != null && idx == v._elementid() && e.getKey().equals(v.getFieldName())) continue;
            error = error + "map entry [" + e + "] doesn't match list value [" + v + "]\n";
        }
        return error == "" ? null : error;
    }

    private int find_last_duplicate(String fieldName, int existing_idx) {
        int ii = existing_idx;
        while (ii > 0) {
            IonValueLite field;
            if (!fieldName.equals((field = this.get_child(--ii)).getFieldName())) continue;
            return ii;
        }
        assert (this.there_is_only_one(fieldName, existing_idx));
        return -1;
    }

    private boolean there_is_only_one(String fieldName, int existing_idx) {
        int count = 0;
        for (int ii = 0; ii < this.get_child_count(); ++ii) {
            IonValueLite v = this.get_child(ii);
            if (!v.getFieldName().equals(fieldName)) continue;
            ++count;
        }
        return count == 1 || count == 0;
    }

    @Override
    int hashCode(PrivateIonValue.SymbolTableProvider symbolTableProvider) {
        int nameHashSalt = 16777619;
        int valueHashSalt = 8191;
        int sidHashSalt = 127;
        int textHashSalt = 31;
        int result = HASH_SIGNATURE;
        if (!this.isNullValue()) {
            for (IonValue v : this) {
                IonValueLite vlite = (IonValueLite)v;
                SymbolToken token = vlite.getFieldNameSymbol(symbolTableProvider);
                String text = token.getText();
                int nameHashCode = text == null ? token.getSid() * 127 : text.hashCode() * 31;
                nameHashCode ^= nameHashCode << 17 ^ nameHashCode >> 15;
                int fieldHashCode = HASH_SIGNATURE;
                fieldHashCode = 8191 * fieldHashCode + vlite.hashCode(symbolTableProvider);
                fieldHashCode = 16777619 * fieldHashCode + nameHashCode;
                fieldHashCode ^= fieldHashCode << 19 ^ fieldHashCode >> 13;
                result += fieldHashCode;
            }
        }
        return this.hashTypeAnnotations(result, symbolTableProvider);
    }

    @Override
    public IonStruct cloneAndRemove(String ... fieldNames) {
        return this.doClone(false, fieldNames);
    }

    @Override
    public IonStruct cloneAndRetain(String ... fieldNames) {
        return this.doClone(true, fieldNames);
    }

    private IonStruct doClone(boolean keep, String ... fieldNames) {
        IonStructLite clone;
        if (this.isNullValue()) {
            clone = this.getSystem().newNullStruct();
        } else {
            HashSet<String> fields = new HashSet<String>(Arrays.asList(fieldNames));
            if (keep && fields.contains(null)) {
                throw new NullPointerException("Can't retain an unknown field name");
            }
            clone = this.getSystem().newEmptyStruct();
            for (IonValue value : this) {
                SymbolToken fieldNameSymbol = value.getFieldNameSymbol();
                String fieldName = fieldNameSymbol.getText();
                if (fields.contains(fieldName) != keep) continue;
                fieldName = value.getFieldName();
                clone.add(fieldName, value.clone());
            }
        }
        clone.setTypeAnnotationSymbols(this.getTypeAnnotationSymbols());
        return clone;
    }

    @Override
    public IonType getType() {
        return IonType.STRUCT;
    }

    @Override
    public boolean containsKey(Object fieldName) {
        String name = (String)fieldName;
        return null != this.get(name);
    }

    @Override
    public boolean containsValue(Object value) {
        IonValue v = (IonValue)value;
        return v.getContainer() == this;
    }

    @Override
    public IonValue get(String fieldName) {
        int field_idx = this.find_field_helper(fieldName);
        IonValueLite field = field_idx < 0 ? null : this.get_child(field_idx);
        return field;
    }

    private int find_field_helper(String fieldName) {
        IonStructLite.validateFieldName(fieldName);
        if (!this.isNullValue()) {
            if (this._field_map != null) {
                Integer idx = this._field_map.get(fieldName);
                if (idx != null) {
                    return idx;
                }
            } else {
                int size = this.get_child_count();
                for (int ii = 0; ii < size; ++ii) {
                    IonValueLite field = this.get_child(ii);
                    if (!fieldName.equals(field.getFieldName())) continue;
                    return ii;
                }
            }
        }
        return -1;
    }

    @Override
    public void clear() {
        super.clear();
        this._field_map = null;
        this._field_map_duplicate_count = 0;
    }

    @Override
    public boolean add(IonValue child) throws NullPointerException, IllegalArgumentException, ContainedValueException {
        String text = child.getFieldNameSymbol().getText();
        if (text != null) {
            IonStructLite.validateFieldName(text);
        }
        IonValueLite concrete = (IonValueLite)child;
        this._add(text, concrete);
        return true;
    }

    @Override
    public ValueFactory add(final String fieldName) {
        return new PrivateCurriedValueFactory(this._context.getSystem()){

            protected void handle(IonValue newValue) {
                IonStructLite.this.add(fieldName, newValue);
            }
        };
    }

    private void _add(String fieldName, IonValueLite child) {
        int size = this.get_child_count();
        this.add(size, child);
        if (this._field_map != null) {
            this.add_field(fieldName, child._elementid());
        }
    }

    @Override
    public void add(String fieldName, IonValue value) {
        this.checkForLock();
        this.validateNewChild(value);
        IonStructLite.validateFieldName(fieldName);
        IonValueLite concrete = (IonValueLite)value;
        this._add(fieldName, concrete);
        concrete.setFieldName(fieldName);
    }

    @Override
    public void add(SymbolToken fieldName, IonValue child) {
        String text = fieldName.getText();
        if (text != null) {
            this.add(text, child);
            return;
        }
        if (fieldName.getSid() < 1) {
            throw new IllegalArgumentException("fieldName has no text or ID");
        }
        this.checkForLock();
        this.validateNewChild(child);
        IonValueLite concrete = (IonValueLite)child;
        concrete.setFieldNameSymbol(fieldName);
        this._add(text, concrete);
    }

    @Override
    public ValueFactory put(final String fieldName) {
        return new PrivateCurriedValueFactory(this._context.getSystem()){

            protected void handle(IonValue newValue) {
                IonStructLite.this.put(fieldName, newValue);
            }
        };
    }

    @Override
    public void putAll(Map<? extends String, ? extends IonValue> m) {
        for (Map.Entry<? extends String, ? extends IonValue> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void put(String fieldName, IonValue value) {
        this.checkForLock();
        IonStructLite.validateFieldName(fieldName);
        if (value != null) {
            this.validateNewChild(value);
        }
        int lowestRemovedIndex = this.get_child_count();
        boolean any_removed = false;
        if (this._field_map != null && this._field_map_duplicate_count == 0) {
            Integer idx = this._field_map.get(fieldName);
            if (idx != null) {
                lowestRemovedIndex = idx;
                this.remove_field_from_field_map(fieldName, lowestRemovedIndex);
                this.remove_child(lowestRemovedIndex);
                any_removed = true;
            }
        } else {
            int copies_removed = 0;
            int ii = this.get_child_count();
            while (ii > 0) {
                IonValueLite child;
                if (!fieldName.equals((child = this.get_child(--ii)).getFieldNameSymbol().getText())) continue;
                this.remove_child(ii);
                lowestRemovedIndex = ii;
                ++copies_removed;
                any_removed = true;
            }
            if (any_removed) {
                this.remove_field(fieldName, lowestRemovedIndex, copies_removed);
            }
        }
        if (any_removed) {
            this.patch_map_elements_helper(lowestRemovedIndex);
            this.patch_elements_helper(lowestRemovedIndex);
        }
        if (value != null) {
            this.add(fieldName, value);
        }
    }

    @Override
    public ListIterator<IonValue> listIterator(int index) {
        return new IonContainerLite.SequenceContentIterator(index, this.isReadOnly()){

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
                if (IonStructLite.this._field_map != null) {
                    IonStructLite.this.remove_field_from_field_map(concrete.getFieldName(), idx);
                }
                super.remove();
                if (IonStructLite.this._field_map != null) {
                    IonStructLite.this.patch_map_elements_helper(idx);
                }
            }
        };
    }

    @Override
    public IonValue remove(String fieldName) {
        this.checkForLock();
        IonValue field = this.get(fieldName);
        if (field == null) {
            return null;
        }
        int idx = ((IonValueLite)field)._elementid();
        if (this._field_map != null) {
            this.remove_field_from_field_map(fieldName, idx);
        }
        super.remove(field);
        if (this._field_map != null) {
            this.patch_map_elements_helper(idx);
        }
        return field;
    }

    @Override
    public boolean remove(IonValue element) {
        if (element == null) {
            throw new NullPointerException();
        }
        this.checkForLock();
        if (element.getContainer() != this) {
            return false;
        }
        IonValueLite concrete = (IonValueLite)element;
        int idx = concrete._elementid();
        if (this._field_map != null) {
            this.remove_field_from_field_map(concrete.getFieldName(), idx);
        }
        super.remove(concrete);
        if (this._field_map != null) {
            this.patch_map_elements_helper(idx);
        }
        return true;
    }

    @Override
    public boolean removeAll(String ... fieldNames) {
        int size;
        boolean removedAny = false;
        this.checkForLock();
        int ii = size = this.get_child_count();
        while (ii > 0) {
            IonValueLite field;
            if (!IonStructLite.isListedField(field = this.get_child(--ii), fieldNames)) continue;
            field.removeFromContainer();
            removedAny = true;
        }
        return removedAny;
    }

    @Override
    public boolean retainAll(String ... fieldNames) {
        int size;
        this.checkForLock();
        boolean removedAny = false;
        int ii = size = this.get_child_count();
        while (ii > 0) {
            IonValueLite field;
            if (IonStructLite.isListedField(field = this.get_child(--ii), fieldNames)) continue;
            field.removeFromContainer();
            removedAny = true;
        }
        return removedAny;
    }

    private static boolean isListedField(IonValue field, String[] fields) {
        String fieldName = field.getFieldName();
        for (String key : fields) {
            if (!key.equals(fieldName)) continue;
            return true;
        }
        return false;
    }

    private static void validateFieldName(String fieldName) {
        if (fieldName == null) {
            throw new NullPointerException("fieldName is null");
        }
        if (fieldName.length() == 0) {
            throw new IllegalArgumentException("fieldName is empty");
        }
    }

    @Override
    final void writeBodyTo(IonWriter writer, PrivateIonValue.SymbolTableProvider symbolTableProvider) throws IOException {
        if (this.isNullValue()) {
            writer.writeNull(IonType.STRUCT);
        } else {
            writer.stepIn(IonType.STRUCT);
            this.writeChildren(writer, this, symbolTableProvider);
            writer.stepOut();
        }
    }

    @Override
    public void accept(ValueVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

