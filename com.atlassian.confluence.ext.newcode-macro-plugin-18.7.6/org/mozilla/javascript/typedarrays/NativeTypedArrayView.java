/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.typedarrays;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ExternalArrayData;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeArrayIterator;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;
import org.mozilla.javascript.typedarrays.NativeArrayBuffer;
import org.mozilla.javascript.typedarrays.NativeArrayBufferView;
import org.mozilla.javascript.typedarrays.NativeTypedArrayIterator;

public abstract class NativeTypedArrayView<T>
extends NativeArrayBufferView
implements List<T>,
RandomAccess,
ExternalArrayData {
    private static final long serialVersionUID = -4963053773152251274L;
    protected final int length;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_get = 3;
    private static final int Id_set = 4;
    private static final int Id_subarray = 5;
    private static final int SymbolId_iterator = 6;
    protected static final int MAX_PROTOTYPE_ID = 6;
    private static final int Id_length = 4;
    private static final int Id_BYTES_PER_ELEMENT = 5;
    private static final int MAX_INSTANCE_ID = 5;

    protected NativeTypedArrayView() {
        this.length = 0;
    }

    protected NativeTypedArrayView(NativeArrayBuffer ab, int off, int len, int byteLen) {
        super(ab, off, byteLen);
        this.length = len;
    }

    @Override
    public Object get(int index, Scriptable start) {
        return this.js_get(index);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return !this.checkIndex(index);
    }

    @Override
    public void put(int index, Scriptable start, Object val) {
        this.js_set(index, val);
    }

    @Override
    public void delete(int index) {
    }

    @Override
    public Object[] getIds() {
        Object[] ret = new Object[this.length];
        for (int i = 0; i < this.length; ++i) {
            ret[i] = i;
        }
        return ret;
    }

    protected boolean checkIndex(int index) {
        return index < 0 || index >= this.length;
    }

    public abstract int getBytesPerElement();

    protected abstract NativeTypedArrayView<T> construct(NativeArrayBuffer var1, int var2, int var3);

    protected abstract Object js_get(int var1);

    protected abstract Object js_set(int var1, Object var2);

    protected abstract NativeTypedArrayView<T> realThis(Scriptable var1, IdFunctionObject var2);

    private NativeArrayBuffer makeArrayBuffer(Context cx, Scriptable scope, int length) {
        return (NativeArrayBuffer)cx.newObject(scope, "ArrayBuffer", new Object[]{(double)length * (double)this.getBytesPerElement()});
    }

    private NativeTypedArrayView<T> js_constructor(Context cx, Scriptable scope, Object[] args) {
        if (!NativeTypedArrayView.isArg(args, 0)) {
            return this.construct(new NativeArrayBuffer(), 0, 0);
        }
        Object arg0 = args[0];
        if (arg0 == null) {
            return this.construct(new NativeArrayBuffer(), 0, 0);
        }
        if (arg0 instanceof Number || arg0 instanceof String) {
            int length = ScriptRuntime.toInt32(arg0);
            NativeArrayBuffer buffer = this.makeArrayBuffer(cx, scope, length);
            return this.construct(buffer, 0, length);
        }
        if (arg0 instanceof NativeTypedArrayView) {
            NativeTypedArrayView src = (NativeTypedArrayView)arg0;
            NativeArrayBuffer na = this.makeArrayBuffer(cx, scope, src.length);
            NativeTypedArrayView<T> v = this.construct(na, 0, src.length);
            for (int i = 0; i < src.length; ++i) {
                v.js_set(i, src.js_get(i));
            }
            return v;
        }
        if (arg0 instanceof NativeArrayBuffer) {
            NativeArrayBuffer na = (NativeArrayBuffer)arg0;
            int byteOff = NativeTypedArrayView.isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : 0;
            int byteLen = NativeTypedArrayView.isArg(args, 2) ? ScriptRuntime.toInt32(args[2]) * this.getBytesPerElement() : na.getLength() - byteOff;
            if (byteOff < 0 || byteOff > na.buffer.length) {
                throw ScriptRuntime.rangeError("offset out of range");
            }
            if (byteLen < 0 || byteOff + byteLen > na.buffer.length) {
                throw ScriptRuntime.rangeError("length out of range");
            }
            if (byteOff % this.getBytesPerElement() != 0) {
                throw ScriptRuntime.rangeError("offset must be a multiple of the byte size");
            }
            if (byteLen % this.getBytesPerElement() != 0) {
                throw ScriptRuntime.rangeError("offset and buffer must be a multiple of the byte size");
            }
            return this.construct(na, byteOff, byteLen / this.getBytesPerElement());
        }
        if (arg0 instanceof NativeArray) {
            NativeArray array = (NativeArray)arg0;
            NativeArrayBuffer na = this.makeArrayBuffer(cx, scope, array.size());
            NativeTypedArrayView<T> v = this.construct(na, 0, array.size());
            for (int i = 0; i < array.size(); ++i) {
                Object value = array.get(i, (Scriptable)array);
                if (value == Scriptable.NOT_FOUND || value == Undefined.instance) {
                    v.js_set(i, ScriptRuntime.NaNobj);
                    continue;
                }
                if (value instanceof Wrapper) {
                    v.js_set(i, ((Wrapper)value).unwrap());
                    continue;
                }
                v.js_set(i, value);
            }
            return v;
        }
        if (ScriptRuntime.isArrayObject(arg0)) {
            Object[] arrayElements = ScriptRuntime.getArrayElements((Scriptable)arg0);
            NativeArrayBuffer na = this.makeArrayBuffer(cx, scope, arrayElements.length);
            NativeTypedArrayView<T> v = this.construct(na, 0, arrayElements.length);
            for (int i = 0; i < arrayElements.length; ++i) {
                v.js_set(i, arrayElements[i]);
            }
            return v;
        }
        throw ScriptRuntime.constructError("Error", "invalid argument");
    }

    private void setRange(NativeTypedArrayView<T> v, int off) {
        if (off >= this.length) {
            throw ScriptRuntime.rangeError("offset out of range");
        }
        if (v.length > this.length - off) {
            throw ScriptRuntime.rangeError("source array too long");
        }
        if (v.arrayBuffer == this.arrayBuffer) {
            int i;
            Object[] tmp = new Object[v.length];
            for (i = 0; i < v.length; ++i) {
                tmp[i] = v.js_get(i);
            }
            for (i = 0; i < v.length; ++i) {
                this.js_set(i + off, tmp[i]);
            }
        } else {
            for (int i = 0; i < v.length; ++i) {
                this.js_set(i + off, v.js_get(i));
            }
        }
    }

    private void setRange(NativeArray a, int off) {
        if (off > this.length) {
            throw ScriptRuntime.rangeError("offset out of range");
        }
        if (off + a.size() > this.length) {
            throw ScriptRuntime.rangeError("offset + length out of range");
        }
        int pos = off;
        for (Object val : a) {
            this.js_set(pos, val);
            ++pos;
        }
    }

    private Object js_subarray(Context cx, Scriptable scope, int s, int e) {
        int start = s < 0 ? this.length + s : s;
        int end = e < 0 ? this.length + e : e;
        start = Math.max(0, start);
        end = Math.min(this.length, end);
        int len = Math.max(0, end - start);
        int byteOff = Math.min(start * this.getBytesPerElement(), this.arrayBuffer.getLength());
        return cx.newObject(scope, this.getClassName(), new Object[]{this.arrayBuffer, byteOff, len});
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(this.getClassName())) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1: {
                if (thisObj != null && cx.getLanguageVersion() >= 200) {
                    throw ScriptRuntime.typeErrorById("msg.only.from.new", this.getClassName());
                }
                return this.js_constructor(cx, scope, args);
            }
            case 2: {
                NativeTypedArrayView<T> realThis = this.realThis(thisObj, f);
                int arrayLength = realThis.getArrayLength();
                StringBuilder builder = new StringBuilder();
                if (arrayLength > 0) {
                    builder.append(ScriptRuntime.toString(realThis.js_get(0)));
                }
                for (int i = 1; i < arrayLength; ++i) {
                    builder.append(',');
                    builder.append(ScriptRuntime.toString(realThis.js_get(i)));
                }
                return builder.toString();
            }
            case 3: {
                if (args.length > 0) {
                    return this.realThis(thisObj, f).js_get(ScriptRuntime.toInt32(args[0]));
                }
                throw ScriptRuntime.constructError("Error", "invalid arguments");
            }
            case 4: {
                if (args.length > 0) {
                    NativeTypedArrayView<T> self = this.realThis(thisObj, f);
                    if (args[0] instanceof NativeTypedArrayView) {
                        int offset = NativeTypedArrayView.isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : 0;
                        NativeTypedArrayView nativeView = (NativeTypedArrayView)args[0];
                        super.setRange(nativeView, offset);
                        return Undefined.instance;
                    }
                    if (args[0] instanceof NativeArray) {
                        int offset = NativeTypedArrayView.isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : 0;
                        super.setRange((NativeArray)args[0], offset);
                        return Undefined.instance;
                    }
                    if (args[0] instanceof Scriptable) {
                        return Undefined.instance;
                    }
                    if (NativeTypedArrayView.isArg(args, 2)) {
                        return self.js_set(ScriptRuntime.toInt32(args[0]), args[1]);
                    }
                }
                throw ScriptRuntime.constructError("Error", "invalid arguments");
            }
            case 5: {
                int end;
                NativeTypedArrayView<T> self = this.realThis(thisObj, f);
                int start = NativeTypedArrayView.isArg(args, 0) ? ScriptRuntime.toInt32(args[0]) : 0;
                int n = end = NativeTypedArrayView.isArg(args, 1) ? ScriptRuntime.toInt32(args[1]) : self.length;
                if (cx.getLanguageVersion() >= 200 || args.length > 0) {
                    return super.js_subarray(cx, scope, start, end);
                }
                throw ScriptRuntime.constructError("Error", "invalid arguments");
            }
            case 6: {
                return new NativeArrayIterator(scope, thisObj, NativeArrayIterator.ARRAY_ITERATOR_TYPE.VALUES);
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        if (id == 6) {
            this.initPrototypeMethod((Object)this.getClassName(), id, SymbolKey.ITERATOR, "[Symbol.iterator]", 0);
            return;
        }
        String fnName = null;
        switch (id) {
            case 1: {
                arity = 3;
                s = "constructor";
                break;
            }
            case 2: {
                arity = 0;
                s = "toString";
                break;
            }
            case 3: {
                arity = 1;
                s = "get";
                break;
            }
            case 4: {
                arity = 2;
                s = "set";
                break;
            }
            case 5: {
                arity = 2;
                s = "subarray";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        this.initPrototypeMethod((Object)this.getClassName(), id, s, fnName, arity);
    }

    @Override
    protected int findPrototypeId(Symbol k) {
        if (SymbolKey.ITERATOR.equals(k)) {
            return 6;
        }
        return 0;
    }

    @Override
    protected int findPrototypeId(String s) {
        int id;
        switch (s) {
            case "constructor": {
                id = 1;
                break;
            }
            case "toString": {
                id = 2;
                break;
            }
            case "get": {
                id = 3;
                break;
            }
            case "set": {
                id = 4;
                break;
            }
            case "subarray": {
                id = 5;
                break;
            }
            default: {
                id = 0;
            }
        }
        return id;
    }

    @Override
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        ctor.defineProperty("BYTES_PER_ELEMENT", (Object)ScriptRuntime.wrapInt(this.getBytesPerElement()), 7);
        super.fillConstructorProperties(ctor);
    }

    @Override
    protected int getMaxInstanceId() {
        return 5;
    }

    @Override
    protected String getInstanceIdName(int id) {
        switch (id) {
            case 4: {
                return "length";
            }
            case 5: {
                return "BYTES_PER_ELEMENT";
            }
        }
        return super.getInstanceIdName(id);
    }

    @Override
    protected Object getInstanceIdValue(int id) {
        switch (id) {
            case 4: {
                return ScriptRuntime.wrapInt(this.length);
            }
            case 5: {
                return ScriptRuntime.wrapInt(this.getBytesPerElement());
            }
        }
        return super.getInstanceIdValue(id);
    }

    @Override
    protected int findInstanceIdInfo(String s) {
        int id;
        switch (s) {
            case "length": {
                id = 4;
                break;
            }
            case "BYTES_PER_ELEMENT": {
                id = 5;
                break;
            }
            default: {
                id = 0;
            }
        }
        if (id == 0) {
            return super.findInstanceIdInfo(s);
        }
        if (id == 5) {
            return NativeTypedArrayView.instanceIdInfo(7, id);
        }
        return NativeTypedArrayView.instanceIdInfo(5, id);
    }

    @Override
    public Object getArrayElement(int index) {
        return this.js_get(index);
    }

    @Override
    public void setArrayElement(int index, Object value) {
        this.js_set(index, value);
    }

    @Override
    public int getArrayLength() {
        return this.length;
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        for (Object o : objects) {
            if (this.contains(o)) continue;
            return false;
        }
        return true;
    }

    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < this.length; ++i) {
            if (!o.equals(this.js_get(i))) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (int i = this.length - 1; i >= 0; --i) {
            if (!o.equals(this.js_get(i))) continue;
            return i;
        }
        return -1;
    }

    @Override
    public Object[] toArray() {
        Object[] a = new Object[this.length];
        for (int i = 0; i < this.length; ++i) {
            a[i] = this.js_get(i);
        }
        return a;
    }

    @Override
    public <U> U[] toArray(U[] ts) {
        Object[] a = ts.length >= this.length ? ts : (Object[])Array.newInstance(ts.getClass().getComponentType(), this.length);
        for (int i = 0; i < this.length; ++i) {
            try {
                a[i] = this.js_get(i);
                continue;
            }
            catch (ClassCastException cce) {
                throw new ArrayStoreException();
            }
        }
        return a;
    }

    @Override
    public int size() {
        return this.length;
    }

    @Override
    public boolean isEmpty() {
        return this.length == 0;
    }

    @Override
    public boolean contains(Object o) {
        return this.indexOf(o) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        try {
            NativeTypedArrayView v = (NativeTypedArrayView)o;
            if (this.length != v.length) {
                return false;
            }
            for (int i = 0; i < this.length; ++i) {
                if (this.js_get(i).equals(v.js_get(i))) continue;
                return false;
            }
            return true;
        }
        catch (ClassCastException cce) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hc = 0;
        for (int i = 0; i < this.length; ++i) {
            hc += this.js_get(i).hashCode();
        }
        return hc;
    }

    @Override
    public Iterator<T> iterator() {
        return new NativeTypedArrayIterator(this, 0);
    }

    @Override
    public ListIterator<T> listIterator() {
        return new NativeTypedArrayIterator(this, 0);
    }

    @Override
    public ListIterator<T> listIterator(int start) {
        if (this.checkIndex(start)) {
            throw new IndexOutOfBoundsException();
        }
        return new NativeTypedArrayIterator(this, start);
    }

    @Override
    public List<T> subList(int i, int i2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T aByte) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int i, T aByte) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        throw new UnsupportedOperationException();
    }
}

